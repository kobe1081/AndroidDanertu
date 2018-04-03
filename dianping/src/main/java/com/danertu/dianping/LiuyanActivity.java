package com.danertu.dianping;


import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.danertu.db.DBManager;
import com.danertu.tools.AppManager;
import com.danertu.widget.CommonTools;


/*
 * liujun
 * 2014-7-15
*/
public class LiuyanActivity extends BaseActivity implements OnClickListener {

    private TextView textView;
    private EditText editText;
    private TextView head_back_text;
    private Boolean isInserted = false; //判断是否新增成功


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_liuyan);
            initTitle("留言", "提交");
            findViewById();
            initView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initTitle(String title, String operationText) {
        Button b_title = (Button) findViewById(R.id.b_title_back2);
        Button b_opera = (Button) findViewById(R.id.b_title_operation2);
        b_title.setText(title);
        b_opera.setText(operationText);
        b_opera.setOnClickListener(this);
        b_title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    @Override
    protected void findViewById() {
        editText = (EditText) findViewById(R.id.remark_edit);
//		head_back_text=(TextView)findViewById(R.id.head_back_text);
    }

    @Override
    protected void initView() {
//		textView.setOnClickListener(this);
//		head_back_text.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_title_operation2:

                String msg = checkData();
                if (!msg.equals("")) {
                    CommonTools.showShortToast(LiuyanActivity.this, msg);
                    break;
                } else {
                    tLiuyan.start();
                    try {
                        tLiuyan.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isInserted) {
                        CommonTools.showShortToast(LiuyanActivity.this, "新增成功");
                        Intent mIntent = new Intent(LiuyanActivity.this, MsgListActivity.class);
                        startActivity(mIntent);
                        break;
                    } else {
                        CommonTools.showShortToast(LiuyanActivity.this, "新增失败，请检查网络及通知管理员。");
                        break;
                    }
                }
            /*case R.id.head_back_text:
             finish();
			break;
		case R.id.cancel_address_button:
			Intent mIntent=new Intent(LiuyanActivity.this, LiuyanActivity.class);
			startActivity(mIntent);*/
            default:
                break;
        }
    }

    private Handler HandlerLiuyan = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // //执行接收到的通知，更新UI 此时执行的顺序是按照队列进行，即先进先出

            super.handleMessage(msg);

        }

    };

    private Thread tLiuyan = new Thread(new Runnable() {

        @Override
        public void run() {
            // 耗时操作
            String message = editText.getText().toString().trim();
            String guid = UUID.randomUUID().toString();
            db.InsertMessage(LiuyanActivity.this, message, db.GetLoginUid(LiuyanActivity.this), guid);
            String s = AppManager.getInstance().postInsertMessage("0032", db.GetLoginUid(LiuyanActivity.this), message, guid);

            isInserted = s.equals("true");
            SystemClock.sleep(1000);

            Message msg = new Message();
            LiuyanActivity.this.HandlerLiuyan.sendMessage(msg);

        }

    });

    private String checkData() {
        String msg = "";
        if (editText.getText().toString().trim().equals("")) {
            //	CommonTools.showShortToast(AddressOperateActivity.this, msg);
            msg = "留言内容不能为空";
        }

        return msg;
    }


}
