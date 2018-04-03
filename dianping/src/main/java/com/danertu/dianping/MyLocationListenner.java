package com.danertu.dianping;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

public class MyLocationListenner implements BDLocationListener {
    //	public MKSearch mSearch;// 搜索模块，也可去掉地图模块独立使用
    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location == null)
            return;
        StringBuilder sb = new StringBuilder(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());

        if (location.getLocType() == BDLocation.TypeGpsLocation) {
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
        }
        // logMsg(sb.toString());
//		MainActivity.lat = location.getLatitude();
//		MainActivity.lng = location.getLongitude();

        //	String s = location.getDistrict();
        //MainActivity.city ="上海";
        //	TextView mTextView =
    }

    public void onReceivePoi(BDLocation poiLocation) {
        if (poiLocation == null) {
            return;
        }
        StringBuilder sb = new StringBuilder(256);
        sb.append("Poi time : ");
        sb.append(poiLocation.getTime());
        sb.append("\nerror code : ");
        sb.append(poiLocation.getLocType());
        sb.append("\nlatitude : ");
        sb.append(poiLocation.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(poiLocation.getLongitude());
        sb.append("\nradius : ");
        sb.append(poiLocation.getRadius());
        if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
            sb.append("\naddr : ");
            sb.append(poiLocation.getAddrStr());
        }
        PublicContext.lat = poiLocation.getLatitude();
        PublicContext.lng = poiLocation.getLongitude();
    }


}
