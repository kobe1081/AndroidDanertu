package com.danertu.dianping;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.danertu.tools.AppManager;

public class MessageDetailActivity extends BaseActivity {
    private Boolean isInserted = false;
    private TextView msgTextView;
    private TextView answerTextView;
    private TextView msgLabel;
    private TextView answerLabel;

//    DBManager db = new DBManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        initTitle("消息详细");
        findViewById();
        initView();
    }

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
    protected void findViewById() {
        msgTextView = (TextView) findViewById(R.id.txtMsg);
        answerTextView = (TextView) findViewById(R.id.txtAnswer);
        msgLabel = (TextView) findViewById(R.id.msgLabel);
        answerLabel = (TextView) findViewById(R.id.answerLabel);
    }


    @Override
    protected void initView() {
        try {
            bindListData();
            tDetails.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void bindListData() {
        //ntent intent = getIntent();
        try {
            Bundle bundle = getIntent().getExtras();
            String guid = bundle.getString("guid");
            String uid = bundle.getString("uid");
            Cursor cursor = db.GetMsgDetail(MessageDetailActivity.this, guid);
            if (cursor.moveToNext()) {
                String contentString = cursor.getString(0);
                String answerString = cursor.getString(3);
                if (uid.equals("all")) {
                    answerTextView.setVisibility(View.GONE);
                    answerLabel.setVisibility(View.GONE);
                    msgTextView.setText(contentString);
                } else {
                    msgTextView.setText(contentString);
                    answerTextView.setText(answerString);
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private Handler HandlerDetails = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // //执行接收到的通知，更新UI 此时执行的顺序是按照队列进行，即先进先出
            //CommonTools.showShortToast(AddressActivity.this, "设置成功");
            super.handleMessage(msg);
        }

    };

    private Thread tDetails = new Thread(new Runnable() {

        @Override
        public void run() {
            // 耗时操作
            Intent intent = getIntent();
            String guid = intent.getStringExtra("guid");
            db.updateMsgState(MessageDetailActivity.this, guid);
            String s = AppManager.getInstance().postUpdateMsgState("0035", guid);
            isInserted = s.equals("true");
            SystemClock.sleep(1000);
            Message msg = new Message();
            MessageDetailActivity.this.HandlerDetails.sendMessage(msg);

        }

    });

}
