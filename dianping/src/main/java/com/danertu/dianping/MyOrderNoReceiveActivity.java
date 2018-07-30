package com.danertu.dianping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.danertu.adapter.MyOrderAdapter;
import com.danertu.entity.MyOrderData;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;

import java.util.ArrayList;
/**
 * 作者:  Viz
 * 日期:  2018/7/30 12:03
 *
 * 描述： 待收货列表 已弃用
*/
public class MyOrderNoReceiveActivity extends MyOrderParent {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data2 = MyOrderData.order_list_noReceive;
        adapter = new MyOrderAdapter(context, data2, TAB_NP_RECEIVE);
        lv_order.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (data2.size() == 0 && MyOrderData.order_noReceive.size() != 0) {
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
        if ((MyOrderData.order_noReceive == null || MyOrderData.order_noReceive.size() <= 0) && MyOrderData.isFinish) {
            CommonTools.showShortToast(this, "已无更多订单");
            lv_order.stopLoadMore();
            lv_order.setPullLoadEnable(false);
            return;
        }
        loadData();
        lv_order.stopLoadMore();
    }

    private void loadData() {
        if (MyOrderData.order_noReceive.size() > MyOrderData.LIST_INIT_SIZE) {
            for (int i = 0; i < MyOrderData.LIST_INIT_SIZE; i++) {
                data2.add(data2.size(), MyOrderData.order_noReceive.get(0));
                MyOrderData.order_noReceive.remove(0);
            }
        } else {
            data2.addAll(data2.size(), MyOrderData.order_noReceive);
            MyOrderData.order_noReceive.clear();
        }
        adapter.notifyDataSetChanged();
    }

}
