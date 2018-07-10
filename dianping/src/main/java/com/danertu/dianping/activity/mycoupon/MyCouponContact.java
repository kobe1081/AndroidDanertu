package com.danertu.dianping.activity.mycoupon;

import android.content.Intent;

import com.danertu.base.BaseView;
import com.danertu.base.IPresenter;
import com.danertu.entity.CouponBean;
import com.danertu.entity.MyCouponBean;

import java.util.List;

public interface MyCouponContact {
    interface MyCouponView extends BaseView {
        void initList(List<MyCouponBean.CouponRecordListBean> list);

        void notifyChange(int listSize);

        void setRefresh(boolean isRefresh);

        void canLoadMore(boolean loadMore);

        void stopRefresh();

        void stopLoadMore();

        void getDataFail();

        void toAgentShop(int levelType, String shopId);
    }

    interface IMyCouponPresenter extends IPresenter {
        void loadData();

        void loadData(int page);

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void refresh();

        void loadMore();

        void toAgentShopIndex(String useAgentAppoint);
    }
}
