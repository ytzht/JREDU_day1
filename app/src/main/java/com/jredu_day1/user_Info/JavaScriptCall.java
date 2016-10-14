package com.jredu_day1.user_Info;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dao.UserDemo;
import com.google.gson.Gson;
import com.jredu_day1.MainActivity;
import com.jredu_day1.MyApplaction;
import com.util.PreLoginUtil;
import com.util.StringPostRequest;
import com.util.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ThinkPad on 2015/12/7.
 */
public class JavaScriptCall {

    public Context context;

    public JavaScriptCall(Context context) {
        this.context = context;
    }

    @android.webkit.JavascriptInterface
    public void showToastMsg(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }


    @android.webkit.JavascriptInterface
    public void login(String name, String pwd) {
//        Toast.makeText(context,"用户名="+name+"\n"+"密码="+pwd,Toast.LENGTH_SHORT).show();
        String url_Login = UrlUtil.UserLogin;
        StringPostRequest strR = new StringPostRequest(url_Login, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    if (!jsonObject.has("info")) {
                        Gson gson = new Gson();
                        UserDemo user = gson.fromJson(s, UserDemo.class);
                        MyApplaction.getMyApplaction().setUser(user);
                        Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent it = new Intent(context, MainActivity.class);
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(it);
                    } else {
                        Toast.makeText(context, "账号密码错误", Toast.LENGTH_SHORT).show();
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
        strR.putParams("uname", name);
        strR.putParams("upwd", pwd);
        strR.putParams("flag", "1");

        MyApplaction.getMyApplaction().getRequestQueue().add(strR);

    }


    @android.webkit.JavascriptInterface
    public void reg(String name, String pwd, String password_confirm, String email) {
//        Toast.makeText(context,"用户名:"+name+"\n密码:"+pwd+"\n确定："+password_confirm+"\n邮箱："+email,Toast.LENGTH_SHORT).show();

        if (name.equals("")) {
            Toast.makeText(context, "请输入用户名", Toast.LENGTH_SHORT).show();
        } else {
            if (pwd.equals("")) {
                Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
            } else {
                if (!pwd.equals(password_confirm)) {
                    Toast.makeText(context, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                } else {
                    UserDemo userDemo = new UserDemo();
                    String date = new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis()));//获取当前日期
                    userDemo.setNickName(name);
                    userDemo.setRgdtDate(date);
                    userDemo.setSexId("男");
                    userDemo.setUname(name);
                    userDemo.setUpwd(pwd);
                    userDemo.setLevel("A");
                    userDemo.setPhotoUri("");
                    inster(userDemo);
                }
            }
        }
    }

    private void inster(UserDemo userDemo) {

        String url = UrlUtil.UserLogin;
        StringPostRequest str = new StringPostRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    Gson gson = new Gson();
                    UserDemo user = gson.fromJson(s, UserDemo.class);
                    if (user.getUname().equals("")) {
                        Toast.makeText(context, "账号已存在，请重新输入", Toast.LENGTH_LONG).show();
                    } else {
                        MyApplaction.getMyApplaction().setUser(user);
                        Toast.makeText(context, "成功", Toast.LENGTH_SHORT).show();
                        //将账号密码在登陆时存到本地
                        PreLoginUtil.putString(context, "name", user.getUname());
                        PreLoginUtil.putString(context, "pwd", user.getUpwd());
                        Intent it = new Intent(context, MainActivity.class);
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(it);
//                        context.startActivity(new Intent(context, MainActivity.class));

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
        str.putParams("uname", userDemo.getUname());
        str.putParams("upwd", userDemo.getUpwd());
        str.putParams("unickname", userDemo.getNickName());
        str.putParams("sexid", userDemo.getSexId());
        str.putParams("rgdtDate", userDemo.getRgdtDate());
        str.putParams("flag", "2");

        // "uname="+userDemo.getUname()+"&upwd="+userDemo.getUpwd()+"&unickname="+userDemo.getNickName()+
//                "&sexid="+userDemo.getSexId()+"&rgdtDate="+userDemo.getRgdtDate()+"&flag=2";
        MyApplaction.getMyApplaction().getRequestQueue().add(str);

    }

    @android.webkit.JavascriptInterface
    public void autolog(String clas) {
        Toast.makeText(context, clas, Toast.LENGTH_SHORT).show();
    }
}
