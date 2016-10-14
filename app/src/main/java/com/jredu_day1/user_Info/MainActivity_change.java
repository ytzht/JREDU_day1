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
import com.jredu_day1.MainActivity;
import com.jredu_day1.MyApplaction;
import com.jredu_day1.R;
import com.util.StringPostRequest;
import com.util.SystemBarTintManager;
import com.util.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity_change extends Activity {
    private TextView textView,change_name;
    private Button change_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_change);
        textView = (TextView)findViewById(R.id.textView_change);
        change_name = (TextView)findViewById(R.id.change);
        change_bt = (Button)findViewById(R.id.change_bt);

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
        //设置沉浸的颜色

        Intent it = getIntent();
        final String change = it.getStringExtra("change_num");
        final UserDemo userDemo = MyApplaction.getMyApplaction().getUser();
        if(change.equals("nick")){
            textView.setText("昵称修改");
        }else if(change.equals("sex")){
            textView.setText("性别修改");
        }else if(change.equals("pwd")){
            textView.setText("密码修改");
        }
        change_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(change.equals("nick")){
                    String nickname =  change_name.getText()+"";
                    userDemo.setNickName(nickname);
                }else if(change.equals("sex")){
                   String sex = change_name.getText()+"";
                    userDemo.setSexId(sex);
                }else if(change.equals("pwd")){
                    String pwd = change_name.getText()+"";
                    userDemo.setUpwd(pwd);
                }
                  String  url = UrlUtil.UserLogin;
                StringPostRequest str_nick = new StringPostRequest(url,new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(s);
                            Gson gson = new Gson();
                            UserDemo user = gson.fromJson(s,UserDemo.class);
                            if(user.getUname().equals("")){
                                Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_LONG).show();
                            }else{
                                MyApplaction.getMyApplaction().setUser(user);
                                if(change.equals("pwd")){
                                    Intent it = new Intent(MainActivity_change.this,MainActivity.class);
                                    Toast.makeText(getApplicationContext(),"检测到您的密码已修改，请重新登陆",Toast.LENGTH_SHORT).show();
                                    startActivity(it);
                                }
                                finish();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
                str_nick.putParams("uname",userDemo.getUname());
                str_nick.putParams("upwd",userDemo.getUpwd());
                str_nick.putParams("unickname",userDemo.getNickName());
                str_nick.putParams("sexid",userDemo.getSexId());
                str_nick.putParams("photoUrl",userDemo.getPhotoUri());
                str_nick.putParams("flag","3");
                MyApplaction.getMyApplaction().getRequestQueue().add(str_nick);}
        });
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

}
