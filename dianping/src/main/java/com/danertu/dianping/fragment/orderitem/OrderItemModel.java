package com.danertu.dianping.fragment.orderitem;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.config.ApiService;
import com.config.Constants;
import com.danertu.base.BaseModel;
import com.danertu.base.ModelCallBack;
import com.danertu.base.ModelParamCallBack;
import com.danertu.entity.NewOrderHeadBean;
import com.danertu.entity.NewOrderBean;
import com.danertu.entity.OrderBody;
import com.danertu.entity.OrderHead;
import com.danertu.entity.TokenExceptionBean;
import com.danertu.tools.AppManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.danertu.dianping.fragment.orderitem.OrderItemPresenter.WHAT_CANCEL_FAIL;
import static com.danertu.dianping.fragment.orderitem.OrderItemPresenter.WHAT_CANCEL_SUCCESS;
import static com.danertu.dianping.fragment.orderitem.OrderItemPresenter.WHAT_MORE_ERROR;
import static com.danertu.dianping.fragment.orderitem.OrderItemPresenter.WHAT_MORE_SUCCESS;
import static com.danertu.dianping.fragment.orderitem.OrderItemPresenter.WHAT_NEED_LOGIN;
import static com.danertu.dianping.fragment.orderitem.OrderItemPresenter.WHAT_NO_MORE_DATA;
import static com.danertu.dianping.fragment.orderitem.OrderItemPresenter.WHAT_ORDER_ERROR;
import static com.danertu.dianping.fragment.orderitem.OrderItemPresenter.WHAT_ORDER_ONE_SUCCESS;
import static com.danertu.dianping.fragment.orderitem.OrderItemPresenter.WHAT_ORDER_SUCCESS;

public class OrderItemModel extends BaseModel {
    private int totalCount = 0;
    private int totalPageCount = 0;
    private List<NewOrderBean> list;
    private List<NewOrderBean> tempList;
    private AppManager appManager;

    public OrderItemModel(Context context) {
        super(context);
        list = new ArrayList<>();
        appManager = AppManager.getInstance();
    }


    public List<NewOrderBean> getTempList() {
        if (tempList == null) {
            tempList = new ArrayList<>();
        }
        return tempList;
    }

    /**
     * @param handler
     * @param orderType
     * @param loginId
     * @param pageIndex
     * @param pageSize
     * @see 此方法必须跑在子线程中
     * TODO 还有优化的空间
     */
    public void getOrders(Handler handler, String orderType, String loginId, final int pageIndex, int pageSize) {
        String orderHead = appManager.postGetOrderByType(loginId, orderType, pageIndex, pageSize);
        if (TextUtils.isEmpty(orderHead)) {
            if (pageIndex > 1) {
                sendMessage(handler, WHAT_MORE_ERROR);
            } else {
                sendMessage(handler, WHAT_ORDER_ERROR);
            }
            return;
        }
        TokenExceptionBean bean = JSONObject.parseObject(orderHead, TokenExceptionBean.class);
        if (bean != null && "false".equals(bean.getResult()) && "-1".equals(bean.getCode())) {
            sendMessage(handler, WHAT_NEED_LOGIN, bean.getInfo());
            return;
        }
        bean = null;
        NewOrderHeadBean newOrderHeadBean = JSONObject.parseObject(orderHead, NewOrderHeadBean.class);

        if (newOrderHeadBean == null || newOrderHeadBean.getVal() == null) {
            if (pageIndex > 1) {
                sendMessage(handler, WHAT_MORE_ERROR);
            } else {
                sendMessage(handler, WHAT_ORDER_ERROR);
            }
            return;
        }
        try {
            List<NewOrderHeadBean.ValBean> beanList = newOrderHeadBean.getVal();
            NewOrderHeadBean.CountBean countBean = newOrderHeadBean.getCount();
            if (countBean == null && newOrderHeadBean.getVal().size() == 0) {
                //无数据
                if (pageIndex > 1) {
                    sendMessage(handler, WHAT_NO_MORE_DATA);
                } else {
                    sendMessage(handler, WHAT_ORDER_SUCCESS);
                }
                return;
            }
            totalCount = Integer.parseInt(countBean.getTotalCount_o() == null ? "0" : countBean.getTotalCount_o());
            totalPageCount = Integer.parseInt(countBean.getTotalPageCount_o() == null ? "0" : countBean.getTotalPageCount_o());
            if (pageIndex > 1) {
                if (pageIndex < totalPageCount) {
                    //添加数据
                    tempList = addData(handler, appManager, beanList, loginId);
                    sendMessage(handler, WHAT_MORE_SUCCESS);
                } else {
                    if (list.size() + beanList.size() <= totalCount) {
                        //添加数据
                        tempList = addData(handler, appManager, beanList, loginId);
                        sendMessage(handler, WHAT_MORE_SUCCESS);
                    } else {
                        sendMessage(handler, WHAT_NO_MORE_DATA);
                    }
                }
            } else {
                //添加数据
                tempList = addData(handler, appManager, beanList, loginId);
                sendMessage(handler, WHAT_ORDER_SUCCESS);
            }

        } catch (Exception e) {
            if (pageIndex > 1) {
                sendMessage(handler, WHAT_MORE_ERROR);
            } else {
                sendMessage(handler, WHAT_ORDER_ERROR);
            }
            if (Constants.isDebug) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 添加数据
     *
     * @param appManager
     * @param beanList
     */
    public List<NewOrderBean> addData(Handler handler, AppManager appManager, List<NewOrderHeadBean.ValBean> beanList, String uid) {
        List<NewOrderBean> list = new ArrayList<>();
        for (NewOrderHeadBean.ValBean bean : beanList) {
            try {
                String orderNumber = bean.getOrderNumber();
                NewOrderBean newOrderBean = new NewOrderBean();
                newOrderBean.setMemLoginId(bean.getMemLoginID());
                newOrderBean.setName(bean.getName());
                newOrderBean.setMobile(bean.getMobile());
                newOrderBean.setAddress(bean.getAddress());
                newOrderBean.setOrderNumber(orderNumber);
                newOrderBean.setAgentID(bean.getAgentID());
                newOrderBean.setDispatchModeName(bean.getDispatchModeName());
                newOrderBean.setDispatchPrice(bean.getDispatchPrice());
                newOrderBean.setShipmentNumber(bean.getShipmentNumber());
                newOrderBean.setOderStatus(bean.getOderStatus());
                newOrderBean.setShipmentStatus(bean.getShipmentStatus());
                newOrderBean.setPaymentStatus(bean.getPaymentStatus());
                newOrderBean.setShouldPayPrice(bean.getShouldPayPrice());
                newOrderBean.setCreateTime(bean.getCreateTime());
                newOrderBean.setPaymentName(bean.getPaymentName());

                String orderBody = appManager.postGetOrderBody(orderNumber, uid);
//                String orderBody = "{\"orderproductlist\": {\"orderproductbean\": [ {\"ShopName\":\"泉眼温泉店\",\"Guid\":\"a6852148\",\"MarketPrice\":\"158.00\",\"Name\":\"温泉成人套票 中山泉眼温泉\",\"CreateUser\":\"shopnum1\",\"BuyNumber\":\"4\",\"AgentID\":\"\",\"Detail\":\"\",\"SupplierLoginID\":\"dsfer\",\"iSGive\":\"0\",\"tel\":\"076085883668\",\"ShopPrice\":\"115.00\",\"SmallImage\":\"20170508162215806.jpg\",\"other1\":\"\",\"other2\":\"\",\"attribute\":\"\",\"ProductRank\":[]}]}}";

                if (TextUtils.isEmpty(orderBody)) {
                    continue;
                }
                OrderBody body = JSONObject.parseObject(orderBody, OrderBody.class);
                if (body == null || body.getOrderproductlist() == null || body.getOrderproductlist().getOrderproductbean() == null) {
                    continue;
                }
                OrderBody.OrderproductlistBean orderproductlist = body.getOrderproductlist();
                List<OrderBody.OrderproductlistBean.OrderproductbeanBean> orderproductbean = orderproductlist.getOrderproductbean();
                newOrderBean.setProductItems(orderproductbean);
                list.add(newOrderBean);
//                handler.sendEmptyMessage(WHAT_ORDER_ONE_SUCCESS);

            } catch (Exception e) {
                if (Constants.isDebug) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public void cancelOrder(Handler handler, String orderNumber, int position, String uid, final ModelCallBack callBack) {
//        String json = appManager.postCancelOrder(orderNumber, uid);
//        if (TextUtils.isEmpty(json)) {
////            callBack.requestError();
//            sendMessage(handler, WHAT_CANCEL_FAIL, position, orderNumber);
//            return;
//        }
//        if ("true".equals(json)) {
////            callBack.requestSuccess();
//            sendMessage(handler, WHAT_CANCEL_SUCCESS, position, orderNumber);
//        } else {
//            TokenExceptionBean bean = JSONObject.parseObject(json, TokenExceptionBean.class);
//            if (bean == null) {
//                sendMessage(handler, WHAT_CANCEL_FAIL, position, orderNumber);
////                callBack.requestError();
//                return;
//            }
//            if ("false".equals(bean.getResult()) && "-1".equals(bean.getCode())) {
//                sendMessage(handler, WHAT_NEED_LOGIN, bean.getInfo());
////                callBack.tokenException(bean.getCode(), bean.getInfo());
//            }
//        }


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

    public void sureTakeGoods(Handler handler, String orderNumber, int position, String uid, final ModelCallBack callBack) {
//        String json = appManager.postFinishOrder(orderNumber, uid);
//        if (TextUtils.isEmpty(json)) {
////            callBack.requestError();
//            sendMessage(handler, WHAT_CANCEL_FAIL, position, orderNumber);
//            return;
//        }
//        if ("true".equals(json)) {
//            callBack.requestSuccess();
//        } else {
//            TokenExceptionBean bean = JSONObject.parseObject(json, TokenExceptionBean.class);
//            if (bean == null) {
//                callBack.requestError();
//                return;
//            }
//            if ("false".equals(bean.getResult()) && "-1".equals(bean.getCode())) {
//                callBack.tokenException(bean.getCode(), bean.getInfo());
//            }
//        }

//
        Call<String> call = retrofit.create(ApiService.class).sureTakeGoods("0076", orderNumber);
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
                    TokenExceptionBean bean = JSONObject.parseObject(response.body(), TokenExceptionBean.class);
                    if (bean != null && "false".equals(bean.getResult()) && "-1".equals(bean.getCode())) {
                        callBack.tokenException(bean.getCode(), bean.getInfo());
                    } else {
                        callBack.requestFailure();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callBack.requestError();
            }
        });
    }

    public void getOrderHead(String orderNum, String uid, final ModelParamCallBack callBack) {
        Call<OrderHead> call = retrofit.create(ApiService.class).getOrderHead("0036", orderNum);
        call.enqueue(new Callback<OrderHead>() {
            @Override
            public void onResponse(Call<OrderHead> call, Response<OrderHead> response) {
                if (response.code() != RESULT_OK || response.body() == null) {
                    callBack.requestError(null);
                    return;
                }
                if ("false".equals(response.body().getResult()) && "-1".equals(response.body().getCode())) {
                    callBack.tokenException(response.body().getCode(), response.body().getInfo());
                    return;
                }
                if (response.body().getOrderinfolist() == null || response.body().getOrderinfolist().getOrderinfobean() == null) {
                    callBack.requestError(null);
                    return;
                }
                List<OrderHead.OrderinfolistBean.OrderinfobeanBean> orderinfobean = response.body().getOrderinfolist().getOrderinfobean();
                if (orderinfobean.size() == 0) {
                    callBack.requestError(null);
                    return;
                }
                callBack.requestSuccess(orderinfobean.get(0));
            }

            @Override
            public void onFailure(Call<OrderHead> call, Throwable t) {
                callBack.requestFailure(null);
            }
        });
    }


    public List<NewOrderBean> getList() {
        return list;
    }

    public void clearData() {
        if (list != null) {
            list.clear();
        }
    }
}
