package com.danertu.dianping.fragment.couponusehistory;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.config.Constants;
import com.danertu.base.NewBasePresenter;
import com.danertu.dianping.activity.couponhistory.CouponHistoryContact;

public class CouponUseHistoryPresenter extends NewBasePresenter<CouponUseHistoryContact.CouponUseHistoryView, CouponUseHistoryModel> implements CouponUseHistoryContact.ICouponUseHistoryPresenter {
    static final int WHAT_LIST_SUCCESS = 100;
    static final int WHAT_LIST_FAIL = 101;
    static final int WHAT_LIST_ERROR = 102;
    static final int WHAT_REFRESH_SUCCESS = 103;
    static final int WHAT_REFRESH_FAIL = 104;
    static final int WHAT_REFRESH_ERROR = 105;
    static final int WHAT_LOAD_MORE_SUCCESS = 106;
    static final int WHAT_LOAD_MORE_FAIL = 7;
    static final int WHAT_LOAD_MORE_ERROR = 28;
    static final int WHAT_NO_MORE_DATA = 29;
    static final int WHAT_NEED_LOGIN = 30;
    private int currentPage = 1;

    public CouponUseHistoryPresenter(Context context) {
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
                    case WHAT_NEED_LOGIN:
                        if (isViewAttached()) {
                            view.jsShowMsg("您的登录信息已过期，请重新登录");
                            view.quitAccount();
                            view.jsFinish();
                            view.jsStartActivity("LoginActivity");
                        }
                        break;

                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public CouponUseHistoryModel initModel() {
        return new CouponUseHistoryModel(context);
    }

    @Override
    public void loadData() {

    }

    @Override
    public void loadData(int page) {
        model.getMyCouponList(handler, model.getUid(context), "1", page, Constants.pageSize);
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
