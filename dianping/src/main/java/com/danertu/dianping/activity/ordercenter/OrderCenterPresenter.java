package com.danertu.dianping.activity.ordercenter;

import android.content.Context;

import com.danertu.base.NewBasePresenter;

public class OrderCenterPresenter extends NewBasePresenter<OrderCenterContact.OrderCenterView, OrderCenterModel> implements OrderCenterContact.IOrderCenterPresenter {
    public OrderCenterPresenter(Context context) {
        super(context);
    }

    @Override
    public void initHandler() {

    }

    @Override
    public OrderCenterModel initModel() {
        return new OrderCenterModel(context);
    }

    @Override
    public void onCreateView() {

    }

    @Override
    public void onDestroy() {

    }
}
