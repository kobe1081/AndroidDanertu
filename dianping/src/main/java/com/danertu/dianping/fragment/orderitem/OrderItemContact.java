package com.danertu.dianping.fragment.orderitem;

import android.content.Intent;
import android.os.Bundle;

import com.danertu.base.BaseView;
import com.danertu.base.IPresenter;
import com.danertu.entity.NewOrderBean;

import java.util.List;

public interface OrderItemContact {

    interface OrderItemView extends BaseView {
        void initList(List<NewOrderBean> list);

        void notifyChange(int listSize);

        void notifyChange();

        void stopLoadMore();

        void stopRefresh();

        void loadError();

        void noMoreData();

        void cancelOrderError(String orderNumber, int position);

        void cancelOrderFailure(String orderNumber, int position);

        void sureTakeGoodsError(String orderNumber, int position);

        void sureTakeGoodsFailure(String orderNumber, int position);

        void changeOrderStatue(int position, String orderNumber, String orderStatue, String payStatue, String shipStatue);

        void initBroadcastReceiver();

        void sendDataChangeBroadcast(String orderNumber, int position, NewOrderBean bean);

        void toLogin();
    }

    interface IOrderItemPresenter extends IPresenter {
        void onCreate(Bundle bundle);

        void loadData(int page);

        void loadMore();

        void refresh();

        void cancelOrder(String orderNumber, int position);

        void sureTakeGoods(String orderNumber, int position);

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void changeOrderStatue(int position, String orderNumber, String orderStatue, String payStatue, String shipStatue);

        void dataChange(Intent intent);
    }
}
