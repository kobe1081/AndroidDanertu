package com.danertu.dianping.fragment.index;

import android.content.Context;

import com.danertu.base.NewBaseActivity;
import com.danertu.base.NewBasePresenter;

public class IndexFragmentPresenter extends NewBasePresenter<IndexFragmentContact.IndexFragmentView, IndexFragmentModel> implements IndexFragmentContact.IIndexFragmentPresenter {
    public IndexFragmentPresenter(Context context) {
        super(context);
    }

    @Override
    public void initHandler() {

    }

    @Override
    public IndexFragmentModel initModel() {
        return new IndexFragmentModel(context);
    }

    @Override
    public void onCreateView() {

    }

    @Override
    public void onDestroy() {
        if (handler != null)
            handler.removeCallbacksAndMessages("");
        handler = null;
        model = null;
    }
}
