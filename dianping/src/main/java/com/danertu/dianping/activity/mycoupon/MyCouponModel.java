package com.danertu.dianping.activity.mycoupon;

import android.os.Handler;

import com.config.ApiService;
import com.danertu.base.BaseModel;
import com.danertu.entity.CouponBean;
import com.danertu.entity.MyCouponBean;
import com.danertu.entity.ShopDetailBean;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.danertu.dianping.activity.mycoupon.MyCouponPresenter.WHAT_COUPON_LIST_ERROR;
import static com.danertu.dianping.activity.mycoupon.MyCouponPresenter.WHAT_COUPON_LIST_FAIL;
import static com.danertu.dianping.activity.mycoupon.MyCouponPresenter.WHAT_COUPON_LIST_SUCCESS;
import static com.danertu.dianping.activity.mycoupon.MyCouponPresenter.WHAT_LOAD_MORE_ERROR;
import static com.danertu.dianping.activity.mycoupon.MyCouponPresenter.WHAT_LOAD_MORE_FAIL;
import static com.danertu.dianping.activity.mycoupon.MyCouponPresenter.WHAT_LOAD_MORE_SUCCESS;
import static com.danertu.dianping.activity.mycoupon.MyCouponPresenter.WHAT_NO_MORE_DATA;
import static com.danertu.dianping.activity.mycoupon.MyCouponPresenter.WHAT_SHOP_DETAIL_ERROR;
import static com.danertu.dianping.activity.mycoupon.MyCouponPresenter.WHAT_SHOP_DETAIL_FAIL;
import static com.danertu.dianping.activity.mycoupon.MyCouponPresenter.WHAT_SHOP_DETAIL_SUCCESS;

public class MyCouponModel extends BaseModel {
    private List<MyCouponBean.CouponRecordListBean> couponList;

    public MyCouponModel() {
        super();
        couponList = new ArrayList<>();
    }


    public void getMyCouponList(final Handler handler, String uid, String isUsedOrIsDelete, final int pageIndex, int pageSize) {
        Call<MyCouponBean> call = retrofit.create(ApiService.class).getMyCouponList("0343", uid, isUsedOrIsDelete, pageIndex, pageSize);
        call.enqueue(new Callback<MyCouponBean>() {
            @Override
            public void onResponse(Call<MyCouponBean> call, Response<MyCouponBean> response) {
                MyCouponBean body = response.body();
                if (response.code() != RESULT_OK || body == null) {
                    if (pageIndex > 1) {
                        handler.sendEmptyMessage(WHAT_LOAD_MORE_FAIL);
                    } else {
                        handler.sendEmptyMessage(WHAT_COUPON_LIST_FAIL);
                    }
                    return;
                }
                List<MyCouponBean.CouponRecordListBean> list = body.getCouponRecordList();
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
                        couponList.addAll(couponList.size(), list);
                        handler.sendEmptyMessage(WHAT_COUPON_LIST_SUCCESS);
                    }
                } catch (Exception e) {
                    if (pageIndex > 1) {
                        handler.sendEmptyMessage(WHAT_LOAD_MORE_ERROR);
                    } else {
                        handler.sendEmptyMessage(WHAT_COUPON_LIST_ERROR);
                    }
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<MyCouponBean> call, Throwable t) {
                if (pageIndex > 1) {
                    handler.sendEmptyMessage(WHAT_LOAD_MORE_ERROR);
                } else {
                    handler.sendEmptyMessage(WHAT_COUPON_LIST_ERROR);
                }
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
                sendMessage(handler, WHAT_SHOP_DETAIL_SUCCESS, Integer.parseInt(response.body().getShopdetails().getShopbean().get(0).getLeveltype()),shopId);
            }

            @Override
            public void onFailure(Call<ShopDetailBean> call, Throwable t) {
                handler.sendEmptyMessage(WHAT_SHOP_DETAIL_FAIL);
            }
        });
    }

    public List<MyCouponBean.CouponRecordListBean> getCouponList() {
        return couponList;
    }
}
