package com.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.dao.Vedio;
import com.jredu_day1.R;
import com.mediedictionary.playerlibrary.PlayerActivity;
import com.util.UrlUtil;

import java.util.List;

/**
 * Created by zhang on 2015/11.18
 */
public class MainViewPagerAdapter extends PagerAdapter{
    private List<ImageView> myData;
    private WifiManager wifiManager;

    private List<Vedio> mydata;
    private Context context;

    public MainViewPagerAdapter(Context context, List<ImageView> myData, List<Vedio> mydata) {
        this.context = context;
        this.myData = myData;
        this.mydata = mydata;
    }

    @Override
    public int getCount() {
        return myData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(myData.get(position));
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        ImageView imageV = myData.get(position);

        imageV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wifiManager.getWifiState()==3) {
                    Intent intent = new Intent(context,PlayerActivity.class);
                    intent.putExtra("url",UrlUtil.BASE_URL + mydata.get(position).getVUri());
                    intent.putExtra("title",mydata.get(position).getVedioName());
                    context.startActivity(intent);
                }else if(wifiManager.getWifiState()==1){
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    View alertView = LayoutInflater.from(context).inflate(R.layout.diy_dialog, null);
                    builder.setView(alertView);
                    final AlertDialog ad = builder.create();
                    alertView.findViewById(R.id.PositiveButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ad.dismiss();
                            wifiManager.setWifiEnabled(true);
                            Toast.makeText(context, "正在开启WIFI", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertView.findViewById(R.id.NegativeButton).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            ad.dismiss();
                            Intent intent = new Intent(context,PlayerActivity.class);
                            intent.putExtra("Vedio",mydata.get(position));
                            intent.putExtra("flag",1);

//                            intent.putExtra("url",UrlUtil.BASE_URL + mydata.get(position).getVUri());
//                            intent.putExtra("title",mydata.get(position).getVedioName());
                            context.startActivity(intent);
                        }
                    });

                    ad.show();
                }




            }
        });

        imageV.setScaleType(ImageView.ScaleType.FIT_XY);
        container.addView(imageV);
        return imageV;
    }
}