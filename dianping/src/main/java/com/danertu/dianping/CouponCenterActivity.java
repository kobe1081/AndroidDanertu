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
import com.danertu.dianping.activity.couponcenter.CouponCenterContact;
import com.danertu.dianping.activity.couponcenter.CouponCenterPresenter;
import com.danertu.dianping.fragment.drinkcoupon.DrinkCouponFragment;
import com.danertu.dianping.fragment.mallcoupon.MallCouponFragment;
import com.danertu.dianping.fragment.springcoupon.SpringCouponFragment;
import com.danertu.listener.CouponCountCallBackListener;
import com.danertu.tools.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CouponCenterActivity extends NewBaseActivity<CouponCenterContact.CouponCenterView, CouponCenterPresenter> implements CouponCenterContact.CouponCenterView, CouponCountCallBackListener {

    @BindView(R.id.b_title_back)
    Button bTitleBack;
    @BindView(R.id.b_title_operation)
    Button bTitleOperation;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.personal_top_layout)
    FrameLayout personalTopLayout;
    @BindView(R.id.rb_coupon_mall)
    RadioButton rbCouponMall;
    @BindView(R.id.rb_coupon_spring)
    RadioButton rbCouponSpring;
    @BindView(R.id.rb_coupon_drink)
    RadioButton rbCouponDrink;
    @BindView(R.id.rg_coupon)
    RadioGroup rgCoupon;
    @BindView(R.id.vp_coupon)
    ViewPager vpCoupon;

    public static final String KEY_PAGE_POSITION = "position";
    private List<Fragment> fragmentList;
    private int pageIndex = 0;
    private PageAdapter adapter;
    private float dimen18;
    private float dimen14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_center);
        setSystemBarWhite();
        ButterKnife.bind(this);
        tvTitle.setText(getResources().getString(R.string.title_coupong_center));
        initView();
        presenter.onCreateView();
    }

    @Override
    protected void initView() {
        super.initView();
        fragmentList = new ArrayList<>();
        Intent intent = getIntent();
        pageIndex = intent.getIntExtra(KEY_PAGE_POSITION, 0);
        MallCouponFragment mallCouponFragment = new MallCouponFragment();
        mallCouponFragment.setListener(this);
        fragmentList.add(mallCouponFragment);
        SpringCouponFragment springCouponFragment = new SpringCouponFragment();
        springCouponFragment.setListener(this);
        fragmentList.add(springCouponFragment);
        DrinkCouponFragment drinkCouponFragment = new DrinkCouponFragment();
        drinkCouponFragment.setListener(this);
        fragmentList.add(drinkCouponFragment);
        if (adapter == null) {
            adapter = new PageAdapter(getSupportFragmentManager());
        }
//        dimen18=getResources().getDimension(R.dimen.text_size_18);
//        dimen14=getResources().getDimension(R.dimen.text_size_14);
        dimen18 = 18;
        dimen14 = 14;
        vpCoupon.setAdapter(adapter);
        vpCoupon.setOffscreenPageLimit(fragmentList.size());
        rgCoupon.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_coupon_mall:
                        vpCoupon.setCurrentItem(0);
                        rbCouponMall.setTextSize(dimen18);
                        rbCouponSpring.setTextSize(dimen14);
                        rbCouponDrink.setTextSize(dimen14);
                        break;
                    case R.id.rb_coupon_spring:
                        vpCoupon.setCurrentItem(1);
                        rbCouponMall.setTextSize(dimen14);
                        rbCouponSpring.setTextSize(dimen18);
                        rbCouponDrink.setTextSize(dimen14);
                        break;
                    case R.id.rb_coupon_drink:
                        vpCoupon.setCurrentItem(2);
                        rbCouponMall.setTextSize(dimen14);
                        rbCouponSpring.setTextSize(dimen14);
                        rbCouponDrink.setTextSize(dimen18);
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
                pageIndex = position;
                switch (position) {
                    case 0:
                        rbCouponMall.setChecked(true);
                        break;
                    case 1:
                        rbCouponSpring.setChecked(true);
                        break;
                    case 2:
                        rbCouponDrink.setChecked(true);
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
                rbCouponMall.setChecked(true);
                break;
            case 1:
                rbCouponSpring.setChecked(true);
                break;
            case 2:
                rbCouponDrink.setChecked(true);
                break;
        }
        vpCoupon.setCurrentItem(pageIndex);
    }

    @OnClick(R.id.b_title_back)
    public void onBackCLick(View view) {
        jsFinish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public CouponCenterPresenter initPresenter() {
        return new CouponCenterPresenter(context);
    }

    @Override
    public void countCallBack(int position, String count) {
        switch (position) {
            case 0:
                rbCouponMall.setText(getResources().getString(R.string.coupon_center_mall) + "(" + count + ")");
                break;
            case 1:
                rbCouponSpring.setText(getResources().getString(R.string.coupon_center_spring) + "(" + count + ")");
                break;
            case 2:
                rbCouponDrink.setText(getResources().getString(R.string.coupon_center_drinking) + "(" + count + ")");
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
