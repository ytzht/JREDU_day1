package com.mediedictionary.playerlibrary;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dao.UserDemo;
import com.dao.Vedio;
import com.jredu_day1.MyApplaction;
import com.jredu_day1.R;
import com.mediedictionary.playerlibrary.PlayerView.OnChangeListener;
import com.service.DownloadService;
import com.util.StringPostRequest;
import com.util.UrlUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class PlayerActivity extends Activity implements OnChangeListener, OnClickListener, OnSeekBarChangeListener, Callback {

	private static final int SHOW_PROGRESS = 0;
	private static final int ON_LOADED = 1;
	private static final int HIDE_OVERLAY = 2;

	private View rlLoading;
	private PlayerView mPlayerView;
	private TextView tvBuffer;
	private TextView tvTime;
	private TextView tvLength;
	private SeekBar sbVideo;
	private ImageButton ibFarward;
	private ImageButton download_class;
	private ImageButton favorite_class;
	private ImageButton ibBackward;
	private ImageButton ibPlay;
	private View llOverlay, rlOverlayTitle;
	private Handler mHandler;
	private Vedio vedio;
	private SQLiteDatabase db;
	private String mUrl;
	private RelativeLayout fx;

	private UserDemo userDemo ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDemo = MyApplaction.getMyApplaction().getUser();

		int flag = getIntent().getIntExtra("flag",1);
		vedio = (Vedio) getIntent().getSerializableExtra("Vedio");
		if(flag == 1){
			mUrl = UrlUtil.BASE_URL+vedio.getVUri();
		}else if(flag == 3){
			mUrl = "file://"+Environment.getExternalStorageDirectory().toString() +"/JREDU"+ vedio.getVUri();
		}

		String title = vedio.getVedioName();
		if (TextUtils.isEmpty(mUrl)) {
			Toast.makeText(this, "error:no url in intent!", Toast.LENGTH_SHORT).show();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_player);
//		setKeepScreenOn(true);

		mHandler = new Handler(this);

		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTime = (TextView) findViewById(R.id.tv_time);
		tvLength = (TextView) findViewById(R.id.tv_length);
		sbVideo = (SeekBar) findViewById(R.id.sb_video);
		sbVideo.setOnSeekBarChangeListener(this);
		ImageButton ibLock = (ImageButton) findViewById(R.id.ib_lock);
		ibLock.setOnClickListener(this);
		ibBackward = (ImageButton) findViewById(R.id.ib_backward);
		ibBackward.setOnClickListener(this);
		ibPlay = (ImageButton) findViewById(R.id.ib_play);
		ibPlay.setOnClickListener(this);
		ibFarward = (ImageButton) findViewById(R.id.ib_forward);
		ibFarward.setOnClickListener(this);
		favorite_class = (ImageButton) findViewById(R.id.favorite_class);
//		favorite_class.setOnClickListener(this);
		download_class = (ImageButton) findViewById(R.id.download_class);
		download_class.setOnClickListener(this);
		ImageButton ibSize = (ImageButton) findViewById(R.id.ib_size);
		ibSize.setOnClickListener(this);
		fx = (RelativeLayout)findViewById(R.id.fx_sp);
		fx.setOnClickListener(this);

		llOverlay = findViewById(R.id.ll_overlay);
		rlOverlayTitle = findViewById(R.id.rl_title);

		rlLoading = findViewById(R.id.rl_loading);
		tvBuffer = (TextView) findViewById(R.id.tv_buffer);
		//使用步骤
		//第一步 ：通过findViewById或者new PlayerView()得到mPlayerView对象
		//mPlayerView= new PlayerView(PlayerActivity.this);
		mPlayerView = (PlayerView) findViewById(R.id.pv_video);

		//第二步：设置参数，毫秒为单位
		mPlayerView.setNetWorkCache(20000);

		//第三步:初始化播放器
		mPlayerView.initPlayer(mUrl);

		//第四步:设置事件监听，监听缓冲进度等
		mPlayerView.setOnChangeListener(this);

		//第五步：开始播放
		mPlayerView.start();

		//init view
		tvTitle.setText(title);
		showLoading();
		hideOverlay();





		db = this.openOrCreateDatabase("mobile_video.db", Activity.MODE_PRIVATE,null);
		String createTable =
				"CREATE TABLE IF NOT EXISTS mobile_video(_id  int NOT NULL ,vedioid  varchar(255) NULL ,vedioName  varchar(255) NULL ,VUri  varchar(255) NULL ,projId  varchar(255) NULL ,instruction  varchar(255) NULL ,author  varchar(255) NULL ,pubDate  varchar(255) NULL ,VPickUri  varchar(255) NULL ,flag  varchar(255) NULL ,PRIMARY KEY (_id))";
		db.execSQL(createTable);




		if (userDemo.getUname().equals("")) {
			favorite_class.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(getApplication(), "请登录后收藏", Toast.LENGTH_SHORT).show();
				}
			});

		} else {
			favorite_class.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					collect();
//					favorite_class.setImageResource(R.drawable.collect_focused);
					Toast.makeText(getApplication(), "收藏成功", Toast.LENGTH_SHORT).show();
				}
			});
		}




	}
	private void collect() {
		String url = UrlUtil.COLLECT_URL+"?uname="+userDemo.getUname()+"&enddate=1"+
				"&projid='"+vedio.getProjId()+"'&vedioid='"+vedio.getVedioid()+"'&flag=1";
		StringPostRequest str = new StringPostRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
			}
		});
//		Log.d("=====user====",userDemo.getUname());
//		Log.d("=====proj====",vedio.getProjId());
//		str.putParams("uname", userDemo.getUname());
//		str.putParams("enddate", vedio.getPubDate());
//		str.putParams("projid", vedio.getProjId());
//		str.putParams("vedioid", vedio.getVedioid());
//		str.putParams("flag", "1");
		Log.d("=====url=====",url);
//		Log.d("=====collect",str+"       ");
		MyApplaction.getMyApplaction().getRequestQueue().add(str);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (llOverlay.getVisibility() != View.VISIBLE) {
				showOverlay();
			} else {
				hideOverlay();
			}
		}
		return false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		mPlayerView.changeSurfaceSize();
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onPause() {
		hideOverlay();
		mPlayerView.stop();
		super.onPause();
	}

	@Override
	public void onResume() {
		if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		super.onResume();


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBufferChanged(float buffer) {
		if (buffer >= 100) {
			hideLoading();
		} else {
			showLoading();
		}
		tvBuffer.setText("正在缓冲中..." + (int) buffer + "%");
	}

	private void showLoading() {
		rlLoading.setVisibility(View.VISIBLE);
	}

	private void hideLoading() {
		rlLoading.setVisibility(View.GONE);
	}

	@Override
	public void onLoadComplet() {
		mHandler.sendEmptyMessage(ON_LOADED);
	}

	@Override
	public void onError() {
		Toast.makeText(getApplicationContext(), "出错了！", Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public void onEnd() {
		finish();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ib_lock:
				break;
			case R.id.ib_forward:
				mPlayerView.seek(10000);
				break;
			case R.id.ib_play:
				if (mPlayerView.isPlaying()) {
					mPlayerView.pause();
					ibPlay.setBackgroundResource(R.drawable.ic_play);
				} else {
					mPlayerView.play();
					ibPlay.setBackgroundResource(R.drawable.ic_pause);
				}
				break;
//			case R.id.favorite_class:
//				//!!!!!!!!!!!!!!!!!!!
//				if(MyApplaction.getMyApplaction().getUser().getUname().equals("")){
//					Toast.makeText(getBaseContext(), "请先登录", Toast.LENGTH_SHORT).show();
//				} else {
//					Log.d("=====", MyApplaction.getMyApplaction().getUser().getUname());
//					UserCollect userCollect = new UserCollect();
//					userCollect.setUname(MyApplaction.getMyApplaction().getUser().getUname());
////					userCollect.setCollectId(vedio.getVedioid() + vedio.getProjId());
//					userCollect.setEndDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
//
//
//
//
//				}
//				break;
			case R.id.download_class:
				//!!!!!!!!!!!!!!!!!!!!
				Intent intent = new Intent(this, DownloadService.class);
				intent.putExtra("vedio", vedio);

				//插入
				int a = 0;
				Cursor cursor = db.rawQuery("SELECT _id FROM mobile_video WHERE _id = ?",new String[]{vedio.getId().toString()});
				while (cursor.moveToNext()) {
					if(cursor.getString(0).equals(vedio.getId().toString())){
						a=1;
					}
				}
				if(a==0) {
					startService(intent);
					String insert = "INSERT INTO mobile_video (_id,vedioid,vedioName,VUri,projId,instruction,author,pubDate,VPickUri,flag) VALUES" +
							"(" + vedio.getId() + ",'" + vedio.getVedioid() + "','" + vedio.getVedioName() + "','" + vedio.getVUri() + "','" + vedio.getProjId() + "','" + vedio.getInstruction() + "','" +
							vedio.getAuthor() + "','" + vedio.getPubDate() + "','" + vedio.getVPickUri() + "','" + vedio.getFlag() + "')";
					db.execSQL(insert);
					Toast.makeText(getApplicationContext(), "插入成功", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getApplicationContext(), "已存在", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.ib_backward:
				mPlayerView.seek(-10000);
				break;
			case R.id.ib_size:
				break;
			case R.id.fx_sp:
				Intent inten = new Intent(Intent.ACTION_SEND);
				inten.setType("text/plain");
				inten.putExtra(Intent.EXTRA_TITLE, "杰瑞教育");
				inten.putExtra(Intent.EXTRA_TEXT, UrlUtil.BASE_URL+vedio.getVUri());
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Intent ch = Intent.createChooser(inten,"分享到...");
				if(ch == null){
					return;
				}
				try {
					startActivity(ch);
				}catch (android.content.ActivityNotFoundException ex){
					Toast.makeText(getApplicationContext(),"Can't find share component to share",Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
		}
	}

	private void showOverlay() {
		rlOverlayTitle.setVisibility(View.VISIBLE);
		llOverlay.setVisibility(View.VISIBLE);
		mHandler.sendEmptyMessage(SHOW_PROGRESS);
		mHandler.removeMessages(HIDE_OVERLAY);
		mHandler.sendEmptyMessageDelayed(HIDE_OVERLAY, 5 * 1000);
	}

	private void hideOverlay() {
		rlOverlayTitle.setVisibility(View.GONE);
		llOverlay.setVisibility(View.GONE);
		mHandler.removeMessages(SHOW_PROGRESS);
	}

	private int setOverlayProgress() {
		if (mPlayerView == null) {
			return 0;
		}
		int time = (int) mPlayerView.getTime();
		int length = (int) mPlayerView.getLength();
		boolean isSeekable = mPlayerView.canSeekable() && length > 0;
//		ibFarward.setVisibility(isSeekable ? View.VISIBLE : View.GONE);
//		ibBackward.setVisibility(isSeekable ? View.VISIBLE : View.GONE);
//		favorite_class.setVisibility(isSeekable ? View.VISIBLE : View.GONE);
//		download_class.setVisibility(isSeekable ? View.VISIBLE : View.GONE);
//		fx.setVisibility(isSeekable ? View.VISIBLE : View.GONE);
		sbVideo.setMax(length);
		sbVideo.setProgress(time);
		if (time >= 0) {
			tvTime.setText(millisToString(time, false));
		}
		if (length >= 0) {
			tvLength.setText(millisToString(length, false));
		}
		return time;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser && mPlayerView.canSeekable()) {
			mPlayerView.setTime(progress);
			setOverlayProgress();
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case SHOW_PROGRESS:
			setOverlayProgress();
			mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 20);
			break;
		case ON_LOADED:
			showOverlay();
			hideLoading();
			break;
		case HIDE_OVERLAY:
			hideOverlay();
			break;
		default:
			break;
		}
		return false;
	}

	private String millisToString(long millis, boolean text) {
		boolean negative = millis < 0;
		millis = Math.abs(millis);
		int mini_sec = (int) millis % 1000;
		millis /= 1000;
		int sec = (int) (millis % 60);
		millis /= 60;
		int min = (int) (millis % 60);
		millis /= 60;
		int hours = (int) millis;

		String time;
		DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.US);
		format.applyPattern("00");

		DecimalFormat format2 = (DecimalFormat) NumberFormat.getInstance(Locale.US);
		format2.applyPattern("000");
		if (text) {
			if (millis > 0)
				time = (negative ? "-" : "") + hours + "h" + format.format(min) + "min";
			else if (min > 0)
				time = (negative ? "-" : "") + min + "min";
			else
				time = (negative ? "-" : "") + sec + "s";
		} else {
			if (millis > 0)
				time = (negative ? "-" : "") + hours + ":" + format.format(min) + ":" + format.format(sec) + ":" + format2.format(mini_sec);
			else
				time = (negative ? "-" : "") + min + ":" + format.format(sec) + ":" + format2.format(mini_sec);
		}
		return time;
	}
}
