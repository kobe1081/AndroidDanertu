package com.danertu.dianping.activity.index;

import android.content.Context;

import com.danertu.base.BaseView;
import com.danertu.db.DBManager;
import com.danertu.tools.DeviceTag;

public interface IndexContact {
    interface IndexView {

    }

    interface IIndexPresenter {
        void onCreate();
        void onDestroy();

        /**
         * 获取当前位置，用于展示不同首页
         */
        void locate();

    }

    interface IIndexModel {


    }
}
