package com.danertu.dianping.fragment.couponusehistory;

import android.content.Context;
import android.os.Handler;

import com.config.ApiService;
import com.danertu.base.BaseModel;
import com.danertu.entity.MyCouponBean;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.danertu.dianping.fragment.couponusehistory.CouponUseHistoryPresenter.WHAT_LIST_ERROR;
import static com.danertu.dianping.fragment.couponusehistory.CouponUseHistoryPresenter.WHAT_LIST_FAIL;
import static com.danertu.dianping.fragment.couponusehistory.CouponUseHistoryPresenter.WHAT_LIST_SUCCESS;
import static com.danertu.dianping.fragment.couponusehistory.CouponUseHistoryPresenter.WHAT_LOAD_MORE_ERROR;
import static com.danertu.dianping.fragment.couponusehistory.CouponUseHistoryPresenter.WHAT_LOAD_MORE_FAIL;
import static com.danertu.dianping.fragment.couponusehistory.CouponUseHistoryPresenter.WHAT_LOAD_MORE_SUCCESS;
import static com.danertu.dianping.fragment.couponusehistory.CouponUseHistoryPresenter.WHAT_NEED_LOGIN;
import static com.danertu.dianping.fragment.couponusehistory.CouponUseHistoryPresenter.WHAT_NO_MORE_DATA;

public class CouponUseHistoryModel extends BaseModel {
    private List<MyCouponBean.CouponRecordListBean> couponList;
    private String totalCount = "0";

    public CouponUseHistoryModel(Context context) {
        super(context);
        couponList = new ArrayList<>();
    }

    public List<MyCouponBean.CouponRecordListBean> getCouponList() {
        return couponList;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void getMyCouponList(final Handler handler, String uid, String isUsedOrIsDelete, final int pageIndex, int pageSize) {
        Call<MyCouponBean> call = retrofit.create(ApiService.class).getMyCouponList("0343",isUsedOrIsDelete, uid, pageIndex, pageSize);
        call.enqueue(new Callback<MyCouponBean>() {
            @Override
            public void onResponse(Call<MyCouponBean> call, Response<MyCouponBean> response) {
                MyCouponBean body = response.body();
                if (response.code() != RESULT_OK || body == null) {
                    if (pageIndex > 1) {
                        handler.sendEmptyMessage(WHAT_LOAD_MORE_FAIL);
                    } else {
                        handler.sendEmptyMessage(WHAT_LIST_FAIL);
                    }
                    return;
                }
                if ("false".equals(response.body().getResult()) && "-1".equals(response.body().getCode())) {
                    handler.sendEmptyMessage(WHAT_NEED_LOGIN);
                    return;
                }
                List<MyCouponBean.CouponRecordListBean> list = body.getCouponRecordList();
                totalCount=body.getTotalCount_o()==null?"0":body.getTotalCount_o();
                try {
                    if (pageIndex > 1) {
                        int totalCountInt = Integer.parseInt(body.getTotalCount_o());
                        int totalPage = Integer.parseInt(body.getTotalPageCount_o());
                        if (pageIndex >= totalPage) {
                            if (couponList.size() + list.size() <= totalCountInt) {
                                couponList.addAll(couponList.size(), list);
                                handler.sendEmptyMessage(WHAT_LOAD_MORE_SUCCESS);
                            } else {
                                handler.sendEmptyMessage(WHAT_NO_MORE_DATA);
                            }
                        } else {
                            couponList.addAll(couponList.size(), list);
                            handler.sendEmptyMessage(WHAT_LOAD_MORE_SUCCESS);
                        }
                    } else {
                        couponList.addAll(couponList.size(), list);
                        handler.sendEmptyMessage(WHAT_LIST_SUCCESS);
                    }
                } catch (Exception e) {
                    if (pageIndex > 1) {
                        handler.sendEmptyMessage(WHAT_LOAD_MORE_ERROR);
                    } else {
                        handler.sendEmptyMessage(WHAT_LIST_ERROR);
                    }
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<MyCouponBean> call, Throwable t) {
                if (pageIndex > 1) {
                    handler.sendEmptyMessage(WHAT_LOAD_MORE_ERROR);
                } else {
                    handler.sendEmptyMessage(WHAT_LIST_ERROR);
                }
            }
        });


    }
}
