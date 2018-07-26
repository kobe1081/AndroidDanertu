package com.danertu.dianping.activity.couponcenter;

import android.content.Context;

import com.danertu.base.ModelParamCallBack;
import com.danertu.base.NewBasePresenter;
import com.danertu.entity.CouponCountBean;

public class CouponCenterPresenter extends NewBasePresenter<CouponCenterContact.CouponCenterView, CouponCenterModel> implements CouponCenterContact.ICouponCenterPresenter {


    public CouponCenterPresenter(Context context) {
        super(context);
    }

    @Override
    public void onCreateView() {
//        view.jsShowLoading();
//        model.getCouponCount(view.getUid(), new ModelParamCallBack<CouponCountBean>() {
//            @Override
//            public void requestSuccess(CouponCountBean type) {
//
//            }
//
//            @Override
//            public void requestError(CouponCountBean type) {
//
//            }
//
//            @Override
//            public void requestFailure(CouponCountBean type) {
//
//            }
//        });
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
