package com.danertu.dianping.activity.orderdetail;

import com.config.ApiService;
import com.danertu.base.BaseModel;
import com.danertu.base.ModelCallBack;
import com.danertu.base.ModelParamCallBack;
import com.danertu.entity.OrderBody;
import com.danertu.entity.OrderHead;
import com.danertu.entity.QuanYanProductCategory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 作者:  Viz
 * 日期:  2018/7/25 14:21
 * <p>
 * 包名：com.danertu.dianping.activity.orderdetail
 * 文件名：OrderDetailModel
 * 描述：TODO
 */
public class OrderDetailModel extends BaseModel {
    private OrderHead.OrderinfolistBean orderHead;
    private OrderBody.OrderproductlistBean orderBody;

    public void getOrderInfo(String orderNumber, final ModelCallBack callBack) {
        Call<OrderHead> orderHeadCall = retrofit.create(ApiService.class).getOrderHead("0036", orderNumber);
        final Call<OrderBody> orderBodyCall = retrofit.create(ApiService.class).getOrderBody("0072", orderNumber);
        orderHeadCall.enqueue(new Callback<OrderHead>() {
            @Override
            public void onResponse(Call<OrderHead> call, Response<OrderHead> response) {
                if (response.code() != RESULT_OK || response.body() == null || response.body().getOrderinfolist() == null || response.body().getOrderinfolist().getOrderinfobean() == null) {
                    callBack.requestError();
                    return;
                }
                orderHead = response.body().getOrderinfolist();
                orderBodyCall.enqueue(new Callback<OrderBody>() {
                    @Override
                    public void onResponse(Call<OrderBody> call, Response<OrderBody> response) {
                        if (response.code() != RESULT_OK || response.body() == null || response.body().getOrderproductlist() == null || response.body().getOrderproductlist().getOrderproductbean() == null) {
                            callBack.requestError();
                            return;
                        }
                        orderBody = response.body().getOrderproductlist();
                        callBack.requestSuccess();
                    }

                    @Override
                    public void onFailure(Call<OrderBody> call, Throwable t) {
                        callBack.requestFailure();
                    }
                });
            }

            @Override
            public void onFailure(Call<OrderHead> call, Throwable t) {
                callBack.requestFailure();
            }
        });

    }

    public void getQuanYanProductCategory(String productGuid, final ModelParamCallBack callBack) {
        Call<QuanYanProductCategory> call = retrofit.create(ApiService.class).getQuanYanProductCategory("0338", productGuid);
        call.enqueue(new Callback<QuanYanProductCategory>() {
            @Override
            public void onResponse(Call<QuanYanProductCategory> call, Response<QuanYanProductCategory> response) {
                if (response.code() != RESULT_OK || response.body() == null || response.body().getVal() == null) {
                    callBack.requestError(null);
                    return;
                }
                callBack.requestSuccess(response.body().getVal().get(0));
            }

            @Override
            public void onFailure(Call<QuanYanProductCategory> call, Throwable t) {
                callBack.requestFailure(null);
            }
        });
    }

    public void cancelOrder(String orderNumber, final ModelCallBack callBack) {
        Call<String> call = retrofit.create(ApiService.class).cancelOrder("0075", orderNumber);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() != RESULT_OK || response.body() == null) {
                    callBack.requestError();
                    return;
                }
                String body = response.body();
                if ("true".equals(body)) {
                    callBack.requestSuccess();
                } else {
                    callBack.requestFailure();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callBack.requestError();
            }
        });
    }

    public void sureTakeGoods(String orderNumber, final ModelCallBack callBack) {
        Call<String> call = retrofit.create(ApiService.class).cancelOrder("0076", orderNumber);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() != RESULT_OK || response.body() == null) {
                    callBack.requestError();
                    return;
                }
                String body = response.body();
                if ("true".equals(body)) {
                    callBack.requestSuccess();
                } else {
                    callBack.requestFailure();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callBack.requestError();
            }
        });
    }

    public OrderHead.OrderinfolistBean getOrderHead() {
        return orderHead;
    }

    public OrderBody.OrderproductlistBean getOrderBody() {
        return orderBody;
    }
}
