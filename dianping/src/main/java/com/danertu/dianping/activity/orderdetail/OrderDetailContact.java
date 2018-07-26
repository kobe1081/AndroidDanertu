package com.danertu.dianping.activity.orderdetail;

import android.content.Intent;

import com.danertu.base.BaseView;
import com.danertu.base.IPresenter;
import com.danertu.entity.OrderBody;
import com.danertu.entity.OrderHead;
import com.danertu.entity.QuanYanProductCategory;

/**
 * 作者:  Viz
 * 日期:  2018/7/25 14:20
 * <p>
 * 包名：com.danertu.dianping.activity.orderdetail
 * 文件名：OrderDetailContact
 * 描述：TODO
 */
public interface OrderDetailContact {
    interface OrderDetailView extends BaseView {

        void showOrderDetail(OrderHead.OrderinfolistBean orderHead, OrderBody.OrderproductlistBean orderBody);

        void stopRefresh();

        void setSwipeRefreshEnable(boolean enable);

        void getOrderInfoError();

        void startToProDetail(String guid, String proName, String img, String detail, String agentID, String supplierID, String price, String mobile);

        void toQuanYanPage(QuanYanProductCategory.ValBean bean, String shopId);


    }

    interface IOrderDetailPresenter extends IPresenter {
        void onCreate(Intent intent);

        void getOrderInfo(boolean isSetResult);

        void copyOrderNumber();

        void refresh();

        void startToProDetail(String guid, String proName, String img, String detail, String agentID, String supplierID, String price, String mobile);

        void toQuanYan(String guid, String shopId);

        void toPayBackForResult(String uid);

        void toShipment();

        void cancelOrder(String orderNumber);

        void sureTakeGoods(String orderNumber);

        void sendDataChangeBroadcast();

        void changeOrderState(String orderStatue, String payStatue, String shipStatue);
    }
}
