package com.danertu.dianping.activity.mycoupon;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.config.Constants;
import com.danertu.base.ModelParamCallBack;
import com.danertu.base.NewBasePresenter;
import com.danertu.entity.LeaderBean;
import com.danertu.entity.ShopStateBean;
import com.danertu.tools.Logger;

import java.util.List;

public class MyCouponPresenter extends NewBasePresenter<MyCouponContact.MyCouponView, MyCouponModel> implements MyCouponContact.IMyCouponPresenter {
    static final int WHAT_COUPON_LIST_SUCCESS = 10;
    static final int WHAT_COUPON_LIST_FAIL = 11;
    static final int WHAT_COUPON_LIST_ERROR = 12;
    static final int WHAT_LOAD_MORE_SUCCESS = 13;
    static final int WHAT_LOAD_MORE_FAIL = 14;
    static final int WHAT_LOAD_MORE_ERROR = 15;
    static final int WHAT_REFRESH_SUCCESS = 16;
    static final int WHAT_REFRESH_FAIL = 17;
    static final int WHAT_REFRESH_ERROR = 18;
    static final int WHAT_NO_MORE_DATA = 19;

    static final int WHAT_SHOP_DETAIL_SUCCESS = 20;
    static final int WHAT_SHOP_DETAIL_FAIL = 21;
    static final int WHAT_SHOP_DETAIL_ERROR = 22;
    static final int WHAT_NEED_LOGIN = 23;

    public static final int REQUEST_GET_COUPON = 99;
    public static final int RESULT_GET_COUPON = 909;

    private int currentPage = 1;

    public MyCouponPresenter(Context context) {
        super(context);
    }

    @Override
    public void onCreateView() {
        view.jsShowLoading();
        view.initList(model.getCouponList());
        currentPage = 1;
        model.checkIsOpenShop(view.getUid(), new ModelParamCallBack<ShopStateBean>() {
            @Override
            public void requestSuccess(ShopStateBean type) {
                final List<ShopStateBean.ValBean> val = type.getVal();
                if (val == null || val.size() == 0 || val.get(0) == null) {
                    //未开店,店铺id为上级的
                    model.getLeaderInfo(view.getUid(), new ModelParamCallBack<LeaderBean>() {

                        @Override
                        public void requestSuccess(LeaderBean type) {
                            if (type == null || type.getLeaderInfo() == null || type.getLeaderInfo().getLeaderBean() == null || type.getLeaderInfo().getLeaderBean().size() == 0 || type.getLeaderInfo().getLeaderBean().get(0) == null) {
                                if (isViewAttached()) {
                                    view.jsShowMsg("出现错误");
                                    view.jsFinish();
                                }
                            } else {
                                String memberid = type.getLeaderInfo().getLeaderBean().get(0).getMemberid();
                                if ("chunkang".equals(memberid)) {
                                    memberid = Constants.CK_SHOPID;
                                }
                                if (isViewAttached()) {
                                    view.setShopId(memberid);
                                    loadData(currentPage);
                                }
                            }
                        }

                        @Override
                        public void tokenException(String code, String info) {
                            if (isViewAttached()) {
                                view.jsShowMsg(info);
                                view.quitAccount();
                                view.jsFinish();
                                view.jsStartActivity("LoginActivity");
                            }
                        }

                        @Override
                        public void requestError(LeaderBean type) {
                            if (isViewAttached()) {
                                view.jsShowMsg("出现错误");
                                view.jsFinish();
                            }
                        }

                        @Override
                        public void requestFailure(LeaderBean type) {
                            if (isViewAttached()) {
                                view.jsShowMsg("出现错误");
                                view.jsFinish();
                            }
                        }
                    });
                } else {
                    //已开店，店铺id为自己的
                    if (isViewAttached()) {
                        view.setShopId(val.get(0).getMemberID());
                        loadData(currentPage);
                    }
                }
            }

            @Override
            public void tokenException(String code, String info) {
                if (isViewAttached()) {
                    view.jsShowMsg(info);
                    view.quitAccount();
                    view.jsFinish();
                    view.jsStartActivity("LoginActivity");
                }
            }

            @Override
            public void requestError(ShopStateBean type) {
                if (isViewAttached()) {
                    view.jsShowMsg("出现错误");
                    view.jsFinish();
                }
            }

            @Override
            public void requestFailure(ShopStateBean type) {
                if (isViewAttached()) {
                    view.jsShowMsg("出现错误");
                    view.jsFinish();
                }
            }
        });


    }

    @Override
    public void initHandler() {
        handler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                view.jsHideLoading();
                switch (msg.what) {
                    case WHAT_COUPON_LIST_SUCCESS:
                        if (isViewAttached()) {
                            view.stopRefresh();
                            view.notifyChange(model.getCouponList().size());
                        }
                        break;
                    case WHAT_COUPON_LIST_FAIL:
                        if (isViewAttached()) {
                            view.jsShowMsg("数据获取失败");
                            view.stopRefresh();
                            view.getDataFail();
                        }
                        break;
                    case WHAT_COUPON_LIST_ERROR:
                        if (isViewAttached()) {
                            view.jsShowMsg("数据获取失败");
                            view.stopRefresh();
                            view.getDataFail();
                        }
                        break;
                    case WHAT_LOAD_MORE_SUCCESS:
                        if (isViewAttached()) {
                            view.notifyChange(model.getCouponList().size());
                            view.stopLoadMore();
                        }
                        break;
                    case WHAT_LOAD_MORE_FAIL:
                        --currentPage;
                        if (isViewAttached()) {
                            view.jsShowMsg("加载更多数据失败");
                            view.stopLoadMore();
                        }
                        break;
                    case WHAT_LOAD_MORE_ERROR:
                        --currentPage;
                        if (isViewAttached()) {
                            view.jsShowMsg("加载更多数据失败");
                            view.stopLoadMore();
                        }
                        break;
                    case WHAT_REFRESH_SUCCESS:
                        currentPage = 0;
                        if (isViewAttached()) {
                            view.notifyChange(model.getCouponList().size());
                        }
                        break;
                    case WHAT_REFRESH_FAIL:
                        if (isViewAttached()) {
                            view.jsShowMsg("刷新失败");
                            view.stopRefresh();
                        }
                        break;
                    case WHAT_REFRESH_ERROR:
                        if (isViewAttached()) {
                            view.jsShowMsg("刷新失败");
                            view.stopRefresh();
                        }
                        break;
                    case WHAT_NO_MORE_DATA:
                        if (isViewAttached()) {
                            view.jsShowMsg("已无更多优惠券");
                            view.stopLoadMore();
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
                        if (isViewAttached()) {
                            view.jsShowMsg(msg.obj == null ? "" : msg.obj.toString());
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
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages("");
            handler = null;
        }
    }


    @Override
    public MyCouponModel initModel() {
        return new MyCouponModel(context);
    }

    @Override
    public void loadData() {
        loadData(++currentPage);
    }

    @Override
    public void loadData(final int page) {
        model.getMyCouponList(handler, model.getUid(context), "0", page, Constants.pageSize);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GET_COUPON:
                if (resultCode == RESULT_GET_COUPON) {
                    refresh();
                }
                break;
        }
    }

    @Override
    public void refresh() {
        model.getCouponList().clear();
        currentPage = 1;
        loadData(currentPage);

    }

    @Override
    public void loadMore() {
        loadData();
    }

    @Override
    public void toAgentShopIndex(final String useAgentAppoint) {
        if (isViewAttached()) {
            view.jsShowLoading();
        }
        if (useAgentAppoint.equals(Constants.CK_SHOPID) || useAgentAppoint.equals("chunkang")) {
            if (isViewAttached()) {
                view.jsHideLoading();
                view.toAgentShop(1, Constants.CK_SHOPID);
            }
            return;
        }
        model.getShopDetail(handler, useAgentAppoint);

    }
}
