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
 * 2018年4月13日
 * 修改为显示 退款订单
 */
/**
 * 作者:  Viz
 * 日期:  2018/7/30 12:01
 *
 * 描述： 退款订单列表页面，已弃用
*/
public class MyOrderNoCommentActivity extends MyOrderParent {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data2 = MyOrderData.order_list_noComment;
        adapter = new MyOrderAdapter(context, data2, TAB_NO_COMMENT);
        lv_order.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (data2.size() == 0 && MyOrderData.order_noComment.size() != 0) {
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
        if ((MyOrderData.order_noComment == null || MyOrderData.order_noComment.size() <= 0) && MyOrderData.isFinish) {
            CommonTools.showShortToast(this, "已无更多订单");
            lv_order.stopLoadMore();
            lv_order.setPullLoadEnable(false);
            return;
        }
        loadData();
        lv_order.stopLoadMore();
    }

    private void loadData() {
        if (MyOrderData.order_noComment.size() > MyOrderData.LIST_INIT_SIZE) {
            for (int i = 0; i < MyOrderData.LIST_INIT_SIZE; i++) {
                data2.add(data2.size(), MyOrderData.order_noComment.get(0));
                MyOrderData.order_noComment.remove(0);
            }
        } else {
            data2.addAll(data2.size(), MyOrderData.order_noComment);
            MyOrderData.order_noComment.clear();
        }
        adapter.notifyDataSetChanged();
    }
}
