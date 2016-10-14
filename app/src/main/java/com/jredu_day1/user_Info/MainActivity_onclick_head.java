package com.jredu_day1.user_Info;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dao.UserDemo;
import com.jredu_day1.MainActivity;
import com.jredu_day1.MyApplaction;
import com.jredu_day1.R;
import com.util.ImageLoaderUtil;
import com.util.PreLoginUtil;
import com.util.SystemBarTintManager;


public class MainActivity_onclick_head extends Activity {
    private ImageView face,left_icon;
    private TextView name,right_btn;
    private UserDemo userDemo;
    private String remember_01,remember_02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sliding_actionbar_layout);
        face = (ImageView)findViewById(R.id.face);
        left_icon= (ImageView)findViewById(R.id.left_icon);
        right_btn = (TextView)findViewById(R.id.right_btn);
        name = (TextView)findViewById(R.id.name);


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


        left_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity_onclick_head.this,MainActivity.class);
                startActivity(it);
                finish();
            }
        });
        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity_onclick_head.this,MainActivity_grzl.class);
                startActivity(it);
                finish();
            }
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

    @Override
    protected void onStart() {
        super.onStart();
        userDemo = MyApplaction.getMyApplaction().getUser();
        name.setText(userDemo.getNickName());
        ImageLoaderUtil.display(userDemo.getPhotoUri(), face);
    }


}
