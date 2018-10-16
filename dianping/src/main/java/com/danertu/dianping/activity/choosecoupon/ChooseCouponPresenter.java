package com.danertu.dianping.activity.choosecoupon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.config.Constants;
import com.danertu.base.ModelCallBack;
import com.danertu.base.NewBasePresenter;
import com.danertu.dianping.LoginActivity;
import com.danertu.entity.ChooseCouponBean;
import com.danertu.entity.MyCouponBean;
import com.danertu.tools.Logger;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ChooseCouponPresenter extends NewBasePresenter<ChooseCouponContact.ChooseCouponView, ChooseCouponModel> implements ChooseCouponContact.IChooseCouponPresenter {

    public static final int REQUEST_CHOOSE_COUPON = 101;
    public static final int RESULT_CHOOSE_COUPON = 102;
    private int currentPage = 1;
    private String shopId;
    private String memLoginId;
    private String productGuid;
    private String multiParam;

    public ChooseCouponPresenter(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Intent intent) {
        if (isViewAttached()) {
            view.jsShowLoading();
            view.initList(model.getCouponList());
        }
        Bundle bundle = intent.getExtras();
        shopId = bundle.getString("shopid");
        multiParam = bundle.getString("multiParam");
        if (TextUtils.isEmpty(multiParam)) {
            try {
                //页面调用时multiParam为空，需要这边拼接
                String[] productGuids = bundle.getString("productGuid").split(",");
                JSONArray paramJSONArray = new JSONArray();
                JSONObject arrayItemJSONObject = new JSONObject();
                arrayItemJSONObject.put("shopId", shopId);
                JSONArray goodsArray = new JSONArray();
                for (String productGuid1 : productGuids) {
                    goodsArray.put(productGuid1);
                }
                arrayItemJSONObject.put("productGuids", goodsArray);
                paramJSONArray.put(arrayItemJSONObject);
                multiParam = paramJSONArray.toString();
            } catch (JSONException e) {
//                e.printStackTrace();
                if (isViewAttached()) {
                    view.jsShowMsg("数据出错");
                    view.jsFinish();
                }
            }
        }
        memLoginId = getUid();
        loadData(currentPage);
    }

    @Override
    public void onCreateView() {

    }


    @Override
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages("");
        }
    }

    @Override
    public void initHandler() {

    }

    @Override
    public ChooseCouponModel initModel() {
        return new ChooseCouponModel(context);
    }

    @Override
    public void loadData(int page) {
        model.getCouponList(multiParam, memLoginId, page, Constants.pageSize, new ModelCallBack() {
            @Override
            public void requestSuccess() {
                if (isViewAttached()) {
                    view.jsHideLoading();
                    view.stopRefresh();
                    view.notifyChange(model.getCouponList().size());
                }
            }

            @Override
            public void tokenException(String code, String info) {
                if (isViewAttached()) {
                    view.jsShowMsg("您的登录信息已过期，请重新登录");
                    view.quitAccount();
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                    view.jsFinish();
                }
            }

            @Override
            public void requestError() {
                if (isViewAttached()) {
                    view.jsHideLoading();
                    view.stopRefresh();
                    view.notifyChange(model.getCouponList().size());
                    view.jsShowMsg("获取优惠券失败");
                    view.stopRefresh();
                }
            }

            @Override
            public void requestFailure() {
                if (isViewAttached()) {
                    view.stopRefresh();
                    view.jsHideLoading();
                    view.notifyChange(model.getCouponList().size());
                    view.jsShowMsg("获取优惠券失败");
                }
            }
        });
    }

    @Override
    public void refresh() {
        currentPage = 1;
        model.getCouponList().clear();
        loadData(currentPage);
    }

    @Override
    public void loadMore() {
        loadData(++currentPage);
    }

    @Override
    public void chooseCoupon(int isUseCoupon, String callBackMethod, ChooseCouponBean.ValBean bean) {
        String builder = "isUseCoupon|" + isUseCoupon + ",;" +
                "callBackMethod|" + callBackMethod + ",;" +
                "data|" + new Gson().toJson(bean);
        if (isViewAttached()) {
            view.jsSetResult(RESULT_CHOOSE_COUPON, builder);
            view.jsFinish();
        }
    }
}
