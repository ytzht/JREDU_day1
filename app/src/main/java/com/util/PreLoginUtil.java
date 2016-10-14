package com.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2015/10/14.
 * 将登陆的信息存到本地
 */
public class PreLoginUtil {
    public static boolean putString(Context context,String key,String values){
        SharedPreferences setting =
                context.getSharedPreferences("login",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString(key,values);
        return editor.commit();
    }
    public static String gettString(Context context,String key) {
        SharedPreferences setting =
                context.getSharedPreferences("login", context.MODE_PRIVATE);
        return setting.getString(key, "");
    }
}
