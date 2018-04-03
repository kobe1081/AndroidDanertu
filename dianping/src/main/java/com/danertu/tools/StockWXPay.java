package com.danertu.tools;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.tencent.mm.sdk.modelpay.PayReq;

import java.util.Map;

/**
 * 2018年1月4日
 * @author  huangyeliang
 * 囤货功能（仓库）微信支付调用此类
 * 更换了服务器异步通知地址
 */
public class StockWXPay {
    private StockWXPayUtil stockWXPayUtil;
    private String proNames;
    private String outOrderNumber;
    private String priceSum;
    private LoadingDialog loadDialog = null;

    public StockWXPay(Context context) {
        super();
        stockWXPayUtil = new StockWXPayUtil(context);
        loadDialog = new LoadingDialog(context);

        Logger.e(getClass().getSimpleName(), this.getClass().getPackage() + "." + this.getClass().getSimpleName() + " init WXPay");
    }
//
//    public void toPay(String proNames, String outOrderNumber, String priceSum) {
//        if (loadDialog.isShowing())
//            return;
//        setProNames(proNames);
//        setOutOrderNumber(outOrderNumber);
//        setPriceSum(priceSum);
//        new GetPrepayIdTask().execute("");
//    }

    /**
     * 2017年12月28日添加
     *
     * @param proNames
     * @param outOrderNumber
     * @param priceSum
     * @param orderType
     */
    public void toPay(String proNames, String outOrderNumber, String priceSum, String orderType) {
        if (loadDialog.isShowing())
            return;
        setProNames(proNames);
        setOutOrderNumber(outOrderNumber);
        setPriceSum(priceSum);
        new GetPrepayIdTask().execute(orderType);
    }

    /**
     * 2017年12月28日 将第一个void参数修改为string标识仓库订单、仓库退货
     */
    private class GetPrepayIdTask extends AsyncTask<String, Void, Map<String, String>> {

        @Override
        protected void onPreExecute() {
            loadDialog.show();
        }

        @Override
        protected Map<String, String> doInBackground(String... params) {
//            String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");//统一下单api
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";//统一下单api
            String orderType = params[0];
            Logger.e(this.getClass().getSimpleName(),"orderType="+orderType);
            //2017年12月28日修改
            String entity = "";
            if (TextUtils.isEmpty(orderType)) {
                entity = stockWXPayUtil.genProductArgs(getProNames(), getOutOrderNumber(), getPriceSum());
            } else {
                entity = stockWXPayUtil.genProductArgs(getProNames(), getOutOrderNumber(), getPriceSum(), orderType);
            }
			Logger.e(getClass().getSimpleName(), "doInBackground: --->entity="+entity );
            byte[] buf = Util.httpPost(url, entity);
            assert buf != null;
            String content = new String(buf);
			Logger.e(getClass().getSimpleName(), "doInBackground: content="+content);
            return stockWXPayUtil.decodeXml(content);
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            String prepay_id = result.get("prepay_id");
			Logger.e("test", "onPostExecute: --->result="+result);
            PayReq req = stockWXPayUtil.genPayReq(prepay_id);//生成签名参数 (第二步)
            stockWXPayUtil.sendPayReq(req);//发送支付请求 (第三步)
            loadDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    public String genTimeStamp() {
        String result = String.valueOf(stockWXPayUtil.genTimeStamp());
        if (result.length() > 10) {
            result = result.substring(0, 10);
        }
        return result;
    }

    public String getProNames() {
        return proNames;
    }

    public void setProNames(String proNames) {
        this.proNames = proNames;
    }

    public String getOutOrderNumber() {
        return outOrderNumber;
    }

    public void setOutOrderNumber(String outOrderNumber) {
        this.outOrderNumber = outOrderNumber;
    }

    public String getPriceSum() {
        return priceSum;
    }

    public void setPriceSum(String priceSum) {
        this.priceSum = priceSum;
    }

}
