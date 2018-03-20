package com.demoapplication.uitil;

import android.content.Context;
import android.media.MediaPlayer;

import com.demoapplication.bean.Keepmotion;

import java.util.List;

/**
 * Created by Administrator on 2016/10/5.
 * Function:   根据  bean   实现对 视频 及 音频 (背景音频 和 动作音频) 媒体文件的播放控制
 */
public class KeepMediaPresenter {

    //这里暂停是控制 KeepMediamp3Player mCustomCountDownTimer
    private Boolean ispause = false;

    PlayerListener playerListener;

    public void setplayerListener(PlayerListener l) {
        playerListener = l;
    }

    public interface PlayerListener {

        /**
         * 开始准备 当前组号  不是重复的组
         *
         * @param position
         */
        void preMotion(int position);

        /**
         * 正式开始 动作
         *
         * @param motionlong
         */
        void start(int motionlong,String motionurl);

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

    /**
     * 播放背景音乐
     */
    KeepMediamp3Player keepMediamp3Player;
    /**
     * 播放背景音乐
     */
    MediaPlayerMp3 mediaPlayerMp3;


    public KeepMediaPresenter(Context context, List<Keepmotion> datas) {
        keepMediamp3Player = new KeepMediamp3Player(context);
        mediaPlayerMp3 = new MediaPlayerMp3(context);

        keepMediamp3Player.setMotions(datas);
        keepMediamp3Player.setplayerListener(new KeepMediamp3Player.PlayerListener() {
            @Override
            public void preMotion(int position) {
                playerListener.preMotion(position);

            }

            @Override
            public void start(int pregroupcount,String motionurl) {
                playerListener.start(pregroupcount,motionurl);

            }

            @Override
            public void end() {
                playerListener.end();

            }

            @Override
            public void countDown(int num) {
                playerListener.countDown(num);
            }

            @Override
            public void count3211Down(String num) {
                playerListener.count3211Down(num);
            }
        });

    }


    List<Keepmotion> datalist;


    /**
     * 设置bean
     */
    public void setListbean(List<Keepmotion> datas) {
        datalist = datas;


    }

    public void start() {

//        mediaPlayerMp3.setVideoPath("keep/bgm05.mp3");
        keepMediamp3Player.startPlay();


    }

    public void pauseOrRestart() {

        if (ispause) {

            restart();
            ispause = false;
        } else {
            ispause = true;
            pause();
        }


    }


    public void pause() {

        if (keepMediamp3Player != null) {
            keepMediamp3Player.pause();
        }
        if (mediaPlayerMp3 != null) {
            if (mediaPlayerMp3.isPlaying()) {
                mediaPlayerMp3.pause();
            }
        }




    }

    public void restart() {

        if (mediaPlayerMp3 != null) {
            mediaPlayerMp3.start();
        }
        if (keepMediamp3Player != null) {
            keepMediamp3Player.start();
        }


    }

    public void nextMotion() {

        keepMediamp3Player.nextMotion();
    }

    public void preMotion() {

        keepMediamp3Player.preMotion();
    }


    public void release() {

        mediaPlayerMp3.release();
        keepMediamp3Player.release();


    }


}
