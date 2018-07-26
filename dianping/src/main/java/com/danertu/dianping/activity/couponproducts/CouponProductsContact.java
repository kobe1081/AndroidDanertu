package com.danertu.dianping.activity.couponproducts;

import android.content.Intent;

import com.danertu.base.BaseView;
import com.danertu.base.IPresenter;
import com.danertu.entity.CouponProductsBean;

import java.util.List;

public interface CouponProductsContact {
    interface CouponProductsView extends BaseView {
        void initList(List<CouponProductsBean.ProductListBean> list);

        void notifyChange(int listSize);

        void stopRefresh();
    }

    interface ICouponProductsPresenter extends IPresenter {
        void onCreateView(Intent intent);

        void refresh();

        void loadData();
    }
}
