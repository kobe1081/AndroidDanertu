package com.danertu.dianping.fragment.personal;

import android.content.Context;

import com.danertu.base.NewBasePresenter;

/**
 * 作者:  Viz
 * 日期:  2018/7/30 14:21
 * <p>
 * 包名：com.danertu.dianping.fragment.personal
 * 文件名：PersonalPresenter
 * 描述：个人中心
 */
public class PersonalPresenter extends NewBasePresenter<PersonalContact.PersonalView, PersonalModel> implements PersonalContact.IPersonalPresenter {
    public PersonalPresenter(Context context) {
        super(context);
    }

    @Override
    public void onCreateView() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void initHandler() {

    }

    @Override
    public PersonalModel initModel() {
        return new PersonalModel();
    }
}
