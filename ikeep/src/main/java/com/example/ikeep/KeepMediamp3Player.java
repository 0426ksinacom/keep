package com.demoapplication.uitil;

import android.content.Context;
import android.media.MediaPlayer;

import com.demoapplication.bean.Keepmotion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import countdownview.CustomCountDownTimer;


/**
 * Created by Administrator on 2016/10/5.
 * Function:   动作音频播放  自动播放下一个  直到  go   开始倒计时
 */
public class KeepMediamp3Player {


    public interface PlayerListener {
        /**
         * 开始准备 当前组号  不是重复的组
         *
         * @param position
         */
        void preMotion(int position);

        /**
         * 正式开始 动作  视频重新加载 和音频同步
         *
         * @param motionlong
         */
        void start(int motionlong, String motionurl);

        void end();

        /**
         * 倒计时数
         *
         * @param num
         */
        void countDown(int num);

        /**
         * 3 2 1  go  倒计时数
         *
         * @param num
         */
        void count3211Down(String num);


    }
//这样些是为了处理倒计时暂停和非倒计时暂停
    private     int STATE_PREHALF_PLAYING = 1;//钱半段
    private     int STATE_BACKHALF_PLAYING = 2;//后前半段播放
    private     int STATE_PREHALF_PAUSED = 3;//前半段暂停
    private     int STATE__BACKHALFPAUSED = 4;//后半段暂停
    private int mCurrentState = STATE_PREHALF_PLAYING;



    PlayerListener playerListener;
    /**
     * 每个motion  长度   就是  倒计时的
     */
    private int pregroupcount;

    public void setplayerListener(PlayerListener l) {
        playerListener = l;
    }


    /**
     * 记下当前动作的位置
     */
    int currentMotionPosition = 0;

    Keepmotion currentKeepmotion;

    /**
     * 记下当前播放的位置  对每个motions
     */
    int currentPlayPosition = 0;
    //所有的动作
    int totalmp3Motions = 0;
    /**
     * 多组动作是用到  ，第几次重复
     */
    int repeatCount = 0;
    /**
     * 所有要播放的音频 直到 Go
     */
    List<HashMap<String, String>> motions;


    List<Keepmotion> datalist;


    private CustomCountDownTimer mCustomCountDownTimer;

    /**
     * set  调用star  才会播放
     *
     * @param datas
     */
    public void setMotions(List<Keepmotion> datas) {
        datalist = datas;
        motions = new ArrayList<>();
//初始化默认 动作   直到go
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("motion", "keep/g_2_first_motion.mp3");
        HashMap<String, String> hashMap1 = new HashMap<String, String>();
        hashMap1.put("motion", "keep/video/_misc_2015_08_13_16_53c98aa031400000.mp3");
        HashMap<String, String> hashMap2 = new HashMap<String, String>();
        hashMap2.put("motion", "keep/one_group.mp3");
        HashMap<String, String> hashMap3 = new HashMap<String, String>();
        hashMap3.put("motion", "keep/number/N020.mp3");
        HashMap<String, String> hashMap4 = new HashMap<String, String>();
        hashMap4.put("motion", "keep/seconds.mp3");

        HashMap<String, String> hashMap5 = new HashMap<String, String>();
        hashMap5.put("motion", "keep/number/N003.mp3");
        HashMap<String, String> hashMap6 = new HashMap<String, String>();
        hashMap6.put("motion", "keep/number/N002.mp3");
        HashMap<String, String> hashMap7 = new HashMap<String, String>();
        hashMap7.put("motion", "keep/number/N001.mp3");

        HashMap<String, String> hashMap8 = new HashMap<String, String>();
        hashMap8.put("motion", "keep/g_9_go.mp3");

        motions.add(hashMap);
        motions.add(hashMap1);
        motions.add(hashMap2);
        motions.add(hashMap3);
        motions.add(hashMap4);
        motions.add(hashMap5);
        motions.add(hashMap6);
        motions.add(hashMap7);
        motions.add(hashMap8);

    }

    public void startPlay() {




        currentKeepmotion = datalist.get(currentMotionPosition);

        pregroupcount = currentKeepmotion.pregroupcount;

        playerListener.preMotion(currentMotionPosition);

//        HashMap<String ,String> hashMap  = new HashMap<String ,String>();
//        hashMap.put("motion","");
//
//        motions.add(hashMap);

        HashMap<String, String> map = motions.get(currentPlayPosition);
        String motion = map.get("motion");

        totalmp3Motions = motions.size();
        currentPlayPosition = 0;


        mediaPlayerMp3.setVideoPath(motion);
        if (currentPlayPosition < totalmp3Motions)
            currentPlayPosition++;

    }


    public void pause() {
        if (mCurrentState == STATE_PREHALF_PLAYING){
            mCurrentState = STATE_PREHALF_PAUSED;

        }else if(mCurrentState  == STATE_BACKHALF_PLAYING){
            mCurrentState = STATE__BACKHALFPAUSED;
        }


        if (mediaPlayerMp3 != null) {
            if (mediaPlayerMp3.isPlaying()) {
                mediaPlayerMp3.pause();
            }
        }
        if (mCustomCountDownTimer != null)
            mCustomCountDownTimer.pause();


    }

    public void start() {

        if (mCurrentState == STATE_PREHALF_PAUSED){
            mCurrentState = STATE_PREHALF_PLAYING;
            currentMotionnNext();

        }else if(mCurrentState  == STATE__BACKHALFPAUSED){
            mCurrentState = STATE_BACKHALF_PLAYING;

            if (mCustomCountDownTimer != null)
                mCustomCountDownTimer.restart();
        }




    }

    /**
     * 播放当前动作的下一个音频
     */

    private void currentMotionnNext() {

        if (currentPlayPosition < totalmp3Motions) {
            HashMap<String, String> map = motions.get(currentPlayPosition);
            String motion = map.get("motion");
            if ((totalmp3Motions - currentPlayPosition) == 4) {
                playerListener.count3211Down("3");

            } else if ((totalmp3Motions - currentPlayPosition) == 3) {
                playerListener.count3211Down("2");

            } else if ((totalmp3Motions - currentPlayPosition) == 2) {
                playerListener.count3211Down("1");


            } else if ((totalmp3Motions - currentPlayPosition) == 1) {
                playerListener.count3211Down("GO!");


            }
            currentPlayPosition++;


            mediaPlayerMp3.setVideoPath(motion);

            mCurrentState =  STATE_PREHALF_PLAYING;

        } else {//正式开始


            playerListener.start(pregroupcount, "keep/video/" + currentKeepmotion.motion_video);


            long countDownInterval = currentKeepmotion.motion_duration * 1000;
            long millisInFuture = currentKeepmotion.pregroupcount * 1000 * currentKeepmotion.motion_duration;
            if (mCurrentState == STATE_PREHALF_PLAYING||mCurrentState  == STATE_BACKHALF_PLAYING){//   采用的是播放完自动加载下一个   即使用pause   可能  因为正在播放  而不能阻止所以加了状态
                mCustomCountDownTimer = new CustomCountDownTimer(millisInFuture, countDownInterval) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        mCurrentState = STATE_BACKHALF_PLAYING;

                        LogMy.info("currentKeepmotion.motion_duration" +
                                " " + currentKeepmotion.motion_duration);
                        LogMy.info("millisUntilFinished " + millisUntilFinished);
                        int count = (int) millisUntilFinished / (1000 * currentKeepmotion.motion_duration);
                        LogMy.info("count v " + count);

                        count = pregroupcount - count;
                        if (count > pregroupcount)
                            count = pregroupcount;

                        playerListener.countDown(count);

                        if (currentKeepmotion.number_sound_type.equals("1")) {//报数字
                            String url = "";
                            if (count < 10) {
                                url = "keep/number/N00" + count + ".mp3";
                            } else if (count >= 10 && count < 100) {
                                url = "keep/number/N0" + count + ".mp3";
                            } else {
                                url = "keep/number/N" + count + ".mp3";
                            }

                            mediaPlayerdidi.setVideoPath(url);

                            LogMy.info(url);
                        } else {//报滴滴
                            mediaPlayerdidi.setVideoPath("keep/timer.mp3");

                        }

                    }

                    @Override
                    public void onFinish() {
                        playerListener.end();
                        nextMotion();

                    }
                };
            mCustomCountDownTimer.start();
        }

        }


    }

    /**
     * 播放下一个动作
     */
    public void nextMotion() {

        if (mCustomCountDownTimer != null)
            mCustomCountDownTimer.stop();

        currentMotionPosition++;
        if (currentMotionPosition < datalist.size()) {

            playerListener.preMotion(currentMotionPosition);

            currentKeepmotion = datalist.get(currentMotionPosition);

            totalmp3Motions = motions.size();
            currentPlayPosition = 0;


            String motion = "";
            if (currentMotionPosition == datalist.size() - 1) {//最后一个
                motion = "keep/g_14_last_motion.mp3";
            } else {
                motion = "keep/g_13_next_motion.mp3";
            }

            String motionname = currentKeepmotion.motion_video_sound;
            HashMap<String, String> hashMap1 = new HashMap<String, String>();
            hashMap1.put("motion", "keep/video/" + motionname);
            motions.remove(1);
            motions.add(1, hashMap1);

            int count = currentKeepmotion.pregroupcount;
            String url = "";
            if (count < 10) {
                url = "keep/number/N00" + count + ".mp3";
            } else if (count >= 10 && count < 100) {
                url = "keep/number/N0" + count + ".mp3";
            } else {
                url = "keep/number/N" + count + ".mp3";
            }


            HashMap<String, String> hashMap3 = new HashMap<String, String>();
            hashMap3.put("motion", url);
            motions.remove(3);
            motions.add(3, hashMap3);


            String timeorsecond = currentKeepmotion.timeorsecond;

            String timeorOrsecondUrl = "";

            HashMap<String, String> hashMap4 = new HashMap<String, String>();

            if (timeorsecond.equals("1"))
                timeorOrsecondUrl = "keep/g_6_time.mp3";
            else
                timeorOrsecondUrl = "keep/seconds.mp3";

            hashMap4.put("motion", timeorOrsecondUrl);
            motions.remove(4);
            motions.add(4, hashMap4);


            pregroupcount = currentKeepmotion.pregroupcount;

//            HashMap<String, String> map = motions.get(currentPlayPosition);
//            String motion = map.get("motion");

            totalmp3Motions = motions.size();
            currentPlayPosition = 0;


            mediaPlayerMp3.setVideoPath(motion);
            if (currentPlayPosition < totalmp3Motions)
                currentPlayPosition++;

        }
    }


    /**
     * 播放上一个动作
     */
    public void preMotion() {

        if (mCustomCountDownTimer != null)
            mCustomCountDownTimer.stop();

        currentMotionPosition--;

        if (currentMotionPosition < 0)
            currentMotionPosition = 0;

        if (currentMotionPosition < datalist.size()) {

            playerListener.preMotion(currentMotionPosition);

            currentKeepmotion = datalist.get(currentMotionPosition);

            totalmp3Motions = motions.size();
            currentPlayPosition = 0;


            String motion = "";
            if (currentMotionPosition == datalist.size() - 1) {//最后一个
                motion = "keep/g_14_last_motion.mp3";
            } else {
                motion = "keep/g_13_next_motion.mp3";
            }

            String motionname = currentKeepmotion.motion_video_sound;
            HashMap<String, String> hashMap1 = new HashMap<String, String>();
            hashMap1.put("motion", "keep/video/" + motionname);
            motions.remove(1);
            motions.add(1, hashMap1);

            int count = currentKeepmotion.pregroupcount;
            String url = "";
            if (count < 10) {
                url = "keep/number/N00" + count + ".mp3";
            } else if (count >= 10 && count < 100) {
                url = "keep/number/N0" + count + ".mp3";
            } else {
                url = "keep/number/N" + count + ".mp3";
            }


            HashMap<String, String> hashMap3 = new HashMap<String, String>();
            hashMap3.put("motion", url);
            motions.remove(3);
            motions.add(3, hashMap3);


            String timeorsecond = currentKeepmotion.timeorsecond;

            String timeorOrsecondUrl = "";

            HashMap<String, String> hashMap4 = new HashMap<String, String>();

            if (timeorsecond.equals("1"))
                timeorOrsecondUrl = "keep/g_6_time.mp3";
            else
                timeorOrsecondUrl = "keep/seconds.mp3";

            hashMap4.put("motion", timeorOrsecondUrl);
            motions.remove(4);
            motions.add(4, hashMap4);


            pregroupcount = currentKeepmotion.pregroupcount;

//            HashMap<String, String> map = motions.get(currentPlayPosition);
//            String motion = map.get("motion");

            totalmp3Motions = motions.size();
            currentPlayPosition = 0;


            mediaPlayerMp3.setVideoPath(motion);
            if (currentPlayPosition < totalmp3Motions)
                currentPlayPosition++;

        }
    }


    MediaPlayerMp3 mediaPlayerMp3;
    MediaPlayerMp3 mediaPlayerdidi;//播放数字或滴滴  和中间的倒计时   在坚持五秒等

    private Context context;

    public KeepMediamp3Player(Context c) {
        context = c;
        mediaPlayerdidi = new MediaPlayerMp3(context);

        mediaPlayerMp3 = new MediaPlayerMp3(context);
        mediaPlayerMp3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                currentMotionnNext();

            }
        });
        mediaPlayerMp3.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }


    public void release() {
        if (mCustomCountDownTimer != null)
            mCustomCountDownTimer.stop();
        mediaPlayerMp3.release();
        mediaPlayerdidi.release();


    }


}
