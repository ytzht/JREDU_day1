package com.jredu_day1.user_Info;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dao.UserDemo;
import com.google.gson.Gson;
import com.jredu_day1.MyApplaction;
import com.jredu_day1.R;
import com.util.PreLoginUtil;
import com.util.StringPostRequest;
import com.util.SystemBarTintManager;
import com.util.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;



import static com.android.volley.Response.ErrorListener;
import static com.android.volley.Response.Listener;


public class MainActivity_zc extends Activity {
    private TextView userName,userPwd,userNick,usersex;
    private Button by_zc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_activity_zc);

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
        userName = (TextView)findViewById(R.id.userName);
        userPwd  = (TextView)findViewById(R.id.userPwd);
        userNick = (TextView)findViewById(R.id.userNick);
        usersex  = (TextView)findViewById(R.id.usersex);
        by_zc    = (Button)findViewById(R.id.bt_zc);
        by_zc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDemo userDemo = new UserDemo();
                String nickname = userNick.getText().toString();
                String sexid = usersex.getText().toString();
                String date = new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis()));//获取当前日期
                String name = userName.getText().toString();
                String pwd = userPwd.getText().toString();
                userDemo.setNickName(nickname);
                userDemo.setRgdtDate(date);
                userDemo.setSexId(sexid);
                userDemo.setUname(name);
                userDemo.setUpwd(pwd);
                inster(userDemo);
            }
        });
    }
    private void inster(UserDemo userDemo){

        String  url = UrlUtil.UserLogin;
        StringPostRequest str = new StringPostRequest(url,new Listener<String>() {
            @Override
            public void onResponse(String s) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    Gson gson = new Gson();
                    UserDemo user = gson.fromJson(s,UserDemo.class);
                    if(user.getUname().equals("")){
                        Toast.makeText(getApplicationContext(),"账号已存在，请重新输入",Toast.LENGTH_LONG).show();
                    }else{
                        MyApplaction.getMyApplaction().setUser(user);
                        //将账号密码在登陆时存到本地
                        PreLoginUtil.putString(getBaseContext(), "name", user.getUname());
                        PreLoginUtil.putString(getBaseContext(), "pwd", user.getUpwd());
                        Intent it = new Intent(MainActivity_zc.this,MainActivity_on.class);
                        startActivity(it);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        str.putParams("uname", userDemo.getUname());
        str.putParams("upwd", userDemo.getUpwd());
        str.putParams("unickname",userDemo.getNickName());
        str.putParams("sexid",userDemo.getSexId());
        str.putParams("rgdtDate",userDemo.getRgdtDate());
        str.putParams("flag","2");

                // "uname="+userDemo.getUname()+"&upwd="+userDemo.getUpwd()+"&unickname="+userDemo.getNickName()+
//                "&sexid="+userDemo.getSexId()+"&rgdtDate="+userDemo.getRgdtDate()+"&flag=2";
        MyApplaction.getMyApplaction().getRequestQueue().add(str);
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
