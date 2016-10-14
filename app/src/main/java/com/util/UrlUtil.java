package com.util;

/**
 * Created by ThinkPad on 2015/11/17.p
 */

public class UrlUtil {
    public static String BASE = "192.168.8.118";
    public static String BASE_URL = "http://"+BASE+":8080/VedioManager";
    public static String TOP_PICTURE = BASE_URL+"/servlet/VedioCtrl?top=1";
    public static String All_Video = BASE_URL+"/servlet/VedioCtrl?top=2";
    public static String All_Class = BASE_URL+"/servlet/ClassProjCtrl?top=1";
    public static String UserLogin = BASE_URL+"/servlet/UserLogin";
    public static String COLLECT_URL = BASE_URL+"/servlet/UserCollectCtrl";//收藏
    public static String VERSION_URL = BASE_URL+"/servlet/ApkVersionCtrl?top=1";//版本号
}