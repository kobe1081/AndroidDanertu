package com.danertu.dianping.activity.coupondetail;

import android.content.Context;
import android.os.Handler;

import com.config.ApiService;
import com.danertu.base.BaseModel;
import com.danertu.base.ModelCallBack;
import com.danertu.base.ModelParamCallBack;
import com.danertu.entity.BaseResultBean;
import com.danertu.entity.CouponAllBean;
import com.danertu.entity.CouponBean;
import com.danertu.entity.CouponDetailBean;
import com.danertu.entity.CouponProductsBean;
import com.danertu.entity.ShopDetailBean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 作者:  Viz
 * 日期:  2018/8/1 11:18
 * <p>
 * 包名：com.danertu.dianping.activity.coupondetail
 * 文件名：CouponDetailModel
 * 描述：TODO
 */
public class CouponDetailModel extends BaseModel {
    private CouponProductsBean couponProductsBean;
    private CouponBean.CouponListBean couponDetailBean;

    public CouponDetailModel(Context context) {
        super(context);
    }

    public CouponProductsBean getCouponProductsBean() {
        return couponProductsBean;
    }

    public CouponBean.CouponListBean getCouponDetailBean() {
        return couponDetailBean;
    }

    /**
     * 获取优惠券指定的商品列表
     *
     * @param couponGuid
     * @param callBack
     */
    public void getProducts(String couponGuid, String loginId, final ModelCallBack callBack) {
        Call<CouponProductsBean> call = retrofit.create(ApiService.class).getProductsFromCoupon("0345", couponGuid,  loginId);
        call.enqueue(new Callback<CouponProductsBean>() {
            @Override
            public void onResponse(Call<CouponProductsBean> call, Response<CouponProductsBean> response) {
                if (response.code() != RESULT_OK || response.body() == null || response.body().getProductList() == null) {
                    callBack.requestError();
                    return;
                }

                if ("false".equals(response.body().getResult()) && "-1".equals(response.body().getCode())) {
                    callBack.tokenException(response.body().getCode(), response.body().getInfo());
                    return;
                }
                couponProductsBean = response.body();
                callBack.requestSuccess();
            }

            @Override
            public void onFailure(Call<CouponProductsBean> call, Throwable t) {
                callBack.requestFailure();
            }
        });
    }

    public void getCoupon(String shopId, String loginId, String couponGuid, final ModelParamCallBack<BaseResultBean> callBack) {
        Call<BaseResultBean> call = retrofit.create(ApiService.class).getCoupon("0342", couponGuid,  loginId, shopId);
        call.enqueue(new Callback<BaseResultBean>() {
            @Override
            public void onResponse(Call<BaseResultBean> call, Response<BaseResultBean> response) {
                if (response.code() != RESULT_OK || response.body() == null) {
                    callBack.requestError(null);
                    return;
                }
                if ("false".equals(response.body().getResult()) && "-1".equals(response.body().getCode())) {
                    callBack.tokenException(response.body().getCode(), response.body().getInfo());
                    return;
                }
                callBack.requestSuccess(response.body());
            }

            @Override
            public void onFailure(Call<BaseResultBean> call, Throwable t) {
                callBack.requestFailure(null);
            }
        });
    }

    public void getShopDetail(final String shopId, final ModelParamCallBack<ShopDetailBean> callBack) {
        Call<ShopDetailBean> call = retrofit.create(ApiService.class).getShopDetail("0041", "", "", shopId);
        call.enqueue(new Callback<ShopDetailBean>() {
            @Override
            public void onResponse(Call<ShopDetailBean> call, Response<ShopDetailBean> response) {

                if (response.code() != RESULT_OK || response.body() == null || response.body().getShopdetails().getShopbean().size() == 0) {
                    callBack.requestError(null);
                    return;
                }
                callBack.requestSuccess(response.body());
            }

            @Override
            public void onFailure(Call<ShopDetailBean> call, Throwable t) {
                callBack.requestFailure(null);
            }
        });
    }

    /**
     * 获取未领取的优惠券详情
     * @param couponGuid
     * @param callBack
     */
    public void getCouponDetail(String couponGuid, final ModelCallBack callBack) {
        Call<CouponBean> call = retrofit.create(ApiService.class).getCouponDetail("0348", couponGuid);
        call.enqueue(new Callback<CouponBean>() {
            @Override
            public void onResponse(Call<CouponBean> call, Response<CouponBean> response) {
                if (response.code() != RESULT_OK || response.body() == null || response.body().getCouponList().size() == 0 || response.body().getCouponList().get(0) == null) {
                    callBack.requestError();
                    return;
                }
                couponDetailBean = response.body().getCouponList().get(0);
                callBack.requestSuccess();

            }

            @Override
            public void onFailure(Call<CouponBean> call, Throwable t) {
                callBack.requestFailure();
            }
        });
    }

    /**
     * 获取已领取的优惠券详情
     * @param couponRecordGuid
     * @param loginId
     * @param callBack
     */
    public void getCouponDetail(String couponRecordGuid, String loginId, final ModelCallBack callBack) {
        Call<CouponBean> call = retrofit.create(ApiService.class).getCouponDetail("0347", couponRecordGuid, loginId);
        call.enqueue(new Callback<CouponBean>() {
            @Override
            public void onResponse(Call<CouponBean> call, Response<CouponBean> response) {
                if (response.code() != RESULT_OK || response.body() == null || response.body().getCouponList().size() == 0 || response.body().getCouponList().get(0) == null) {
                    callBack.requestError();
                    return;
                }
                couponDetailBean = response.body().getCouponList().get(0);
                callBack.requestSuccess();

            }

            @Override
            public void onFailure(Call<CouponBean> call, Throwable t) {
                callBack.requestFailure();
            }
        });
    }
}
