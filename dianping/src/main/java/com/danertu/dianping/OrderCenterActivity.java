package com.danertu.dianping;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.danertu.base.NewBaseActivity;
import com.danertu.dianping.activity.ordercenter.OrderCenterContact;
import com.danertu.dianping.activity.ordercenter.OrderCenterPresenter;

public class OrderCenterActivity extends NewBaseActivity<OrderCenterContact.OrderCenterView,OrderCenterPresenter> implements OrderCenterContact.OrderCenterView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_center);
    }

    @Override
    public OrderCenterPresenter initPresenter() {
        return new  OrderCenterPresenter(context);
    }
}
