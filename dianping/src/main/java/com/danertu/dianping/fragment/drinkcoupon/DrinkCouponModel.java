package com.danertu.dianping.fragment.drinkcoupon;

import android.os.Handler;

import com.config.ApiService;
import com.danertu.base.BaseModel;
import com.danertu.entity.BaseResultBean;
import com.danertu.entity.CouponBean;
import com.danertu.entity.ShopDetailBean;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.danertu.dianping.fragment.drinkcoupon.DrinkCouponPresenter.WHAT_GET_COUPON_ERROR;
import static com.danertu.dianping.fragment.drinkcoupon.DrinkCouponPresenter.WHAT_GET_COUPON_FAIL;
import static com.danertu.dianping.fragment.drinkcoupon.DrinkCouponPresenter.WHAT_GET_COUPON_SUCCESS;
import static com.danertu.dianping.fragment.drinkcoupon.DrinkCouponPresenter.WHAT_LIST_ERROR;
import static com.danertu.dianping.fragment.drinkcoupon.DrinkCouponPresenter.WHAT_LIST_FAIL;
import static com.danertu.dianping.fragment.drinkcoupon.DrinkCouponPresenter.WHAT_LIST_SUCCESS;
import static com.danertu.dianping.fragment.drinkcoupon.DrinkCouponPresenter.WHAT_LOAD_MORE_ERROR;
import static com.danertu.dianping.fragment.drinkcoupon.DrinkCouponPresenter.WHAT_LOAD_MORE_FAIL;
import static com.danertu.dianping.fragment.drinkcoupon.DrinkCouponPresenter.WHAT_LOAD_MORE_SUCCESS;
import static com.danertu.dianping.fragment.drinkcoupon.DrinkCouponPresenter.WHAT_NO_MORE_DATA;
import static com.danertu.dianping.fragment.drinkcoupon.DrinkCouponPresenter.WHAT_SHOP_DETAIL_ERROR;
import static com.danertu.dianping.fragment.drinkcoupon.DrinkCouponPresenter.WHAT_SHOP_DETAIL_FAIL;
import static com.danertu.dianping.fragment.drinkcoupon.DrinkCouponPresenter.WHAT_SHOP_DETAIL_SUCCESS;

public class DrinkCouponModel extends BaseModel {
    private List<CouponBean.CouponListBean> couponList;
    private String totalCount = "0";

    public DrinkCouponModel() {
        super();
        couponList = new ArrayList<>();
    }


    public void getCouponList(final Handler handler, String uid, String useScope, final int pageIndex, int pageSize) {
        Call<CouponBean> call = retrofit.create(ApiService.class).getCouponCenterList("0341", uid, useScope, pageIndex, pageSize);
        call.enqueue(new Callback<CouponBean>() {
            @Override
            public void onResponse(Call<CouponBean> call, Response<CouponBean> response) {
                CouponBean body = response.body();
                if (response.code() != RESULT_OK || body == null) {
                    if (pageIndex > 1) {
                        handler.sendEmptyMessage(WHAT_LOAD_MORE_FAIL);
                    } else {
                        handler.sendEmptyMessage(WHAT_LIST_FAIL);
                    }
                    return;
                }
                List<CouponBean.CouponListBean> list = body.getCouponList();
                totalCount = body.getTotalCount_o() == null ? "0" : body.getTotalCount_o();
                try {
                    if (pageIndex > 1) {
                        int totalCountInt = Integer.parseInt(body.getTotalCount_o());
                        int totalPage = Integer.parseInt(body.getTotalPageCount_o());
                        if (pageIndex >= totalPage) {
                            if (couponList.size() + list.size() <= totalCountInt) {
                                couponList.addAll(couponList.size(), list);
                                handler.sendEmptyMessage(WHAT_LOAD_MORE_SUCCESS);
                            } else {
                                handler.sendEmptyMessage(WHAT_NO_MORE_DATA);
                            }
                        } else {
                            couponList.addAll(couponList.size(), list);
                            handler.sendEmptyMessage(WHAT_LOAD_MORE_SUCCESS);
                        }
                    } else {
                        handler.sendEmptyMessage(WHAT_LIST_SUCCESS);
                        couponList.addAll(couponList.size(), list);
                    }
                } catch (Exception e) {
                    if (pageIndex > 1) {
                        handler.sendEmptyMessage(WHAT_LOAD_MORE_ERROR);
                    } else {
                        handler.sendEmptyMessage(WHAT_LIST_ERROR);
                    }
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<CouponBean> call, Throwable t) {
                if (pageIndex > 1) {
                    handler.sendEmptyMessage(WHAT_LOAD_MORE_ERROR);
                } else {
                    handler.sendEmptyMessage(WHAT_LIST_ERROR);
                }
            }
        });
    }

    public void getCoupon(final Handler handler, final int position, String loginId, String couponGuid) {
        Call<BaseResultBean> call = retrofit.create(ApiService.class).getCoupon("0342", loginId, couponGuid);
        call.enqueue(new Callback<BaseResultBean>() {
            @Override
            public void onResponse(Call<BaseResultBean> call, Response<BaseResultBean> response) {
                BaseResultBean body = response.body();
                if (response.code() != RESULT_OK || body == null) {
                    handler.sendEmptyMessage(WHAT_GET_COUPON_FAIL);
                    return;
                }
                if ("true".equals(body.getResult())) {
                    sendMessage(handler, WHAT_GET_COUPON_SUCCESS, position);
                } else {
                    sendMessage(handler, WHAT_GET_COUPON_FAIL, position, body.getInfo());
                }
            }

            @Override
            public void onFailure(Call<BaseResultBean> call, Throwable t) {
                handler.sendEmptyMessage(WHAT_GET_COUPON_ERROR);
            }
        });
    }

    public void getShopDetail(final Handler handler, final String shopId) {
        Call<ShopDetailBean> call = retrofit.create(ApiService.class).getShopDetail("0041", shopId, "", "");
        call.enqueue(new Callback<ShopDetailBean>() {
            @Override
            public void onResponse(Call<ShopDetailBean> call, Response<ShopDetailBean> response) {
                if (response.code() != RESULT_OK || response.body() == null) {
                    handler.sendEmptyMessage(WHAT_SHOP_DETAIL_ERROR);
                    return;
                }
                sendMessage(handler, WHAT_SHOP_DETAIL_SUCCESS, Integer.parseInt(response.body().getShopdetails().getShopbean().get(0).getLeveltype()), shopId);
            }

            @Override
            public void onFailure(Call<ShopDetailBean> call, Throwable t) {
                handler.sendEmptyMessage(WHAT_SHOP_DETAIL_FAIL);
            }
        });
    }

    public List<CouponBean.CouponListBean> getCouponList() {
        return couponList;
    }

    public String getTotalCount() {
        return totalCount;
    }
}
