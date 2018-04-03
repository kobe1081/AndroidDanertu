package com.danertu.dianping.frag.stockorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.config.Constants;
import com.danertu.base.BasePresenter;
import com.danertu.base.IPresenter;

import static com.danertu.dianping.StockOrderDetailActivity.RESULT_RETURN;
import static com.danertu.dianping.frag.stockorder.StockOrderFragment.REQUEST_TO_ORDER_DETAIL;

/**
 * Created by Viz on 2017/12/21.
 */

public class StockOrderPresenter extends BasePresenter<StockOrderView> implements IPresenter {
    public static final int MSG_GET_DATA_SUCCESS = 211;
    public static final int MSG_GET_DATA_FAIL = 212;
    public static final int MSG_LOAD_MORE_SUCCESS = 213;
    public static final int MSG_LOAD_MORE_FAIL = 214;
    public static final int MSG_NO_MORE_DATA = 215;
    private StockOrderModel model;
    private int currentPage = 1;
    private boolean isLoading = false;

    public StockOrderPresenter(Context context) {
        super(context);
        model = new StockOrderModel();
        initHandler();
    }

    @Override
    public void initHandler() {
        handler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_GET_DATA_SUCCESS:
                        view.updateView(model.getOrderLists().size());
                        view.hideLoading();
                        view.stopRefresh();
                        isLoading = false;
                        break;
                    case MSG_GET_DATA_FAIL:
                        view.jsShowToast("加载数据时发生错误");
                        view.hideLoading();
                        isLoading = false;
                        break;
                    case MSG_LOAD_MORE_SUCCESS:
                        view.updateView();
                        view.stopLoadMore();
                        isLoading = false;
                        break;
                    case MSG_LOAD_MORE_FAIL:
                        --currentPage;
                        view.jsShowToast("加载更多数据时发生错误");
                        view.stopLoadMore();
                        isLoading = false;
                        break;
                    case MSG_NO_MORE_DATA:
                        --currentPage;
                        isLoading = false;
                        view.jsShowToast("已无更多数据");
                        view.stopLoadMore();
                        view.noMoreData();
                        break;
                }
            }
        };
    }


    @Override
    public void onCreateView() {
        view.initList(model.getOrderLists());
        view.showLoading();
        model.clearData();
        loadData(currentPage);
    }

    public void loadData(int page) {
        model.getStockOrder(handler, view.getUid(), page, Constants.pagesize);
    }

    public void loadMore() {
        if (isLoading) {
            view.jsShowToast("正在加载更多数据，请稍候");
            return;
        }
        isLoading = true;
        loadData(++currentPage);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
        model = null;
    }

    public void refresh() {
        currentPage = 1;
        model.clearData();
        view.updateView();
        loadData(currentPage);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_TO_ORDER_DETAIL:
                    switch (resultCode) {
                        case RESULT_RETURN:
                            //退货成功
                            int position = data.getIntExtra("position", -1);
                            if (position != -1) {
                                model.getOrderLists().get(position).setShipmentStatus("5");
                                view.updateView();
                            }
                            break;
                    }
                    refresh();
                    break;
            }
        }
    }
}
