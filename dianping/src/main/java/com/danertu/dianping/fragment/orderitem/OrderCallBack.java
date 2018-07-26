package com.danertu.dianping.fragment.orderitem;

import com.danertu.base.ModelCallBack;

public interface OrderCallBack extends ModelCallBack {
    void loadMoreSuccess();

    void loadMoreFailure();

    void loadMoreError();

    void noMoreData();
}
