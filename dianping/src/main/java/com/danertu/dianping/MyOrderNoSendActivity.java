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

public class MyOrderNoSendActivity extends MyOrderParent {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data2 = MyOrderData.order_list_noSend;
		adapter = new MyOrderAdapter(context, data2,TAB_NO_SEND);
		lv_order.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (data2.size() == 0 && MyOrderData.order_noSend.size() != 0) {
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
		if((MyOrderData.order_noSend==null||MyOrderData.order_noSend.size()<=0)&&MyOrderData.isFinish){
			CommonTools.showShortToast(this,"已无更多订单");
			lv_order.stopLoadMore();
			return;
		}
		loadData();
		lv_order.stopLoadMore();
	}

	private void loadData() {
		if(MyOrderData.order_noSend.size()>MyOrderData.LIST_INIT_SIZE){
			for (int i = 0; i < MyOrderData.LIST_INIT_SIZE; i++) {
				data2.add(data2.size(),MyOrderData.order_noSend.get(0));
				MyOrderData.order_noSend.remove(0);
			}
		}else {
			data2.addAll(data2.size(),MyOrderData.order_noSend);
			MyOrderData.order_noSend.clear();
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
}
