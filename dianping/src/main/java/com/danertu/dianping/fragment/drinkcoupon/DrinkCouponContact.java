package com.danertu.dianping.fragment.drinkcoupon;

import com.danertu.base.BaseView;
import com.danertu.base.IPresenter;
import com.danertu.entity.CouponBean;

import java.util.List;

public interface DrinkCouponContact {
    interface DrinkCouponView extends BaseView {
        void initList(List<CouponBean.CouponListBean> list);

        void notifyChange(int listSize, String totalCount);

        void stopRefresh();

        void stopLoadMore();

        void noMoreData();

        void toAgentShop(int levelType, String shopId);
    }

    interface IDrinkCouponPresenter extends IPresenter {
        void loadData();

        void loadData(int page);

        void loadMore();

        void refresh();

        void getCoupon(int position, String guid);

        void toAgentShopIndex(String useAgentAppoint);

    }
}
