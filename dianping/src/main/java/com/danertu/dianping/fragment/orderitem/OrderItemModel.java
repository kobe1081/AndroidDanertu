package com.danertu.dianping.fragment.orderitem;

import android.os.Handler;

import com.config.ApiService;
import com.config.Constants;
import com.danertu.base.BaseModel;
import com.danertu.base.ModelCallBack;
import com.danertu.base.ModelParamCallBack;
import com.danertu.entity.NewOrderHeadBean;
import com.danertu.entity.NewOrderBean;
import com.danertu.entity.OrderBody;
import com.danertu.entity.OrderHead;
import com.danertu.tools.AppManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.danertu.dianping.fragment.orderitem.OrderItemPresenter.WHAT_MORE_ERROR;
import static com.danertu.dianping.fragment.orderitem.OrderItemPresenter.WHAT_MORE_SUCCESS;
import static com.danertu.dianping.fragment.orderitem.OrderItemPresenter.WHAT_NO_MORE_DATA;
import static com.danertu.dianping.fragment.orderitem.OrderItemPresenter.WHAT_ORDER_ERROR;
import static com.danertu.dianping.fragment.orderitem.OrderItemPresenter.WHAT_ORDER_SUCCESS;

public class OrderItemModel extends BaseModel {
    private int totalCount = 0;
    private int totalPageCount = 0;
    private List<NewOrderBean> list;
    private AppManager appManager;
    private Gson gson;

    public OrderItemModel() {
        super();
        list = new ArrayList<>();
        appManager = AppManager.getInstance();
        gson = new Gson();
    }

    /**
     * @param handler
     * @param orderType
     * @param loginId
     * @param pageIndex
     * @param pageSize
     * @see 此方法必须跑在子线程中
     */
    public void getOrders(Handler handler, String orderType, String loginId, final int pageIndex, int pageSize) {
        String orderHead = appManager.postGetOrderByType(loginId, orderType, pageIndex, pageSize);
        NewOrderHeadBean newOrderHeadBean = gson.fromJson(orderHead, NewOrderHeadBean.class);

        if (newOrderHeadBean == null || newOrderHeadBean.getVal()==null) {
            if (pageIndex > 1) {
                sendMessage(handler,WHAT_MORE_ERROR);
            } else {
                sendMessage(handler,WHAT_ORDER_ERROR);
            }
            return;
        }
        try {
            List<NewOrderHeadBean.ValBean> beanList = newOrderHeadBean.getVal();
            NewOrderHeadBean.CountBean countBean = newOrderHeadBean.getCount();
            if (countBean==null&&newOrderHeadBean.getVal().size()==0){
                //无数据
                if (pageIndex>1){
                    sendMessage(handler,WHAT_NO_MORE_DATA);
                }else {
                    sendMessage(handler,WHAT_ORDER_SUCCESS);
                }
                return;
            }
            totalCount = Integer.parseInt(countBean.getTotalCount_o() == null ? "0" : countBean.getTotalCount_o());
            totalPageCount = Integer.parseInt(countBean.getTotalPageCount_o() == null ? "0" : countBean.getTotalPageCount_o());
            if (pageIndex > 1) {
                if (pageIndex < totalPageCount) {
                    //添加数据
                    addData(appManager, gson, list, beanList);
                    sendMessage(handler,WHAT_MORE_SUCCESS);
                } else {
                    if (list.size() + beanList.size() <= totalCount) {
                        //添加数据
                        addData(appManager, gson, list, beanList);
                        sendMessage(handler,WHAT_MORE_SUCCESS);
                    } else {
                        sendMessage(handler,WHAT_NO_MORE_DATA);
                    }
                }
            } else {
                //添加数据
                addData(appManager, gson, list, beanList);
                sendMessage(handler,WHAT_ORDER_SUCCESS);
            }

        } catch (Exception e) {
            if (pageIndex > 1) {
                sendMessage(handler,WHAT_MORE_ERROR);
            } else {
                sendMessage(handler,WHAT_ORDER_ERROR);
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
     * @param gson
     * @param list
     * @param beanList
     */
    public void addData(AppManager appManager, Gson gson, List<NewOrderBean> list, List<NewOrderHeadBean.ValBean> beanList) {
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

                String orderBody = appManager.postGetOrderBody(orderNumber);
                OrderBody body = gson.fromJson(orderBody, OrderBody.class);
                OrderBody.OrderproductlistBean orderproductlist = body.getOrderproductlist();
                List<OrderBody.OrderproductlistBean.OrderproductbeanBean> orderproductbean = orderproductlist.getOrderproductbean();
                if (body == null || orderproductlist == null || orderproductbean == null) {
                    continue;
                }
                newOrderBean.setProductItems(orderproductlist.getOrderproductbean());
                list.add(newOrderBean);
            } catch (Exception e) {
                if (Constants.isDebug) {
                    e.printStackTrace();
                }
                continue;
            }

        }
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

    public void getOrderHead(String orderNum, final ModelParamCallBack callBack){
        Call<OrderHead> call = retrofit.create(ApiService.class).getOrderHead("0036", orderNum);
        call.enqueue(new Callback<OrderHead>() {
            @Override
            public void onResponse(Call<OrderHead> call, Response<OrderHead> response) {
                if (response.code()!=RESULT_OK||response.body()==null||response.body().getOrderinfolist()==null||response.body().getOrderinfolist().getOrderinfobean()==null){
                    callBack.requestError(null);
                    return;
                }
                List<OrderHead.OrderinfolistBean.OrderinfobeanBean> orderinfobean = response.body().getOrderinfolist().getOrderinfobean();
                if (orderinfobean.size()==0){
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
