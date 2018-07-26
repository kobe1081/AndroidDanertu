package com.danertu.dianping.fragment.couponexpired;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.config.Constants;
import com.danertu.base.NewBasePresenter;

public class CouponExpiredPresenter extends NewBasePresenter<CouponExpiredContact.CouponExpiredView, CouponExpiredModel> implements CouponExpiredContact.ICouponExpiredPresenter {
    static final int WHAT_LIST_SUCCESS = 200;
    static final int WHAT_LIST_FAIL = 201;
    static final int WHAT_LIST_ERROR = 202;
    static final int WHAT_REFRESH_SUCCESS = 203;
    static final int WHAT_REFRESH_FAIL = 204;
    static final int WHAT_REFRESH_ERROR = 205;
    static final int WHAT_LOAD_MORE_SUCCESS = 206;
    static final int WHAT_LOAD_MORE_FAIL = 207;
    static final int WHAT_LOAD_MORE_ERROR = 208;
    static final int WHAT_NO_MORE_DATA = 209;
    private int currentPage = 1;

    public CouponExpiredPresenter(Context context) {
        super(context);
    }

    @Override
    public void onCreateView() {
        if (isViewAttached()) {
            view.jsShowLoading();
            view.initList(model.getCouponList());
        }
        currentPage = 1;
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
                    case WHAT_LIST_SUCCESS:
                        if (isViewAttached()) {

                            view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                            view.stopRefresh();
                        }
                        break;
                    case WHAT_LIST_FAIL:
                        if (isViewAttached()) {

                            view.jsShowMsg("优惠券获取失败");
                            view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                            view.stopRefresh();
                        }
                        break;
                    case WHAT_LIST_ERROR:
                        if (isViewAttached()) {

                            view.jsShowMsg("优惠券获取失败");
                            view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                            view.stopRefresh();
                        }
                        break;

                    case WHAT_REFRESH_SUCCESS:
                        if (isViewAttached()) {

                            view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                            view.stopRefresh();
                        }
                        break;
                    case WHAT_REFRESH_FAIL:
                        if (isViewAttached()) {

                            view.stopRefresh();
                        }
                        break;
                    case WHAT_REFRESH_ERROR:
                        if (isViewAttached()) {

                            view.stopRefresh();
                        }
                        break;
                    case WHAT_LOAD_MORE_SUCCESS:
                        if (isViewAttached()) {

                            view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                            view.stopLoadMore();
                        }
                        break;
                    case WHAT_LOAD_MORE_FAIL:
                        --currentPage;
                        if (isViewAttached()) {

                            view.jsShowMsg("优惠券获取失败");
                            view.stopLoadMore();
                        }
                        break;
                    case WHAT_LOAD_MORE_ERROR:
                        --currentPage;
                        if (isViewAttached()) {
                            view.jsShowMsg("优惠券获取失败");
                            view.stopLoadMore();
                        }
                        break;
                    case WHAT_NO_MORE_DATA:
                        if (isViewAttached()) {
                            view.jsShowMsg("已无更多优惠券");
                            view.stopLoadMore();
                            view.noMoreData();
                        }
                        break;

                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public CouponExpiredModel initModel() {
        return new CouponExpiredModel();
    }

    @Override
    public void loadData() {

    }

    @Override
    public void loadData(int page) {
        model.getMyCouponList(handler, model.getUid(context), "2", page, Constants.pageSize);
    }

    @Override
    public void loadMore() {
        loadData(++currentPage);
    }

    @Override
    public void refresh() {
        if (isViewAttached()) {
            view.jsShowMsg("正在刷新...");
        }
        model.getCouponList().clear();
        currentPage = 1;
        loadData(currentPage);
    }
}
