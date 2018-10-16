package com.danertu.base;

public interface ModelCallBack {
    void requestSuccess();

    void tokenException(String code, String info);

    void requestError();

    void requestFailure();
}
