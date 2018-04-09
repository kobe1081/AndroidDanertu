package com.danertu.dianping;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.danertu.entity.MyOrderData;
import com.danertu.tools.AppManager;
import com.danertu.tools.MyDialog;


public class PayBackActivity extends BaseActivity {

    private TextView reason, paybackprice;
    private EditText backRemark;
    private Button submit;

    private Dialog popupWindow;
    String ordernumber, memberid;
    private final String TITLE = "退款申请";
    private boolean isHotel = false;
    private String totalPrice = null;

    public String getOrdernumber() {
        return ordernumber;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public final String TAG_REASON = "reason";
    public final String TAG_REASON_CONFIRM = "reasonConfirm";
    public static final int RESULT_PAY_BACK=323;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_back);
        handler = new Handler(callback);
        Bundle b = getIntent().getExtras();
        ordernumber = b.getString("ordernumber"); // 获取传过来的订单ID
        memberid = b.getString("memberid"); // 获取传过来的用户ID
        totalPrice = b.getString("price");
        String tHotel = b.getString("isHotel", "false");
        isHotel = Boolean.parseBoolean(tHotel);

        if (!isHotel) {
            initTitle();
            findViewById();
            initView();
        } else {
            findViewById(R.id.paybackmain).setVisibility(View.GONE);
            addFragment(new PayBackFragment(), TAG_REASON);
        }
    }

    public void addFragment(Fragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.container, fragment, tag);
        transaction.commit();
    }

    private void initTitle() {
        TextView title = (TextView) findViewById(R.id.tv_title4);
        title.setText(TITLE);
        findViewById(R.id.b_title_back4).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    protected void findViewById() {
        reason = (TextView) this.findViewById(R.id.reason);
        paybackprice = (TextView) this.findViewById(R.id.d3);
        backRemark = (EditText) this.findViewById(R.id.editText1);
        submit = (Button) this.findViewById(R.id.button2);
    }

    String payBackResult = null;

    protected void initView() {
        popupWindow = MyDialog.getDefineDialog(this, R.layout.dialog_payback_reason);
        LinearLayout reasons = (LinearLayout) popupWindow.findViewById(R.id.ll_reasons);
        int count = reasons.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = reasons.getChildAt(i);
            if (v instanceof TextView) {
                final TextView tv = (TextView) v;
                tv.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        reason.setText(tv.getText().toString());
                        popupWindow.dismiss();
                    }
                });
            }
        }

        paybackprice.setText(totalPrice);

        findViewById(R.id.payreturnreson).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                showPopUp(arg0);
            }
        });

        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                payBack(reason.getText().toString(), backRemark.getText().toString());
            }
        });
    }

    private boolean isShowTips = false;

    public void payBack(String reason, String remark, boolean isShowTips) {
        this.isShowTips = isShowTips;
        payBack(reason, remark);
    }

    public void payBack(String reason, String remark) {
        if (reason.equals("请选择退款原因")) {
            jsShowMsg("请选择退款原因");
        } else if (totalPrice.equals("0.00")) {
            jsShowMsg("退款金额出错");
        } else {
            if (!isLoading()) {
                showLoadDialog();
                new Thread(new Payback(reason, remark)).start();
            }
        }
    }

    private class Payback implements Runnable {
        private String reason;
        private String remark;

        public Payback(String reason, String remark) {
            this.reason = reason;
            this.remark = remark;
        }

        public void run() {
            String result = AppManager.getInstance().setPayBack("0079", ordernumber, memberid, reason, remark);
            Message msg = new Message();
            msg.obj = result;
            msg.what = 1;
            handler.sendMessage(msg);
        }
    }

    public Handler.Callback callback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                hideLoadDialog();
                if (msg.obj.toString().equals("true")) {
                    jsShowMsg("退款信息已提交");
                    MyOrderData.payBackHandle(ordernumber);
                    if (isShowTips) {
                        addFragment(new PayBackTipsFragment(), "tips");
                    } else
                        finish();
                } else {
                    jsShowMsg("您已提交过退款信息，请耐心等候！");
                }
            }
            setResult(RESULT_PAY_BACK);
            return true;
        }
    };

    private void showPopUp(View v) {
        popupWindow.show();
    }

    public void onBackPressed() {
        if (isShowTips) {
            isShowTips = false;
            finish();
            return;
        }
        setResult(RESULT_PAY_BACK);
        super.onBackPressed();
    }
}
