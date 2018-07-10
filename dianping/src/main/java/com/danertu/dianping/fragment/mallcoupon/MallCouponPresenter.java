package com.danertu.dianping.fragment.mallcoupon;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.danertu.base.NewBasePresenter;
import com.danertu.entity.CouponBean;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;

import java.util.List;

import static com.danertu.dianping.activity.mycoupon.MyCouponPresenter.RESULT_GET_COUPON;

public class MallCouponPresenter extends NewBasePresenter<MallCouponContact.MallCouponView, MallCouponModel> implements MallCouponContact.IMallCouponPresenter {
    static final int WHAT_LIST_SUCCESS = 20;
    static final int WHAT_LIST_FAIL = 21;
    static final int WHAT_LIST_ERROR = 22;
    static final int WHAT_REFRESH_SUCCESS = 23;
    static final int WHAT_REFRESH_FAIL = 24;
    static final int WHAT_REFRESH_ERROR = 25;
    static final int WHAT_LOAD_MORE_SUCCESS = 26;
    static final int WHAT_LOAD_MORE_FAIL = 27;
    static final int WHAT_LOAD_MORE_ERROR = 28;
    static final int WHAT_NO_MORE_DATA = 29;
    static final int WHAT_GET_COUPON_SUCCESS = 30;
    static final int WHAT_GET_COUPON_FAIL = 31;
    static final int WHAT_GET_COUPON_ERROR = 32;
    static final int WHAT_SHOP_DETAIL_SUCCESS = 33;
    static final int WHAT_SHOP_DETAIL_FAIL = 34;
    static final int WHAT_SHOP_DETAIL_ERROR = 35;
    private int currentPage = 1;

    public MallCouponPresenter(Context context) {
        super(context);
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
                        view.jsShowMsg("获取商城优惠券失败");
                        view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                        view.stopRefresh();
                        break;
                    case WHAT_LIST_ERROR:
                        view.jsShowMsg("获取商城优惠券失败");
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
    public MallCouponModel initModel() {
        return new MallCouponModel();
    }

    @Override
    public void loadData() {

    }

    @Override
    public void loadData(int page) {
        model.getCouponList(handler, getUid(), "0", page, 12);
    }

    @Override
    public void loadMore() {
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
