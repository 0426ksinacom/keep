package com.demoapplication.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/4.
 * Function:
 */
public class Keepmotion implements Serializable{

//    "motion_order":"1",//
//     "motion_duration":"",//动作间隔时间  单位秒
//     "motion_video":"",//动作的视频
//     "motion_video_sound":"" , 视频的name
//    "shouldcountdownend":"",	结束是否播放吹停1 要  0  不要
//    "number_sound_type":"", 数字变化的时候是读书还是滴滴， 1  读数 0 滴滴
//    "repeatcount":"",  重复几组
//    "pregroupcount":"",每组次数或秒数
//    "timeorsecond":""  每组的计量单位 是秒还是次  1 秒   0 次（也可以根据   motion_duration判断）



    public int motion_duration=1;//s
    public String motion_video="";
    public String motion_video_sound="";
    public String shouldcountdownend="";
    public String number_sound_type="";
    public int repeatcount=1;
    public int pregroupcount=0;
    public String timeorsecond="";

}
