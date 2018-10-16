package com.danertu.dianping.activity.index;

import android.content.Context;

import com.danertu.base.NewBasePresenter;

public class IndexPresenter extends NewBasePresenter<IndexContact.IndexView, IndexModel> implements IndexContact.IIndexPresenter {

    public IndexPresenter(Context context) {
        super(context);
    }

    @Override
    public void initHandler() {


    }

    @Override
    public IndexModel initModel() {
        return new IndexModel(context);
    }


    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        if (handler != null)
            handler.removeCallbacksAndMessages("");
        handler = null;
        model = null;
    }

    @Override
    public void locate() {

    }

}
