package com.danertu.dianping.fragment.orderitem;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;

import com.config.Constants;
import com.danertu.base.ModelCallBack;
import com.danertu.base.ModelParamCallBack;
import com.danertu.base.NewBasePresenter;
import com.danertu.entity.NewOrderBean;
import com.danertu.entity.OrderBody;
import com.danertu.entity.OrderHead;
import com.danertu.tools.Logger;
import com.testin.agent.c.d;

import java.util.ArrayList;
import java.util.List;


/**
 * 作者:  Viz
 * 日期:  2018/7/23 14:53
 */
public class OrderItemPresenter extends NewBasePresenter<OrderItemContact.OrderItemView, OrderItemModel> implements OrderItemContact.IOrderItemPresenter {
    static final int WHAT_ORDER_SUCCESS = 600;
    static final int WHAT_ORDER_FAIL = 601;
    static final int WHAT_ORDER_ERROR = 602;
    static final int WHAT_MORE_SUCCESS = 603;
    static final int WHAT_MORE_FAIL = 604;
    static final int WHAT_MORE_ERROR = 605;
    static final int WHAT_NO_MORE_DATA = 606;

    public static final int REQUEST_QRCODE = 122;
    private String orderType = "";
    private int tabIndex = 0;
    private int currentPage = 1;
    private boolean isLoading = false;

    public OrderItemPresenter(Context context) {
        super(context);
    }

    @Override
    public void onCreateView() {

    }

    @Override
    public void onCreate(Bundle bundle) {
        orderType = bundle.getString("orderType");
        tabIndex = bundle.getInt("tabIndex");
        if (isViewAttached()) {
            view.jsShowLoading();
            view.initList(model.getList());
        }
        loadData(currentPage);
    }

    @Override
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages("");
            handler = null;
        }
    }

    @Override
    public void initHandler() {
        handler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (isViewAttached()) {
                    view.jsHideLoading();
                }
                switch (msg.what) {
                    case WHAT_ORDER_SUCCESS:
                        if (isViewAttached()) {
                            view.stopRefresh();
                            view.notifyChange(model.getList().size());
                        }
                        break;
                    case WHAT_ORDER_FAIL:
                        if (isViewAttached()) {
                            view.jsShowMsg("获取数据失败");
                            view.stopRefresh();
                            view.loadError();
                        }
                        break;
                    case WHAT_ORDER_ERROR:
                        if (isViewAttached()) {
                            view.jsShowMsg("获取数据失败");
                            view.stopRefresh();
                            view.loadError();
                        }
                        break;
                    case WHAT_MORE_SUCCESS:
                        if (isViewAttached()) {
                            view.stopLoadMore();
                            view.notifyChange(model.getList().size());
                        }
                        break;
                    case WHAT_MORE_ERROR:
                        if (isViewAttached()) {
                            view.jsShowMsg("获取数据失败");
                            view.stopLoadMore();
                            view.loadError();
                            --currentPage;
                        }
                        break;
                    case WHAT_MORE_FAIL:
                        if (isViewAttached()) {
                            view.jsShowMsg("获取数据失败");
                            view.stopLoadMore();
                            view.loadError();
                            --currentPage;
                        }
                        break;
                    case WHAT_NO_MORE_DATA:
                        if (isViewAttached()) {
                            view.noMoreData();
                            view.jsShowMsg("已无更多数据");
                        }
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public OrderItemModel initModel() {
        return new OrderItemModel();
    }


    @Override
    public void loadData(final int page) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isViewAttached()) {
                    model.getOrders(handler, orderType, view.getUid(), page, Constants.pageSize);
                }
            }
        }).start();

    }

    @Override
    public void loadMore() {

        if (isViewAttached()) {
            view.jsShowLoading();
        }
        loadData(++currentPage);
    }

    @Override
    public void refresh() {
        if (isViewAttached()) {
            view.jsShowMsg("正在刷新....");
            view.jsShowLoading();
        }
        model.clearData();
        currentPage = 1;
        loadData(currentPage);
    }

    @Override
    public void cancelOrder(final String orderNumber, final int position) {
        model.cancelOrder(orderNumber, new ModelCallBack() {
            @Override
            public void requestSuccess() {
                if (isViewAttached()) {
                    view.jsShowMsg("取消成功");
                    switch (orderType) {
                        case "0":
                            view.changeOrderStatue(position, orderNumber, "2", null, null);
                            break;
                        case "1":
                        case "2":
                        case "3":
                        case "4":
                            List<NewOrderBean> list = model.getList();
                            list.remove(position);
                            view.notifyChange(list.size());
                            break;
                    }

                }
            }

            @Override
            public void requestError() {
                if (isViewAttached()) {
                    view.jsShowMsg("取消订单失败");
                    view.cancelOrderError(orderNumber, position);
                }
            }

            @Override
            public void requestFailure() {
                if (isViewAttached()) {
                    view.jsShowMsg("取消订单失败");
                    view.cancelOrderFailure(orderNumber, position);
                }
            }
        });
    }

    @Override
    public void sureTakeGoods(final String orderNumber, final int position) {
        model.sureTakeGoods(orderNumber, new ModelCallBack() {
            @Override
            public void requestSuccess() {
                if (isViewAttached()) {
                    view.jsShowMsg("确认收货成功");
                    switch (orderType) {
                        case "0":
                            view.changeOrderStatue(position, orderNumber, "5", "2", "2");
                            break;
                        case "1":
                        case "2":
                        case "3":
                        case "4":
                            List<NewOrderBean> list = model.getList();
                            list.remove(position);
                            view.notifyChange(list.size());
                            break;
                    }

                }
            }

            @Override
            public void requestError() {
                if (isViewAttached()) {
                    view.jsShowMsg("确认收货失败");
                    view.sureTakeGoodsError(orderNumber, position);
                }
            }

            @Override
            public void requestFailure() {
                if (isViewAttached()) {
                    view.jsShowMsg("确认收货失败");
                    view.sureTakeGoodsFailure(orderNumber, position);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_QRCODE:

                break;
        }
    }

    @Override
    public void changeOrderStatue(int position, String orderNumber, String orderStatue, String payStatue, String shipStatue) {
        if (isViewAttached()){
            view.changeOrderStatue(position, orderNumber, orderStatue, payStatue, shipStatue);
            view.sendDataChangeBroadcast(orderNumber,position,model.getList().get(position));
        }
    }

    @Override
    public void dataChange(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        String orderNumber = bundle.getString("orderNumber");
        if (TextUtils.isEmpty(orderNumber)) {
            return;
        }
        int position = bundle.getInt("position");
        NewOrderBean orderBean = bundle.getParcelable("orderBean");
        if (orderBean == null) {
            return;
        }
        List<OrderBody.OrderproductlistBean.OrderproductbeanBean> productItems = orderBean.getProductItems();
        List<NewOrderBean> newOrderBeanList = model.getList();
        switch (orderType) {
            case "0":
                if (position >= newOrderBeanList.size()) {
                    return;
                }
                NewOrderBean bean = newOrderBeanList.get(position);
                if (bean.getOrderNumber().equals(orderNumber)) {
                    bean.setPaymentName(orderBean.getPaymentName());
                    bean.setPaymentStatus(orderBean.getPaymentStatus());
                    bean.setOderStatus(orderBean.getOderStatus());
                    bean.setShipmentStatus(orderBean.getShipmentStatus());
                    bean.setLogisticsCompanyCode(orderBean.getLogisticsCompanyCode());
                    bean.setDispatchModeName(orderBean.getDispatchModeName());
                    bean.setShipmentNumber(orderBean.getShipmentNumber());
                    List<OrderBody.OrderproductlistBean.OrderproductbeanBean> items = bean.getProductItems();
                    for (OrderBody.OrderproductlistBean.OrderproductbeanBean item : items) {
                        for (OrderBody.OrderproductlistBean.OrderproductbeanBean orderproductbeanBean : productItems) {
                            if (item.getGuid().equals(orderproductbeanBean.getGuid())) {
                                item.setBuyNumber(orderproductbeanBean.getBuyNumber());
                            }
                        }
                    }
                    if (isViewAttached()) {
                        view.notifyChange(model.getList().size());
                    }
                    return;
                }
                for (NewOrderBean newOrderBean : newOrderBeanList) {
                    if (newOrderBean.getOrderNumber().equals(orderNumber)) {
                        newOrderBean.setPaymentName(orderBean.getPaymentName());
                        newOrderBean.setPaymentStatus(orderBean.getPaymentStatus());
                        newOrderBean.setOderStatus(orderBean.getOderStatus());
                        newOrderBean.setShipmentStatus(orderBean.getShipmentStatus());
                        newOrderBean.setLogisticsCompanyCode(orderBean.getLogisticsCompanyCode());
                        newOrderBean.setDispatchModeName(orderBean.getDispatchModeName());
                        newOrderBean.setShipmentNumber(orderBean.getShipmentNumber());
                        List<OrderBody.OrderproductlistBean.OrderproductbeanBean> items = newOrderBean.getProductItems();
                        for (OrderBody.OrderproductlistBean.OrderproductbeanBean item : items) {
                            for (OrderBody.OrderproductlistBean.OrderproductbeanBean orderproductbeanBean : productItems) {
                                if (item.getGuid().equals(orderproductbeanBean.getGuid())) {
                                    item.setBuyNumber(orderproductbeanBean.getBuyNumber());
                                }
                            }
                        }
                        break;
                    }
                }
                if (isViewAttached()) {
                    view.notifyChange(model.getList().size());
                }
                break;
            case "1":
                if (removeData(orderNumber, position, orderBean, newOrderBeanList))
                    return;

                if ("1".equals(orderBean.getOderStatus()) && "0".equals(orderBean.getShipmentStatus()) && "0".equals(orderBean.getPaymentStatus())) {
                    newOrderBeanList.add(0, orderBean);
                    if (isViewAttached()) {
                        view.notifyChange(model.getList().size());
                    }
                }
                break;
            case "2":
                if (removeData(orderNumber, position, orderBean, newOrderBeanList))
                    return;

                if ("1".equals(orderBean.getOderStatus()) && "0".equals(orderBean.getShipmentStatus()) && "2".equals(orderBean.getPaymentStatus())) {
                    newOrderBeanList.add(0, orderBean);
                    if (isViewAttached()) {
                        view.notifyChange(model.getList().size());
                    }
                }
                break;
            case "3":
                if (removeData(orderNumber, position, orderBean, newOrderBeanList))
                    return;

                if ("1".equals(orderBean.getOderStatus()) && "1".equals(orderBean.getShipmentStatus()) && "2".equals(orderBean.getPaymentStatus())) {
                    newOrderBeanList.add(0, orderBean);
                    if (isViewAttached()) {
                        view.notifyChange(model.getList().size());
                    }
                }
                break;
            case "4":
                if (removeData(orderNumber, position, orderBean, newOrderBeanList))
                    return;
                if ("3".equals(orderBean.getPaymentName())) {
                    newOrderBeanList.add(0, orderBean);
                    if (isViewAttached()) {
                        view.notifyChange(model.getList().size());
                    }
                }
                break;
        }

    }

    private boolean removeData(String orderNumber, int position, NewOrderBean orderBean, List<NewOrderBean> newOrderBeanList) {
        if (position >= newOrderBeanList.size()) {
            return false;
        }
        NewOrderBean bean = newOrderBeanList.get(position);
        if (bean.getOrderNumber().equals(orderNumber)) {
            if (!bean.getOderStatus().equals(orderBean.getOderStatus())) {
                newOrderBeanList.remove(position);
                if (isViewAttached()) {
                    view.notifyChange(model.getList().size());
                }
                return true;
            }
        }
        for (NewOrderBean newOrderBean : newOrderBeanList) {
            if (newOrderBean.getOrderNumber().equals(orderNumber)) {
                if (!newOrderBean.getOderStatus().equals(orderBean.getOderStatus())) {
                    newOrderBeanList.remove(newOrderBean);
                    if (isViewAttached()) {
                        view.notifyChange(model.getList().size());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public int getTabIndex() {
        return tabIndex;
    }
}
