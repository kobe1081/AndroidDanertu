package com.danertu.dianping;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.danertu.base.NewBaseActivity;
import com.danertu.dianping.activity.index.IndexContact;
import com.danertu.dianping.activity.index.IndexPresenter;
import com.danertu.tools.AppManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IndexNewActivity extends NewBaseActivity<IndexContact.IndexView, IndexPresenter> implements IndexContact.IndexView {

    @BindView(R.id.fl_main)
    FrameLayout flMain;
    @BindView(R.id.tv_menu_index)
    TextView tvMenuIndex;
    @BindView(R.id.iv_menu_index_point)
    ImageView ivMenuIndexPoint;
    @BindView(R.id.ll_menu_index)
    LinearLayout llMenuIndex;
    @BindView(R.id.tv_menu_search)
    TextView tvMenuSearch;
    @BindView(R.id.iv_menu_search_point)
    ImageView ivMenuSearchPoint;
    @BindView(R.id.ll_menu_search)
    LinearLayout llMenuSearch;
    @BindView(R.id.tv_menu_shop_car)
    TextView tvMenuShopCar;
    @BindView(R.id.iv_menu_shop_car_point)
    ImageView ivMenuShopCarPoint;
    @BindView(R.id.ll_menu_shop_car)
    LinearLayout llMenuShopCar;
    @BindView(R.id.tv_menu_message)
    TextView tvMenuMessage;
    @BindView(R.id.iv_menu_message_point)
    ImageView ivMenuMessagePoint;
    @BindView(R.id.ll_menu_message)
    LinearLayout llMenuMessage;
    @BindView(R.id.tv_menu_mine)
    TextView tvMenuMine;
    @BindView(R.id.iv_menu_mine_point)
    ImageView ivMenuMinePoint;
    @BindView(R.id.ll_menu_mine)
    LinearLayout llMenuMine;
    @BindView(R.id.ll_menu)
    LinearLayout llMenu;
    @BindView(R.id.root)
    LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_new);
        ButterKnife.bind(this);
        presenter.onCreate();
    }

    @Override
    public IndexPresenter initPresenter() {
        return new IndexPresenter(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    /**
     * 返回键
     */
    private long st, et;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            et = System.currentTimeMillis();
            if (et - st > 2000) {
                st = et;
                jsShowMsg("再按一次退出单耳兔");
                return true;
            } else {
                //TODO
                AppManager.getInstance().appExit(context);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
