package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.config.Constants;
import com.danertu.tools.AppManager;
import com.danertu.widget.CommonTools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.danertu.db.DBManager;
import com.danertu.widget.XListView;
import com.danertu.widget.XListView.IXListViewListener;

/**
 * 2017年7月26日
 * huangyeliang
 * MyAdapter添加ViewHolder类实现item复用
 * shang
 */
public class ProductCommentActivity extends Activity implements
        IXListViewListener {
    private XListView mListView;
    private TextView noComment;
    private Handler mHandler;
    private MyAdapter adapter;
    private int pageIndex; // 当前页数
    private Button addButton;
    String productGuid = "";
    String shopID = "";
    int allCount = 0;
    String couldComment = "false";
    String uid = "";

    private void initTitle(String title, String opera) {
        Button b_title = (Button) findViewById(R.id.b_title_back2);
        addButton = (Button) findViewById(R.id.b_title_operation2);
        addButton.setText(opera);
        b_title.setText(title);
        b_title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_comment);
        initTitle("商品评论", "写评论");
        productGuid = getIntent().getExtras().getString("proGuid");
        shopID = getIntent().getExtras().getString("shopid");
        noComment = (TextView) findViewById(R.id.noComment);

        mListView = (XListView) findViewById(R.id.comment_xListView);
        mListView.setPullLoadEnable(true);// 设置让它上拉，FALSE为不让上拉，便不加载更多数据
        mListView.setPullRefreshEnable(false);
        mHandler = new Handler();
        Thread commentThread = new Thread(commentRunnable);
        commentThread.start();
        try {
            commentThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adapter = new MyAdapter();
        mListView.setAdapter(adapter);
        mListView.setXListViewListener(this);

        comCounThread.start();
        try {
            comCounThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//		addButton = (Button) findViewById(R.id.addComment);
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DBManager db = DBManager.getInstance();
                uid = db.GetLoginUid(ProductCommentActivity.this);
                if (uid == null || uid.equals("")) {
                    CommonTools.showShortToast(ProductCommentActivity.this, "请先登录");
                    Intent toLogin = new Intent(ProductCommentActivity.this, LoginActivity.class);
                    startActivity(toLogin);
                    finish();
                } else {
                    couldThread.start();
                    try {
                        couldThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (couldComment.equals("true")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("proGuid", productGuid);
                        bundle.putString("loginID", uid);
                        bundle.putString("agentID", shopID);
                        Intent intent = new Intent();
                        intent.putExtras(bundle);
                        intent.setClassName(ProductCommentActivity.this, "com.danertu.dianping.AddCommentActivity");
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ProductCommentActivity.this, "亲，您还没有购买过该产品哦！", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    private ArrayList<HashMap<String, Object>> data = new ArrayList<>();

    Runnable commentRunnable = new Runnable() {
        @Override
        public void run() {

            String result = AppManager.getInstance().getProductComment("0066", productGuid, Constants.pageSize, 1);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result).getJSONObject("procommentList");
                JSONArray jsonArray = jsonObject.getJSONArray("procommentbean");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject oj = jsonArray.getJSONObject(i);
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("comGuid", oj.getString("Guid")); // 商品品论表的guid
                    item.put("proGuid", oj.getString("ProductGuid")); // 商品guid
                    item.put("loginID", oj.getString("MemLoginID"));
                    item.put("sendTime", oj.getString("SendTime"));
                    item.put("rank", oj.getString("Rank"));
                    item.put("content", oj.getString("Content"));
                    data.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (data.size() == 0) {
                noComment.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
            }
            SystemClock.sleep(1000);
        }
    };

    private class MyAdapter extends BaseAdapter {

        public Object getItem(int position) {
            return data.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public int getCount() {
            return data.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(ProductCommentActivity.this).inflate(R.layout.comment_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = ((ViewHolder) convertView.getTag());
            }
            viewHolder.memLoginID.setText(data.get(position).get("loginID").toString());
            viewHolder.sendTime.setText(data.get(position).get("sendTime").toString());
            viewHolder.content.setText(data.get(position).get("content").toString());
            return convertView;
        }

        class ViewHolder {
            TextView memLoginID;
            TextView sendTime;
            TextView content;

            public ViewHolder(View view) {
                memLoginID = (TextView) view.findViewById(R.id.com_id);
                sendTime = (TextView) view.findViewById(R.id.com_time);
                content = (TextView) view.findViewById(R.id.com_content);
            }
        }
    }

    Runnable commentMoreRunnable = new Runnable() {
        @Override
        public void run() {

            String result = AppManager.getInstance().getProductComment("0066", productGuid, Constants.pageSize, pageIndex);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result).getJSONObject("procommentList");
                JSONArray jsonArray = jsonObject.getJSONArray("procommentbean");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject oj = jsonArray.getJSONObject(i);
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("comGuid", oj.getString("Guid")); // 商品品论表的guid
                    item.put("proGuid", oj.getString("ProductGuid")); // 商品guid
                    item.put("loginID", oj.getString("MemLoginID"));
                    item.put("sendTime", oj.getString("SendTime"));
                    item.put("rank", oj.getString("Rank"));
                    item.put("content", oj.getString("Content"));
                    data.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (data.size() == 0) {
                noComment.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
            }
            SystemClock.sleep(1000);
        }
    };

    private void loadMoreDate() {
        int count = adapter.getCount();
        if (count < allCount) {
            if (count + 10 < 999) {
                // 每次加载5条
                // for (int i = count; i < count + 10; i++) {
                pageIndex = (count + 10) / 10;
                if (pageIndex != 1) {
                    // new Thread(moresendable).start();
                    Thread moreCommentThread = new Thread(commentMoreRunnable);
                    moreCommentThread.start();
                    try {
                        moreCommentThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    // 数据已经不足5条
                    for (int i = count; i < 999; i++) {
                        // tGetMoreShopList.start();
                        // new Thread(moresendable).start();
                        Thread moreCommentThread = new Thread(commentMoreRunnable);
                        moreCommentThread.start();
                        try {
                            moreCommentThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private ArrayList<HashMap<String, Object>> getData() {
        Thread threadComment = new Thread(commentRunnable);
        threadComment.start();
        try {
            threadComment.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // new Thread(sendable).start();
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
                mListView.setAdapter(adapter);
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
                // getmoreData();
                loadMoreDate();
                adapter.notifyDataSetChanged();
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
        Double wwc = 0.9296;// 换算百度精准倍率
        return (int) (Math.sqrt((poA * poA) + (poB * poB)) / 10 / wwc);
    }

    public static double computeDistance(double lat1, double lon1, double lat2,
                                         double lon2) {
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

    Thread comCounThread = new Thread(new Runnable() {

        @Override
        public void run() {
            String comCount = AppManager.getInstance().getCommentCount("0068", productGuid);
            try {
                if (!TextUtils.isEmpty(comCount))
                    allCount = Integer.parseInt(comCount);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    });

    Thread couldThread = new Thread(new Runnable() {

        @Override
        public void run() {
            couldComment = AppManager.getInstance().couldComment("0069", productGuid, uid);
        }
    });
}
