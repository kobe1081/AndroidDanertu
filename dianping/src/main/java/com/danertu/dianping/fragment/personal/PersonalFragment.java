package com.danertu.dianping.fragment.personal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danertu.base.NewBaseFragment;
import com.danertu.dianping.R;

/**
 * 作者:  Viz
 * 日期:  2018/7/30 14:22
 * <p>
 * 包名：com.danertu.dianping.fragment.personal
 * 文件名：PersonalFragment
 * 描述：个人中心
 */
public class PersonalFragment extends NewBaseFragment<PersonalContact.PersonalView, PersonalPresenter> implements PersonalContact.PersonalView {
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null){
            view=inflater.inflate(R.layout.fragment_personal,container,false);
        }
        return view;
    }

    @Override
    public PersonalPresenter initPresenter() {
        return new PersonalPresenter(context);
    }
}
