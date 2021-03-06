package com.danertu.tools;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;

import wl.codelibrary.utils.NetInfoUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.config.Constants;
import com.danertu.widget.CommonTools;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayUtil {
    private final String TAG = getClass().getSimpleName();
    private IWXAPI msgApi = null;

    public WXPayUtil(Context context) {
        super();
        msgApi = WXAPIFactory.createWXAPI(context, null);

        msgApi.registerApp(Constants.APP_ID);
    }

    /**
     * 生成签名
     */
    @SuppressLint("DefaultLocale")
    private String genPackageSign(LinkedHashMap<String,String> params) {
        String param = genSignParam(params);

        return MD5.getMessageDigest(param.getBytes()).toUpperCase();
    }

    @SuppressLint("DefaultLocale")
    private String genAppSign(LinkedHashMap<String,String> params) {
        String param = genSignParam(params);
        return MD5.getMessageDigest(param.getBytes()).toUpperCase();
    }

    private String genSignParam(LinkedHashMap<String,String> params) {
        StringBuilder sb = new StringBuilder();
        Set<String> strings = params.keySet();
        for (String key : strings) {
            sb.append(key);
            sb.append('=');
            sb.append(params.get(key));
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.API_KEY);
        return sb.toString();
    }

    private String toXml(LinkedHashMap<String,String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            sb.append("<").append(key).append(">");
            sb.append(params.get(key));
            sb.append("</").append(key).append(">");
        }
        sb.append("</xml>");
        return sb.toString();
    }


    public Map<String, String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if (!"xml".equals(nodeName)) {
                            //实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion", e.toString());
        }
        return null;

    }

    /**
     * 产生随机数字符串
     */
    public String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    public long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * @param proBody     商品描述
     * @param orderNumber 订单号
     * @param payMoney    支付金额（单位：元）
     * @return 提交到微信的商品参数
     */
    public String genProductArgs(String proBody, String orderNumber, String payMoney) {
        try {
            int money = CommonTools.mul(payMoney, "100");//币种：rmb，单位：分
            String nonceStr = genNonceStr();
            LinkedHashMap<String,String> packageParams = new LinkedHashMap<>();
            packageParams.put("appid", Constants.APP_ID);
            packageParams.put("body", proBody);
            packageParams.put("mch_id", Constants.MCH_ID);
            packageParams.put("nonce_str", nonceStr);//随机数
//			packageParams.put("notify_url", "http://121.40.35.3/test");//微信支付后通知的商户后台地址
//            packageParams.put("notify_url", "http://www.danertu.com/PayReturn/WeiPay/App_Notify.aspx");//微信支付后通知的商户后台地址
            packageParams.put("notify_url", Constants.APP_URL.WECHAT_PAY_CALLBACK_URL_SIMPLE);//微信支付后通知的商户后台地址
            packageParams.put("out_trade_no", orderNumber);//订单号
            packageParams.put("spbill_create_ip", NetInfoUtil.getInstance().getLocalIPAddress());//订单生成的机器IP，指用户浏览器端IP
            packageParams.put("total_fee", String.valueOf(money));//支付价
            packageParams.put("trade_type", "APP");//固定格式
            String sign = genPackageSign(packageParams);
            packageParams.put("sign", sign);
            return toXml(packageParams);
            //不转码，转码反而导致乱码
//			return new String(xmlstring.getBytes(), "ISO8859-1");

        } catch (Exception e) {
            Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }


    }

    /**
     * 2017年12月28日添加
     * 为囤货功能添加订单类型参数，
     * 同时修改通知的商户后台地址
     *
     * @param proBody
     * @param orderNumber
     * @param payMoney
     * @param orderType
     * @return
     */
    public String genProductArgs(String proBody, String orderNumber, String payMoney, String orderType) {
        try {
            int money = CommonTools.mul(payMoney, "100");//币种：rmb，单位：分
            String nonceStr = genNonceStr();
            LinkedHashMap<String,String> packageParams = new LinkedHashMap<>();
            packageParams.put("appid", Constants.APP_ID);
            //为囤货功能添加的自定义参数
            packageParams.put("attach", orderType);//定义参数
            packageParams.put("body", proBody);
            packageParams.put("mch_id", Constants.MCH_ID);
            packageParams.put("nonce_str", nonceStr);//随机数
//			packageParams.add("notify_url", "http://121.40.35.3/test");//微信支付后通知的商户后台地址
//            packageParams.add("notify_url", "http://www.danertu.com/PayReturn/WeiPay/App_Notify_WareHouse.aspx");//微信支付后通知的商户后台地址
            packageParams.put("notify_url", Constants.APP_URL.WECHAT_PAY_CALLBACK_URL_STOCK);//微信支付后通知的商户后台地址
            packageParams.put("out_trade_no", orderNumber);//订单号
            packageParams.put("spbill_create_ip", NetInfoUtil.getInstance().getLocalIPAddress());//订单生成的机器IP，指用户浏览器端IP
            packageParams.put("total_fee", String.valueOf(money));//支付价
            packageParams.put("trade_type", "APP");//固定格式

            String sign = genPackageSign(packageParams);
            packageParams.put("sign", sign);


            return toXml(packageParams);
            //不转码，转码反而导致乱码
//			return new String(xmlstring.getBytes(), "ISO8859-1");

        } catch (Exception e) {
            Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }


    }

    public PayReq genPayReq(String prepayId) {
        PayReq req = new PayReq();
        req.appId = Constants.APP_ID;
        req.partnerId = Constants.MCH_ID;
        req.prepayId = prepayId;
        req.packageValue = "Sign=WXPay";
        req.nonceStr = genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());
        LinkedHashMap signParams = new LinkedHashMap();
        signParams.put("appid", req.appId);
        signParams.put("noncestr", req.nonceStr);
        signParams.put("package", req.packageValue);
        signParams.put("partnerid", req.partnerId);
        signParams.put("prepayid", req.prepayId);
        signParams.put("timestamp", req.timeStamp);
        req.sign = genAppSign(signParams);
        return req;
    }

    public void sendPayReq(PayReq req) {
        if (req == null) {
            Log.e(TAG, "PayReq mustn't null");
            Logger.e("test", this.getClass().getPackage() + "." + this.getClass().getSimpleName() + " PayReq mustn't null");
            return;
        }
        boolean b = msgApi.registerApp(Constants.APP_ID);
        if (b) {
            Logger.e("test", this.getClass().getPackage() + "." + this.getClass().getSimpleName() + " IWXAPI register success");
        } else {
            Logger.e("test", this.getClass().getPackage() + "." + this.getClass().getSimpleName() + " IWXAPI register fail");

        }
        msgApi.sendReq(req);
    }

}
