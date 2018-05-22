package com.danertu.dianping.fragment.index;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.danertu.base.NewBaseFragment;
import com.danertu.dianping.R;
import com.danertu.widget.MWebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import wl.codelibrary.widget.v4.SwipeRefreshLayout;

public class IndexFragment extends NewBaseFragment<IndexFragmentContact.IndexFragmentView, IndexFragmentPresenter> implements IndexFragmentContact.IndexFragmentView {

    @BindView(R.id.wv_index_content)
    MWebView wvIndexContent;
    @BindView(R.id.srl_webView)
    SwipeRefreshLayout srlWebView;
    @BindView(R.id.root)
    LinearLayout root;
    Unbinder unbinder;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_index, container, false);
        unbinder = ButterKnife.bind(this, view);
        presenter.onCreateView();
        return view;
    }

    @Override
    public IndexFragmentPresenter initPresenter() {
        return new IndexFragmentPresenter(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        presenter.onDestroy();
    }


}
