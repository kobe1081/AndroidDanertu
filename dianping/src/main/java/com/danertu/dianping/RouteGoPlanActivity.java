package com.danertu.dianping;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.config.Constants;
import com.danertu.baidumapapi.adapter.RouteLineAdapter;
import com.danertu.baidumapapi.overlayutil.BikingRouteOverlay;
import com.danertu.baidumapapi.overlayutil.DrivingRouteOverlay;
import com.danertu.baidumapapi.overlayutil.OverlayManager;
import com.danertu.baidumapapi.overlayutil.TransitRouteOverlay;
import com.danertu.baidumapapi.overlayutil.WalkingRouteOverlay;
import com.danertu.tools.AppManager;
import com.danertu.tools.LocationUtil;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;

import java.io.File;
import java.util.List;

import wl.codelibrary.widget.IOSDialog;


public class RouteGoPlanActivity extends BaseActivity implements BaiduMap.OnMapClickListener, OnGetRoutePlanResultListener, SensorEventListener {

    private MyLocationListener myLocationListener;
    private LocationClient locationClient;

    // UI相关
    RadioGroup radioGroup = null;
    RadioButton mBtnDrive = null; // 驾车搜索
    RadioButton mBtnTransit = null; // 公交搜索
    RadioButton mBtnWalk = null; // 步行搜索
    RadioButton mBtnBike = null; // 骑行搜索
    Button title = null;
    ImageView ivDingwei = null;

    PopupWindow popupWindow;
    IOSDialog iosDialog;
    Button mWebNiv;
    // 浏览路线节点相关
    Button mBtnPre = null; // 上一个节点
    Button mBtnNext = null; // 下一个节点
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = false;
    private int currentTytpe = -1;
    private TextView popupText = null; // 泡泡view

    // 地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
    // 如果不处理touch事件，则无需继承，直接使用MapView即可
    MapView mMapView = null;    // 地图View
    BaiduMap mBaidumap = null;
    // 搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
    //定位相关

    //罗盘传感器
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;

    private MyLocationData locData;
    boolean isFirstLoc = true; // 是否首次定位

    private MyLocationConfiguration.LocationMode currentLocationMode;
    BitmapDescriptor mCurrentMarker;

    WalkingRouteResult nowResultwalk = null;
    BikingRouteResult nowResultbike = null;
    TransitRouteResult nowResultransit = null;
    DrivingRouteResult nowResultdrive = null;

    int nowSearchType = -1; // 当前进行的检索，供判断浏览节点时结果使用。

    private String endName, cityName, type;//目的地名称 城市  出行方式
    private int endLa, endLt;   //终点经度  终点纬度

    Button postlocation;//提交我的坐标按钮
    String result = "";

    boolean hasShownDialogue = false;
    private boolean isRequest = false;
    private boolean isFirstIn = true;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LocationUtil(this).startLocate();
        setContentView(R.layout.activity_route_go_plan);
        findViewById();
        initData();
        initTitle();
        initRadioListener();
        initDingwei();
        initBDMap();
        postLocation();
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);

    }

    /**
     * 给定位按钮设定相关事件
     */
    private void initDingwei() {
        ivDingwei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentLocationMode) {
                    case NORMAL:
                        ivDingwei.setImageResource(R.mipmap.ic_following);
                        currentLocationMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        mBaidumap.setMyLocationConfiguration(new MyLocationConfiguration(currentLocationMode, true, mCurrentMarker));
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.overlook(0);
                        mBaidumap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                        break;
                    case COMPASS:
                        ivDingwei.setImageResource(R.mipmap.ic_normal);
                        currentLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
                        mBaidumap.setMyLocationConfiguration(new MyLocationConfiguration(currentLocationMode, true, mCurrentMarker));
                        MapStatus.Builder builder1 = new MapStatus.Builder();
                        builder1.overlook(0);
                        mBaidumap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
                        break;
                    case FOLLOWING:
                        ivDingwei.setImageResource(R.mipmap.ic_compass);
                        currentLocationMode = MyLocationConfiguration.LocationMode.COMPASS;
                        mBaidumap.setMyLocationConfiguration(new MyLocationConfiguration(currentLocationMode, true, mCurrentMarker));
                        break;
                    default:
                        break;
                }
                isRequest = true;
                requestLocClick();
            }
        });

    }


    /**
     * 手动触发一次定位请求
     */
    public void requestLocClick() {
        isRequest = true;
        locationClient.requestLocation();
    }

    /**
     * 设定标题
     */
    private void initTitle() {
        CharSequence titleLabel = "路线规划功能";
        setTitle(titleLabel);
        title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 获取传递过来的数据
     */
    private void initData() {
        Intent intent = getIntent();
        endName = intent.getExtras().getString("endName");
        cityName = intent.getExtras().getString("cityName");
        type = intent.getExtras().getString("type");
        try {
            currentTytpe = Integer.parseInt(type);
        } catch (NumberFormatException e) {
            currentTytpe = 1;
            e.printStackTrace();
        }
//        title.setText("我的位置 —> " + endName+"("+Constants.getCityName()+Constants.getDcityName()+")");
        title.setText("我的位置 —> " + endName);

        try {
            endLa = Integer.valueOf(intent.getExtras().getString("la"));
        } catch (Exception e) {
            endLa = 0;
        }
        try {
            endLt = Integer.valueOf(intent.getExtras().getString("lt"));
        } catch (Exception e) {
            endLt = 0;
        }

        switch (type) {
            case "1":
                mBtnDrive.setChecked(true);
                break;
            case "2":
                mBtnTransit.setChecked(true);
                break;
            case "3":
                mBtnWalk.setChecked(true);
                break;
            case "4":
                mBtnBike.setChecked(true);
                break;
            default:
                mBtnWalk.setChecked(true);
                break;
        }
    }

    /**
     * 给RadioGroup添加监听
     */
    private void initRadioListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                searchButtonProcess(checkedId);
            }
        });
    }

    /**
     * 初始化地图定位相关
     */
    private void initBDMap() {
        //设定模式为普通模式
        currentLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
        //获取传感器服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 初始化地图
        mBaidumap = mMapView.getMap();
        //开启定位图层
        mBaidumap.setMyLocationEnabled(true);
        //定位初始化
        locationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener();
        locationClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        //开启GPS
        option.setOpenGps(true);
        //坐标类型
        option.setCoorType("bd09ll");
        //扫描间隔
        option.setScanSpan(1000);
        locationClient.setLocOption(option);
        locationClient.start();
        mBaidumap.setMyLocationConfiguration(new MyLocationConfiguration(currentLocationMode, true, mCurrentMarker));
        // 地图点击事件处理
        mBaidumap.setOnMapClickListener(this);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        mWebNiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!Constants.getLa().equals("") && !Constants.getLt().equals("") && endLa != 0 && endLt != 0) {
                    showPopupWindow();

                }
            }
        });
    }

    private void openBaiduMap() {
        try {

            if (isInstallByread("com.baidu.BaiduMap")) {
                startBaiduNavi();
                Logger.e("GasStation", "百度地图客户端已经安装");
            } else {
                Logger.e("GasStation", "没有安装百度地图客户端");
                showOpenNaviError("提示", "您尚未安装百度地图客户端，是否前去应用市场安装？", "确定", "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToMarket("com.baidu.BaiduMap");
                    }
                }, new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        iosDialog.dismiss();
                        popupWindow.dismiss();
                    }
                });
            }
        } catch (Exception e) {
            CommonTools.showShortToast(RouteGoPlanActivity.this, "数据出错，请重试");
            e.printStackTrace();
        }
    }

    private void openGaodeMap() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            String data = "androidamap://route?sourceApplication=单耳兔商城&slat=" + locData.latitude + "&slon=" + locData.longitude + "&sname=" + cityName + "&dlat=" + endLa / 1E6 + "&dlon=" + endLt / 1E6 + "&dname=" + endName + "&dev=0&m=0&t=1";
            intent.setData(Uri.parse(data));
            intent.setPackage("com.autonavi.minimap");
            startActivity(intent);
        } catch (Exception e) {
            showOpenNaviError("提示", "您尚未安装高德地图客户端，是否前去应用市场安装？", "确定", "取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToMarket("com.autonavi.minimap");
                }

            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iosDialog.dismiss();
                    popupWindow.dismiss();
                }

            });
            e.printStackTrace();

        }
    }

    private void goToMarket(String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            popupWindow.dismiss();
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            CommonTools.showShortToast(RouteGoPlanActivity.this, "您的手机未安装应用市场或者应用市场不支持调用");
            e.printStackTrace();
        }

    }

    /**
     * 错误dialog
     *
     * @param title
     * @param message
     * @param posiText
     * @param negaText
     * @param posiListener
     * @param negaListener
     */
    private void showOpenNaviError(String title, String message, String posiText, String negaText, View.OnClickListener posiListener, View.OnClickListener negaListener) {
        if (iosDialog == null) {
            iosDialog = new IOSDialog(this);
        }
        iosDialog.setTitle(title);
        iosDialog.setMessage(message);
        iosDialog.setPositiveButton(posiText, posiListener);
        iosDialog.setNegativeButton(negaText, negaListener);
        iosDialog.setCanceledOnTouchOutside(true);
        iosDialog.show();
    }

    /**
     * 选择导航方式popupWindow
     */
    private void showPopupWindow() {
        //设置contentView
        View contentView = LayoutInflater.from(this).inflate(R.layout.popup_route_select_app, null);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(contentView);
        TextView tvSelectBaiduMap = (TextView) contentView.findViewById(R.id.tv_select_baiduMap);
        TextView tvSelectGaodeMap = (TextView) contentView.findViewById(R.id.tv_select_gaodeMap);
        TextView tvSelectCancel = (TextView) contentView.findViewById(R.id.tv_select_cancel);
        View view = contentView.findViewById(R.id.view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        //百度地图
        tvSelectBaiduMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBaiduMap();
            }
        });
        //高德地图
        tvSelectGaodeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGaodeMap();
            }
        });
        tvSelectCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        //显示PopupWindow
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.AnimationBottomFade);
        //得到当前activity的rootView
        View rootView = ((ViewGroup) RouteGoPlanActivity.this.findViewById(android.R.id.content)).getChildAt(0);
        //底部弹出
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //    按返回键的时候收起popupWindow
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (popupWindow != null)
            popupWindow.dismiss();
    }

    /**
     * 判断是否安装了应用
     *
     * @param packageName
     * @return
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    /**
     * 发送我的位置--业务员版
     */
    private void postLocation() {
        if (Constants.isSalesman) {
            postlocation.setVisibility(View.VISIBLE);
        } else {
            postlocation.setVisibility(View.GONE);
        }
        postlocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Thread posThread = new Thread(setGpsRunnable);
                posThread.start();
                try {
                    posThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (result.equals("True")) {
                    Toast.makeText(RouteGoPlanActivity.this, "提交坐标成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RouteGoPlanActivity.this, result, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
//
//    /**
//     * 开启导航
//     */
//    public void startWebNavi() {
//        LatLng start = new LatLng(locData.latitude, locData.longitude);
//        LatLng end = new LatLng(endLa / 1E6, endLt / 1E6);
//        NaviParaOption para = new NaviParaOption()
//                .startPoint(start)
//                .endPoint(end);
//        if (Constants.location != null) {
//            para.startName(Constants.location.getAddrStr());
//        }
//        BaiduMapNavigation.setSupportWebNavi(true);
//        BaiduMapNavigation.openWebBaiduMapNavi(para, this);
//    }

    /**
     * 调用百度地图开启导航
     */
    public void startBaiduNavi() {
        LatLng start = null;
        LatLng end = null;
        //某些情况下，locData会为空，所以需要调用app启动时定位得到的位置参数
        if (locData == null) {
            try {
                start = new LatLng(Double.parseDouble(Constants.getLa()), Double.parseDouble(Constants.getLt()));
            } catch (NumberFormatException e) {
                Toast.makeText(RouteGoPlanActivity.this, "数据错误，请重试", Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }
        } else {
            start = new LatLng(locData.latitude, locData.longitude);
        }
        end = new LatLng(endLa / 1E6, endLt / 1E6);
        //导航参数
        NaviParaOption para = new NaviParaOption()
                .startPoint(start)
                .endPoint(end);
        if (Constants.location != null) {
            para.startName(Constants.location.getAddrStr());
        }
        try {
            switch (currentTytpe) {
                case 1:
                case 2://驾车、公交
                    BaiduMapNavigation.openBaiduMapNavi(para, this);
                    break;
                case 3://步行
                    BaiduMapNavigation.openBaiduMapWalkNavi(para, this);
                    break;
                case 4://骑行
                    BaiduMapNavigation.openBaiduMapBikeNavi(para, this);
                    break;
            }
        } catch (Exception e) {
            showDialog();
            e.printStackTrace();
        }

    }

    /**
     * 提示未安装百度地图app或app版本过低
     */
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您的百度地图app版本过低，点击确认安装最新版本");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OpenClientUtil.getLatestBaiduMapApp(RouteGoPlanActivity.this);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showPopupWindow();
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    /**
     * 发起路线规划搜索
     *
     * @param resId
     */
    public void searchButtonProcess(int resId) {
        // 重置浏览节点的路线数据
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaidumap.clear();
        double startLa = 0;
        double startLt = 0;
        if (locData == null) {
            try {
                startLa = Double.parseDouble(Constants.getLa());
                startLt = Double.parseDouble(Constants.getLt());

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "定位信息有误，请重试。", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            startLa = locData.latitude;
            startLt = locData.longitude;
        }

        LatLng startLatLng = new LatLng(startLa, startLt);
        LatLng endLatLng = new LatLng(endLa / 1E6, endLt / 1E6);
        PlanNode startNode = PlanNode.withLocation(startLatLng);
        PlanNode endNode = PlanNode.withLocation(endLatLng);
        switch (resId) {
            case R.id.drive:
                mSearch.drivingSearch((new DrivingRoutePlanOption()).from(startNode).to(endNode));
                nowSearchType = 1;

                break;
            case R.id.transit:
                mSearch.transitSearch((new TransitRoutePlanOption()).from(startNode).city(Constants.getCityName()).to(endNode).city(cityName));
                nowSearchType = 2;
                break;
            case R.id.walk:
                mSearch.walkingSearch((new WalkingRoutePlanOption()).from(startNode).to(endNode));
                nowSearchType = 3;
                break;
            case R.id.bike:
                mSearch.bikingSearch((new BikingRoutePlanOption()).from(startNode).to(endNode));
                nowSearchType = 4;
                break;
        }

    }

    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = null;

        if (nowSearchType != 0 && nowSearchType != -1) {
            // 非跨城综合交通
            if (route == null || route.getAllStep() == null) {
                return;
            }
            if (nodeIndex == -1 && v.getId() == R.id.pre) {
                return;
            }
            // 设置节点索引
            if (v.getId() == R.id.next) {
                if (nodeIndex < route.getAllStep().size() - 1) {
                    nodeIndex++;
                } else {
                    return;
                }
            } else if (v.getId() == R.id.pre) {
                if (nodeIndex > 0) {
                    nodeIndex--;
                } else {
                    return;
                }
            }
            // 获取节结果信息
            step = route.getAllStep().get(nodeIndex);
            if (step instanceof DrivingRouteLine.DrivingStep) {
                nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
                nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
            } else if (step instanceof WalkingRouteLine.WalkingStep) {
                nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
                nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
            } else if (step instanceof TransitRouteLine.TransitStep) {
                nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
                nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
            } else if (step instanceof BikingRouteLine.BikingStep) {
                nodeLocation = ((BikingRouteLine.BikingStep) step).getEntrance().getLocation();
                nodeTitle = ((BikingRouteLine.BikingStep) step).getInstructions();
            }
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }

        // 移动节点至中心
        mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        popupText = new TextView(RouteGoPlanActivity.this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        mBaidumap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RouteGoPlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            AlertDialog.Builder builder = new AlertDialog.Builder(RouteGoPlanActivity.this);
            builder.setTitle("提示");
            builder.setMessage("检索地址有歧义，请重新设置。\n可通过getSuggestAddrInfo()接口获得建议查询信息");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);

            if (result.getRouteLines().size() > 1) {
                nowResultwalk = result;
                if (!hasShownDialogue) {
                    MyTransitDlg myTransitDlg = new MyTransitDlg(RouteGoPlanActivity.this, result.getRouteLines(), RouteLineAdapter.Type.WALKING_ROUTE);
                    myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShownDialogue = false;
                        }
                    });
                    myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                        public void onItemClick(int position) {
                            route = nowResultwalk.getRouteLines().get(position);
                            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
                            mBaidumap.setOnMarkerClickListener(overlay);
                            routeOverlay = overlay;
                            overlay.setData(nowResultwalk.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }

                    });
                    myTransitDlg.show();
                    hasShownDialogue = true;
                }
            } else if (result.getRouteLines().size() == 1) {
                // 直接显示
                route = result.getRouteLines().get(0);
                WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
                mBaidumap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();

            } else {
                Log.d("route result", "结果数<0");
                return;
            }

        }

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RouteGoPlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);


            if (result.getRouteLines().size() > 1) {
                nowResultransit = result;
                if (!hasShownDialogue) {
                    MyTransitDlg myTransitDlg = new MyTransitDlg(RouteGoPlanActivity.this, result.getRouteLines(), RouteLineAdapter.Type.TRANSIT_ROUTE);
                    myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShownDialogue = false;
                        }
                    });
                    myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                        public void onItemClick(int position) {

                            route = nowResultransit.getRouteLines().get(position);
                            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
                            mBaidumap.setOnMarkerClickListener(overlay);
                            routeOverlay = overlay;
                            overlay.setData(nowResultransit.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }

                    });
                    myTransitDlg.show();
                    hasShownDialogue = true;
                }
            } else if (result.getRouteLines().size() == 1) {
                // 直接显示
                route = result.getRouteLines().get(0);
                TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
                mBaidumap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();

            } else {
                Log.d("route result", "结果数<0");
                return;
            }


        }
    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult result) {

    }


    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RouteGoPlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;


            if (result.getRouteLines().size() > 1) {
                nowResultdrive = result;
                if (!hasShownDialogue) {
                    MyTransitDlg myTransitDlg = new MyTransitDlg(RouteGoPlanActivity.this, result.getRouteLines(), RouteLineAdapter.Type.DRIVING_ROUTE);
                    myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShownDialogue = false;
                        }
                    });
                    myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                        public void onItemClick(int position) {
                            route = nowResultdrive.getRouteLines().get(position);
                            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
                            mBaidumap.setOnMarkerClickListener(overlay);
                            routeOverlay = overlay;
                            overlay.setData(nowResultdrive.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }

                    });
                    myTransitDlg.show();
                    hasShownDialogue = true;
                }
            } else if (result.getRouteLines().size() == 1) {
                route = result.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
                routeOverlay = overlay;
                mBaidumap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                mBtnPre.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);
            } else {
                Log.d("route result", "结果数<0");
                return;
            }

        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult result) {
        Logger.e("result", "result=" + result + ",result.error=" + result.error + "/" + result.getSuggestAddrInfo() + "/");
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RouteGoPlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            AlertDialog.Builder builder = new AlertDialog.Builder(RouteGoPlanActivity.this);
            builder.setTitle("提示");
            builder.setMessage("检索地址有歧义，请重新设置。\n可通过getSuggestAddrInfo()接口获得建议查询信息");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);

            if (result.getRouteLines().size() > 1) {
                nowResultbike = result;
                if (!hasShownDialogue) {
                    MyTransitDlg myTransitDlg = new MyTransitDlg(RouteGoPlanActivity.this, result.getRouteLines(), RouteLineAdapter.Type.DRIVING_ROUTE);
                    myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShownDialogue = false;
                        }
                    });
                    myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                        public void onItemClick(int position) {
                            route = nowResultbike.getRouteLines().get(position);
                            BikingRouteOverlay overlay = new MyBikingRouteOverlay(mBaidumap);
                            mBaidumap.setOnMarkerClickListener(overlay);
                            routeOverlay = overlay;
                            overlay.setData(nowResultbike.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }

                    });
                    myTransitDlg.show();
                    hasShownDialogue = true;
                }
            } else if (result.getRouteLines().size() == 1) {
                route = result.getRouteLines().get(0);
                BikingRouteOverlay overlay = new MyBikingRouteOverlay(mBaidumap);
                routeOverlay = overlay;
                mBaidumap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                mBtnPre.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);
            } else {
                Log.d("route result", "结果数<0");
                return;
            }

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaidumap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    Runnable setGpsRunnable = new Runnable() {

        @Override
        public void run() {
            String mId = db.GetLoginUid(getApplicationContext());
            if (locData.longitude == 0.0 || locData.latitude == 0.0) {
                result = "提交坐标错误，请确认定位成功后再试";
            } else if (mId == null || mId.equals("")) {
                result = "请先登录！";
            } else {
                String gps = locData.longitude + "," + locData.latitude;
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String dm = tm.getDeviceId();
                result = AppManager.getInstance().addGPS("0020", mId, gps, dm);
            }
        }
    };


    // 定制RouteOverly
    //驾车
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    //    步行
    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    //公交
    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    //    骑行
    private class MyBikingRouteOverlay extends BikingRouteOverlay {
        public MyBikingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }


    }


    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        if (isFirstIn) {
            isFirstIn = false;
            switch (currentTytpe) {
                case 1:
                    searchButtonProcess(R.id.drive);
                    break;
                case 2:
                    searchButtonProcess(R.id.transit);
                    break;
                case 3:
                    searchButtonProcess(R.id.walk);
                    break;
                case 4:
                    searchButtonProcess(R.id.bike);
                    break;
                default:
                    searchButtonProcess(R.id.walk);
                    break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        if (mSearch != null) {
            mSearch.destroy();
        }
        locationClient.stop();
        mBaidumap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mBaidumap = null;
        super.onDestroy();
    }

    @Override
    protected void findViewById() {
        title = (Button) findViewById(R.id.b_order_title_back);
        mBtnDrive = ((RadioButton) findViewById(R.id.drive));
        mBtnTransit = ((RadioButton) findViewById(R.id.transit));
        mBtnWalk = ((RadioButton) findViewById(R.id.walk));
        mBtnBike = ((RadioButton) findViewById(R.id.bike));
        radioGroup = ((RadioGroup) findViewById(R.id.rg_map_type));
        mBtnPre = (Button) findViewById(R.id.pre);
        mBtnNext = (Button) findViewById(R.id.next);
        postlocation = (Button) findViewById(R.id.btn_my_location);
        mMapView = (MapView) findViewById(R.id.map);
        mWebNiv = (Button) findViewById(R.id.button2);
        ivDingwei = ((ImageView) findViewById(R.id.dingwei));
    }

    @Override
    protected void initView() {

    }

    // 响应DLg中的List item 点击
    interface OnItemInDlgClickListener {
        public void onItemClick(int position);
    }

    // 供路线选择的Dialog
    class MyTransitDlg extends Dialog {

        private List<? extends RouteLine> mtransitRouteLines;
        private ListView transitRouteList;
        private RouteLineAdapter mTransitAdapter;

        OnItemInDlgClickListener onItemInDlgClickListener;

        public MyTransitDlg(Context context, int theme) {
            super(context, theme);
        }

        public MyTransitDlg(Context context, List<? extends RouteLine> transitRouteLines, RouteLineAdapter.Type
                type) {
            this(context, 0);
            mtransitRouteLines = transitRouteLines;
            mTransitAdapter = new RouteLineAdapter(context, mtransitRouteLines, type);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override
        public void setOnDismissListener(OnDismissListener listener) {
            super.setOnDismissListener(listener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transit_dialog);

            transitRouteList = (ListView) findViewById(R.id.transitList);
            transitRouteList.setAdapter(mTransitAdapter);

            transitRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onItemInDlgClickListener.onItemClick(position);
                    mBtnPre.setVisibility(View.VISIBLE);
                    mBtnNext.setVisibility(View.VISIBLE);
                    dismiss();
                    hasShownDialogue = false;
                }
            });
        }

        public void setOnItemInDlgClickLinster(OnItemInDlgClickListener itemListener) {
            onItemInDlgClickListener = itemListener;
        }

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            mBaidumap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaidumap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
            //获取到当前位置后停止定位以省电
            locationClient.stop();
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }


}