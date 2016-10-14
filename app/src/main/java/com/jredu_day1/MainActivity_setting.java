package com.jredu_day1;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dao.UnVersion;
import com.dao.UserDemo;
import com.dao.Vedio;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jredu_day1.user_Info.MainActivity_grzl;
import com.jredu_day1.user_Info.MainActivity_on;
import com.util.ImageLoaderUtil;
import com.util.StringPostRequest;
import com.util.SystemBarTintManager;
import com.util.UrlUtil;
import com.util.VersionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity_setting extends Activity {
    private UnVersion newVersion = null;

    private ImageView head_image;
    private TextView nickname, press, tv_cache;
    private Button exit;
    private RelativeLayout info, cache, version, aboutjredu, back;
    private TextView this_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity__setting);
/**
 * 沉浸式状态栏
 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.actionbar_bg);
        //设置沉浸的颜色        tintManager.setStatusBarTintResource(R.color.statusbar_bg);}
        head_image = (ImageView) findViewById(R.id.head_image);
        nickname = (TextView) findViewById(R.id.nickname);
        press = (TextView) findViewById(R.id.press);
        this_version = (TextView)findViewById(R.id.this_version);
        PackageManager manager = getPackageManager();
        PackageInfo ino;
        String versionName = null;
        int versionCode;
        try {
            ino = manager.getPackageInfo(this.getPackageName(),0);
            versionName = ino.versionName;
            versionCode = ino.versionCode;
            MyApplaction.getMyApplaction().setVerCode(versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        this_version.setText("当前版本："+versionName);


        info = (RelativeLayout) findViewById(R.id.info);
        cache = (RelativeLayout) findViewById(R.id.cache);
        version = (RelativeLayout) findViewById(R.id.version);
        aboutjredu = (RelativeLayout) findViewById(R.id.aboutjredu);
        back = (RelativeLayout) findViewById(R.id.back);
        exit = (Button) findViewById(R.id.exit);
        tv_cache = (TextView) findViewById(R.id.tv_cache);

//        android.os.Process.setThreadPriority(TRIM_MEMORY_BACKGROUND);


        final String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/"+getPackageName()+"/cache";
        Log.d("=====cache=====",path);

        final File file = new File(path);
        tv_cache.setText(convertStorage(getFolderSize(file)));

        //个人信息
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDemo userDemo = MyApplaction.getMyApplaction().getUser();
                if (!userDemo.getUname().equals("")) {
                    Intent intent = new Intent(MainActivity_setting.this, MainActivity_grzl.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity_setting.this, MainActivity_on.class);
                    startActivity(intent);
                }

            }
        });


        //清除缓存
        cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFolderFile(path);
                tv_cache.setText(convertStorage(getFolderSize(file)));
            }
        });

        //版本更新
        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VersionManager vManager = new VersionManager(MainActivity_setting.this);
                vManager.downloadApkInfo();
//                StringPostRequest spr = new StringPostRequest(UrlUtil.VERSION_URL, new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        Log.d("=====s=====",s);
//
//                        Gson gson = new Gson();
//                        List<UnVersion> li = gson.fromJson(s,new TypeToken<ArrayList<UnVersion>>(){}.getType());    //li存放临时下载的实体
//                        newVersion = li.get(0);
//                        Log.d("=====",newVersion.getVcode()+"");
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//
//                    }
//                });
//                MyApplaction.getMyApplaction().getRequestQueue().add(spr);

            }
        });
        //关于杰瑞
        aboutjredu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_setting.this,MapActivity.class);
                startActivity(intent);
            }
        });
        //退出登录
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDemo userDemo = MyApplaction.getMyApplaction().getUser();
                if(userDemo.getUname().equals("")){
                    Toast.makeText(getApplicationContext(),"当前未登录",Toast.LENGTH_SHORT).show();
                }else {
                    UserDemo u = new UserDemo();
                    u.setUname("");
                    MyApplaction.getMyApplaction().setUser(u);
                    startActivity(new Intent(MainActivity_setting.this, MainActivity_on.class));
                }
            }
        });

        //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    //清除缓存
    public void deleteFolderFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath());
                    }
                }
                if (!file.isDirectory()) {// 如果是文件，删除
                    file.delete();
                } else {// 目录
                    if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                        file.delete();
                    }
                }
                //  }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //获取缓存大小
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return size;
    }

    //转换文件大小
    public static String convertStorage(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size > kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        UserDemo user = MyApplaction.getMyApplaction().getUser();
        if (!user.getUname().equals("")) {
            press.setText("");
            nickname.setText(user.getNickName());
            ImageLoaderUtil.display(user.getPhotoUri(), head_image);
        }

    }

    /**
     * 沉浸式状态栏
     *
     */
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


}



