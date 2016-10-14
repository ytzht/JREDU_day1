package com.jredu_day1;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class MapActivity extends Activity {

    private WebView map_view;
    private String ss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);



        map_view = (WebView)findViewById(R.id.map_view);
        WebSettings webSettings = map_view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        map_view.setWebViewClient(new WebViewClient());
        map_view.loadUrl("http://m.amap.com/navi/?" +
                "dest=121.442946,37.468676&" +
                "destName=杰瑞教育&" +
                "hideRouteIcon=1&" +
                "key=32237cd317df61f972ca68bc8ec059f0");
//自己的位置时用
//        if (gpsIsOpen()){
//            getLocation();
//        }


    }


    private boolean gpsIsOpen() {
        boolean bRet = true;
        LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS未开启", Toast.LENGTH_SHORT).show();
            bRet = false;
        }
        return bRet;
    }
    private Location getLocation() {
        //获取位置管理服务
        LocationManager mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //查找服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); //定位精度: 最高
        criteria.setAltitudeRequired(false); //海拔信息：不需要
        criteria.setBearingRequired(false); //方位信息: 不需要
        criteria.setCostAllowed(true);  //是否允许付费
        criteria.setPowerRequirement(Criteria.POWER_LOW); //耗电量: 低功耗
        String provider = mLocationManager.getBestProvider(criteria, true); //获取GPS信息
        Location location = mLocationManager.getLastKnownLocation(provider);
        mLocationManager.requestLocationUpdates(provider, 2000, 5, locationListener);
        return location;
    }
    //监听GPS位置改变后得到新的经纬度
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Address address = getAddress(location).get(0);
            String str = String.valueOf(getAddress(location));
            if (location != null) {
//                textView.setText("纬度:" + location.getLatitude() +
//                        "\n经度:" + location.getLongitude() +
//                        "\n国家编号:" + address.getCountryCode() +
//                        "\n国家：" + address.getCountryName() +
//                        "\n省份:" + address.getAdminArea() +
//                        "\n所在市:" + address.getLocality() +
//                        "\n所在区:" + address.getSubLocality() +
//                        "\n所在街道:" + address.getThoroughfare() +
//                        "\n\n详细地址：" + str.substring(str.indexOf("0:\"") + 3, str.indexOf("\",1:\"")) +
//                        "\n\n您附近有：" +
//                        "\n" + str.substring(str.indexOf("1:\"") + 3, str.indexOf("\",2:\"")) +
//                        "\n" + str.substring(str.indexOf("2:\"") + 3, str.indexOf("\",3:\"")) +
//                        "\n" + str.substring(str.indexOf("3:\"") + 3, str.indexOf("\",4:\"")) +
//                        "\n" + str.substring(str.indexOf("4:\"") + 3, str.indexOf("\",5:\"")) +
//                        "\n" + str.substring(str.indexOf("5:\"") + 3, str.indexOf("\",6:\"")) +
//                        "\n" + str.substring(str.indexOf("6:\"") + 3, str.indexOf("\",7:\"")) +
//                        "\n" + str.substring(str.indexOf("7:\"") + 3, str.indexOf("\",8:\"")) +
//                        "\n" + str.substring(str.indexOf("8:\"") + 3, str.indexOf("\",9:\"")) +
//                        "\n" + str.substring(str.indexOf("9:\"") + 3, str.indexOf("\",10:\"")) +
//                        "\n" + str.substring(str.indexOf("10:\"") + 4, str.indexOf("\"],")));
                Log.d("=====地址=====", getAddress(location) + "");


                ss = "http://m.amap.com/navi/?dest="+location.getLongitude()+","+location.getLatitude()+"&destName="+str.substring(str.indexOf("1:\"") + 3, str.indexOf("\",2:\""))+"&hideRouteIcon=1&key=32237cd317df61f972ca68bc8ec059f0";
                if(ss!=null){
                    map_view.loadUrl(ss);
                }else {
                    map_view.loadUrl("http://m.amap.com/navi/?" +
                            "dest=121.442946,37.468676&" +
                            "destName=杰瑞教育&" +
                            "hideRouteIcon=1&" +
                            "key=32237cd317df61f972ca68bc8ec059f0");
                }
            } else {
//                textView.setText("获取不到数据");
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    };
    // 获取地址信息
    private List<Address> getAddress(Location location) {
        List<Address> result = null;
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
