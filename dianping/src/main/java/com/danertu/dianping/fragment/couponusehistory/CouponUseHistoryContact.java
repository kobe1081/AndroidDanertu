package com.danertu.dianping.fragment.couponusehistory;

import com.danertu.base.BaseView;
import com.danertu.base.IPresenter;
import com.danertu.entity.MyCouponBean;

import java.util.List;

public interface CouponUseHistoryContact {


    interface CouponUseHistoryView extends BaseView {
        void initList(List<MyCouponBean.CouponRecordListBean> list);

        void notifyChange(int listSize,String totalCount);

        void stopRefresh();

        void stopLoadMore();

        void noMoreData();
    }

    interface ICouponUseHistoryPresenter extends IPresenter {
        void loadData();

        void loadData(int page);

        void loadMore();

        void refresh();
    }
}
