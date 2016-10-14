package com.jredu_day1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.ClassProjAdapter;
import com.adapter.MainViewPagerAdapter;
import com.adapter.VedioAdapter;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dao.ClassProj;
import com.dao.UserDemo;
import com.dao.Vedio;
import com.dao.VedioDao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jredu_day1.user_Info.HTMLActivity;
import com.jredu_day1.user_Info.MainActivity_grzl;
import com.mediedictionary.playerlibrary.PlayerActivity;
import com.util.AppManager;
import com.util.ImageLoaderUtil;
import com.util.SystemBarTintManager;
import com.util.UrlUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends Activity {
    private PullToRefreshListView pullTRLV;
    private SlidingMenu slidingMenu;
    private View v1;
    private View v2;
    private View v3;
    private ViewPager vp;
    private List<Vedio> allList = null;
    private List<ImageView> topImgList = new ArrayList<>();
    private List<ClassProj> classProjsList = null;
    private List<Vedio> vedioTopList = null;
    private List<Vedio> child_vedio = null;
    private List<Vedio> partList = null;
    private List<Vedio> localList = new ArrayList<>();
    private RelativeLayout allcourse_layout;
    private RelativeLayout article_layout;
    private RelativeLayout mycourse_layout;
    private RelativeLayout download_layout;
    private RelativeLayout mymessage_layout;
    private RelativeLayout mynote_layout;
    private RelativeLayout setting_layout;
    private VedioDao vedioDao;
    private LinearLayout linearLayout;
    private MainViewPagerAdapter viewPagerAdapter;
    private TextView top_title;
    private int currentItem = 0;
    private VedioAdapter vA;
    private ClassProjAdapter classProjAdapter;
    private TextView title_tv;
    private TextView right_title;
    private int flag = 0;//表示用户还没点击侧边栏，加载视频的list为allList
    private int count = 2;
    private WifiManager wifiManager;
    private SQLiteDatabase db;
    private ImageView login_pic;
    private TextView login_rightnow;

    private LinearLayout info_layout;
    private Button secondright_btn;
    private ImageView allcourse_icon;
    private ImageView mycourse_icon;
    private ImageView download_icon;
    private ImageView mymessage_icon;
    private ImageView mynote_icon;
    private ImageView article_icon;
    private ImageView setting_icon;

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    private View menuView_L;
    private int item = 0;

    public void init() {
        secondright_btn = (Button) findViewById(R.id.secondright_btn);
        pullTRLV = (PullToRefreshListView) findViewById(R.id.main_pulltorefresh);
        title_tv = (TextView) findViewById(R.id.title_tv);
        menuView_L = LayoutInflater.from(this).inflate(R.layout.sliding_behind_layout, null);
        login_pic = (ImageView) menuView_L.findViewById(R.id.login_pic);
        login_rightnow = (TextView) menuView_L.findViewById(R.id.login_rightnow);
        info_layout = (LinearLayout) menuView_L.findViewById(R.id.info_layout);
        allcourse_layout = (RelativeLayout) menuView_L.findViewById(R.id.allcourse_layout);
        setting_layout = (RelativeLayout) menuView_L.findViewById(R.id.setting_layout);
        mynote_layout = (RelativeLayout) menuView_L.findViewById(R.id.mynote_layout);
        article_layout = (RelativeLayout) menuView_L.findViewById(R.id.article_layout);
        mycourse_layout = (RelativeLayout) menuView_L.findViewById(R.id.mycourse_layout);
        download_layout = (RelativeLayout) menuView_L.findViewById(R.id.download_layout);
        mymessage_layout = (RelativeLayout) menuView_L.findViewById(R.id.mymessage_layout);

        allcourse_icon = (ImageView) menuView_L.findViewById(R.id.allcourse_icon);
        mycourse_icon = (ImageView) menuView_L.findViewById(R.id.mycourse_icon);
        download_icon = (ImageView) menuView_L.findViewById(R.id.download_icon);
        mymessage_icon = (ImageView) menuView_L.findViewById(R.id.mymessage_icon);
        mynote_icon = (ImageView) menuView_L.findViewById(R.id.mynote_icon);
        article_icon = (ImageView) menuView_L.findViewById(R.id.article_icon);
        setting_icon = (ImageView) menuView_L.findViewById(R.id.setting_icon);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
        init();
        AppManager.getAppManager().addActivity(this);
//        initViews();
        //参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
//        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(MainActivity.this, "100424468",
//                "c7394704798a158208a74ab60104f0ba");
//        qqSsoHandler.addToSocialSDK();
/**
 * WIFI
 */

        wifiManager = (WifiManager) super.getSystemService(Context.WIFI_SERVICE);
/**
 * 搜索框
 */
        secondright_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE, "hehe");
                intent.putExtra(Intent.EXTRA_TEXT, "www.baidu.com");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Intent ch = Intent.createChooser(intent, "分享到...");
                if (ch == null) {
                    return;
                }
                try {
                    startActivity(ch);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "Can't find share component to share", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

        v2 = LayoutInflater.from(this).inflate(R.layout.layout_addto_main, null);//找到布局
        v1 = LayoutInflater.from(this).inflate(R.layout.slidingmenu_right, null);//找到布局
        //将头布局加入下拉刷新中
        v3 = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_classproj, null);
/**
 * 下拉刷新、上拉加载数据
 */
        pullTRLV.getRefreshableView().addHeaderView(v2);                        //将布局加载到下拉刷新
        pullTRLV.setMode(PullToRefreshBase.Mode.BOTH);

        pullTRLV.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                new FinishRefresh().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                Toast.makeText(getApplicationContext(), "上拉加载数据", Toast.LENGTH_SHORT).show();
                new UpToRefresh().execute();
            }
        });
/**
 *
 * partList
 */


/**
 * viewpager
 */
        top_title = (TextView) v2.findViewById(R.id.top_title);
        ListView lv_right = (ListView) v1.findViewById(R.id.lv_right);
        vp = (ViewPager) v2.findViewById(R.id.mainadd_viewpager);        //找到ViewPager
        right_title = (TextView) v1.findViewById(R.id.right_title);

        allList = new ArrayList<>();//正文主界面
        partList = new ArrayList<>();
        topImgList = new ArrayList<>();//顶部图片
        vedioTopList = new ArrayList<>();//顶部仨视频
        classProjsList = new ArrayList<>();//右侧边栏
        child_vedio = new ArrayList<>();

        //右侧边栏
        classProjAdapter = new ClassProjAdapter(this, classProjsList);

        lv_right.setAdapter(classProjAdapter);

        //右侧边栏监听
        lv_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                flag = 1;//表示用户点击侧边栏，加载视频的list为child_vedio
                child_vedio.clear();
                //其他课程栏目
                for (int i = 0; i < allList.size(); i++) {
                    if (allList.get(i).getProjId().equals(classProjsList.get(position).getProjId())) {
                        child_vedio.add(allList.get(i));
                    }
                }
                //置顶一栏
                for (int i = 0; i < vedioTopList.size(); i++) {
                    if (vedioTopList.get(i).getProjId().equals(classProjsList.get(position).getProjId())) {
                        child_vedio.add(vedioTopList.get(i));
                    }
                }
                pullTRLV.getRefreshableView().removeHeaderView(v2);
                title_tv.setText(classProjsList.get(position).getRemark());
                right_title.setText(classProjsList.get(position).getRemark());
                //对勾
                ImageView imageView_i = (ImageView) v3.findViewById(R.id.item_click);
                imageView_i.setImageResource(R.drawable.banner_02);

                vA.notifyDataSetChanged();
                vA = new VedioAdapter(MainActivity.this, child_vedio);
                pullTRLV.setAdapter(vA);
                slidingMenu.toggle();
            }
        });

        //顶部仨图
        viewPagerAdapter = new MainViewPagerAdapter(this, topImgList, vedioTopList);
        vp.setAdapter(viewPagerAdapter);
        vp.setCurrentItem(0);


        //主界面数据
        vA = new VedioAdapter(this, partList);                        //数组放入适配器中
        pullTRLV.setAdapter(vA);

        loadAllVideoInfo();
        downloadImage();                                                //调用此方法下载ViewPager三张图片，装载图片list
        loadAllClassProj();

/**
 * 跳转到播放界面
 */
        pullTRLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (wifiManager.getWifiState() == 3) {//wifi
                    if (flag == 0) {
                        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                        intent.putExtra("Vedio", allList.get(position - 2));
                        intent.putExtra("flag", 1);

                        startActivity(intent);
                    } else if (flag == 1) {
                        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                        intent.putExtra("Vedio", child_vedio.get(position - 1));
                        intent.putExtra("flag", 1);

                        startActivity(intent);
                    } else if (flag == 3) {
                        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                        intent.putExtra("Vedio", localList.get(position - 1));
                        intent.putExtra("flag", 3);

                        startActivity(intent);
                    }
                } else if (wifiManager.getWifiState() == 1) {//非wifi
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    View alertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.diy_dialog, null);
                    builder.setView(alertView);
                    final AlertDialog ad = builder.create();

                    if (flag == 3) {
                        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                        intent.putExtra("Vedio", localList.get(position - 1));
                        intent.putExtra("flag", 3);

                        startActivity(intent);
                    } else {


                        alertView.findViewById(R.id.PositiveButton).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ad.dismiss();
                                wifiManager.setWifiEnabled(true);
                                Toast.makeText(getApplicationContext(), "正在开启WIFI", Toast.LENGTH_SHORT).show();
                            }
                        });
                        alertView.findViewById(R.id.NegativeButton).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                ad.dismiss();
                                if (flag == 0) {
                                    Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                                    intent.putExtra("Vedio", allList.get(position - 2));
                                    intent.putExtra("flag", 1);

                                    startActivity(intent);
                                } else if (flag == 1) {
                                    Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                                    intent.putExtra("Vedio", child_vedio.get(position - 1));
                                    intent.putExtra("flag", 1);
                                    startActivity(intent);
                                }
                            }
                        });
                        ad.show();
                    }
                }
            }
        });
/**
 * 加载侧边栏
 */
        slidingMenu = new SlidingMenu(this);                            //new一个侧边栏
        slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        slidingMenu.setMenu(menuView_L);                                  //将布局加载到侧边栏
        slidingMenu.setSecondaryMenu(v1);
        slidingMenu.setBehindWidth(300);                                //侧边栏出来的宽度和？
        slidingMenu.setBehindOffset(380);                               //剩余部分的宽度
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);//侧边栏执行开始
//        SlidingMenu.CanvasTransformer cf = new SlidingMenu.CanvasTransformer() {
//            @Override
//            public void transformCanvas(Canvas canvas, float percentOpen) {     //渐变效果
//                float scale = (float)(percentOpen*0.25+0.75);
//                canvas.scale(scale,scale,canvas.getWidth()/2,canvas.getHeight()/2);
//            }
//        };
//        slidingMenu.setBehindCanvasTransformer(cf);
//侧边栏——立即登录
        info_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 0;
                UserDemo user = MyApplaction.getMyApplaction().getUser();
                if (user.getUname().equals("")) {
//                    startActivity(new Intent(MainActivity.this, MainActivity_on.class));
                    startActivity(new Intent(MainActivity.this, HTMLActivity.class));
                } else {
                    Intent it = new Intent(MainActivity.this, MainActivity_grzl.class);
                    startActivity(it);
                }


            }
        });
//侧边栏——全部课程

        allcourse_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allcourse_layout.getBackground().setLevel(1);
                allcourse_icon.getBackground().setLevel(1);
                if (item != 1) {
                    bg_color(item);
                    item = 1;
                }
                title_tv.setText("全部课程");
                right_title.setText("全部课程");
                vA.notifyDataSetChanged();
                vA = new VedioAdapter(MainActivity.this, partList);
                pullTRLV.setAdapter(vA);
                slidingMenu.toggle();
                if (pullTRLV.getRefreshableView().getHeaderViewsCount() == 1) {
                    pullTRLV.getRefreshableView().addHeaderView(v2);
                    flag = 0;//表示用户点击左侧边栏
                }
            }
        });
//侧边栏 文章
        article_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                article_layout.getBackground().setLevel(1);
                article_icon.getBackground().setLevel(1);
                if (item != 6) {
                    bg_color(item);
                    item = 6;
                }
                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);

                startActivity(intent);
            }
        });


        /**
         * 本地下载的视频列表localList
         */

        db = this.openOrCreateDatabase("mobile_video.db", Activity.MODE_PRIVATE, null);


/**
 * 离线缓存
 */
        download_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download_layout.getBackground().setLevel(1);
                download_icon.getBackground().setLevel(1);
                if (item != 3) {
                    bg_color(item);
                    item = 3;
                }
                title_tv.setText("我的下载");
                right_title.setText("我的下载");
                vA.notifyDataSetChanged();
                vA = new VedioAdapter(MainActivity.this, localList);
                pullTRLV.setAdapter(vA);
                slidingMenu.toggle();
                pullTRLV.getRefreshableView().removeHeaderView(v2);
                flag = 3;
            }
        });
/**
 * 我的收藏课程
 */

        mycourse_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mycourse_layout.getBackground().setLevel(1);
                mycourse_icon.getBackground().setLevel(1);
                if (item != 2) {
                    bg_color(item);
                    item = 2;
                }
                Intent intent = new Intent(MainActivity.this, MainActivity_collect.class);
                startActivity(intent);

            }
        });
/**
 * 我的消息
 */

        mymessage_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mymessage_layout.getBackground().setLevel(1);
                mymessage_icon.getBackground().setLevel(1);
                if (item != 4) {
                    bg_color(item);
                    item = 4;
                }
                Intent intent = new Intent(MainActivity.this, SocketA.class);
                startActivity(intent);

            }
        });
/**
 * 设置
 */
        setting_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setting_layout.getBackground().setLevel(1);
                setting_icon.getBackground().setLevel(1);
                if (item != 7) {
                    bg_color(item);
                    item = 7;
                }
                Intent intent = new Intent(MainActivity.this, MainActivity_setting.class);
                startActivity(intent);
            }
        });
/**
 * 我的笔记
 */
        mynote_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mynote_layout.getBackground().setLevel(1);
                mynote_icon.getBackground().setLevel(1);
                if (item != 5) {
                    bg_color(item);
                    item = 5;
                }
            }
        });
/**
 * 两个侧边栏的监听
 */
        findViewById(R.id.left_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });
        findViewById(R.id.right_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.showSecondaryMenu();
            }
        });


/**
 * 解决 viewpager 嵌套 手势冲突
 */

        final PointF downPoint = new PointF();
        vp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent evt) {
                switch (evt.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 记录按下时候的坐标
                        downPoint.x = evt.getX();
                        downPoint.y = evt.getY();
                        if (vp.getChildCount() > 1) { //有内容，多于1个时
                            // 通知其父控件，现在进行的是本控件的操作，不允许拦截
                            vp.requestDisallowInterceptTouchEvent(true);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (vp.getChildCount() > 1) { //有内容，多于1个时
                            // 通知其父控件，现在进行的是本控件的操作，不允许拦截
                            vp.requestDisallowInterceptTouchEvent(true);
                        }
                        break;
                }
                return false;
            }
        });
        //顶部图片轮转
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 5, 5, TimeUnit.SECONDS);

    }

    //顶部图片轮转
    public class ViewPagerTask implements Runnable {


        @Override
        public void run() {
            currentItem = (currentItem + 1) % topImgList.size();
            handler.obtainMessage().sendToTarget();
        }
    }

    //顶部图片轮转
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            vp.setCurrentItem(currentItem);
        }
    };

    /**
     * 下载ViewPager图片，装进topImgList
     */
    private ImageView iv;

    public void downloadImage() {
        RequestQueue newRequestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(UrlUtil.TOP_PICTURE, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                List<Vedio> li = gson.fromJson(s, new TypeToken<ArrayList<Vedio>>() {
                }.getType());    //li存放临时下载的实体
                vedioTopList.clear();
                vedioTopList.addAll(li);
                top_title.setText(vedioTopList.get(0).getVedioName());                              //设置顶部第一个图课程的初始标题
                //小蓝点
                for (int i = 0; i < vedioTopList.size(); i++) {                                                //遍历
                    ImageView im = new ImageView(getApplication());                                 //定义空图片
                    ImageLoaderUtil.display(UrlUtil.BASE_URL + vedioTopList.get(i).getVPickUri(), im); //接收到图片

                    topImgList.add(im);                                                                   //将图片存放到list
                    linearLayout = (LinearLayout) v2.findViewById(R.id.mainadd_viewpager_check);     //小蓝点的布局位置
                    iv = new ImageView(getBaseContext());                                             //定义一个ImageView
                    if (i == 0) {
                        iv.setImageResource(R.drawable.image_indicator_focus);                      //小圆点的初始位置
                    } else {
                        iv.setImageResource(R.drawable.image_indicator);                            //第一个为蓝色其余白色
                    }
                    linearLayout.addView(iv);                                                       //add size个圆点

                }
                viewPagerAdapter.notifyDataSetChanged();
                //滑动监听
                vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {


                        for (int i = 0; i < topImgList.size(); i++) {                         //小圆点改变
                            iv = (ImageView) linearLayout.getChildAt(i);
                            if (i == position % topImgList.size()) {
                                iv.setImageResource(R.drawable.image_indicator_focus);
                                top_title.setText(vedioTopList.get(i).getVedioName());
                            } else {
                                iv.setImageResource(R.drawable.image_indicator);
                            }
                        }
                        currentItem = position;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        newRequestQueue.add(stringRequest);
    }

    /**
     * 下载所有视频信息，不包括顶部的三个
     */

    public void loadAllVideoInfo() {

        vedioDao = MyApplaction.getDaoSession(getBaseContext()).getVedioDao();
        List<Vedio> localList = vedioDao.loadAll();
        if (localList.size() > 0) {
            allList.clear();
            allList.addAll(localList);
            vA.notifyDataSetChanged();
        }

        StringRequest stR = new StringRequest(UrlUtil.All_Video, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                List<Vedio> allVideoList = gson.fromJson(s, new TypeToken<ArrayList<Vedio>>() {
                }.getType());

                Log.d("=====all=====", allVideoList.size() + "");
                if (allVideoList != null && allVideoList.size() > 0) {
                    allList.clear();
                    allList.addAll(allVideoList);
                    vA.notifyDataSetChanged();
                }
                vedioDao.deleteAll();
                for (int i = 0; i < allList.size(); i++) {
                    vedioDao.insertOrReplace(allList.get(i));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        MyApplaction.getMyApplaction().getRequestQueue().add(stR);

        if (allList.size() > 10) {
            for (int i = 0; i < 10; i++) {
                partList.add(allList.get(i));
            }
        } else {
            for (int i = 0; i < allList.size(); i++) {
                partList.add(allList.get(i));
            }
        }

    }

    /**
     * 下载所有课程名
     */


    public void loadAllClassProj() {

        StringRequest stR = new StringRequest(UrlUtil.All_Class, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                List<ClassProj> allClassList = gson.fromJson(s, new TypeToken<ArrayList<ClassProj>>() {
                }.getType());

                if (allClassList != null && allClassList.size() > 0) {
                    classProjsList.clear();
                    classProjsList.addAll(allClassList);
                    classProjAdapter.notifyDataSetChanged();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        MyApplaction.getMyApplaction().getRequestQueue().add(stR);

    }

    /**
     * onStart方法，当重新回到这个Activity时，重新加载一次数据
     */

    @Override
    protected void onStart() {
        super.onStart();
        //头像

        UserDemo user = MyApplaction.getMyApplaction().getUser();
        if (!user.getUname().equals("")) {
            ImageLoaderUtil.display(user.getPhotoUri(), login_pic);
            login_rightnow.setText(user.getNickName());
        }

    }


    /**
     * 沉浸式状态栏
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

    public void alertDialogShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View alertView = LayoutInflater.from(this).inflate(R.layout.diy_dialog, null);
        builder.setView(alertView);
        final AlertDialog ad = builder.create();
        alertView.findViewById(R.id.PositiveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
                wifiManager.setWifiEnabled(true);
                Toast.makeText(getApplicationContext(), "正在开启WIFI", Toast.LENGTH_SHORT).show();
            }
        });
        alertView.findViewById(R.id.NegativeButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });
//        TextView title = (TextView)alertView.findViewById(R.id.Title);
////        title.setText("提示");
//
//        TextView subTitle= (TextView)alertView.findViewById(R.id.SubTitle);
////        subTitle.setText("WIFI未开启");
        ad.show();
    }


    private class FinishRefresh extends AsyncTask<Void, Void, Void> {//下拉刷新

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            vA.notifyDataSetChanged();
            classProjAdapter.notifyDataSetChanged();
            pullTRLV.onRefreshComplete();
            count = 2;
            partList.clear();
            if (allList.size() > 10) {
                for (int i = 0; i < 10; i++) {
                    partList.add(allList.get(i));
                }
            } else {
                for (int i = 0; i < allList.size(); i++) {
                    partList.add(allList.get(i));
                }
            }


            if (flag == 3) {
                localList.clear();
                String search = "select * from mobile_video";
                Cursor cursor = db.rawQuery(search, null);
                Vedio vedio;
                while (cursor.moveToNext()) {
                    vedio = new Vedio();
                    vedio.setId(cursor.getLong(0));
                    vedio.setVedioid(cursor.getString(1));
                    vedio.setVedioName(cursor.getString(2));
                    vedio.setVUri(cursor.getString(3));
                    vedio.setProjId(cursor.getString(4));
                    vedio.setInstruction(cursor.getString(5));
                    vedio.setAuthor(cursor.getString(6));
                    vedio.setPubDate(cursor.getString(7));
                    vedio.setVPickUri(cursor.getString(8));
                    vedio.setFlag(cursor.getString(9));
                    localList.add(vedio);
                }
            }

            loadAllVideoInfo();
            loadAllClassProj();
            vA.notifyDataSetChanged();
        }
    }

    private class UpToRefresh extends AsyncTask<Void, Void, Void> {//上拉加载数据

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            vA.notifyDataSetChanged();
//            int m = (allList.size()%10);
            if (flag == 0) {
                int n = (allList.size() / 10) + 1;//1是不够十个，2是十几个
                partList.clear();
                if (count >= n) {
                    for (int i = 0; i < allList.size(); i++) {
                        partList.add(allList.get(i));
                    }
                    Toast.makeText(getBaseContext(), "已经加载全部课程", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < count * 10; i++) {
                        partList.add(allList.get(i));
                    }
                    count = count + 1;
                }
                vA.notifyDataSetChanged();
            }
            pullTRLV.onRefreshComplete();
        }
    }


    public void bg_color(int item) {
        if (item == 1) {
            allcourse_layout.getBackground().setLevel(0);
            allcourse_icon.getBackground().setLevel(0);
        } else if (item == 2) {
            mycourse_layout.getBackground().setLevel(0);
            mycourse_icon.getBackground().setLevel(0);
        } else if (item == 3) {
            download_layout.getBackground().setLevel(0);
            download_icon.getBackground().setLevel(0);
        } else if (item == 4) {
            mymessage_layout.getBackground().setLevel(0);
            mymessage_icon.getBackground().setLevel(0);
        } else if (item == 5) {
            mynote_layout.getBackground().setLevel(0);
            mynote_icon.getBackground().setLevel(0);
        } else if (item == 6) {
            article_layout.getBackground().setLevel(0);
            article_icon.getBackground().setLevel(0);
        } else if (item == 7) {
            setting_layout.getBackground().setLevel(0);
            setting_icon.getBackground().setLevel(0);
        } else {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.show();
        }
        return false;
    }

    public void show() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage("确定退出？").setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        AppManager.getAppManager().finishAllActivity();
//                        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//                        activityManager.killBackgroundProcesses(getPackageName());
//                        System.exit(0);
//                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                }).create();
        alertDialog.show();
    }
}

