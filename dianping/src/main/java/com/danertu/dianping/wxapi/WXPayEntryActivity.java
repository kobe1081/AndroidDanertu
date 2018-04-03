package com.danertu.dianping.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.config.Constants;
import com.danertu.dianping.MyOrderDetail;
import com.danertu.dianping.PayHtmlActivity;
import com.danertu.dianping.PaymentCenterActivity;
import com.danertu.dianping.PaymentCenterHandler;
import com.danertu.dianping.PayPrepareActivity;
import com.danertu.dianping.StockOrderDetailActivity;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);
//		tv_tips = (TextView) findViewById(R.id.tv_tips);

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(final BaseResp resp) {
//		0	成功		展示成功页面
//		-1	错误		可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
//		-2	用户取消	无需处理。发生场景：用户不支付了，点击取消，返回APP。
//		Handler myHandler = PaymentCenterActivity.myHandler == null ? PayPrepareActivity.myHandler : PaymentCenterActivity.myHandler;
//		Log.e("test","WXPayEntryActivity onResp resp.errStr="+resp.errStr);
//		Log.e("test","WXPayEntryActivity onResp resp.errCode="+resp.errCode);
//		Log.e("test","WXPayEntryActivity onResp resp.transaction="+resp.transaction);

        Handler[] handlers = {PaymentCenterActivity.handler, PayPrepareActivity.handler, PayHtmlActivity.handler, MyOrderDetail.handler, StockOrderDetailActivity.newHandler};
        for (Handler handler : handlers) {
            if (handler != null) {
                Message msg = Message.obtain();
                msg.what = PaymentCenterHandler.WHAT_WECHAT_PAY;
                msg.arg1 = resp.errCode;
                handler.sendMessage(msg);
            }
        }
        finish();
    }
}