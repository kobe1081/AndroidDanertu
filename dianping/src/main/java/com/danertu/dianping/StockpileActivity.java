package com.danertu.dianping;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.danertu.dianping.frag.stockorder.StockOrderFragment;
import com.danertu.dianping.frag.warehouse.WarehouseFragment;
import com.danertu.listener.LoadingListener;
import com.danertu.tools.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.danertu.dianping.frag.stockorder.StockOrderFragment.REQUEST_TO_ORDER_DETAIL;
import static com.danertu.dianping.frag.warehouse.WarehouseFragment.REQUEST_DETAIL;

/**
 * 2017年11月9日
 *
 * @author huangyeliang
 *         仓库页面
 */
public class StockpileActivity extends BaseActivity implements View.OnClickListener, LoadingListener {
    public static final int POSITION_PAGE_STOCK = 0;
    public static final int POSITION_PAGE_ORDER = 1;
    public static final String KEY_PAGE_POSITION = "position";
    private ImageView ivBack;
    private RadioGroup rgStock;
    private RadioButton rbStock;
    private RadioButton rbOrder;
    private ViewPager vpStock;
    private List<Fragment> fragmentList;
    private int pageIndex;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stockpile);
        setSystemBarWhite();
        initData();
        findViewById();
        initView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Logger.e(TAG, "onNewIntent");
        super.onNewIntent(intent);
        fragmentList.clear();
        if (adapter != null)
            adapter.notifyDataSetChanged();
        pageIndex = intent.getIntExtra(KEY_PAGE_POSITION, 0);
        initView();
    }

    private void initData() {
        fragmentList = new ArrayList<>();
        Intent intent = getIntent();
        pageIndex = intent.getIntExtra(KEY_PAGE_POSITION, 0);
    }

    @Override
    protected void findViewById() {
        ivBack = ((ImageView) findViewById(R.id.iv_back));
        rgStock = ((RadioGroup) findViewById(R.id.rg_stock));
        rbStock = ((RadioButton) findViewById(R.id.rb_stock_stock));
        rbOrder = ((RadioButton) findViewById(R.id.rb_stock_order));
        vpStock = ((ViewPager) findViewById(R.id.vp_stock));
        ivBack.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        WarehouseFragment warehouseFragment = new WarehouseFragment();
        warehouseFragment.setLoadingListener(this);
        fragmentList.add(warehouseFragment);
        StockOrderFragment orderFragment = new StockOrderFragment();
        orderFragment.setLoadingListener(this);
        fragmentList.add(orderFragment);
        if (adapter == null)
            adapter = new ViewPagerAdapter(getSupportFragmentManager());
        vpStock.setAdapter(adapter);
        vpStock.setOffscreenPageLimit(2);
        vpStock.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageIndex = position;
                switch (position) {
                    case 0:
                        rbStock.setChecked(true);
                        break;
                    case 1:
                        rbOrder.setChecked(true);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                setSwipeBackEnable(pageIndex == 0);
            }
        });
        rgStock.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_stock_stock:
                        vpStock.setCurrentItem(0);
                        break;
                    case R.id.rb_stock_order:
                        vpStock.setCurrentItem(1);
                        break;
                }
            }
        });
        vpStock.setCurrentItem(pageIndex);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void showLoading() {
        showLoadDialog();
    }

    @Override
    public void hideLoading() {
        hideLoadDialog();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.e(TAG,"onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_DETAIL:

                break;
            case REQUEST_TO_ORDER_DETAIL:

                break;
        }
    }
}
