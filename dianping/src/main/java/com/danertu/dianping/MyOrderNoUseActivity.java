package com.danertu.dianping;

import android.content.Intent;
import android.os.Bundle;

import com.danertu.adapter.MyOrderAdapter;
import com.danertu.adapter.MyOrderQRCodeAdapter;
import com.danertu.entity.MyOrderData;
import com.danertu.entity.MyOrderDataQRCode;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;

public class MyOrderNoUseActivity extends MyOrderQRCodeParentActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data2 = MyOrderDataQRCode.order_list_no_use;
        adapter = new MyOrderQRCodeAdapter(context, data2,TAB_NO_USE,getUid());
        lv_order.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        Logger.e(getClass().getSimpleName(),"onResume");
        super.onResume();
        if (data2.size() == 0 && MyOrderDataQRCode.order_no_use.size() != 0) {
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
        if ((MyOrderDataQRCode.order_no_use == null || MyOrderDataQRCode.order_no_use.size() <= 0)&&MyOrderDataQRCode.isFinish) {
            CommonTools.showShortToast(this, "已无更多订单");
            lv_order.stopLoadMore();
            lv_order.setPullLoadEnable(false);
            return;
        }
        loadData();
        lv_order.stopLoadMore();
    }

    private void loadData() {
        if (MyOrderDataQRCode.order_no_use.size() > MyOrderDataQRCode.LIST_INIT_SIZE) {
            for (int i = 0; i < MyOrderDataQRCode.LIST_INIT_SIZE; i++) {
                data2.add(data2.size(), MyOrderDataQRCode.order_no_use.get(0));
                MyOrderDataQRCode.order_no_use.remove(0);
            }
        } else {
            data2.addAll(data2.size(), MyOrderDataQRCode.order_no_use);
            MyOrderDataQRCode.order_no_use.clear();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}