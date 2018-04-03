package com.danertu.dianping;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class IsNoActivity extends BaseActivity implements OnClickListener {

    private TextView head_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_is_no);
        initTitle("返回");
        findViewById();
        initView();
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

    @Override
    protected void findViewById() {
        head_back = (TextView) findViewById(R.id.head_back_text);
        head_back.setOnClickListener(this);
    }

    @Override
    protected void initView() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_back_text:
                finish();
                break;

            default:
                break;
        }
    }

}
