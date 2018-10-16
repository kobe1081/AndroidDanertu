package com.danertu.dianping.fragment.warehouse;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.config.Constants;
import com.danertu.base.BasePresenter;
import com.danertu.base.IPresenter;

import static com.danertu.base.BaseModel.MSG_SERVER_ERROR;


/**
 * Created by Viz on 2017/12/21.
 */

public class WarehousePresenter extends BasePresenter<WarehouseView> implements IPresenter {
    public static final int MSG_DATA_SUCCESS = 111;
    public static final int MSG_DATA_FAIL = 112;
    public static final int MSG_LOAD_MORE_SUCCESS = 1113;
    public static final int MSG_LOAD_MORE_FAIL = 114;
    public static final int MSG_LOAD_MORE_NO_DATA = 115;
    static final int MSG_NEED_LOGIN = 116;

    private WarehouseModel model;
    private int currentPage = 1;
    private String productCategoryId;
    private String orderBy;
    private boolean isLoading = false;//表示是否正在加载数据，防止某些情况下重复请求

    public WarehousePresenter(Context context) {
        super(context);
        model = new WarehouseModel(context);
        initHandler();
    }

    @Override
    public void initHandler() {
        handler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_SERVER_ERROR:
                        view.jsShowToast("数据加载失败");
                        view.jsFinish();
                        break;
                    case MSG_DATA_SUCCESS:
                        isLoading = false;
                        view.updateView(model.getProductList().size());
                        view.stopFresh();
                        view.hideLoading();
                        break;
                    case MSG_DATA_FAIL:
                        isLoading = false;
                        view.jsShowToast("数据加载失败");
                        view.jsFinish();
                        break;
                    case MSG_LOAD_MORE_SUCCESS:
                        isLoading = false;
                        view.updateView();
                        view.hideLoading();
                        view.stopLoadMore();
                        break;
                    case MSG_LOAD_MORE_FAIL:
                        view.hideLoading();
                        view.stopLoadMore();
                        isLoading = false;
                        view.jsShowToast("加载更多数据时发生错误");
                        --currentPage;
                        break;
                    case MSG_LOAD_MORE_NO_DATA:
                        --currentPage;
                        isLoading = false;
                        if (isViewAttached()){
                            view.noMoreData();
                            view.stopLoadMore();
                            view.jsShowToast("已无更多数据");
                        }
                        break;
                    case MSG_NEED_LOGIN:
                        if (isViewAttached()){
                            view.jsShowMsg("您的登录信息已过期，请重新登录");
                            view.quitAccount();
                            view.jsFinish();
                            view.jsStartActivity("LoginActivity");
                        }
                        break;
                }
            }
        };
    }

    @Override
    public void onCreateView() {
        view.initListView(model.getProductList());
        view.jsShowLoading();
        model.clearData();
        view.updateView();
        productCategoryId = "";//重置筛选条件
        loadData(currentPage);
    }

    public void clearData() {
        model.clearData();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }

    public void loadData(int page) {
        loadData(page, productCategoryId, orderBy);
    }

    public void loadData(int page, String productCategoryId, String orderBy) {
        model.getStockList(handler, view.getUid(), page, Constants.pageSize, productCategoryId, orderBy);
    }

    /**
     * @param orderBy 1===库存降序排列
     */
    public void loadData(String orderBy) {
        view.showLoading();
        model.clearData();
        view.updateView();
        currentPage = 1;
        model.getStockList(handler, view.getUid(), currentPage, Constants.pageSize, productCategoryId, orderBy);
    }

    public void filterData(String productCategoryId, String orderBy) {
        this.productCategoryId = productCategoryId;
        this.orderBy = orderBy;
        model.getStockList(handler, view.getUid(), currentPage, Constants.pageSize, productCategoryId, orderBy);
    }

    /**
     * 过滤数据
     *
     * @param productCategoryId
     */
    public void filterData(String productCategoryId) {
        view.showLoading();
        currentPage = 1;
        model.clearData();
        view.updateView();
        filterData(productCategoryId, "");
    }

    /**
     * 加载更多
     */
    public void loadMore() {
        if (isLoading) {
            view.jsShowToast("正在加载更多数据，请稍候");
            return;
        }
        isLoading = true;
        loadData(++currentPage);
    }

    /**
     * 刷新
     */

    public void refresh() {
        view.showLoading();
        model.clearData();
        view.updateView();
        currentPage = 1;
        productCategoryId = "";
        orderBy = "";
        loadData(currentPage);
    }

    public void showStockPopup() {
        view.showStockPopup(model.getCategoryList());
    }

}
