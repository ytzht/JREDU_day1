package com.jredu_day1.user_Info;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dao.QQInfo;
import com.dao.UserDemo;
import com.google.gson.Gson;
import com.jredu_day1.MyApplaction;
import com.jredu_day1.R;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.util.PreLoginUtil;
import com.util.StringPostRequest;
import com.util.SystemBarTintManager;
import com.util.UrlUtil;
import com.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity_on extends Activity {
    private Button dl_bt,zc_bt;
    private TextView zh,mm;
    private CheckBox dl_jz,dl_zd;
    private List<UserDemo> list;
    private String remember_01 = "no";
    private String remember_02 = "no";
    private Button mNewLoginButton;
    public static QQAuth mQQAuth;
    public static String mAppid;
    private final String APP_ID = "222222";// 测试时使用，真正发布的时候要换成自己的APP_ID
    private Tencent mTencent;
    private UserInfo mInfo;
    private TextView mUserInfo;
    private ImageView mUserLogo;


    //由于 传递对象不成功 使用静态变量
    public static String qqName;
    public static Bitmap qqLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();

        dl_bt = (Button)findViewById(R.id.loginBtn);
        zh = (TextView)findViewById(R.id.userName);
        mm = (TextView)findViewById(R.id.userPwd);
        dl_jz = (CheckBox)findViewById(R.id.cb_remPwd);
        dl_zd = (CheckBox)findViewById(R.id.cb_antoLogin);
        zc_bt = (Button)findViewById(R.id.registBtn);
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


        //注册新用户
        zc_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity_on.this,MainActivity_zc.class);
                startActivity(it);
                finish();
            }
        });
        dl_jz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PreLoginUtil.gettString(getBaseContext(), "remember_01").equals("yes")){
                    remember_01 = "no";
                    PreLoginUtil.putString(getBaseContext(),"remember_01",remember_01);
                    dl_jz.setChecked(false);
                }else{
                    remember_01 = "yes";
                    PreLoginUtil.putString(getBaseContext(),"remember_01",remember_01);
                    dl_jz.setChecked(true);
                }
            }
        });
        dl_zd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PreLoginUtil.gettString(getBaseContext(), "remember_01").equals("yes")){
                    remember_02 = "no";
                    PreLoginUtil.putString(getBaseContext(),"remember_02",remember_02);
                    dl_zd.setChecked(false);
                }else{
                    remember_02 = "yes";
                    PreLoginUtil.putString(getBaseContext(),"remember_02",remember_02);
                    dl_zd.setChecked(true);
                }
            }
        });
        //自动登录
        if (PreLoginUtil.gettString(getBaseContext(), "remember_02").equals("yes")) {
            dl_zd.setChecked(true);//使选中的对勾一直存在
            String name = PreLoginUtil.gettString(getBaseContext(), "name");
            String pwd = PreLoginUtil.gettString(getBaseContext(), "pwd");
            login(name, pwd);
        } else {
            dl_zd.setChecked(false);
            //登录实现
            dl_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uno = zh.getText() + "";
                    String pwd = mm.getText() + "";
                    login(uno, pwd);
                }

            });
        }

    }
    //设置状态栏透明
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

    public void login(String uno, String pwd) {
        String url_Login = UrlUtil.UserLogin;
        StringPostRequest strR = new StringPostRequest(url_Login, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    if (!jsonObject.has("info")) {
                        Gson gson = new Gson();
                        UserDemo user = gson.fromJson(s,UserDemo.class);
                        MyApplaction.getMyApplaction().setUser(user);
                        //将账号密码在登陆时存到本地
                        PreLoginUtil.putString(getBaseContext(), "name", user.getUname());
                        PreLoginUtil.putString(getBaseContext(), "pwd", user.getUpwd());
                        //记住密码
                        if (dl_jz.isChecked() && remember_01.equals("no")) {
                            remember_01 = "yes";
                            PreLoginUtil.putString(getBaseContext(), "remember_01", remember_01);
                        } else {
                            PreLoginUtil.putString(getBaseContext(), "remember_01", remember_01);
                        }
                        //自动登录
                        if (dl_zd.isChecked() && remember_02.equals("no")) {
                            remember_02 = "yes";
                            PreLoginUtil.putString(getBaseContext(), "remember_02", remember_02);
                        } else {
                            PreLoginUtil.putString(getBaseContext(), "remember_02", remember_02);
                        }

                        if (user != null) {
//                            Intent intent = new Intent();
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("user", user);
//                            intent.putExtra("data", bundle);
                            MyApplaction.getMyApplaction().setUser(user);
//                            startActivity(intent);
                            finish();

                    }
                    } else {
                        Toast.makeText(getBaseContext(), "账号密码错误", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        strR.putParams("uname",uno);
        strR.putParams("upwd",pwd);
        strR.putParams("flag","1");

        MyApplaction.getMyApplaction().getRequestQueue().add(strR);
        //登陆按钮点击事件
        dl_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sturl = UrlUtil.UserLogin;

                StringPostRequest stringRequest = new StringPostRequest(sturl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        UserDemo user = new UserDemo();
                        Gson gson = new Gson();
                        user = gson.fromJson(s, UserDemo.class);
                        if (user.getUname().equals("")) {
                            Toast.makeText(getBaseContext(), "密码错误或账号不存在", Toast.LENGTH_LONG).show();
                        } else {
                            MyApplaction.getMyApplaction().setUser(user);
                            Toast.makeText(getBaseContext(), "登录成功", Toast.LENGTH_LONG).show();

                            finish();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplication(), "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
                stringRequest.putParams("uname", zh.getText().toString());
                stringRequest.putParams("upwd", mm.getText().toString());
                stringRequest.putParams("flag", "1");

                MyApplaction.getMyApplaction().getRequestQueue().add(stringRequest);

            }
        });

    }

    @Override
    protected void onStart() {

        final Context context = MainActivity_on.this;
        final Context ctxContext = context.getApplicationContext();
        mAppid = APP_ID;
        mQQAuth = QQAuth.createInstance(mAppid, ctxContext);
        mTencent = Tencent.createInstance(mAppid, MainActivity_on.this);

        super.onStart();
        if (PreLoginUtil.gettString(getBaseContext(), "remember_01").equals("yes")) {
            dl_jz.setChecked(true);//使选中的对勾一直存在
            String name = PreLoginUtil.gettString(getBaseContext(), "name");
            zh.setText(name);
            String pwd = PreLoginUtil.gettString(getBaseContext(), "pwd");
            mm.setText(pwd);
        } else if (dl_jz.isChecked()) {
            remember_01 = "no";
            dl_jz.setChecked(false);
        }





    }
    private void initViews() {
        mNewLoginButton = (Button) findViewById(R.id.new_login_btn);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_container);
        View.OnClickListener listener = new NewClickListener();
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View view = linearLayout.getChildAt(i);
            if (view instanceof Button) {
                view.setOnClickListener(listener);
            }
        }
        mUserInfo = (TextView) findViewById(R.id.user_name);
        mUserLogo = (ImageView) findViewById(R.id.user_logo);
        updateLoginButton();
    }
    class NewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Class<?> cls = null;
            switch (v.getId()) {
                case R.id.new_login_btn:
                    onClickLogin();
                    return;
            }
            if (cls != null) {
                Intent intent = new Intent(context, cls);
                context.startActivity(intent);
            }
        }
    }
    private void onClickLogin() {
        if (!mQQAuth.isSessionValid()) {
            IUiListener listener = new BaseUiListener() {
                @Override
                protected void doComplete(JSONObject values) {
                    updateUserInfo();
                    updateLoginButton();
                }
            };
            mQQAuth.login(this, "all", listener);
            // mTencent.loginWithOEM(this, "all",
            // listener,"10000144","10000144","xxxx");
            mTencent.login(this, "all", listener);
        } else {
            mQQAuth.logout(this);
            updateUserInfo();
            updateLoginButton();
        }
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            Util.showResultDialog(MainActivity_on.this, response.toString(),
                    "登录成功");
            Log.d("==========",response.toString());
            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            Util.toastMessage(MainActivity_on.this, "onError: " + e.errorDetail);
            Util.dismissDialog();
        }

        @Override
        public void onCancel() {
            Util.toastMessage(MainActivity_on.this, "onCancel: ");
            Util.dismissDialog();
        }
    }
    private void updateUserInfo() {
        if (mQQAuth != null && mQQAuth.isSessionValid()) {
            IUiListener listener = new IUiListener() {

                @Override
                public void onError(UiError e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onComplete(final Object response) {
                    Message msg = new Message();
                    msg.obj = response;
                    msg.what = 0;
                    mHandler.sendMessage(msg);

                    new Thread() {

                        @Override
                        public void run() {
                            JSONObject json = (JSONObject) response;//
                            Log.d("=====JSON=====",response.toString());
                            if (json.has("figureurl")) {
                                Bitmap bitmap = null;
                                try {
                                    bitmap = Util.getbitmap(json
                                            .getString("figureurl_qq_2"));
                                    UserDemo userDemo = new UserDemo();
                                    login("1","1");
                                    userDemo.setUname("1");
                                    userDemo.setUpwd("1");
                                    userDemo.setNickName(json.getString("nickname"));
                                    userDemo.setPhotoUri(json.getString("figureurl_qq_2").replace("\\", ""));
                                    Log.d("=====fig=====", json.getString("figureurl_qq_2").replace("\\", ""));
                                    userDemo.setSexId(json.getString("gender"));
                                    userDemo.setRgdtDate(new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis())));//获取当前日期
                                    MyApplaction.getMyApplaction().setUser(userDemo);
                                } catch (JSONException e) {
                                }
                                Message msg = new Message();
                                msg.obj = bitmap;
                                msg.what = 1;
                                mHandler.sendMessage(msg);
                            }
                        }

                    }.start();
                }

                @Override
                public void onCancel() {
                }
            };
            mInfo = new UserInfo(this, mQQAuth.getQQToken());
            mInfo.getUserInfo(listener);
        } else {
            mUserInfo.setText("");
            mUserInfo.setVisibility(android.view.View.GONE);
//            mUserLogo.setVisibility(android.view.View.GONE);
        }
    }
    private void updateLoginButton() {
        if (mQQAuth != null && mQQAuth.isSessionValid()) {
            mNewLoginButton.setTextColor(Color.RED);
            mNewLoginButton.setText(R.string.qq_logout);
        } else {
            mNewLoginButton.setTextColor(Color.BLUE);
            mNewLoginButton.setText(R.string.qq_login);
        }
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            QQInfo ui=new QQInfo();
            Bitmap bitmap = null ;
            //消息0是获取qq名称
            if (msg.what == 0) {
                JSONObject response = (JSONObject) msg.obj;
                Log.d("=====response=====",response.toString());
                if (response.has("nickname")) {
                    try {
                        mUserInfo.setVisibility(android.view.View.VISIBLE);
                        mUserInfo.setText(response.getString("nickname"));
//						ui.setQqname(response.getString("nickname"));
                        qqName=response.getString("nickname");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } else if (msg.what == 1) {//发送1消息是 获取qq头像
                bitmap = (Bitmap) msg.obj;
                mUserLogo.setImageBitmap(bitmap);
                mUserLogo.setVisibility(android.view.View.VISIBLE);
                qqLogo=bitmap;
//				ui.setQqLogo(bitmap);
//                Intent intent=new Intent(MainActivity_on.this,SecondActivity.class);
//				intent.putExtra("ui",ui);
//                startActivity(intent);
            }
        }

    };

}
