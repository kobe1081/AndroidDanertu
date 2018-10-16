package com.danertu.dianping.activity.choosecoupon;

import android.content.Context;

import com.config.ApiService;
import com.danertu.base.BaseModel;
import com.danertu.base.ModelCallBack;
import com.danertu.entity.ChooseCouponBean;
import com.danertu.entity.MyCouponBean;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseCouponModel extends BaseModel {
    private List<ChooseCouponBean.ValBean> couponList;
    private String totalCount = "0";

    public ChooseCouponModel(Context context) {
        super(context);
        couponList = new ArrayList<>();
    }

    public void getCouponList(String multiParam, String memLoginId, final int pageIndex, int pageSize, final ModelCallBack callBack) {
        Call<ChooseCouponBean> call = retrofit.create(ApiService.class).chooseCoupon("0344", memLoginId, multiParam);
        call.enqueue(new Callback<ChooseCouponBean>() {
            @Override
            public void onResponse(Call<ChooseCouponBean> call, Response<ChooseCouponBean> response) {
                ChooseCouponBean body = response.body();
                if (response.code() != RESULT_OK || body == null) {
                    callBack.requestError();
                    return;
                }
                if ("false".equals(response.body().getResult()) && "-1".equals(response.body().getCode())) {
                    callBack.tokenException(response.body().getCode(), response.body().getInfo());
                    return;
                }
                List<ChooseCouponBean.ValBean> list = body.getVal();
                if (list == null) {
                    callBack.requestError();
                    return;
                }
                if (couponList.size() > 0) {
                    couponList.clear();
                }
                couponList.addAll(list);
                callBack.requestSuccess();

            }

            @Override
            public void onFailure(Call<ChooseCouponBean> call, Throwable t) {
                callBack.requestFailure();
            }
        });
    }

    public List<ChooseCouponBean.ValBean> getCouponList() {
        return couponList;
    }
}
