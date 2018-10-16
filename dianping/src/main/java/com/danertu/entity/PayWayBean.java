package com.danertu.entity;

import java.util.List;

/**
 * 作者:  Viz
 * 日期:  2018/9/3 10:22
 * <p>
 * 包名：com.danertu.entity
 * 文件名：PayWayBean
 * 描述：TODO
 */
public class PayWayBean {
    private String result;
    private String info;
    private String code;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private List<ValBean> val;

    public List<ValBean> getVal() {
        return val;
    }

    public void setVal(List<ValBean> val) {
        this.val = val;
    }

    public static class ValBean {
        /**
         * PayWay : Alipay,AccountPay,WechatPay,ArrivedPay
         */

        private String PayWay;

        public String getPayWay() {
            return PayWay;
        }

        public void setPayWay(String PayWay) {
            this.PayWay = PayWay;
        }
    }
}
