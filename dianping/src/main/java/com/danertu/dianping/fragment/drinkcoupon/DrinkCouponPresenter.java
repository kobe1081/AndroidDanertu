package com.danertu.dianping.fragment.drinkcoupon;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.danertu.base.NewBasePresenter;
import com.danertu.entity.CouponBean;

import java.util.List;

import static com.danertu.dianping.activity.mycoupon.MyCouponPresenter.RESULT_GET_COUPON;


public class DrinkCouponPresenter extends NewBasePresenter<DrinkCouponContact.DrinkCouponView, DrinkCouponModel> implements DrinkCouponContact.IDrinkCouponPresenter {
    static final int WHAT_LIST_SUCCESS = 300;
    static final int WHAT_LIST_FAIL = 301;
    static final int WHAT_LIST_ERROR = 302;
    static final int WHAT_REFRESH_SUCCESS = 303;
    static final int WHAT_REFRESH_FAIL = 304;
    static final int WHAT_REFRESH_ERROR = 305;
    static final int WHAT_LOAD_MORE_SUCCESS = 306;
    static final int WHAT_LOAD_MORE_FAIL = 307;
    static final int WHAT_LOAD_MORE_ERROR = 308;
    static final int WHAT_NO_MORE_DATA = 309;
    static final int WHAT_GET_COUPON_SUCCESS = 310;
    static final int WHAT_GET_COUPON_FAIL = 311;
    static final int WHAT_GET_COUPON_ERROR = 312;

    static final int WHAT_SHOP_DETAIL_SUCCESS = 313;
    static final int WHAT_SHOP_DETAIL_FAIL = 314;
    static final int WHAT_SHOP_DETAIL_ERROR = 315;
    private int currentPage = 1;

    public DrinkCouponPresenter(Context context) {
        super(context);
    }

    @Override
    public void initHandler() {
        handler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                view.jsHideLoading();
                switch (msg.what) {
                    case WHAT_LIST_SUCCESS:
                        view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                        view.stopRefresh();
                        break;
                    case WHAT_LIST_FAIL:
                        view.jsShowMsg("获取酒水优惠券失败");
                        view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                        view.stopRefresh();
                        break;
                    case WHAT_LIST_ERROR:
                        view.jsShowMsg("获取酒水优惠券失败");
                        view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                        view.stopRefresh();
                        break;
                    case WHAT_REFRESH_SUCCESS:
                        view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                        view.stopRefresh();
                        break;
                    case WHAT_REFRESH_FAIL:
                        view.stopRefresh();
                        break;
                    case WHAT_REFRESH_ERROR:
                        view.stopRefresh();
                        break;
                    case WHAT_LOAD_MORE_SUCCESS:
                        view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                        view.stopLoadMore();
                        break;
                    case WHAT_LOAD_MORE_FAIL:
                        --currentPage;
                        view.stopLoadMore();
                        break;
                    case WHAT_LOAD_MORE_ERROR:
                        --currentPage;
                        view.stopLoadMore();
                        break;
                    case WHAT_NO_MORE_DATA:
                        view.jsShowMsg("已无更多优惠券");
                        view.noMoreData();
                        break;
                    case WHAT_GET_COUPON_SUCCESS:
                        int position = msg.arg1;
                        List<CouponBean.CouponListBean> list = model.getCouponList();
                        CouponBean.CouponListBean bean = list.get(position);
                        bean.setIsUsed("0");
                        bean.setRemainCount("" + (Integer.parseInt(bean.getRemainCount()) - 1));
                        view.notifyChange(list.size(), model.getTotalCount());
                        view.jsSetResult(RESULT_GET_COUPON);
                        view.jsShowMsg("领取成功");
                        break;
                    case WHAT_GET_COUPON_FAIL:
                        view.jsShowMsg(msg.obj.toString());
                        break;
                    case WHAT_GET_COUPON_ERROR:
                        view.jsShowMsg("领取失败");
                        break;
                    case WHAT_SHOP_DETAIL_SUCCESS:
                        view.toAgentShop(msg.arg1,msg.obj.toString());
                        break;
                    case WHAT_SHOP_DETAIL_FAIL:
                        view.jsShowMsg("获取经销商信息失败");
                        break;
                    case WHAT_SHOP_DETAIL_ERROR:
                        view.jsShowMsg("获取经销商信息失败");
                        break;

                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public DrinkCouponModel initModel() {
        return new DrinkCouponModel();
    }

    @Override
    public void onCreateView() {
        view.jsShowLoading();
        view.initList(model.getCouponList());
        loadData(currentPage);
    }

    @Override
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages("");
        }
    }

    @Override
    public void loadData() {

    }

    @Override
    public void loadData(int page) {
        model.getCouponList(handler, getUid(), "2", page, 12);
    }

    @Override
    public void loadMore() {
        view.jsShowLoading();
        loadData(++currentPage);
    }

    @Override
    public void refresh() {
        view.jsShowMsg("正在刷新...");
        model.getCouponList().clear();
        currentPage = 1;
        loadData(currentPage);
    }

    @Override
    public void getCoupon(int position, String guid) {
        model.getCoupon(handler, position, model.getUid(context), guid);
    }

    @Override
    public void toAgentShopIndex(String shopId) {
        view.jsShowLoading();
        model.getShopDetail(handler, shopId);
    }
}
