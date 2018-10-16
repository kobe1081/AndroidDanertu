package com.danertu.dianping.activity.couponproducts;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.danertu.base.ModelCallBack;
import com.danertu.base.NewBasePresenter;

public class CouponProductsPresenter extends NewBasePresenter<CouponProductsContact.CouponProductsView, CouponProductsModel> implements CouponProductsContact.ICouponProductsPresenter {
    private String couponGuid;

    public CouponProductsPresenter(Context context) {
        super(context);
    }

    @Override
    public void onCreateView() {

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

    }

    @Override
    public CouponProductsModel initModel() {
        return new CouponProductsModel(context);
    }

    @Override
    public void onCreateView(Intent intent) {
        couponGuid = intent.getStringExtra("couponGuid");
        if (isViewAttached()) {
            if (TextUtils.isEmpty(couponGuid)) {
                view.jsShowMsg("数据有误");
                view.jsFinish();
                return;
            }
            view.initList(model.getList());
            view.jsShowLoading();
            loadData();
        }
    }

    @Override
    public void refresh() {
        loadData();
    }

    @Override
    public void loadData() {

        model.getCouponProduct(model.getUid(context), couponGuid, new ModelCallBack() {
            @Override
            public void requestSuccess() {
                if (isViewAttached()) {
                    view.jsHideLoading();
                    view.stopRefresh();
                    view.notifyChange(model.getList().size());
                }
            }

            @Override
            public void tokenException(String code,String info) {
                if (isViewAttached()){
                    view.jsShowMsg(info);
                    view.quitAccount();
                    view.jsFinish();
                    view.jsStartActivity("LoginActivity");
                }
            }

            @Override
            public void requestError() {
                if (isViewAttached()) {
                    view.jsHideLoading();
                    view.stopRefresh();
                    view.jsShowMsg("获取商品列表失败");
                    view.notifyChange(model.getList().size());
                }

            }

            @Override
            public void requestFailure() {
                if (isViewAttached()) {
                    view.jsHideLoading();
                    view.stopRefresh();
                    view.jsShowMsg("获取商品列表失败");
                    view.notifyChange(model.getList().size());
                }
            }
        });
    }
}
