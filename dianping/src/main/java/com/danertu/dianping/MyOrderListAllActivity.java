package com.danertu.dianping;

import android.content.Intent;
import android.os.Bundle;

import com.danertu.adapter.MyOrderAdapter;
import com.danertu.entity.MyOrderData;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;

import java.util.ArrayList;
import java.util.HashMap;

import static com.danertu.adapter.MyOrderAdapter.REQUEST_ORDER_DETAIL;
import static com.danertu.adapter.MyOrderAdapter.REQUEST_QRCODE;

public class MyOrderListAllActivity extends MyOrderParent {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data2 = MyOrderData.order_list_all;
        adapter = new MyOrderAdapter(context, data2, TAB_ALL);
        lv_order.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e(getClass().getSimpleName(), "onResume");
        if (data2.size() == 0 && MyOrderData.order_all.size() != 0) {
            loadData();
        }
        updateView();
    }

    /**
     * 刷新
     */
    @Override
    void refresh() {

    }

    /**
     * 加载更多
     */
    @Override
    void loadMore() {
        if ((MyOrderData.order_all == null || MyOrderData.order_all.size() <= 0) && MyOrderData.isFinish) {
            CommonTools.showShortToast(this, "已无更多订单");
            lv_order.stopLoadMore();
            lv_order.setPullLoadEnable(false);
            return;
        }
        loadData();
        lv_order.stopLoadMore();
    }

    private void loadData() {
        if (MyOrderData.order_all.size() > MyOrderData.LIST_INIT_SIZE) {
            for (int i = 0; i < MyOrderData.LIST_INIT_SIZE; i++) {
                data2.add(data2.size(), MyOrderData.order_all.get(0));
                MyOrderData.order_all.remove(0);
            }
        } else {
            data2.addAll(data2.size(), MyOrderData.order_all);
            MyOrderData.order_all.clear();
        }
        adapter.notifyDataSetChanged();
    }


}