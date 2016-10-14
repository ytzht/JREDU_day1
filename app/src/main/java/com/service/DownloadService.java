package com.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dao.Vedio;
import com.jredu_day1.R;
import com.util.UrlUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends Service {
    public DownloadService() {
    }

    private Vedio vedio;
    private NotificationManager manager;
    private int count = 0;
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        vedio = (Vedio) intent.getSerializableExtra("vedio");
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        count+=1;

        if(vedio == null){
            Log.d("====空====","");
        }else {
            new DownloadAsy().execute(UrlUtil.BASE_URL + vedio.getVUri());
        }
        return super.onStartCommand(intent, flags, startId);
    }
    public class DownloadAsy extends AsyncTask<String,Integer,String>{

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext())
                    .setContentTitle("正在下载:"+vedio.getVedioName())
                    .setContentText("下载中")
                    .setSmallIcon(R.drawable.logo)
                    .setProgress(100, values[0], false);
            Notification nf = builder.build();
            manager.notify(count,nf);
            if(values[0]==100){
                builder.setContentText("下载完成").setContentTitle(vedio.getVedioName() + "已下载完成");
                nf = builder.build();
                manager.notify(count, nf);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            android.os.Process.setThreadPriority(TRIM_MEMORY_BACKGROUND);
            HttpURLConnection httpURLConnection;
            InputStream is;
            BufferedOutputStream bos;
            try {
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(5000);
                double x = httpURLConnection.getContentLength();
                if(httpURLConnection.getResponseCode()==200){
                    is = httpURLConnection.getInputStream();
                    String stus = Environment.getExternalStorageState();
                    if(stus.equals(Environment.MEDIA_MOUNTED)){
                        File root = Environment.getExternalStorageDirectory();


                        String path = vedio.getVUri();
                        String path1 = "JREDU"+ "/"+path.substring(path.indexOf("/")+1, path.lastIndexOf("/"));
                        Log.d("====目录====",path1);
                        File myDir = new File(root,path1);
                        if(!myDir.exists()){
                            myDir.mkdirs();
                        }
                        String path2 = "/"+path.substring(path.lastIndexOf("/")+1,path.length());
                        bos = new BufferedOutputStream(new FileOutputStream(myDir+path2));
                        int next;
                        int a = 0;
                        byte[] bt = new byte[1024*1024*10];
                        while((next = is.read(bt))>0){
                            a += next;
                            int b = (int)(a/x*100);
                            publishProgress(b);
                            bos.write(bt, 0, next);
                        }
                        bos.flush();
                        bos.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
