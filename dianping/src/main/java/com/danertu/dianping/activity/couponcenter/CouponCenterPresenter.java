package com.danertu.dianping.activity.couponcenter;

import android.content.Context;

import com.danertu.base.NewBasePresenter;

public class CouponCenterPresenter extends NewBasePresenter<CouponCenterContact.CouponCenterView, CouponCenterModel> implements CouponCenterContact.ICouponCenterPresenter {


    public CouponCenterPresenter(Context context) {
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
    public CouponCenterModel initModel() {
        return new CouponCenterModel();
    }
}
