package com.danertu.dianping.fragment.orderitem;


import android.content.Context;

import com.danertu.base.NewBasePresenter;

public class OrderItemPresenter extends NewBasePresenter<OrderItemContact.OrderItemView, OrderItemModel> implements OrderItemContact.IOrderItemPresenter {
    public OrderItemPresenter(Context context) {
        super(context);
    }

    @Override
    public void onCreateView() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void initHandler() {

    }

    @Override
    public OrderItemModel initModel() {
        return new OrderItemModel();
    }
}
