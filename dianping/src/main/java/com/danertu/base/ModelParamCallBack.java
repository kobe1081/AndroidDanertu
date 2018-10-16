package com.danertu.base;

public interface ModelParamCallBack<T> {
    void requestSuccess(T type);

    void tokenException(String code, String info);

    void requestError(T type);

    void requestFailure(T type);
}
