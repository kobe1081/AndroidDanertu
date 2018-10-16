package com.danertu.dianping.activity.coupondetail;

import android.content.Intent;

import com.danertu.base.BaseView;
import com.danertu.base.IPresenter;
import com.danertu.entity.CouponAllBean;
import com.danertu.entity.CouponBean;
import com.danertu.entity.CouponDetailBean;
import com.danertu.entity.CouponProductsBean;

/**
 * 作者:  Viz
 * 日期:  2018/8/1 11:17
 * <p>
 * 包名：com.danertu.dianping.activity.coupondetail
 * 文件名：CouponDetailContact
 * 描述：TODO
 */
public interface CouponDetailContact {
    interface CouponDetailView extends BaseView {

        void showCoupon(CouponBean.CouponListBean couponDetailBean, CouponProductsBean productsBean);

        void updateCouponState(String isUsed);

        void toAgentShop(String levelType, String shopId);
    }

    interface ICouponDetailPresenter extends IPresenter {

        void onCreate(Intent intent);

        void toAgentShopIndex(String shopId);

        void getCoupon(String couponGuid, String uid, String shopId);

        void getCouponProduct(String couponGuid);
    }
}
