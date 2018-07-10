package com.danertu.dianping;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.danertu.base.NewBaseActivity;
import com.danertu.dianping.activity.couponhistory.CouponHistoryContact;
import com.danertu.dianping.activity.couponhistory.CouponHistoryPresenter;
import com.danertu.dianping.fragment.couponexpired.CouponExpiredFragment;
import com.danertu.dianping.fragment.couponusehistory.CouponUseHistoryFragment;
import com.danertu.dianping.fragment.drinkcoupon.DrinkCouponFragment;
import com.danertu.dianping.fragment.mallcoupon.MallCouponFragment;
import com.danertu.dianping.fragment.springcoupon.SpringCouponFragment;
import com.danertu.listener.CouponCountCallBackListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CouponHistoryActivity extends NewBaseActivity<CouponHistoryContact.CouponHistoryView, CouponHistoryPresenter> implements CouponHistoryContact.CouponHistoryView, CouponCountCallBackListener {

    @BindView(R.id.b_title_back)
    Button bTitleBack;
    @BindView(R.id.b_title_operation)
    Button bTitleOperation;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.personal_top_layout)
    FrameLayout personalTopLayout;

    public static final String KEY_PAGE_POSITION = "position";
    @BindView(R.id.rb_coupon_use_history)
    RadioButton rbCouponUseHistory;
    @BindView(R.id.rb_coupon_out_date)
    RadioButton rbCouponOutDate;
    @BindView(R.id.rg_coupon)
    RadioGroup rgCoupon;
    @BindView(R.id.vp_coupon)
    ViewPager vpCoupon;
    private List<Fragment> fragmentList;
    private int pageIndex = 0;
    private PageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_history);
        setSystemBarWhite();
        ButterKnife.bind(this);
        tvTitle.setText(getResources().getString(R.string.title_coupon_history));
        initView();
        presenter.onCreateView();
    }

    @Override
    protected void initView() {
        super.initView();
        fragmentList = new ArrayList<>();
        Intent intent = getIntent();
        pageIndex = intent.getIntExtra(KEY_PAGE_POSITION, 0);
        CouponUseHistoryFragment historyFragment = new CouponUseHistoryFragment();
        historyFragment.setListener(this);
        fragmentList.add(historyFragment);
        CouponExpiredFragment expiredFragment = new CouponExpiredFragment();
        expiredFragment.setListener(this);
        fragmentList.add(expiredFragment);

        if (adapter == null) {
            adapter = new PageAdapter(getSupportFragmentManager());
        }
        vpCoupon.setAdapter(adapter);
        vpCoupon.setOffscreenPageLimit(fragmentList.size());
        rgCoupon.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_coupon_use_history:
                        vpCoupon.setCurrentItem(0);
                        break;
                    case R.id.rb_coupon_out_date:
                        vpCoupon.setCurrentItem(1);
                        break;
                }
            }

        });
        vpCoupon.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageIndex=position;
                switch (position) {
                    case 0:
                        rbCouponUseHistory.setChecked(true);
                        break;
                    case 1:
                        rbCouponOutDate.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                setSwipeBackEnable(pageIndex == 0);
            }
        });
        switch (pageIndex) {
            case 0:
                rbCouponUseHistory.setChecked(true);
                break;
            case 1:
                rbCouponOutDate.setChecked(true);
                break;
        }
        vpCoupon.setCurrentItem(pageIndex);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @OnClick(R.id.b_title_back)
    public void onBackCLick(View view) {
        jsFinish();
    }

    @Override
    public CouponHistoryPresenter initPresenter() {
        return new CouponHistoryPresenter(context);
    }

    @Override
    public void countCallBack(int position, String count) {
        switch (position) {
            case 0:
                rbCouponUseHistory.setText(getResources().getString(R.string.coupon_history_use) + "(" + count + ")");
                break;
            case 1:
                rbCouponOutDate.setText(getResources().getString(R.string.coupon_history_out_date) + "(" + count + ")");
                break;
        }
    }

    class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }

    }
}
