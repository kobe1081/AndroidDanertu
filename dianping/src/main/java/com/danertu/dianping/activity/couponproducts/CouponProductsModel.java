package com.danertu.dianping.activity.couponproducts;

import android.content.Context;

import com.config.ApiService;
import com.danertu.base.BaseModel;
import com.danertu.base.ModelCallBack;
import com.danertu.entity.CouponProductsBean;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponProductsModel extends BaseModel {
    private List<CouponProductsBean.ProductListBean> list;

    public CouponProductsModel(Context context) {
        super(context);
        list = new ArrayList<>();
    }

    public void getCouponProduct(String loginId, String couponGuid,final ModelCallBack callBack) {
        Call<CouponProductsBean> call = retrofit.create(ApiService.class).getProductsFromCoupon("0345",couponGuid,loginId);
        call.enqueue(new Callback<CouponProductsBean>() {
            @Override
            public void onResponse(Call<CouponProductsBean> call, Response<CouponProductsBean> response) {
                if (response.code() != RESULT_OK || response.body() == null) {
                    callBack.requestError();
                    return;
                }

                if ("false".equals(response.body().getResult()) && "-1".equals(response.body().getCode())) {
                    callBack.tokenException(response.body().getCode(), response.body().getInfo());
                    return;
                }
                List<CouponProductsBean.ProductListBean> beanList = response.body().getProductList();
                if (beanList == null) {
                    callBack.requestError();
                    return;
                }
                if (list.size() > 0) {
                    list.clear();
                }
                list.addAll(beanList);
                callBack.requestSuccess();
            }

            @Override
            public void onFailure(Call<CouponProductsBean> call, Throwable t) {
                callBack.requestFailure();
            }
        });
    }

    public List<CouponProductsBean.ProductListBean> getList() {
        return list;
    }
}
