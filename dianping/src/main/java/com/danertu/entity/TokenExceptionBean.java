package com.danertu.entity;

/**
 * 作者:  Viz
 * 日期:  2018/8/3 14:16
 * <p>
 * 包名：com.danertu.entity
 * 文件名：TokenExceptionBean
 * 描述：TODO
 */
public class TokenExceptionBean {

    /**
     * result : false
     * info : token异常
     * code : -1
     * extra : {}
     */

    private String result;
    private String info;
    private String code;
    private ExtraBean extra;

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

    public ExtraBean getExtra() {
        return extra;
    }

    public void setExtra(ExtraBean extra) {
        this.extra = extra;
    }

    public static class ExtraBean {
    }
}
