package com.danertu.tools;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.config.Constants;

/**
 * Created by Viz on 2017/9/8.
 * 定位工具类
 */

public class LocationUtil {
    // 定位相关
    private LocationClient mLocClient;
    private MyLocationData locData = null;
    private Context context;
    public LocationUtil(Context context) {
        this.context=context;
        MyLocationListener myListener = new MyLocationListener();
        // 定位初始化
        mLocClient = new LocationClient(context);
        mLocClient.registerLocationListener(myListener);
    }

    /**
     * 开启定位
     */
    public void startLocate(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // Receive Location
            if (location != null) {
                try {
                    locData=new MyLocationData.Builder()
                            .latitude(location.getLatitude())
                            .longitude(location.getLongitude())
                            .build();
                    String city = location.getCity();
                    Constants.setCurrentProvince(location.getProvince());
                    Constants.setCityName(city);
                    Constants.setDcityName(city);
                    String la = String.valueOf(location.getLatitude());
                    String lt = String.valueOf(location.getLongitude());
                    Constants.setLa(la);
                    Constants.setLt(lt);
                    Constants.location = location;
                    mLocClient.stop();
                    //发广播
                    LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
                    Intent intent = new Intent(Constants.GET_LOCATION_FINISH);
                    manager.sendBroadcast(intent);
                    // IndexActivity.index.txtcity.setText(s);
                    // IndexActivity.index.bind();
                    //IndexActivity.index.adapter1.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
