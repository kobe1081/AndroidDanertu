package com.danertu.dianping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ZhanghuActivity extends BaseActivity implements OnClickListener {

    private Button goBack = null;
    private RelativeLayout changePassword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        findViewById();
        initView();

    }

    @Override
    protected void findViewById() {
        goBack = (Button) findViewById(R.id.b_order_title_back);
        changePassword = (RelativeLayout) findViewById(R.id.change_password);
    }

    public void changePhoneNum(View v) {
        jsShowMsg("未开放");
    }

    @Override
    protected void initView() {
        goBack.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        goBack.setText("账户安全");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_order_title_back:
                finish();
                break;
            case R.id.change_password:
                Intent intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);
                break;
        }
    }

}
