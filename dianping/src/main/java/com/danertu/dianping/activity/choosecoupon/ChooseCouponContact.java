package com.danertu.dianping.activity.choosecoupon;

import android.content.Intent;

import com.danertu.base.BaseView;
import com.danertu.base.IPresenter;
import com.danertu.entity.ChooseCouponBean;
import com.danertu.entity.MyCouponBean;

import java.util.List;

public interface ChooseCouponContact {
    interface ChooseCouponView extends BaseView {
        void initList(List<ChooseCouponBean.ValBean> list);

        void notifyChange(int listSize);

        void stopRefresh();

        void stopLoadMore();
    }

    interface IChooseCouponPresenter extends IPresenter {
        void onCreate(Intent intent);

        void loadData( int page);

        void refresh();

        void loadMore();

        void chooseCoupon(int isUseCoupon, String callBackMethod, ChooseCouponBean.ValBean bean);
    }
}
