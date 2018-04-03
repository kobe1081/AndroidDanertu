package com.danertu.tools;

import android.content.Context;
import android.util.Log;

import com.config.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 2018年1月4日
 *
 * @author huangyeliang
 *         关于囤货功能（仓库）的支付宝支付都调用这个类
 *         更换了服务器异步通知地址
 */
public class StockAlipayUtil {

    public StockAlipayUtil(Context context) {

    }

    // "partner=\"2088011866089193\"&out_trade_no=\"080910240483088\"
    // &subject=\"发箍发带
    // 【肉来来】+超热卖百变小领巾兔耳朵布艺发箍发带\"&body=\"【肉来来】+超热卖百变小领巾兔耳朵布艺发箍发带\"
    // &total_fee=\"0.1\"&notify_url=\"http%3A%2F%2Fnotify.java.jpxx.org%2Findex.jsp\"
    // &service=\"mobile.securitypay.pay\"&_input_charset=\"UTF-8\"&return_url=\"http%3A%2F%2Fm.alipay.com\"
    // &payment_type=\"1\"&seller_id=\"1506546895@qq.com\"&it_b_pay=\"1m\"
    // &sign=\"BeiyjhLWFsj4Nh0ynTImtgC%2BMjsXLaKdx4A4xuK4PWy9CwihIATbMmD9OGnCkfCG0qrj%2FU8BiWAo2gH23433KJYQZk0ngKh12iRxyVFjibju4%2FFn9zNvhpBK6Tj%2FeNqbAFfZRCD%2FXv1qWcTvsvXhfQukY%2Fulztku%2BXvRJBidXSg%3D\"&sign_type=\"RSA\"";

    /**
     * 获取订单拼接支付宝支付信息
     *
     * @param orderNumber 商户网站唯一订单号
     * @param subject     商城名
     * @param body        订单体
     * @param totalMoney  订单总价
     * @return 提交到后台的订单数据
     */
    public String getSignPayOrderInfo(String orderNumber, String subject, String body, String totalMoney) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + Constants.pId + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + Constants.seller + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + orderNumber + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + totalMoney + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url="
                + "\""
                + "http://115.28.55.222:8085/AppPayReturnWareHouse.aspx"
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return getSignInfo(orderInfo);
    }

    /**
     * @param orderNumber
     * @param subject
     * @param body
     * @param totalMoney
     * @param orderType
     * @return
     */
    public String getSignPayOrderInfo(String orderNumber, String subject, String body, String totalMoney, String orderType) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + Constants.pId + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + Constants.seller + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + orderNumber + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + totalMoney + "\"";

        orderInfo += "&passback_params=" + "\"" + orderType + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url="
                + "\""
                + "http://115.28.55.222:8085/AppPayReturnWareHouse.aspx"
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return getSignInfo(orderInfo);
    }

    private String getSignInfo(String info) {
        String sign = Rsa.sign(info, Constants.privateCode);
        try {
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        info += "&sign=\"" + sign + "\"&" + getSignType();
        Log.i("ExternalPartner", "start pay");
        return info;
    }

    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

}
