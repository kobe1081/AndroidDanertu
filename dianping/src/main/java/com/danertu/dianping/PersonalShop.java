package com.danertu.dianping;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PersonalShop extends BaseActivity implements OnClickListener {
    Button b_title;
    Button b_operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_personal_shop);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        b_title = (Button) findViewById(R.id.b_title_back2);
        b_operation = (Button) findViewById(R.id.b_title_operation2);
        b_title.setOnClickListener(this);
        b_operation.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        b_title.setText("我的商城");
        b_operation.setText("添加");
    }

    @Override
    public void onClick(View v) {
        if (v == b_title) {
            finish();
        } else if (v == b_operation) {
            openActivity(PShopAdd.class);
        }
    }

}
