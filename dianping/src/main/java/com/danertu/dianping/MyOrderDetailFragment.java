package com.danertu.dianping;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.danertu.entity.MyOrderData;
import com.danertu.tools.AppManager;
import com.danertu.tools.StatusBarUtil;
import com.danertu.widget.YsnowScrollViewPageOne;
import com.danertu.widget.YsnowScrollViewPageOne.ScrollChangeListener;

/**
 * 客房详细
 */
/**
 * 作者:  Viz
 * 日期:  2018/7/30 12:05
 *
 * 描述： 客房订单详细页面 已弃用
*/
public class MyOrderDetailFragment extends Fragment {
    private MyOrderDetail base = null;
    private View top_bg;
    private ImageButton back;
    private View v;
    private ImageView pro_banner;
    private TextView pro_title;
    //	private SearchTipsGroupView pro_attr;
    private TextView time_start, time_end;
    private TextView tv_hotel_contact;
    private ImageView iv_qr_code;
    private FrameLayout fl_qr_code;
    public static final int REQUEST_QR_CODE = 12;
    /**
     * 1：未支付；2：已支付
     */
    int payState = 0;
    private long firstClick;

    /**
     * 1：未支付；2：已支付
     */
    public void setPayState(int state) {
        this.payState = state;
    }

    private String bannerUrl, proName, buyNum, arrTime, leaveTime, agentMobile;

    public void initOrderBody(String bannerUrl, String proName,
                              String buyNum, String arrTime, String leaveTime,
                              String agentMobile) {
        this.bannerUrl = bannerUrl;
        this.proName = proName;
        this.buyNum = buyNum;
        this.arrTime = arrTime;
        this.leaveTime = leaveTime;
        this.agentMobile = agentMobile;
    }

    private String totalprice, recName, recMobile, outOrderNumber, paymentName, orderCreate;
    private TextView tv_order_num;
    private TextView tv_order_payway;
    private TextView tv_order_createTime;
    private TextView contact_msg;
    private TextView total_price;
    private TextView pro_num;
    private Button toPay;
    private View bg_statusbar;

    public void initOrderHead(String totalprice, String recName,
                              String recMobile, String outOrderNumber, String paymentName,
                              String orderCreate) {
        this.totalprice = totalprice;
        this.recName = recName;
        this.recMobile = recMobile;
        this.outOrderNumber = outOrderNumber;
        this.paymentName = paymentName;
        this.orderCreate = orderCreate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.base = (MyOrderDetail) getActivity();
        handler = new Handler(callback);
        firstClick = System.currentTimeMillis();
        v = inflater.inflate(R.layout.order_hotel, null);
        initView(v);
        return v;
    }

    private void initView(View v) {
        top_bg = v.findViewById(R.id.top_bg);
        bg_statusbar = v.findViewById(R.id.bg_statusbar);
        top_bg.setVisibility(View.VISIBLE);
        final int visiStatus = StatusBarUtil.StatusBarLightMode(base, false) == 0 ? View.VISIBLE : View.GONE;
        bg_statusbar.setVisibility(visiStatus);
        back = (ImageButton) v.findViewById(R.id.back);
        pro_banner = (ImageView) v.findViewById(R.id.pro_banner);
        pro_title = (TextView) v.findViewById(R.id.pro_title);
        time_start = (TextView) v.findViewById(R.id.time_start);
        time_end = (TextView) v.findViewById(R.id.time_end);
        tv_hotel_contact = (TextView) v.findViewById(R.id.tv_hotel_contact);
        tv_order_num = (TextView) v.findViewById(R.id.tv_order_num);
        tv_order_payway = (TextView) v.findViewById(R.id.tv_order_payway);
        tv_order_createTime = (TextView) v.findViewById(R.id.tv_order_createTime);
        total_price = (TextView) v.findViewById(R.id.total_price);
        pro_num = (TextView) v.findViewById(R.id.pro_num);
        contact_msg = (TextView) v.findViewById(R.id.contact_msg);
        toPay = (Button) v.findViewById(R.id.toPay);
        iv_qr_code = ((ImageView) v.findViewById(R.id.iv_qr_code));
        fl_qr_code = ((FrameLayout) v.findViewById(R.id.fl_qr_code));

        YsnowScrollViewPageOne sv_content = (YsnowScrollViewPageOne) v.findViewById(R.id.sv_content);

        final int statusBarHeight = base.getStatusBarHeight();
        if (VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            base.setMargins(back, 0, statusBarHeight, 0, 0);
        View parent = ((View) back.getParent());
        parent.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        top_bg.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, parent.getMeasuredHeight()));
        bg_statusbar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, base.getStatusBarHeight()));
        setTitleBgAlpha(0);

        int visi = payState == 1 ? View.VISIBLE : View.GONE;
        setPayVisibility(visi);
        if (payState == 1) {
            if (!paymentName.contains("到付")) {
                getPayTime = new GetPayTime();
                getPayTime.execute();
            }
        } else if (payState == 2) {
            v.findViewById(R.id.cancel_line).setVisibility(View.VISIBLE);
            TextView cancel = (TextView) v.findViewById(R.id.cancel_book);
            cancel.setVisibility(View.VISIBLE);
            cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String param = "ordernumber|" + outOrderNumber + ",;|memberid" + base.getUid() + ",;price|" + totalprice + ",;isHotel|true";
                    base.jsStartActivity("PayBackActivity", param);
                }
            });
        }

        initBanner = new InitBanner();
        initBanner.execute();
        pro_title.setText(proName);
        tv_order_num.setText("订单编号：" + outOrderNumber);
        tv_order_payway.setText("支付方式：" + paymentName);
        tv_order_createTime.setText("订单生成日期：" + orderCreate.replace("/", "."));
        total_price.setText("￥" + totalprice);
        pro_num.setText(buyNum);

        final String tag_st = "入住日期";
        final String tag_et = "离店日期";
        final int m_size = (int) getResources().getDimension(R.dimen.small_middle_text_size);
        final String t_et = tag_et + "\n" + leaveTime.replace(" ", "\n");
        final String t_st = tag_st + "\n" + arrTime.replace(" ", "\n");
        time_end.setText(genTimeString(t_et, tag_et, m_size));
        time_start.setText(genTimeString(t_st, tag_st, m_size));
        contact_msg.setText(genSizeString(recName + "\n" + recMobile, m_size, 0, recName.length()));

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                base.finish();
            }
        });
        sv_content.setScrollChangeListener(new ScrollChangeListener() {
            private int oid;

            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                setTitleBgAlpha(t);
                boolean isBgWhite = t >= 100;
                int id = isBgWhite ? R.drawable.arrow_back_l_black : R.drawable.arrow_back_l_white;
                if (id != oid) {
                    oid = id;
                    back.setImageResource(id);
                    StatusBarUtil.StatusBarLightMode(base, isBgWhite);
                }
            }
        });
        toPay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                base.selectPayWay();
            }
        });
        tv_hotel_contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                base.dial(agentMobile);
            }
        });

        fl_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickMoreTimesShortTime()) {
                    Intent intent = new Intent(getContext(), QRCodeDetailActivity.class);
                    intent.putExtra("orderNumber", outOrderNumber);
                    startActivityForResult(intent, REQUEST_QR_CODE);
                }
            }
        });
        iv_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickMoreTimesShortTime()) {
                    Intent intent = new Intent(getContext(), QRCodeDetailActivity.class);
                    intent.putExtra("orderNumber", outOrderNumber);
                    startActivityForResult(intent, REQUEST_QR_CODE);
                }
            }
        });
    }

    public void setPayVisibility(int payState) {
        v.findViewById(R.id.bottom).setVisibility(payState);
        v.findViewById(R.id.shadow).setVisibility(payState);
    }

    @SuppressWarnings("deprecation")
    public SpannableStringBuilder genTimeString(String content, String tag, int m_size) {
        final int flags = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;
        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
        ssb.setSpan(new AbsoluteSizeSpan(m_size), tag.length(), content.length(), flags);
        ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.deep_gray)), 0, tag.length(), flags);
        return ssb;
    }

    public SpannableStringBuilder genSizeString(String content, int size, int start, int end) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
        ssb.setSpan(new AbsoluteSizeSpan(size), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    private GetPayTime getPayTime;
    private Timer timer;
    private TimerTask timerTask;

    private class GetPayTime extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... params) {
            int result = 0;
            try {
                result = Integer.parseInt(AppManager.getInstance().postGetOrderPayTime(outOrderNumber));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return result;
//			return 6;//test
        }

        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result <= 0) {
                setPayVisibility(View.GONE);
            } else {
                timer = new Timer();
                timerTask = new TimerTask() {
                    int count = result;

                    public void run() {
                        if (count > 0) {
                            int h = count / 60;
                            int m = count % 60;
                            String t = h > 0 ? h + "小时" + m + "分" : m + "分";
                            String value = "支付 (还剩" + t + ")";
                            handler.sendMessage(base.getMessage(1, value));
                        } else {
                            handler.sendEmptyMessage(0);
                        }
                        count--;
                    }
                };
                timer.schedule(timerTask, 0, 1000 * 60);
            }
        }

    }

    private Handler handler;
    public Handler.Callback callback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                String value = msg.obj.toString();
                toPay.setText(genSizeString(value, (int) getResources().getDimension(R.dimen.micro_text_size), 3, value.length()));
            } else {
                setPayVisibility(View.GONE);
                MyOrderData.cancelOrder(outOrderNumber);
                MyOrderActivity.isCurrentPage = false;
                base.jsShowMsg("订单已被取消！");
                timerTask.cancel();
            }
            return true;
        }
    };

    private InitBanner initBanner;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (initBanner != null && !initBanner.isCancelled()) initBanner.cancel(true);
        if (getPayTime != null && !getPayTime.isCancelled()) getPayTime.cancel(true);
        if (timerTask != null) timerTask.cancel();
        if (timer != null) timer.cancel();
        if (handler != null) handler.removeCallbacksAndMessages(null);
    }

    private void setTitleBgAlpha(int alpha) {
        if (base.getAndroidVersion() >= 11) {
            float a = (float) alpha / 255;
            top_bg.setAlpha(a);
            bg_statusbar.setAlpha(a);
        }
    }

    private class InitBanner extends AsyncTask<Void, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap banner = null;
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();

                //设为true，那么BitmapFactory.decodeFile(String path, Options opt)
                //并不会真的返回一个Bitmap给你，它仅仅会把它的宽，高取回来给你
                options.inJustDecodeBounds = false;
                HttpURLConnection url = (HttpURLConnection) new URL(bannerUrl).openConnection();
                if (url.getResponseCode() == 200) {
                    InputStream in = url.getInputStream();
                    banner = BitmapFactory.decodeStream(in, null, options);
                    banner = base.zoomBitmap(banner, base.getScreenWidth(), base.getScreenWidth());
                    in.close();
                }
                url.disconnect();
            } catch (Error | IOException e) {
                e.printStackTrace();
            }
            return banner;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {
                pro_banner.setImageBitmap(result);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_QR_CODE:
                //从二维码界面返回来，刷新界面

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 为解决点击多次打开多个页面问题添加
     * huangyeliang
     *
     * @param secondClick 点击时的毫秒数
     */
    public boolean isClickMoreTimesShortTime(long secondClick) {
        if (secondClick - firstClick > 800) {
            firstClick = secondClick;
            return true;
        } else {
            return false;
        }
    }

    public boolean isClickMoreTimesShortTime() {
        return isClickMoreTimesShortTime(System.currentTimeMillis());
    }

}
