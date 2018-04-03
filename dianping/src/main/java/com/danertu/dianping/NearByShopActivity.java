package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.widget.XListView;
import com.danertu.widget.XListView.IXListViewListener;
import com.danertu.tools.AppManager;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 2017年7月24日
 * 新增adapter的holder类，实现listview的item复用
 *
 * @link ViewHolder
 */
public class NearByShopActivity extends Activity implements IXListViewListener {
    private XListView mListView;
    private NearShopAdapter shopAdapter;
    private Handler mHandler;

    private TextView goBack;

    private int pageIndex; // 当前页数

    private void initTitle(String string) {
        Button b_title = (Button) findViewById(R.id.b_order_title_back);
        b_title.setText(string);
        b_title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_shop);
        /**
         * 下拉刷新，上拉加载
         */
        initTitle("附近店铺");
        mListView = (XListView) findViewById(R.id.techan_xListView);// 这个listview是在这个layout里面
        mListView.setPullLoadEnable(true);// 设置让它上拉，FALSE为不让上拉，便不加载更多数据
        /*shopAdapter = new SimpleAdapter(NearByShopActivity.this, getData(),
                R.layout.expandnearby_item, new String[] {"imgUrl","shopname", "mobile",58
						"adress","juli" }, new int[] {R.id.ivIcon,R.id.tvName2, R.id.tvTel,
						R.id.tvadress,R.id.juli });*/
        Thread thread = new Thread(sendAble);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        shopAdapter = new NearShopAdapter(this);
        mListView.setAdapter(shopAdapter);
//		shopAdapter.notifyDataSetChanged();
//		onLoad();

        mListView.setXListViewListener(this);
        mHandler = new Handler();
//        mListView.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//                                    long arg3) {
//
//                Map<String, Object> item = (Map<String, Object>) arg0.getItemAtPosition(arg2);
//                String shopid = String.valueOf(item.get("shopid"));
//                Bundle bundle = new Bundle();
//                bundle.putString("shopid", shopid);
//                Intent intent = new Intent();
//                intent.putExtras(bundle);
//                intent.setClassName(getApplicationContext(), "com.danertu.dianping.DetailActivity");
//                startActivity(intent);
//            }
//        });
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> item = (Map<String, Object>) parent.getItemAtPosition(position);
                String shopid = String.valueOf(item.get("shopid"));
                Bundle bundle = new Bundle();
                bundle.putString("shopid", shopid);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClassName(getApplicationContext(), "com.danertu.dianping.DetailActivity");
                startActivity(intent);
            }
        });

// add by xhx
//		goBack=(TextView)findViewById(R.id.goBack);
//		goBack.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
// add end
    }

    /**
     * 初始化本地数据
     */
//	String data[] = new String[] { "三块石国家森林公园", "关山湖国家水利风景区", "小鹿沟青龙寺景区",
//			"天女山风景区", "后安腰堡采摘园" };
//	String data1[] = new String[] { "抚顺县救兵乡王木村", "抚顺县救兵乡王木村", "抚顺县救兵乡王木村",
//			"抚顺县救兵乡王木村", "抚顺县救兵乡王木村" };

    private ArrayList<HashMap<String, Object>> data = new ArrayList<>();

    Runnable sendAble = new Runnable() {
        @Override
        public void run() {
            String result = AppManager.getInstance().postGetNearByShopList("0037", Constants.getCityName(), Constants.pagesize, 1, "", "");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result).getJSONObject("shoplist");
                JSONArray jsonArray = jsonObject.getJSONArray("shopbean");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject oj = jsonArray.getJSONObject(i);
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("shopname", oj.getString("s"));
                    item.put("img", oj.getString("e"));
                    item.put("mobile", oj.getString("m"));
                    if (oj.getString("w").indexOf("市") > 0) {
                        item.put("adress", oj.getString("w").substring(oj.getString("w").indexOf("市") + 1));
                    } else {
                        item.put("adress", oj.getString("w"));
                    }
                    item.put("jyfw", oj.getString("jyfw"));
                    item.put("shopid", oj.getString("id"));
                    item.put("la", oj.getString("la"));
                    item.put("la", oj.getString("lt"));
                    item.put("juli", getJuli(Double.valueOf(Constants.getLa().substring(0, Constants.getLa().indexOf(".") + 7)), Double.valueOf(Constants.getLt().substring(0, Constants.getLt().indexOf(".") + 7)), Double.valueOf(Double.valueOf(oj.getString("la")) / 1000000), Double.valueOf(Double.valueOf(oj.getString("lt")) / 1000000)) + "米");

                    data.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            SystemClock.sleep(1000);
        }
    };


    private class NearShopAdapter extends BaseAdapter {
        // 自己定义的构造函数
        private LayoutInflater mInflater;
        private Context mContext = null;

        public NearShopAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        public Object getItem(int arg0) {
            return data.get(arg0);
        }

        public long getItemId(int position) {
            return position;
        }

        public int getCount() {
            return data.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.expandnearby_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = ((ViewHolder) convertView.getTag());
            }
            String imgName = data.get(position).get("img").toString();
            String shopid = data.get(position).get("shopid").toString();
            String imgUrl = ActivityUtils.getImgUrl(imgName, shopid, null);
            viewHolder.shopName.setText(data.get(position).get("shopname").toString());
            viewHolder.address.setText(data.get(position).get("adress").toString());
            viewHolder.jy.setText(data.get(position).get("jyfw").toString());
            viewHolder.juli.setText(data.get(position).get("juli").toString());
            ImageLoader.getInstance().displayImage(imgUrl, viewHolder.imageView);
            return convertView;
        }

        /**
         * 2017年7月24日
         * 黄业良
         * 添加ViewHolder类，并修改getView，使listview实现item复用
         */
        class ViewHolder {
            ImageView imageView;
            TextView shopName;
            TextView address;
            TextView jy;
            TextView juli;

            public ViewHolder(View view) {
                imageView = (ImageView) view.findViewById(R.id.ivIcon);
                shopName = (TextView) view.findViewById(R.id.index_type_shopName);
                address = (TextView) view.findViewById(R.id.index_type_address);
                jy = (TextView) view.findViewById(R.id.index_type_jyfw);
                juli = (TextView) view.findViewById(R.id.index_type_juli);
            }
        }
    }

    private void loadMoreDate() {
        int count = shopAdapter.getCount();
        if (count + 10 < 999) {
            // 每次加载5条
            // for (int i = count; i < count + 10; i++) {
            pageIndex = (count + 10) / 10;
            //	new Thread(moresendable).start();
            Thread threadCHI = new Thread(moresendable);
            threadCHI.start();
            try {
                threadCHI.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // 数据已经不足5条
            for (int i = count; i < 999; i++) {

                // tGetMoreShopList.start();
                //		new Thread(moresendable).start();
                Thread threadCHI = new Thread(moresendable);
                threadCHI.start();
                try {
                    threadCHI.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    Runnable moresendable = new Runnable() {
        @Override
        public void run() {
            String result = AppManager.getInstance().postGetNearByShopList("0037", Constants.getCityName(), Constants.pagesize, pageIndex, "", "");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result).getJSONObject("shoplist");
                JSONArray jsonArray = jsonObject.getJSONArray("shopbean");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject oj = jsonArray.getJSONObject(i);
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("shopname", oj.getString("s"));
                    item.put("img", oj.getString("e"));
                    item.put("mobile", oj.getString("m"));
                    if (oj.getString("w").indexOf("市") > 0) {
                        item.put("adress", oj.getString("w").substring(oj.getString("w").indexOf("市") + 1));
                    } else {
                        item.put("adress", oj.getString("w"));
                    }

                    item.put("jyfw", oj.getString("jyfw"));
                    item.put("shopid", oj.getString("id"));
                    item.put("juli", getJuli(Double.valueOf(Constants.getLa().substring(0, Constants.getLa().indexOf(".") + 7)), Double.valueOf(Constants.getLt().substring(0, Constants.getLt().indexOf(".") + 7)), Double.valueOf(Double.valueOf(oj.getString("la")) / 1000000), Double.valueOf(Double.valueOf(oj.getString("lt")) / 1000000)) + "米");

                    data.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            SystemClock.sleep(1000);
        }
    };

    private ArrayList<HashMap<String, Object>> getData() {
        Thread threadCHI = new Thread(sendAble);
        threadCHI.start();
        try {
            threadCHI.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //	new Thread(sendable).start();
        return data;
    }

    private ArrayList<HashMap<String, Object>> getmoreData() {
        new Thread(moresendable).start();
        return data;
    }

    /**
     * 停止刷新，
     */
    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime("刚刚");
    }

    // 刷新
    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                data = new ArrayList<>();
                getData();
                mListView.setAdapter(shopAdapter);
                onLoad();
            }
        }, 2000);
    }

    // 加载更多
    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                //	getmoreData();
                loadMoreDate();
                shopAdapter.notifyDataSetChanged();
                onLoad();
            }
        }, 2000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.finish();
        }
        return false;
    }


    public static Integer getJuli(double lat1, double lon1, double lat2, double lon2) {
        int wuce = 1000000;
        Double poA = Math.abs(lat1 * wuce - lat2 * wuce);
        Double poB = Math.abs(lon1 * wuce - lon2 * wuce);
        Double wwc = 0.9296;//换算百度精准倍率
//        return Integer.valueOf((int) (Math.sqrt((poA * poA) + (poB * poB)) / 10 / wwc));
        return (int) (Math.sqrt((poA * poA) + (poB * poB)) / 10 / wwc);
    }

    public static double computeDistance(double lat1, double lon1, double lat2, double lon2) {
        // Based on http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf
        // using the "Inverse Formula" (section 4)

        int MAXITERS = 20;
        // Convert lat/long to radians
        lat1 *= Math.PI / 180.0;
        lat2 *= Math.PI / 180.0;
        lon1 *= Math.PI / 180.0;
        lon2 *= Math.PI / 180.0;

        double a = 6378137.0; // WGS84 major axis
        double b = 6356752.3142; // WGS84 semi-major axis
        double f = (a - b) / a;
        double aSqMinusBSqOverBSq = (a * a - b * b) / (b * b);

        double L = lon2 - lon1;
        double A = 0.0;
        double U1 = Math.atan((1.0 - f) * Math.tan(lat1));
        double U2 = Math.atan((1.0 - f) * Math.tan(lat2));

        double cosU1 = Math.cos(U1);
        double cosU2 = Math.cos(U2);
        double sinU1 = Math.sin(U1);
        double sinU2 = Math.sin(U2);
        double cosU1cosU2 = cosU1 * cosU2;
        double sinU1sinU2 = sinU1 * sinU2;

        double sigma = 0.0;
        double deltaSigma = 0.0;
        double cosSqAlpha = 0.0;
        double cos2SM = 0.0;
        double cosSigma = 0.0;
        double sinSigma = 0.0;
        double cosLambda = 0.0;
        double sinLambda = 0.0;

        double lambda = L; // initial guess
        for (int iter = 0; iter < MAXITERS; iter++) {
            double lambdaOrig = lambda;
            cosLambda = Math.cos(lambda);
            sinLambda = Math.sin(lambda);
            double t1 = cosU2 * sinLambda;
            double t2 = cosU1 * sinU2 - sinU1 * cosU2 * cosLambda;
            double sinSqSigma = t1 * t1 + t2 * t2; // (14)
            sinSigma = Math.sqrt(sinSqSigma);
            cosSigma = sinU1sinU2 + cosU1cosU2 * cosLambda; // (15)
            sigma = Math.atan2(sinSigma, cosSigma); // (16)
            double sinAlpha = (sinSigma == 0) ? 0.0 : cosU1cosU2 * sinLambda / sinSigma; // (17)
            cosSqAlpha = 1.0 - sinAlpha * sinAlpha;
            cos2SM = (cosSqAlpha == 0) ? 0.0 : cosSigma - 2.0 * sinU1sinU2 / cosSqAlpha; // (18)

            double uSquared = cosSqAlpha * aSqMinusBSqOverBSq; // defn
            A = 1 + (uSquared / 16384.0) * (4096.0 + uSquared * (-768 + uSquared * (320.0 - 175.0 * uSquared)));// (3)
            double B = (uSquared / 1024.0) * (256.0 + uSquared * (-128.0 + uSquared * (74.0 - 47.0 * uSquared)));// (4)
            double C = (f / 16.0) * cosSqAlpha * (4.0 + f * (4.0 - 3.0 * cosSqAlpha)); // (10)
            double cos2SMSq = cos2SM * cos2SM;
            deltaSigma = B * sinSigma * (cos2SM + (B / 4.0) * (cosSigma * (-1.0 + 2.0 * cos2SMSq) - (B / 6.0) * cos2SM * (-3.0 + 4.0 * sinSigma * sinSigma) * (-3.0 + 4.0 * cos2SMSq)));// (6)
            lambda = L + (1.0 - C) * f * sinAlpha * (sigma + C * sinSigma * (cos2SM + C * cosSigma * (-1.0 + 2.0 * cos2SM * cos2SM))); // (11)
            double delta = (lambda - lambdaOrig) / lambda;
            if (Math.abs(delta) < 1.0e-12) {
                break;
            }
        }

        return b * A * (sigma - deltaSigma);
    }

}
