package com.danertu.dianping.activity.coupondetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.config.Constants;
import com.danertu.base.ModelCallBack;
import com.danertu.base.ModelParamCallBack;
import com.danertu.base.NewBasePresenter;
import com.danertu.dianping.LoginActivity;
import com.danertu.entity.BaseResultBean;
import com.danertu.entity.CouponAllBean;
import com.danertu.entity.CouponBean;
import com.danertu.entity.CouponDetailBean;
import com.danertu.entity.ShopDetailBean;
import com.google.gson.Gson;

/**
 * 作者:  Viz
 * 日期:  2018/8/1 11:18
 * <p>
 * 包名：com.danertu.dianping.activity.coupondetail
 * 文件名：CouponDetailPresenter
 * 描述：TODO
 */
public class CouponDetailPresenter extends NewBasePresenter<CouponDetailContact.CouponDetailView, CouponDetailModel> implements CouponDetailContact.ICouponDetailPresenter {

    public static final int RESULT_COUPON_GET = 212;
    private int position;
    private String couponGuid;
    private String couponRecordGuid;
    private String isUsed;

    public CouponDetailPresenter(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Intent intent) {
        if (isViewAttached()) {
            view.jsShowLoading();
        }
        Bundle extras = intent.getExtras();
        position = extras.getInt("position", -1);
        couponGuid = extras.getString("couponGuid", "");
        couponRecordGuid = extras.getString("couponRecordGuid", "");
        isUsed = extras.getString("isUsed", "");

        if (TextUtils.isEmpty(couponGuid) && TextUtils.isEmpty(couponRecordGuid)) {
            if (isViewAttached()) {
                view.jsShowMsg("出现错误");
                return;
            }
        }
        if ("0".equals(isUsed)) {
            model.getCouponDetail(couponRecordGuid, getUid(), new ModelCallBack() {
                @Override
                public void requestSuccess() {
                    CouponBean.CouponListBean couponDetailBean = model.getCouponDetailBean();
                    if ("4".equals(couponDetailBean.getUseScope())) {
                        getCouponProduct(couponGuid);
                    } else {
                        if (isViewAttached()) {
                            view.jsHideLoading();
                            view.showCoupon(couponDetailBean, null);
                        }
                    }

                }

                @Override
                public void tokenException(String code,String info) {
                    if (isViewAttached()) {
                        view.jsShowMsg(info);
                        view.quitAccount();
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                        view.jsFinish();
                    }
                }

                @Override
                public void requestError() {
                    if (isViewAttached()) {
                        view.jsShowMsg("获取优惠券数据失败");
                        view.jsFinish();
                    }
                }

                @Override
                public void requestFailure() {
                    if (isViewAttached()) {
                        view.jsShowMsg("获取优惠券数据失败");
                        view.jsFinish();
                    }
                }
            });
        } else {
            model.getCouponDetail(couponGuid, new ModelCallBack() {
                @Override
                public void requestSuccess() {
                    CouponBean.CouponListBean couponDetailBean = model.getCouponDetailBean();
                    if ("4".equals(couponDetailBean.getUseScope())) {
                        getCouponProduct(couponGuid);
                    } else {
                        if (isViewAttached()) {
                            view.jsHideLoading();
                            view.showCoupon(couponDetailBean, null);
                        }
                    }
                }

                @Override
                public void tokenException(String code,String info) {
                    if (isViewAttached()) {
                        view.jsShowMsg(info);
                        view.quitAccount();
                        view.jsFinish();
                        view.jsStartActivity("LoginActivity");
                    }
                }

                @Override
                public void requestError() {
                    if (isViewAttached()) {
                        view.jsShowMsg("获取优惠券数据失败");
                        view.jsFinish();
                    }
                }

                @Override
                public void requestFailure() {
                    if (isViewAttached()) {
                        view.jsShowMsg("获取优惠券数据失败");
                        view.jsFinish();
                    }
                }
            });
        }
    }

    @Override
    public void getCouponProduct(String couponGuid) {
        model.getProducts(couponGuid, getUid(), new ModelCallBack() {
            @Override
            public void requestSuccess() {
                if (isViewAttached()) {
                    view.jsHideLoading();
                    view.showCoupon(model.getCouponDetailBean(), model.getCouponProductsBean());
                }
            }

            @Override
            public void tokenException(String code,String info) {
                if (isViewAttached()) {
                    view.jsShowMsg(info);
                    view.quitAccount();
                    view.jsFinish();
                    view.jsStartActivity("LoginActivity");
                }
            }

            @Override
            public void requestError() {
                if (isViewAttached()) {
                    view.jsShowMsg("获取优惠券数据失败");
                    view.jsFinish();
                }
            }

            @Override
            public void requestFailure() {
                if (isViewAttached()) {
                    view.jsShowMsg("获取优惠券数据失败");
                    view.jsFinish();
                }
            }
        });
    }

    @Override
    public void toAgentShopIndex(final String shopId) {
        if (isViewAttached()) {
            view.jsShowLoading();
        }
        if (shopId.equals(Constants.CK_SHOPID) || shopId.equals("chunkang")) {
            if (isViewAttached()) {
                view.jsHideLoading();
                view.toAgentShop("1", Constants.CK_SHOPID);
            }
            return;
        }
        model.getShopDetail(shopId, new ModelParamCallBack<ShopDetailBean>() {
            @Override
            public void requestSuccess(ShopDetailBean type) {
                if (isViewAttached()) {
                    view.jsHideLoading();
                    ShopDetailBean.ShopdetailsBean.ShopbeanBean bean = type.getShopdetails().getShopbean().get(0);
                    view.toAgentShop(TextUtils.isEmpty(bean.getLeveltype()) ? "1" : bean.getLeveltype(), shopId);
                }
            }

            @Override
            public void tokenException(String code, String info) {
                if (isViewAttached()) {
                    view.jsShowMsg(info);
                    view.quitAccount();
                    view.jsFinish();
                    view.jsStartActivity("LoginActivity");
                }
            }

            @Override
            public void requestError(ShopDetailBean type) {
                if (isViewAttached()) {
                    view.jsHideLoading();
                    view.jsShowMsg("获取店铺信息失败");
                }
            }

            @Override
            public void requestFailure(ShopDetailBean type) {
                if (isViewAttached()) {
                    view.jsHideLoading();
                    view.jsShowMsg("获取店铺信息失败");
                }
            }
        });

    }

    @Override
    public void getCoupon(String couponGuid, String uid, final String shopId) {
        model.getCoupon(shopId, uid, couponGuid, new ModelParamCallBack<BaseResultBean>() {
            @Override
            public void requestSuccess(BaseResultBean type) {
                if (isViewAttached()) {
                    if ("true".equals(type.getResult())) {
                        view.jsShowMsg("领取成功");
                        CouponBean.CouponListBean couponDetailBean = model.getCouponDetailBean();
                        String couponRecordGuid = type.getInfo();
                        couponDetailBean.setCouponRecordGuid(couponRecordGuid);
                        couponDetailBean.setShopId(shopId);
                        couponDetailBean.setIsUsed("0");
                        view.showCoupon(model.getCouponDetailBean(), model.getCouponProductsBean());
//                        view.updateCouponState("0");
                        view.jsSetResult(RESULT_COUPON_GET, "position|" + position + ",;isUsed|0" + ",;couponRecordGuid|" + couponRecordGuid + ",;shopId|" + shopId);
                    } else {
                        view.jsShowMsg(type.getInfo());
                    }
                    view.jsHideLoading();
                }
            }

            @Override
            public void tokenException(String code, String info) {
                if (isViewAttached()) {
                    view.jsShowMsg("您的登录信息已过期，请重新登录");
                    view.quitAccount();
                    view.jsFinish();
                    view.jsStartActivity("LoginActivity");
                }
            }

            @Override
            public void requestError(BaseResultBean type) {
                if (isViewAttached()) {
                    view.jsShowMsg("领取失败");
                    view.jsHideLoading();
                }
            }

            @Override
            public void requestFailure(BaseResultBean type) {
                if (isViewAttached()) {
                    view.jsShowMsg("领取失败");
                    view.jsHideLoading();
                }
            }
        });
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
    public CouponDetailModel initModel() {
        return new CouponDetailModel(context);
    }


}
