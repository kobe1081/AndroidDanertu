package com.danertu.dianping.fragment.couponusehistory;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

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
    private int currentPage = 1;

    public CouponUseHistoryPresenter(Context context) {
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
                if (view != null) {
                    view.jsHideLoading();
                }
                switch (msg.what) {
                    case WHAT_LIST_SUCCESS:
                        view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                        view.stopRefresh();
                        break;
                    case WHAT_LIST_FAIL:
                        view.jsShowMsg("优惠券获取失败");
                        view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                        view.stopRefresh();
                        break;
                    case WHAT_LIST_ERROR:
                        view.jsShowMsg("优惠券获取失败");
                        view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                        view.stopRefresh();
                        break;
                    case WHAT_REFRESH_SUCCESS:
                        view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                        view.stopRefresh();
                        break;
                    case WHAT_REFRESH_FAIL:
                        view.stopRefresh();
                        break;
                    case WHAT_REFRESH_ERROR:
                        view.stopRefresh();
                        break;
                    case WHAT_LOAD_MORE_SUCCESS:
                        view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                        view.stopLoadMore();
                        break;
                    case WHAT_LOAD_MORE_FAIL:
                        --currentPage;
                        view.jsShowMsg("优惠券获取失败");
                        view.stopLoadMore();
                        break;
                    case WHAT_LOAD_MORE_ERROR:
                        --currentPage;
                        view.jsShowMsg("优惠券获取失败");
                        view.stopLoadMore();
                        break;
                    case WHAT_NO_MORE_DATA:
                        view.jsShowMsg("已无更多优惠券");
                        view.stopLoadMore();
                        view.noMoreData();
                        break;

                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public CouponUseHistoryModel initModel() {
        return new CouponUseHistoryModel();
    }

    @Override
    public void loadData() {

    }

    @Override
    public void loadData(int page) {
        model.getMyCouponList(handler, model.getUid(context), "1", page, 12);
    }

    @Override
    public void loadMore() {
        loadData(++currentPage);
    }

    @Override
    public void refresh() {
        view.jsShowMsg("正在刷新...");
        model.getCouponList().clear();
        currentPage = 1;
        loadData(currentPage);
    }
}
