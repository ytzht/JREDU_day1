package com.jredu_day1;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dao.DaoMaster;
import com.dao.DaoSession;
import com.dao.UserDemo;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by ThinkPad on 2015/11/17.
 */
public class MyApplaction extends Application {
    private static MyApplaction myApplaction;
    private RequestQueue requestQueue;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    private UserDemo user = new UserDemo();
    public UserDemo getUser() {
        return user;
    }
    public void setUser(UserDemo user) {
        this.user = user;
    }



    private String verName;
    private int verCode;//版本编号
    public int getVerCode() {
        return verCode;
    }
    public void setVerCode(int verCode) {
        this.verCode = verCode;
    }
    public String getVerName() {
        return verName;
    }
    public void setVerName(String verName) {
        this.verName = verName;
    }
    @Override
    public void onCreate() {
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        super.onCreate();
        myApplaction = this;//单例模式
        requestQueue = Volley.newRequestQueue(this);
        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration.Builder(this)
                        .denyCacheImageMultipleSizesInMemory()
                        .threadPriority(Thread.NORM_PRIORITY - 2)
                        .memoryCacheSize((int) Runtime.getRuntime().maxMemory())
                        .diskCacheSize(50 * 1024 * 1024)
                        .diskCacheFileNameGenerator(new Md5FileNameGenerator())
//                        .discCache(new UnlimitedDiskCache(FileUitlity.getInstance ))   //往SD卡那个位置写缓存
                        .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                        .imageDownloader(new BaseImageDownloader(this,60*1000,60*1000))
                        .build();
        ImageLoader.getInstance().init(config);

    }
    public static MyApplaction getMyApplaction() {
        return myApplaction;
    }
    public RequestQueue getRequestQueue(){
        return this.requestQueue;
    }
    public static DaoMaster getDaoMaster(Context context){
        if(daoMaster == null){
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context,"JREDU.db",null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }
    public static DaoSession getDaoSession(Context context){

        if(daoSession == null){
            if(daoMaster == null){
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}
