package com.danertu.dianping.frag.warehouse;

import com.danertu.base.BaseView;
import com.danertu.entity.WarehouseBean;

import java.util.List;

/**
 * Created by Viz on 2017/12/21.
 */

public interface WarehouseView extends BaseView {
    void initListView(List<WarehouseBean.WareHouseListBean> productList);

    void updateView();

    void showStockPopup(List<WarehouseBean.ProductCategoryBean> categoryList);

    void showLoading();

    void hideLoading();

    void updateView(int size);

    void stopLoadMore();

    void noMoreData();

    void stopFresh();

}
