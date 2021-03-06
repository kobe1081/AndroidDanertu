package com.danertu.dianping.fragment.drinkcoupon;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.config.Constants;
import com.danertu.base.NewBasePresenter;
import com.danertu.entity.CouponBean;
import com.danertu.tools.DateTimeUtils;

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
    static final int WHAT_NEED_LOGIN=316;
    private int currentPage = 1;

    public DrinkCouponPresenter(Context context) {
        super(context);
    }

    @Override
    public void initHandler() {
        handler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (isViewAttached()) {
                    view.jsHideLoading();
                }
                switch (msg.what) {
                    case WHAT_LIST_SUCCESS:
                        if (isViewAttached()) {
                            view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                            view.stopRefresh();
                        }
                        break;
                    case WHAT_LIST_FAIL:
                        if (isViewAttached()) {
                            view.jsShowMsg("获取酒水优惠券失败");
                            view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                            view.stopRefresh();
                        }
                        break;
                    case WHAT_LIST_ERROR:
                        if (isViewAttached()) {
                            view.jsShowMsg("获取酒水优惠券失败");
                            view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                            view.stopRefresh();
                        }
                        break;
                    case WHAT_REFRESH_SUCCESS:
                        if (isViewAttached()) {
                            view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                            view.stopRefresh();
                        }
                        break;
                    case WHAT_REFRESH_FAIL:
                        if (isViewAttached()) {
                            view.stopRefresh();
                        }
                        break;
                    case WHAT_REFRESH_ERROR:
                        if (isViewAttached()) {
                            view.stopRefresh();
                        }
                        break;
                    case WHAT_LOAD_MORE_SUCCESS:
                        if (isViewAttached()) {
                            view.notifyChange(model.getCouponList().size(), model.getTotalCount());
                            view.stopLoadMore();
                        }
                        break;
                    case WHAT_LOAD_MORE_FAIL:
                        --currentPage;
                        if (isViewAttached()) {
                            view.stopLoadMore();
                        }
                        break;
                    case WHAT_LOAD_MORE_ERROR:
                        --currentPage;
                        if (isViewAttached()) {
                            view.stopLoadMore();
                        }
                        break;
                    case WHAT_NO_MORE_DATA:
                        if (isViewAttached()) {
                            view.jsShowMsg("已无更多优惠券");
                            view.noMoreData();
                        }
                        break;
                    case WHAT_GET_COUPON_SUCCESS:
                        if (isViewAttached()) {
                            int position = msg.arg1;
                            List<CouponBean.CouponListBean> list = model.getCouponList();
                            CouponBean.CouponListBean bean = list.get(position);
                            bean.setIsUsed("0");
                            bean.setShopId(view.getShopId());
                            bean.setGetTime(DateTimeUtils.getDateToyyyyMMddHHmmss());
                            bean.setCouponRecordGuid(msg.obj.toString());
                            switch (bean.getUseValidityType()) {
                                case "1":
                                    bean.setEndTime(DateTimeUtils.getSpecifiedDayAfterN(bean.getGetTime(), Integer.parseInt(bean.getUseFromTomorrow())));
                                    break;
                                case "2":
                                    bean.setEndTime(DateTimeUtils.getSpecifiedDayAfterN(bean.getGetTime(), Integer.parseInt(bean.getUseFromToday())));
                                    break;
                            }
                            bean.setRemainCount("" + (Integer.parseInt(bean.getRemainCount()) - 1));
                            view.notifyChange(list.size(), model.getTotalCount());
                            view.jsSetResult(RESULT_GET_COUPON);
                            view.jsShowMsg("领取成功");
                        }
                        break;
                    case WHAT_GET_COUPON_FAIL:
                        if (msg.obj != null) {
                            if (isViewAttached()) {
                                view.jsShowMsg(msg.obj.toString());
                            }
                        }
                        break;
                    case WHAT_GET_COUPON_ERROR:
                        if (isViewAttached()) {
                            view.jsShowMsg("领取失败");
                        }
                        break;
                    case WHAT_SHOP_DETAIL_SUCCESS:
                        if (isViewAttached()) {
                            view.toAgentShop(msg.arg1, msg.obj.toString());
                        }
                        break;
                    case WHAT_SHOP_DETAIL_FAIL:
                        if (isViewAttached()) {
                            view.jsShowMsg("获取经销商信息失败");
                        }
                        break;
                    case WHAT_SHOP_DETAIL_ERROR:
                        if (isViewAttached()) {
                            view.jsShowMsg("获取经销商信息失败");
                        }
                        break;
                    case WHAT_NEED_LOGIN:
                        if (isViewAttached()){
                            view.jsShowMsg("您的登录信息已过期，请重新登录");
                            view.quitAccount();
                            view.jsFinish();
                            view.jsStartActivity("LoginActivity");
                        }
                        break;

                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public DrinkCouponModel initModel() {
        return new DrinkCouponModel(context);
    }

    @Override
    public void onCreateView() {
        if (isViewAttached()) {
            view.jsShowLoading();
            view.initList(model.getCouponList());
        }
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
        model.getCouponList(handler, getUid(), "2", page, Constants.pageSize);
    }

    @Override
    public void loadMore() {
        if (isViewAttached()) {
            view.jsShowLoading();
        }
        loadData(++currentPage);
    }

    @Override
    public void refresh() {
        if (isViewAttached()) {
            view.jsShowMsg("正在刷新...");
        }
        model.getCouponList().clear();
        currentPage = 1;
        loadData(currentPage);
    }

    @Override
    public void getCoupon(int position, String guid) {
        if (isViewAttached()) {
            model.getCoupon(handler, position, view.getShopId(), model.getUid(context), guid);
        }
    }

    @Override
    public void toAgentShopIndex(String shopId) {
        if (isViewAttached()) {
            view.jsShowLoading();
        }
        if (shopId.equals(Constants.CK_SHOPID) || shopId.equals("chunkang")) {
            if (isViewAttached()) {
                view.jsHideLoading();
                view.toAgentShop(1, Constants.CK_SHOPID);
            }
            return;
        }
        model.getShopDetail(handler, shopId);
    }
}
