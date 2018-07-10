package com.danertu.dianping.activity.mycoupon;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.danertu.base.NewBasePresenter;
import com.danertu.tools.Logger;

import java.util.List;

public class MyCouponPresenter extends NewBasePresenter<MyCouponContact.MyCouponView, MyCouponModel> implements MyCouponContact.IMyCouponPresenter {
    static final int WHAT_COUPON_LIST_SUCCESS = 10;
    static final int WHAT_COUPON_LIST_FAIL = 11;
    static final int WHAT_COUPON_LIST_ERROR = 12;
    static final int WHAT_LOAD_MORE_SUCCESS = 13;
    static final int WHAT_LOAD_MORE_FAIL = 14;
    static final int WHAT_LOAD_MORE_ERROR = 15;
    static final int WHAT_REFRESH_SUCCESS = 16;
    static final int WHAT_REFRESH_FAIL = 17;
    static final int WHAT_REFRESH_ERROR = 18;
    static final int WHAT_NO_MORE_DATA = 19;

    static final int WHAT_SHOP_DETAIL_SUCCESS = 20;
    static final int WHAT_SHOP_DETAIL_FAIL = 21;
    static final int WHAT_SHOP_DETAIL_ERROR = 22;

    public static final int REQUEST_GET_COUPON = 99;
    public static final int RESULT_GET_COUPON = 909;

    private int currentPage = 1;

    public MyCouponPresenter(Context context) {
        super(context);
    }

    @Override
    public void onCreateView() {
        view.jsShowLoading();
        view.initList(model.getCouponList());
        currentPage = 1;
        loadData(currentPage);
    }

    @Override
    public void initHandler() {
        handler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                view.jsHideLoading();
                switch (msg.what) {
                    case WHAT_COUPON_LIST_SUCCESS:
                        view.stopRefresh();
                        view.notifyChange(model.getCouponList().size());
                        break;
                    case WHAT_COUPON_LIST_FAIL:
                        view.jsShowMsg("数据获取失败");
                        view.stopRefresh();
                        view.getDataFail();
                        break;
                    case WHAT_COUPON_LIST_ERROR:
                        view.jsShowMsg("数据获取失败");
                        view.stopRefresh();
                        view.getDataFail();
                        break;
                    case WHAT_LOAD_MORE_SUCCESS:
                        view.notifyChange(model.getCouponList().size());
                        view.stopLoadMore();
                        break;
                    case WHAT_LOAD_MORE_FAIL:
                        --currentPage;
                        view.jsShowMsg("加载更多数据失败");
                        view.stopLoadMore();
                        break;
                    case WHAT_LOAD_MORE_ERROR:
                        --currentPage;
                        view.jsShowMsg("加载更多数据失败");
                        view.stopLoadMore();
                        break;
                    case WHAT_REFRESH_SUCCESS:
                        currentPage = 0;
                        view.notifyChange(model.getCouponList().size());
                        break;
                    case WHAT_REFRESH_FAIL:
                        view.jsShowMsg("刷新失败");
                        view.stopRefresh();
                        break;
                    case WHAT_REFRESH_ERROR:
                        view.jsShowMsg("刷新失败");
                        view.stopRefresh();
                        break;
                    case WHAT_NO_MORE_DATA:
                        view.jsShowMsg("已无更多优惠券");
                        view.stopLoadMore();
                        break;
                    case WHAT_SHOP_DETAIL_SUCCESS:
                        view.toAgentShop(msg.arg1,msg.obj.toString());
                        break;
                    case WHAT_SHOP_DETAIL_FAIL:
                        view.jsShowMsg("获取经销商信息失败");
                        break;
                    case WHAT_SHOP_DETAIL_ERROR:
                        view.jsShowMsg("获取经销商信息失败");
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages("");
        }
    }


    @Override
    public MyCouponModel initModel() {
        return new MyCouponModel();
    }

    @Override
    public void loadData() {
        loadData(++currentPage);
    }

    @Override
    public void loadData(int page) {
        model.getMyCouponList(handler, model.getUid(context), "0", page, 12);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GET_COUPON:
                if (resultCode == RESULT_GET_COUPON) {
                    refresh();
                }
                break;
        }
    }

    @Override
    public void refresh() {
        model.getCouponList().clear();
        currentPage = 1;
        loadData(currentPage);
    }

    @Override
    public void loadMore() {
        loadData();
    }

    @Override
    public void toAgentShopIndex(String useAgentAppoint) {
        view.jsShowLoading();
        model.getShopDetail(handler, useAgentAppoint);
    }
}
