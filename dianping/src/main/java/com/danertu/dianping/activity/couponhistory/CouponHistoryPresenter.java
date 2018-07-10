package com.danertu.dianping.activity.couponhistory;

import android.content.Context;

import com.danertu.base.NewBasePresenter;

public class CouponHistoryPresenter extends NewBasePresenter<CouponHistoryContact.CouponHistoryView, CouponHistoryModel> implements CouponHistoryContact.ICouponHistoryPresenter {

    public CouponHistoryPresenter(Context context) {
        super(context);
    }

    @Override
    public void onCreateView() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void initHandler() {

    }

    @Override
    public CouponHistoryModel initModel() {
        return new CouponHistoryModel();
    }
}
