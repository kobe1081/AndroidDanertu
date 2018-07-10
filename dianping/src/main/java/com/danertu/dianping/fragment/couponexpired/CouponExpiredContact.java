package com.danertu.dianping.fragment.couponexpired;

import com.danertu.base.BaseView;
import com.danertu.base.IPresenter;
import com.danertu.entity.MyCouponBean;

import java.util.List;

public interface CouponExpiredContact {
    interface CouponExpiredView extends BaseView {
        void initList(List<MyCouponBean.CouponRecordListBean> list);

        void notifyChange(int listSize,String totalCount);

        void stopRefresh();

        void stopLoadMore();

        void noMoreData();
    }

    interface ICouponExpiredPresenter extends IPresenter {
        void loadData();

        void loadData(int page);

        void loadMore();

        void refresh();
    }
}
