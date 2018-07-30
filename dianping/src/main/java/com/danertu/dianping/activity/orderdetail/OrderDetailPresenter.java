package com.danertu.dianping.activity.orderdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.ClipboardManager;
import android.text.TextUtils;

import com.config.Constants;
import com.danertu.base.ModelCallBack;
import com.danertu.base.ModelParamCallBack;
import com.danertu.base.NewBasePresenter;
import com.danertu.dianping.MyOrderShipmentActivity;
import com.danertu.dianping.OrderDetailActivity;
import com.danertu.dianping.PayBackActivity;
import com.danertu.entity.NewOrderBean;
import com.danertu.entity.OrderBody;
import com.danertu.entity.OrderHead;
import com.danertu.entity.QuanYanProductCategory;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * 作者:  Viz
 * 日期:  2018/7/25 14:22
 * <p>
 * 包名：com.danertu.dianping.activity.orderdetail
 * 文件名：OrderDetailPresenter
 * 描述：TODO
 */
public class OrderDetailPresenter extends NewBasePresenter<OrderDetailContact.OrderDetailView, OrderDetailModel> implements OrderDetailContact.IOrderDetailPresenter {
    private String orderNumber;
    private int tabIndex;
    private int position;
    private boolean isQRCode;
    public static final int RESULT_ORDER_STATUS_CHANGE = 123;
    public static final int RESULT_QRCODE_STATUS_CHANGE = 124;
    public static final int REQUEST_SHOW_QRCODE = 22;

    public OrderDetailPresenter(Context context) {
        super(context);
    }

    @Override
    public void initHandler() {

    }

    @Override
    public OrderDetailModel initModel() {
        return new OrderDetailModel();
    }

    @Override
    public void onCreate(Intent intent) {
        Bundle bundle = intent.getExtras();
        orderNumber = bundle.getString("orderNumber", "");
        tabIndex = bundle.getInt("tab_index",-1);
        position = bundle.getInt("position",-1);
        isQRCode = bundle.getBoolean("isQRCode", false);
        if (TextUtils.isEmpty(orderNumber)) {
            if (isViewAttached()) {
                view.jsShowMsg("订单号不能为空");
                view.jsFinish();
                return;
            }
        }
        if (isViewAttached()) {
            view.jsShowLoading();
            getOrderInfo(false);
        }

    }

    @Override
    public void getOrderInfo(final boolean isSetResult) {
        model.getOrderInfo(orderNumber, new ModelCallBack() {
            @Override
            public void requestSuccess() {
                if (isViewAttached()) {
                    view.stopRefresh();
                    view.setSwipeRefreshEnable(false);
                    view.jsHideLoading();
                    OrderHead.OrderinfolistBean orderHead = model.getOrderHead();
                    view.showOrderDetail(orderHead, model.getOrderBody());
                    if (isSetResult) {
//                        OrderHead.OrderinfolistBean.OrderinfobeanBean bean = orderHead.getOrderinfobean().get(0);
//                        String data = "tabIndex|" + tabIndex + ",;position|" + position + ",;orderNumber|" + orderNumber + ",;orderStatue|" + bean.getOderStatus() + ",;payStatue|" + bean.getPaymentStatus() + ",;shipStatue|" + bean.getShipmentStatus();
//                        int requestCode;
//                        if (isQRCode) {
//                            requestCode = RESULT_QRCODE_STATUS_CHANGE;
//                        } else {
//                            requestCode = RESULT_QRCODE_STATUS_CHANGE;
//                        }
//                        view.jsSetResult(requestCode, data);
                        sendDataChangeBroadcast();

                    }

                }
            }

            @Override
            public void requestError() {
                if (isViewAttached()) {
                    view.jsHideLoading();
                    view.stopRefresh();
                    view.getOrderInfoError();
                    view.setSwipeRefreshEnable(true);
                    view.jsShowMsg("获取订单信息失败");
                }
            }

            @Override
            public void requestFailure() {
                if (isViewAttached()) {
                    view.jsHideLoading();
                    view.stopRefresh();
                    view.getOrderInfoError();
                    view.setSwipeRefreshEnable(true);
                    view.jsShowMsg("获取订单信息失败");
                }
            }
        });
    }

    @Override
    public void copyOrderNumber() {
        final ClipboardManager cm = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        cm.setText(orderNumber);
        if (isViewAttached()) {
            view.jsShowMsg("复制成功");
        }
    }

    @Override
    public void refresh() {
        getOrderInfo(false);
    }

    @Override
    public void startToProDetail(String guid, String proName, String img, String detail, String agentID, String supplierID, String price, String mobile) {
        view.startToProDetail(guid, proName, img, detail, agentID, supplierID, price, mobile);
    }

    @Override
    public void toQuanYan(String guid, final String shopId) {
        if (isViewAttached()) {
            view.jsShowLoading();
        }
        model.getQuanYanProductCategory(guid, new ModelParamCallBack<QuanYanProductCategory.ValBean>() {
            @Override
            public void requestSuccess(QuanYanProductCategory.ValBean type) {
                if (isViewAttached()) {
                    view.jsHideLoading();
                    view.toQuanYanPage(type, shopId);
                }
            }

            @Override
            public void requestError(QuanYanProductCategory.ValBean type) {
                if (isViewAttached()) {
                    view.jsHideLoading();
                    view.jsShowMsg("出错了");
                }
            }

            @Override
            public void requestFailure(QuanYanProductCategory.ValBean type) {
                if (isViewAttached()) {
                    view.jsHideLoading();
                    view.jsShowMsg("出错了");
                }
            }
        });
    }

    @Override
    public void toPayBackForResult(String uid) {
        Intent intent = new Intent(context, PayBackActivity.class);
        intent.putExtra("ordernumber", orderNumber);
        intent.putExtra("memberid", uid);
        intent.putExtra("price", model.getOrderHead().getOrderinfobean().get(0).getShouldPayPrice());
        intent.putExtra("position", position);
        ((OrderDetailActivity) context).startActivityForResult(intent, REQUEST_SHOW_QRCODE);
    }

    @Override
    public void toShipment() {
        OrderHead.OrderinfolistBean.OrderinfobeanBean bean = model.getOrderHead().getOrderinfobean().get(0);
        Intent intent = new Intent(context, MyOrderShipmentActivity.class);
        intent.putExtra(MyOrderShipmentActivity.KEY_SHIPMENT_CODE, TextUtils.isEmpty(bean.getLogisticsCompanyCode()) ? "" : bean.getLogisticsCompanyCode());
        intent.putExtra(MyOrderShipmentActivity.KEY_SHIPMENT_NUMBER, bean.getShipmentNumber());
        intent.putExtra(MyOrderShipmentActivity.KEY_SHIPMENT_NAME, bean.getDispatchModeName());
        context.startActivity(intent);
    }

    @Override
    public void cancelOrder(String orderNumber) {
        view.jsShowLoading();
        model.cancelOrder(orderNumber, new ModelCallBack() {
            @Override
            public void requestSuccess() {
                if (isViewAttached()) {
                    view.jsShowMsg("取消成功");
                    getOrderInfo(true);
                }
            }

            @Override
            public void requestError() {
                if (isViewAttached()) {
                    view.jsShowMsg("取消订单失败");
                }
            }

            @Override
            public void requestFailure() {
                if (isViewAttached()) {
                    view.jsShowMsg("取消订单失败");
                }
            }
        });
    }

    @Override
    public void sureTakeGoods(String orderNumber) {
        view.jsShowLoading();
        model.sureTakeGoods(orderNumber, new ModelCallBack() {
            @Override
            public void requestSuccess() {
                if (isViewAttached()) {
                    view.jsShowMsg("确认收货成功");
                    getOrderInfo(true);
                }
            }

            @Override
            public void requestError() {
                if (isViewAttached()) {
                    view.jsShowMsg("确认收货失败");
                }
            }

            @Override
            public void requestFailure() {
                if (isViewAttached()) {
                    view.jsShowMsg("确认收货失败");
                }
            }
        });
    }

    @Override
    public void sendDataChangeBroadcast() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(Constants.ORDER_DATA_CHANGE);
        Bundle bundle = new Bundle();
        bundle.putString("orderNumber", orderNumber);
        bundle.putInt("position", position);
        OrderHead.OrderinfolistBean.OrderinfobeanBean bean = model.getOrderHead().getOrderinfobean().get(0);
        List<OrderBody.OrderproductlistBean.OrderproductbeanBean> orderproductbean = model.getOrderBody().getOrderproductbean();
        NewOrderBean newOrderBean = new NewOrderBean();
        newOrderBean.setOrderNumber(orderNumber);
        newOrderBean.setMobile(bean.getMobile());
        newOrderBean.setAddress(bean.getAddress());
//        newOrderBean.setAgentID(bean.getA);
        newOrderBean.setClientToSellerMsg(bean.getClientToSellerMsg());
        newOrderBean.setInvoiceTitle(bean.getInvoiceTitle());
        newOrderBean.setCreateTime(bean.getCreateTime());
        newOrderBean.setPaymentName(bean.getPaymentName());
        newOrderBean.setPaymentStatus(bean.getPaymentStatus());
        newOrderBean.setDispatchModeName(bean.getDispatchModeName());
        newOrderBean.setShipmentNumber(bean.getShipmentNumber());
        newOrderBean.setShouldPayPrice(bean.getShouldPayPrice());
        newOrderBean.setMemLoginId(bean.getMemLoginId());
        newOrderBean.setOderStatus(bean.getOderStatus());
        newOrderBean.setOrderType(bean.getOrderType());
        newOrderBean.setShipmentStatus(bean.getShipmentStatus());
        newOrderBean.setDispatchPrice(bean.getDispatchPrice());
        newOrderBean.setDispatchTime(TextUtils.isEmpty(bean.getDispatchTime()) ? "" : bean.getDispatchTime());
        newOrderBean.setLogisticsCompanyCode(bean.getLogisticsCompanyCode());
        newOrderBean.setProductItems(orderproductbean);
        bundle.putParcelable("orderBean", newOrderBean);

        intent.putExtras(bundle);
        manager.sendBroadcast(intent);
    }

    @Override
    public void changeOrderState(String orderStatue, String payStatue, String shipStatue) {
        OrderHead.OrderinfolistBean.OrderinfobeanBean bean = model.getOrderHead().getOrderinfobean().get(0);
        if (!TextUtils.isEmpty(orderStatue)) {
            bean.setOderStatus(orderStatue);
        }
        if (!TextUtils.isEmpty(payStatue)) {
            bean.setPaymentStatus(payStatue);
        }
        if (!TextUtils.isEmpty(shipStatue)) {
            bean.setShipmentStatus(shipStatue);
        }
        sendDataChangeBroadcast();
        if (isViewAttached()) {
            view.showOrderDetail(model.getOrderHead(), model.getOrderBody());
        }
    }

    @Override
    public void onCreateView() {

    }

    @Override
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages("");
            handler = null;
        }
    }
}
