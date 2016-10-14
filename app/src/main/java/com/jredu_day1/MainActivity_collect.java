package com.jredu_day1;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.UserCollectAdapter;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.dao.UserCollect;
import com.dao.UserDemo;
import com.dao.Vedio;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mediedictionary.playerlibrary.PlayerActivity;
import com.util.StringPostRequest;
import com.util.SystemBarTintManager;
import com.util.UrlUtil;

import java.util.ArrayList;
import java.util.List;


public class MainActivity_collect extends Activity {

    private ListView list_collect;

    private TextView title_tv;
    private ImageView back_icon;
    private UserCollectAdapter ua;
    private List<UserCollect> list = new ArrayList<>();
    private RequestQueue requestQueue = MyApplaction.getMyApplaction().getRequestQueue();
    UserDemo userDemo = MyApplaction.getMyApplaction().getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_collect);
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
        list_collect = (ListView) findViewById(R.id.lv_collect);

        title_tv = (TextView)findViewById(R.id.title_tv);
        title_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        back_icon = (ImageView) findViewById(R.id.back_icon);
        ua = new UserCollectAdapter(getBaseContext(), list);
        list_collect.setAdapter(ua);
        load();
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /**
         *视频播放
         */
        list_collect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity_collect.this, PlayerActivity.class);

                UserCollect userCollect = list.get(position);
                Vedio vedio = new Vedio();
                vedio.setVedioName(userCollect.getRemark());
                vedio.setAuthor(userCollect.getUname());
                vedio.setProjId(userCollect.getProjid());
                vedio.setVedioid(userCollect.getVedioid());
                vedio.setVUri(userCollect.getVedioUrl());

                intent.putExtra("Vedio",vedio);
//                intent.putExtra("url", UrlUtil.BASE_URL + list.get(position).getVedioUrl());
//                intent.putExtra("vedioName", list.get(position).getRemark());
//                intent.putExtra("uName", list.get(position).getUname());
//                intent.putExtra("projid", list.get(position).getProjid());
//                intent.putExtra("vedioid", list.get(position).getVedioid());



                startActivity(intent);

            }
        });


    }

    public void load() {
        //加载网络数据
        StringPostRequest request = new StringPostRequest(UrlUtil.COLLECT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Gson gson = new Gson();
                        ArrayList<UserCollect> data = gson.fromJson(s,
                                new TypeToken<ArrayList<UserCollect>>() {
                                }.getType());
                        if (data != null && data.size() > 0) {
                            list.clear();
                            list.addAll(data);
                            ua.notifyDataSetChanged();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });

        request.putParams("uname", userDemo.getUname());
        request.putParams("flag", "3");
        requestQueue.add(request);



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
