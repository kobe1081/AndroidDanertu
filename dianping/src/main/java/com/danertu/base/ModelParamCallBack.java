package com.danertu.base;

public interface ModelParamCallBack<T> {
    void requestSuccess(T type);

    void requestError(T type);

    void requestFailure(T type);
}
