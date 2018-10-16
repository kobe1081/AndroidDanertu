package com.danertu.dianping;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.dianping.sign.CalendarAdapter;
import com.danertu.dianping.sign.Seference;
import com.danertu.tools.AppManager;
import com.danertu.tools.Logger;

public class CalendarActivity extends BaseActivity implements OnGestureListener, OnClickListener {

    private GestureDetector gestureDetector = null;
    private CalendarAdapter calV = null;
    private GridView gridView = null;
    private TextView topText = null;
    private TextView signText;
    private LinearLayout btn_prev_month;
    private LinearLayout btn_next_month;
    private int jumpMonth = 0; // 每次滑动，增加或减去一个月,默认为0（即显示当前月）
    private int jumpYear = 0; // 滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
    private int year_c = 0;
    private int month_c = 0;
    private int day_c = 0;
    private String currentDate = "";
    private Bundle bd = null;// 发送参数
    private Bundle bun = null;// 接收参数
    private String ruzhuTime;
    private String lidianTime;
    private String state = "";

    private String MID = "", DATE = "", GetResult = "";
    //    private DBManager db = new DBManager();

    boolean isSignToday = false;// 今天是否签到了
    Seference seference;
    String dateStr = "";// 今天的日期
    private String result;
    boolean isSetResult = false;

    public CalendarActivity() {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        currentDate = sdf.format(date); // 当期日期
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);

    }

    public void initTitle(String title) {
        Button b_title = (Button) findViewById(R.id.b_order_title_back);
        b_title.setText(title);
        b_title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    private Handler.Callback callBack = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            initView();
            return true;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
//        db=DBManager.getInstance();
        initTitle("签到");

        handler = new Handler(callBack);
        showLoadDialog();
        seference = new Seference(this);
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
        dateStr = format.format(date);

        // 判断有没有登录客户
        if (isLogined()) {
            GetThread.start();
        } else {
            jsShowMsg("请先登录");
            finish();
        }

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // int gvFlag = 0; //每次添加gridview到viewflipper中时给的标记
        if (e1.getX() - e2.getX() > 120) {
            // 像左滑动

            forwardNextMonth();
            return true;
        } else if (e1.getX() - e2.getX() < -120) {
            // 向右滑动

            forwardLastMonth();
            return true;
        }
        return false;
    }

    /**
     * 下一个月
     */
    public void forwardNextMonth() {
        jumpMonth++; // 下一个月

        calV = new CalendarAdapter(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
        gridView.setAdapter(calV);
        addTextToTopTextView(topText);

    }

    /**
     * 上一个月
     */
    public void forwardLastMonth() {
        jumpMonth--; // 上一个月

        calV = new CalendarAdapter(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
        gridView.setAdapter(calV);

        addTextToTopTextView(topText);
    }

    // 从服务器中取出客户签到信息
    private Thread GetSignTodayThread = new Thread(new Runnable() {

        @Override
        public void run() {
            try {
                result = AppManager.getInstance().judgeSignIn("0050", MID, DATE);
                judgeIsTokenException(result, "您的登录信息已过期，请重新登录", -1);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("test", "CalendarActivity Thread GetSignTodayThread 错误：" + e.toString());
            }
        }
    });


    /**
     * 显示今天是否签到了
     */
    private void sign() {

//		String isSign = seference.getPreferenceData(seference.signFileName,
//				dateStr, "");
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd");
        DATE = sDateFormat.format(new java.util.Date());
        GetSignTodayThread.start();
        try {
            GetSignTodayThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //	if (!TextUtils.isEmpty(isSign)) {// 已经签到了
        if (result.equals("true")) {
            signText.setText("已签到");
            isSignToday = true;
        } else {
            signText.setText("点击签到");
            isSignToday = false;
        }
        // 点击签到
        signText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isSignToday) {
                    jsShowMsg("今天已经签到了哦");
                } else {

                    if (isLogined()) {
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DATE = sDateFormat.format(new java.util.Date());
                        MID = getUid();
                        db.InsertRegistrationForm(CalendarActivity.this, MID, DATE);

                        InsertThread.start();

                        isSignToday = true;
                        seference.savePreferenceData(seference.signFileName, dateStr, true + "");
                        jsShowMsg("今天签到成功");
                        signText.setText("已签到");
                        if (calV != null) {
                            calV = new CalendarAdapter(CalendarActivity.this,
                                    getResources(), jumpMonth, jumpYear,
                                    year_c, month_c, day_c);
                            gridView.setAdapter(calV);
                        }

                    } else {
                        jsShowMsg("请先登录");
                    }
                }
            }
        });
    }

    // 将签到添加到服务器
    private Thread InsertThread = new Thread(new Runnable() {

        @Override
        public void run() {
            try {
                // false代表传入日期未签到
                String json = AppManager.getInstance().insertSignIn("0051", MID);
                judgeIsTokenException(json, "您的登录信息已过期，请重新登录", -1);
                isSetResult = true;
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("test", "CalendarActivity Thread InsertThread 错误：" + e.toString());
            }
        }
    });

    public void finish() {
        if (isSetResult)
            setResult(1);
        super.finish();
    }

    /**
     * 向服务器请求当前账户的签到记录
     */
    private Thread GetThread = new Thread(new Runnable() {
        public void run() {
            try {
                MID = getUid();
                // 查询这个账户签到时间
                Cursor c = db.selectRegistrationForm(getContext(), MID);
                if (c.getCount() <= 0) {
                    seference.clearPreData(seference.signFileName);
                }
                while (c.moveToNext()) {
                    String historyDate = c.getString(1).replace("/", "-");
                    seference.savePreferenceData(seference.signFileName, historyDate, true + "");
                }
                c.close();
                GetResult = AppManager.getInstance().getSignIn("0052", MID);
                BindData();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sendMessage(1);
        }
    });

    private void BindData() {
        if (GetResult == null) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(GetResult).getJSONObject("signdateList");
            JSONArray jsonArray = jsonObject.getJSONArray("signdatebean");
            for (int i = jsonArray.length(); i > 0; i--) {
                String signdate = "";
                JSONObject oj = jsonArray.getJSONObject(i - 1);
                signdate = oj.getString("singdate").split(" ")[0].replace("/", "-");
                seference.savePreferenceData(seference.signFileName, signdate, true + "");
            }
        } catch (JSONException e) {
            judgeIsTokenException(GetResult, "您的登录信息已过期，请重新登录", -1);
            if (Constants.isDebug)
                e.printStackTrace();
            Logger.e("test", "CalendarActivity Thread bindData 错误：" + e.toString());
        }

    }

    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, Menu.FIRST, Menu.FIRST, "今天");
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 选择菜单
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST:
                // 跳转到今天
//                int xMonth = jumpMonth;
//                int xYear = jumpYear;
                int gvFlag = 0;
                jumpMonth = 0;
                jumpYear = 0;
                year_c = Integer.parseInt(currentDate.split("-")[0]);
                month_c = Integer.parseInt(currentDate.split("-")[1]);
                day_c = Integer.parseInt(currentDate.split("-")[2]);
                calV = new CalendarAdapter(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
                gridView.setAdapter(calV);
                addTextToTopTextView(topText);
                gvFlag++;
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return this.gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    // 添加头部的年份 闰哪月等信息
    public void addTextToTopTextView(TextView view) {
        StringBuffer textDate = new StringBuffer();
        textDate.append(calV.getShowYear()).append("年")
                .append(calV.getShowMonth()).append("月").append("\t");
        view.setText(textDate);
        view.setTextColor(Color.WHITE);
        view.setTypeface(Typeface.DEFAULT_BOLD);
    }

    // 添加gridview
    private void addGridView() {

        gridView = (GridView) findViewById(R.id.gridview);

        gridView.setOnTouchListener(new OnTouchListener() {
            // 将gridview中的触摸事件回传给gestureDetector
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return CalendarActivity.this.gestureDetector
                        .onTouchEvent(event);
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener() {
            // gridView中的每一个item的点击事件

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // 点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
                int startPosition = calV.getStartPositon();
                int endPosition = calV.getEndPosition();
                if (startPosition <= position + 7
                        && position <= endPosition - 7) {
                    String scheduleDay = calV.getDateByClickItem(position)
                            .split("\\.")[0]; // 这一天的阳历
                    // String scheduleLunarDay =
                    // calV.getDateByClickItem(position).split("\\.")[1];
                    // //这一天的阴历
//                    String scheduleYear = calV.getShowYear();
                    String scheduleMonth = calV.getShowMonth();
                    // Toast.makeText(CalendarActivity.this,
                    // scheduleYear+"-"+scheduleMonth+"-"+scheduleDay,
                    // 2000).show();
                    ruzhuTime = scheduleMonth + "月" + scheduleDay + "日";
                    lidianTime = scheduleMonth + "月" + scheduleDay + "日";
//                    Intent intent = new Intent();
                    if (state.equals("ruzhu")) {

                        bd.putString("ruzhu", ruzhuTime);
                        System.out.println("ruzhuuuuuu" + bd.getString("ruzhu"));
                    } else if (state.equals("lidian")) {

                        bd.putString("lidian", lidianTime);
                    }
                    // intent.setClass(CalendarActivity.this,
                    // HotelActivity.class);
                    // intent.putExtras(bd);
                    // startActivity(intent);
                    // finish();
                }
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_prev_month:
                forwardLastMonth();
                break;

            case R.id.btn_next_month:
                forwardNextMonth();
                break;
        }
    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void initView() {
        hideLoadDialog();
        signText = (TextView) findViewById(R.id.signText);

        btn_prev_month = (LinearLayout) findViewById(R.id.btn_prev_month);
        btn_next_month = (LinearLayout) findViewById(R.id.btn_next_month);

        btn_prev_month.setOnClickListener(this);
        btn_next_month.setOnClickListener(this);
        sign();

        bd = new Bundle();// out

        bun = getIntent().getExtras();// in
        state = bun != null ? bun.getString("state") : "";

        gestureDetector = new GestureDetector(this);
        addGridView();

        calV = new CalendarAdapter(this, getResources(), jumpMonth, jumpYear,
                year_c, month_c, day_c);
        gridView.setAdapter(calV);

        topText = (TextView) findViewById(R.id.tv_month);
        addTextToTopTextView(topText);
    }
}