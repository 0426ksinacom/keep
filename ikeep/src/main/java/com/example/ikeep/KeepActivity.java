package com.demoapplication.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.demoapplication.R;
import com.demoapplication.base.BaseAppCompatActivity;
import com.demoapplication.bean.Keepmotion;
import com.demoapplication.ui.RoundProgressBar;
import com.demoapplication.ui.SteppingProgressBar;
import com.demoapplication.ui.TextureVideoView;
import com.demoapplication.uitil.AnimUtils;
import com.demoapplication.uitil.Fileutil;
import com.demoapplication.uitil.KeepMediaPresenter;
import com.demoapplication.uitil.MediaPlayerMp3;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class KeepActivity extends BaseAppCompatActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.roundProgressBar)
    RoundProgressBar roundProgressBar;

    @BindView(R.id.steppingprogressbar)
    SteppingProgressBar mProgressBar;


    @BindView(R.id.countdowm)
    TextView countdowm;
    @BindView(R.id.totalnum)
    TextView totalnum;
    @BindView(R.id.go)
    TextView go;

    PowerManager powerManager = null;
    PowerManager.WakeLock wakeLock = null;

    @OnClick(R.id.left)
    void left() {

        keepMediaPresenter.preMotion();


        totalnum.setText("");
        countdowm.setText("");
        go.setText("3");
        roundProgressBar.setProgress(0);


    }


    @OnClick(R.id.staerorstop)
    void staerorstop() {

//        keepMediaPresenter.pauseOrRestart();
        if (mVideoView.isPlaying()) {
            keepMediaPresenter.pause();
            mVideoView.pause();

            // show the status bar.
            mVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
        } else {
            mVideoView.start();
            keepMediaPresenter.restart();

            toolbar.setVisibility(View.GONE);
            // hide the status bar.
            mVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        }


    }

    @OnClick(R.id.right)
    void right() {

        keepMediaPresenter.nextMotion();

        totalnum.setText("");
        countdowm.setText("");
        go.setText("￥");
        roundProgressBar.setProgress(0);
    }


    @BindView(R.id.videoview)
    /**
     * 播放控件
     */
            TextureVideoView mVideoView;

    MediaPlayerMp3 mediaPlayerMp3;


    KeepMediaPresenter keepMediaPresenter;


    private final long TIME = (long) 8 * 24 * 60 * 60 * 1000;

    List<Keepmotion> datalist;

    /**
     * 每个motion  长度   就是  倒计时的
     */
    private int pregroupcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep);


    }

    @Override
    protected void initView() {

        powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        mVideoView.setOnPreparedListener(this);
//        mVideoView.setOnPlayStateListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnClickListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnCompletionListener(this);

//        mVideoView.getLayoutParams().height = Hardware.getScreenWidth(this);

    }

    final String path = "keep/video/_video_913b21ef3a1be2e04a0eb686d1ca6baac7c8558f.mp4";

    @Override
    protected void initData() {
        //将字体文件保存在assets/fonts/目录下，创建Typeface对象
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/DIN_Condensed_Bold.ttf");
        Typeface typeFace1 = Typeface.createFromAsset(getAssets(), "fonts/ArchivoNarrow-Bold.otf");
        Typeface typeFace2 = Typeface.createFromAsset(getAssets(), "fonts/ArchivoNarrow-Regular.otf");
        //使用字体
        totalnum.setTypeface(typeFace);
        countdowm.setTypeface(typeFace1);
        go.setTypeface(typeFace2);


        String sjson = Fileutil.getJson(this, "keep/keepdata.json");
        datalist = JSON.parseArray(sjson, Keepmotion.class);

        keepMediaPresenter = new KeepMediaPresenter(this, datalist);
        keepMediaPresenter.setplayerListener(new KeepMediaPresenter.PlayerListener() {
            @Override
            public void preMotion(int position) {

                Keepmotion currentKeepmotion = datalist.get(position);

                mVideoView.setVideoPath("keep/video/" + currentKeepmotion.motion_video);

            }

            @Override
            public void start(int lpregroupcount, String motionurl) {

//视频重新加载  和音频同步
                mVideoView.setVideoPath(motionurl);

                go.setText("");


                pregroupcount = lpregroupcount;

                countdowm.setText("1");
                totalnum.setText("/" + lpregroupcount + "\"");

                roundProgressBar.setMax(pregroupcount);

            }

            @Override
            public void end() {

                totalnum.setText("");
                countdowm.setText("");
                go.setText("3");

                roundProgressBar.setProgress(0);

            }

            @Override
            public void countDown(int num) {

                countdowm.setText("" + num);
                roundProgressBar.setProgress(num);


            }

            @Override
            public void count3211Down(String num) {


                go.setText("" + num);


            }
        });


        mediaPlayerMp3 = new MediaPlayerMp3(this.getApplicationContext());

        mProgressBar.setMax(1000);

//        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("a2.mp3");
//        mediaPlayer = new MediaPlayer();
//
//        mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),file:///android_asset


        final String path1 = "keep/number/N002.mp3";
//        mediaPlayerMp3.setVideoPath(path1 );


//        keepMediaPresenter.start();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//
//
////                countdownView.pause();
//
//                String path = "keep/g_2_first_motion.mp3";
//
////                mediaPlayerMp3.setVideoPath(path );
////
////
////                try {
////                    AssetFileDescriptor fileDescriptor = KeepActivity.this.getAssets().openFd(path);
////                    MediaPlayer  mediaPlayer = new MediaPlayer();
//////                    mediaPlayer.setDataSource(path);
////                    mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
////                            fileDescriptor.getStartOffset(),
////                            fileDescriptor.getLength());
////                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
////                        @Override
////                        public void onCompletion(MediaPlayer mp) {
////
////                        }
////                    });
////                    mediaPlayer.prepare();
////                    mediaPlayer.start();
////                }catch (Exception e){
////                    e.printStackTrace();
////                }
//
//
//                mProgressBar.setProgress(400);
//                mProgressBar.setTimeStamp(true);
//
//                mProgressBar.setProgress(800);
//                mProgressBar.setTimeStamp(true);
//
//            }
//        }, 2000);


        showDoalog();

    }

     boolean  isfirst = false;

    void   showDoalog(){


         AlertDialog.Builder builder = new AlertDialog.Builder(this );
         builder.setTitle("keep");
         builder.setMessage("模仿keep 视频播放     请尊重原著 ！"+"\n"+"倒计时有问题 ，了解了基本原理"+"\n"+"0426k@sina.com"+"\n"+"https://github.com/0426ksinacom/keep");
         builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {

                 isfirst = true;

                 keepMediaPresenter.start();

                 if (mVideoView == null) {
                     ToastMessage("哎呀~ 播放器出错");
                 } else {
                     mVideoView.setVideoPath(path);
                 }
             }
         });
//         builder.setNegativeButton("Cancel", null);
         builder.show();


     }





    @Override
    public void onResume() {
        super.onResume();


        if (isfirst){
            if (mVideoView != null) {
                if (mVideoView.isRelease())
                    mVideoView.reOpen();
                else
                    mVideoView.start();
            }

            keepMediaPresenter.restart();

        }

        wakeLock.acquire();




    }

    @Override
    public void onPause() {
        super.onPause();

        if (mVideoView != null) {
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
            }
        }

        keepMediaPresenter.pause();

        wakeLock.release();


    }

    @Override
    protected void onDestroy() {
        if (mVideoView != null) {
            mVideoView.release();
        }
        if (mediaPlayerMp3 != null) {
            mediaPlayerMp3.release();
        }
        if (keepMediaPresenter != null) {
            keepMediaPresenter.release();
        }


        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mVideoView.setVolume(getSystemVolumn(this.getApplicationContext()));
        mVideoView.start();
//		new Handler().postDelayed(new Runnable() {
//
//			@SuppressWarnings("deprecation")
//			@Override
//			public void run() {
//				if (DeviceUtils.hasJellyBean()) {
//					mVideoView.setBackground(null);
//				} else {
//					mVideoView.setBackgroundDrawable(null);
//				}
//			}
//		}, 300);
        // Hide the status bar.
        go.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {//跟随系统音量走
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                mVideoView.dispatchKeyEvent(this.getApplicationContext(), event);
                break;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (!isFinishing()) {
            //播放失败
            ToastMessage("播放失败");
        }
//        finish();
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.root:
//                finish();
//                break;
            case R.id.videoview:


                if (mVideoView.isPlaying()) {

                    keepMediaPresenter.pause();

                    mVideoView.pause();

                    // show the status bar.
                    go.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                } else {
                    mVideoView.start();

                    keepMediaPresenter.restart();

                    toolbar.setVisibility(View.GONE);
                    // hide the status bar.
                    go.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

                }


                break;

        }
    }

    //播放完毕
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (!isFinishing())
            mVideoView.reOpen();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                //音频和视频数据不正确
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (!isFinishing())
                    mVideoView.pause();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (!isFinishing())
                    mVideoView.start();
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                if (Build.VERSION.SDK_INT >= 16) {
                    mVideoView.setBackground(null);
                } else {
                    mVideoView.setBackgroundDrawable(null);
                }
                break;
        }
        return false;
    }


    /**
     * 更新音量
     */
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
