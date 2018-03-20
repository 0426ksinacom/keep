package com.demoapplication.uitil;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.Surface;

import java.io.FileNotFoundException;

/**
 * Created by Administrator on 2016/10/4.
 * Function:  用系统MediaPlayer   负责播放asset   下的MP3
 *
 */
public class MediaPlayerMp3 {

    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private MediaPlayer.OnErrorListener mOnErrorListener;




    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    /**
     * PlaybackCompleted状态：文件正常播放完毕，而又没有设置循环播放的话就进入该状态，
     * 并会触发OnCompletionListener的onCompletion
     * ()方法。此时可以调用start()方法重新从头播放文件，也可以stop()停止MediaPlayer，或者也可以seekTo()来重新定位播放位置。
     */
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    /**
     * Released/End状态：通过release()方法可以进入End状态
     */
    private static final int STATE_RELEASED = 5;

    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;

    private MediaPlayer mMediaPlayer = null;


    private int mDuration;
    private String  mUri;

private Context context;


    public MediaPlayerMp3( Context c){
        context = c;


    }
    public void setOnErrorListener(MediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * 开始播放
     * @param path
     */
    public void setVideoPath(String path ) {
        // && MediaUtils.isNative(path)
        if (!TextUtils.isEmpty(path)) {
            mTargetState = STATE_PREPARED;
            openVideo( path);
        }
    }


    public void setVolume(float volume) {
        //可用状态{Idle, Initialized, Stopped, Prepared, Started, Paused, PlaybackCompleted}
        if (mMediaPlayer != null && (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
            try {
                mMediaPlayer.setVolume(volume, volume);
            } catch (Exception e) {
                LogMy.e(e);
            }
        }
    }


    private void openVideo(String uri) {

        //Idle 状态：当使用new()方法创建一个MediaPlayer对象或者调用了其reset()方法时，该MediaPlayer对象处于idle状态。
        //End 状态：通过release()方法可以进入End状态，只要MediaPlayer对象不再被使用，就应当尽快将其通过release()方法释放掉
        //Initialized 状态：这个状态比较简单，MediaPlayer调用setDataSource()方法就进入Initialized状态，表示此时要播放的文件已经设置好了。
        //Prepared 状态：初始化完成之后还需要通过调用prepare()或prepareAsync()方法，这两个方法一个是同步的一个是异步的，只有进入Prepared状态，才表明MediaPlayer到目前为止都没有错误，可以进行文件播放。

        Exception exception = null;

        try {
            if (mMediaPlayer == null) {

//                mMediaPlayer =  MediaPlayer.create(getContext(),);
//                mMediaPlayer =   MediaPlayer.create( getContext(),mUri);
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnPreparedListener(mPreparedListener);
                mMediaPlayer.setOnCompletionListener(mCompletionListener);
                mMediaPlayer.setOnErrorListener(mErrorListener);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //			mMediaPlayer.setScreenOnWhilePlaying(true);
                //				mMediaPlayer.setVolume(mSystemVolumn, mSystemVolumn); surface
            } else {
                mMediaPlayer.reset();
            }

            AssetFileDescriptor fileDescriptor =  context.getAssets().openFd(uri);
            mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());

            //			if (mLooping)
            //				mMediaPlayer.setLooping(true);//循环播放
            mMediaPlayer.prepareAsync();
            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = STATE_PREPARING;
        } catch (FileNotFoundException ex) {
            exception = ex;
        } catch (IllegalArgumentException ex) {
            exception = ex;
        } catch (Exception ex) {
            exception = ex;
        }
        if (exception != null) {
            LogMy.e(exception);
            mCurrentState = STATE_ERROR;
            if (mErrorListener != null)
                mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        }

    }


    public void start() {
        mTargetState = STATE_PLAYING;
        //可用状态{Prepared, Started, Paused, PlaybackCompleted}
//        LogMy.e("[SurfaceVideoView]start...1");
        if (mMediaPlayer != null && (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
            try {
                if (!isPlaying())
                    mMediaPlayer.start();
//                LogMy.e("[SurfaceVideoView]start...2");
                mCurrentState = STATE_PLAYING;

            } catch (IllegalStateException e) {
//                tryAgain(e);
            } catch (Exception e) {
//                tryAgain(e);
            }
        }
    }

    public void pause() {
        mTargetState = STATE_PAUSED;
        //可用状态{Started, Paused}
        if (mMediaPlayer != null && (mCurrentState == STATE_PLAYING)) {
            try {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;

            } catch (IllegalStateException e) {
                tryAgain(e);
            } catch (Exception e) {
                tryAgain(e);
            }
        }
    }

    /**
     * 是否已经释放
     */
    public boolean isRelease() {
        return mMediaPlayer == null || mCurrentState == STATE_IDLE || mCurrentState == STATE_ERROR || mCurrentState == STATE_RELEASED;
    }
    /**
     * 调用release方法以后MediaPlayer无法再恢复使用
     */
    public void release() {
        mTargetState = STATE_RELEASED;
        mCurrentState = STATE_RELEASED;
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release();
            } catch (IllegalStateException e) {
                LogMy.e(e);
            } catch (Exception e) {
                LogMy.e(e);
            }
            mMediaPlayer = null;
        }
    }

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            //			mTargetState = STATE_PLAYBACK_COMPLETED;
            if (mOnCompletionListener != null)
                mOnCompletionListener.onCompletion(mp);
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
            LogMy.e("[VideoView]Error:" + framework_err + "," + impl_err);
            mCurrentState = STATE_ERROR;
            //			mTargetState = STATE_ERROR;
            //FIX，可以考虑出错以后重新开始
            if (mOnErrorListener != null)
                mOnErrorListener.onError(mp, framework_err, impl_err);

            return true;
        }
    };


    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //必须是正常状态
            if (mCurrentState == STATE_PREPARING) {
                mCurrentState = STATE_PREPARED;
                try {
                    mDuration = mp.getDuration();
                } catch (IllegalStateException e) {
                    LogMy.e(e);
                }

                setVolume(getSystemVolumn(context));

                switch (mTargetState) {
                    case STATE_PREPARED:
                        start();
                        break;
                    case STATE_PLAYING:
                        start();
                        break;
                }
            }
        }
    };




    public int getDuration() {
        return mDuration;
    }

    /**
     * 重试
     */
    private void tryAgain(Exception e) {
        mCurrentState = STATE_ERROR;
        LogMy.e(e);
        openVideo(mUri);
    }


    public boolean isPlaying() {
        //可用状态{Idle, Initialized, Prepared, Started, Paused, Stopped, PlaybackCompleted}
        if (mMediaPlayer != null && mCurrentState == STATE_PLAYING) {
            try {
                return mMediaPlayer.isPlaying();
            } catch (IllegalStateException e) {
                LogMy.e(e);
            } catch (Exception e) {
                LogMy.e(e);
            }
        }
        return false;
    }



    /** 更新音量 */
    public static float getSystemVolumn(Context context) {
        if (context != null) {
            try {
                AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int maxVolumn = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 1.0F / maxVolumn;
            } catch (UnsupportedOperationException e) {

            }
        }
        return 0.5F;
    }





}
