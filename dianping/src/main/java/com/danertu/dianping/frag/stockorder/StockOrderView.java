package com.danertu.dianping.frag.stockorder;

import com.danertu.base.BaseView;
import com.danertu.entity.WareHouseOrderBean;

import java.util.List;

/**
 * Created by Viz on 2017/12/21.
 */

public interface StockOrderView extends BaseView {
    void updateView();

    void updateView(int listSize);

    void initList(List<WareHouseOrderBean.WareHouseOrderListBean> list);

    void stopLoadMore();

    void noMoreData();

    void showLoading();

    void hideLoading();

    void stopRefresh();

}
