package com.danertu.dianping.activity.choosecoupon;

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

    public ChooseCouponModel() {
        super();
        couponList=new ArrayList<>();
    }

    public void getCouponList(String multiParam, String memLoginId, final int pageIndex, int pageSize, final ModelCallBack callBack) {
        Call<ChooseCouponBean> call = retrofit.create(ApiService.class).chooseCoupon("0344", multiParam, memLoginId, pageIndex, pageSize);
        call.enqueue(new Callback<ChooseCouponBean>() {
            @Override
            public void onResponse(Call<ChooseCouponBean> call, Response<ChooseCouponBean> response) {
                ChooseCouponBean body = response.body();
                if (response.code() != RESULT_OK || body == null) {
                    callBack.requestError();
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
