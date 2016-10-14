package com.util;


import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import com.dao.UnVersion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jredu_day1.MyApplaction;
import com.jredu_day1.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by ThinkPad on 2015/10/16.a
 */
public class VersionManager {
    private UnVersion newVersion = null;
    private static final int DOWNLOAD = 1;// 下载中
    private static final int DOWNLOAD_FINISH = 2;// 现在结束
    private static final int DOWNLOAD_VERSION_INFO_FINISH = 3;   // 下载版本信息结束
    private File mSavePath;// 下载保存路径
    private int progress;// 记录进度条数量
    private boolean cancelUpdate = false;   // 是否取消更新
    private Context mContext;
    private ProgressBar mProgress;          // 更新进度条

    //notification
    private NotificationManager nm;
    private NotificationCompat.Builder ncBuilder;

    private boolean vcheck=true;
    public void setVcheck(boolean vcheck) {
        this.vcheck = vcheck;
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD:
                    // 设置进度条
                    //mProgress.setProgress(progress);
                    // 下载文件
                    //downloadApk();
                    if(progress==100){
                        ncBuilder.setContentText("下载完成，点击安装！");
                        ncBuilder.setProgress(100, 100, false);
                        //ncBuilder.setAutoCancel(true);

                    }else{
                        ncBuilder.setContentText("下载进度："+progress+"%");
                        ncBuilder.setProgress(100, progress, false);

                    }
                    ncBuilder.setAutoCancel(true);
                    Notification no = ncBuilder.build();
                    //no.flags = Notification.;
                    nm.notify(1,no);
                    no=null;
                    break;
                case DOWNLOAD_FINISH:
                    installApk();
                    break;
                case DOWNLOAD_VERSION_INFO_FINISH:
                    // 比对版本号并开始提示用户是否要下载
                    checkUpdate();
                    break;
                default:
                    break;
            }
        }

    };

    public VersionManager(Context ctx) {
        this.mContext = ctx;
        nm = (NotificationManager)this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void checkUpdate() {
        if (isUpdate()) {
            showNoticeDialog();

        }else{
            if(vcheck){
                Toast.makeText(mContext, "当前为最新版本！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isUpdate(){


        if (newVersion != null && (newVersion.getVcode() > MyApplaction.getMyApplaction().getVerCode())) {
            return true;
        }else{
            return false;
        }
    }




    public void showNoticeDialog() {
        // 构造对话框
        Builder builder = new Builder(mContext);
        builder.setTitle("软件更新");
        builder.setMessage("检测到新版本，立即更新吗？");
        builder.setPositiveButton("更新", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 显示下载对话框
                //showDownLoadDialog();
                // 下载文件
                downloadApk();
            }
        });
        builder.setNegativeButton("稍后更新", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }
//
//    public void showDownLoadDialog() {
//        Builder builder = new Builder(mContext);
//        builder.setTitle("正在更新");
//        // 给下载对话框增加进度条
//        final LayoutInflater inflater = LayoutInflater.from(mContext);
//        View v = inflater.inflate(R.layout.progressbar, null);
//        mProgress = (ProgressBar) v.findViewById(R.id.bar);
//        builder.setView(v);
//
//        builder.setNegativeButton("取消", new OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                // 设置取消状态
//                cancelUpdate = true;
//            }
//        });
//        Dialog mDownLoadDialog = builder.create();
//        mDownLoadDialog.show();
//        // 下载文件
//        downloadApk();
//    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {

        mSavePath = FileUitlity.getInstance(mContext).makeDir(APK_DIR);
        File outApk = new File(mSavePath, newVersion.getUrl().substring(newVersion.getUrl().lastIndexOf("/")));
        if(outApk.exists()){
            try {
                outApk.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //取得系统的下载服务
        DownloadManager downloadManager = (DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        String downUrl =UrlUtil.BASE_URL+"/"+newVersion.getUrl();
        Log.d("=====downUrl=====",downUrl);
        //创建下载请求对象
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downUrl));
        request.setDestinationInExternalPublicDir("/yt_xyt/apk/", newVersion.getUrl().substring(newVersion.getUrl().lastIndexOf("/")));
        request.setNotificationVisibility(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
    }
    /**
     * 下载文件线程
     */
    public final static String APK_DIR="apk";



    public void downloadApkInfo(){
        StringPostRequest request = new StringPostRequest(
                UrlUtil.VERSION_URL, new Listener<String>() {
            @Override
            public void onResponse(String result) {

                if(!TextUtils.isEmpty(result) && !"[]".equals(result)){
                    Gson gson = new Gson();
                    List<UnVersion> li = gson.fromJson(result,new TypeToken<ArrayList<UnVersion>>(){}.getType());    //li存放临时下载的实体
                    newVersion = li.get(0);
                }
                mHandler.sendEmptyMessage(DOWNLOAD_VERSION_INFO_FINISH);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                //Toast.makeText(mContext, "网络不给力，连接不到服务器！", Toast.LENGTH_SHORT).show();
            }
        });
        MyApplaction.getMyApplaction().getRequestQueue().add(request);
    }





    /**
     * 安装APK文件
     */
    public void installApk() {
        mSavePath = FileUitlity.getInstance(mContext).makeDir(APK_DIR);
        File apkFile = new File(mSavePath, newVersion.getUrl().substring(newVersion.getUrl().lastIndexOf("/")));
        if (!apkFile.exists()) {
            return;
        }
        Intent i = new Intent();
        //i.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }
}
