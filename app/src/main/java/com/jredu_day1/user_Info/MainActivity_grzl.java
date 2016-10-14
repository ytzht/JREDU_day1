package com.jredu_day1.user_Info;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dao.UserDemo;
import com.google.gson.Gson;
import com.jredu_day1.MyApplaction;
import com.jredu_day1.R;
import com.util.FileUitlity;
import com.util.ImageLoaderUtil;
import com.util.StringPostRequest;
import com.util.SystemBarTintManager;
import com.util.UrlUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class MainActivity_grzl extends Activity implements View.OnClickListener {
    private RelativeLayout relativeLayout3,relativeLayout4,relativeLayout5,relativeLayout6;
    private TextView nickname,sexId,level;
    private ImageView face_grzl,left_icon;
    private UserDemo userDemo;
    private View contentview;
    private String capturePath = "";
    private PopupWindow popupWindow = null;
    private static final int PHONE_PHOTO = 0;//相册
    private static final int TAKE_PHOTO = 1;//相机
    private static final int RESULT_PHOTO = 2;//剪切
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_grzl);
        nickname = (TextView)findViewById(R.id.nickname);
        sexId = (TextView)findViewById(R.id.sexId);
        level = (TextView)findViewById(R.id.level);
        face_grzl = (ImageView)findViewById(R.id.face_grzl);
        left_icon = (ImageView)findViewById(R.id.left_icon);
        relativeLayout3 = (RelativeLayout)findViewById(R.id.relativeLayout3);
        relativeLayout4 = (RelativeLayout)findViewById(R.id.relativeLayout4);
        relativeLayout5 = (RelativeLayout)findViewById(R.id.relativeLayout5);
        relativeLayout6 = (RelativeLayout)findViewById(R.id.relativeLayout7);
        userDemo = MyApplaction.getMyApplaction().getUser();


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

        left_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //头像点击
        relativeLayout3.setOnClickListener(this);
        contentview = this.getLayoutInflater().inflate(R.layout.config_userinfo_face_popwindow_layout, null);
        //相机点击
        contentview.findViewById(R.id.btn_take_photo).setOnClickListener(this);
        //相册点击
        contentview.findViewById(R.id.btn_phone_photo).setOnClickListener(this);
        //取消点击
        contentview.findViewById(R.id.btn_cancel).setOnClickListener(this);
        //昵称修改
        relativeLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity_grzl.this,MainActivity_change.class);
                it.putExtra("change_num","nick");
                startActivity(it);


            }
        });
        //性别修改
        relativeLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity_grzl.this,MainActivity_change.class);
                it.putExtra("change_num","sex");
                startActivity(it);
            }
        });
        //密码修改
        relativeLayout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity_grzl.this,MainActivity_change.class);
                it.putExtra("change_num","pwd");
                startActivity(it);
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
    //跳出选项框
    public PopupWindow getPopwindow(View view) {
        PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = 0.6f;
        getWindow().setAttributes(layoutParams);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.showAtLocation(findViewById(R.id.face_grzl), Gravity.BOTTOM, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                layoutParams.alpha = 1f;
                getWindow().setAttributes(layoutParams);
            }
        });
        return popupWindow;
    }


    @Override
    protected void onStart() {
        super.onStart();
        userDemo = MyApplaction.getMyApplaction().getUser();
        nickname.setText(userDemo.getNickName());
        sexId.setText(userDemo.getSexId());
        level.setText(userDemo.getLevel());
        ImageLoaderUtil.display(userDemo.getPhotoUri(), face_grzl);
    }
    //设置照片传送的格式
    public String convertBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        try {
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes=bos.toByteArray();
        byte[] enCode= Base64.encode(bytes, Base64.DEFAULT);
        return new String(enCode);
    }
    //上传照片
    public void alertDialogShow(final Bitmap bitmap1){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.diy_dialog_1, null);
        builder.setView(view);
        final AlertDialog ad=builder.create();
        view.findViewById(R.id.PositiveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上传图片
                ad.dismiss();
                uploadPic(convertBitmap(bitmap1));
            }
        });
        view.findViewById(R.id.NegativeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });
        TextView title=(TextView) view.findViewById(R.id.Title);
        title.setText("提示");
        TextView subtitle=(TextView) view.findViewById(R.id.SubTitle);
        subtitle.setText("是否上传此头像？");
        ad.show();
    }

    //
    public void uploadPic(String pic){
        userDemo = MyApplaction.getMyApplaction().getUser();

        String url= UrlUtil.UserLogin;
        StringPostRequest stringPost=new StringPostRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                UserDemo user = gson.fromJson(s,UserDemo.class);
                MyApplaction.getMyApplaction().setUser(user);
                Toast.makeText(getApplicationContext(), "头像上传成功", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getBaseContext(),"网络异常",Toast.LENGTH_LONG).show();
            }
        });
        stringPost.putParams("uname",userDemo.getUname());
        stringPost.putParams("flag", "4");
        stringPost.putParams("image", pic);
        MyApplaction.getMyApplaction().getRequestQueue().add(stringPost);


//        stringPost.putParams("enddate","1");
//        stringPost.putParams("pwd",userDemo.getUpwd());
//        stringPost.setTag("pwd");
    }
    //设置照片样式
    public void startPhoneZoom(Uri uri){
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        //设置裁剪
        intent.putExtra("crop","true");
        //设置宽度，高度比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY",1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_PHOTO);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.relativeLayout3:
                popupWindow = getPopwindow(contentview);
                break;
            //相机
            case R.id.btn_take_photo:
                String state = Environment.getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                    File parent= FileUitlity.getInstance(getApplicationContext()).makeDir("head_img");
                    capturePath = parent.getPath()+File.separatorChar + System.currentTimeMillis() + ".jpg";
                    getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(capturePath)));
                    getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    startActivityForResult(getImageByCamera, TAKE_PHOTO);
                }
                else {
                    Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
                }
                break;
            //相册
            case R.id.btn_phone_photo:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PHONE_PHOTO);
                break;
            //取消
            case R.id.btn_cancel:
                popupWindow.dismiss();
                break;
            default:

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK){
            return;
        }
        switch (requestCode){
            case PHONE_PHOTO:
                Cursor cursor = this.getContentResolver().query(data.getData(),
                        new String[]{ MediaStore.Images.Media.DATA },
                        null, null, null);
                cursor.moveToFirst();
                capturePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
                startPhoneZoom(Uri.fromFile(new File(capturePath)));
                break;
            case TAKE_PHOTO:
                startPhoneZoom(Uri.fromFile(new File(capturePath)));
                break;
            case RESULT_PHOTO:
                Bundle bundle = data.getExtras();
                if(bundle!=null){
                    Bitmap bitmap = bundle.getParcelable("data");
                    face_grzl.setImageBitmap(bitmap);
                    alertDialogShow(bitmap);
                }
                break;
        }
        popupWindow.dismiss();
    }
}
