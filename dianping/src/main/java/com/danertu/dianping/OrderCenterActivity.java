package com.danertu.dianping;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.danertu.base.NewBaseActivity;
import com.danertu.dianping.activity.ordercenter.OrderCenterContact;
import com.danertu.dianping.activity.ordercenter.OrderCenterPresenter;
import com.danertu.dianping.fragment.orderitem.OrderItemFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * 作者:  Viz
 * 日期:  2018/7/30 14:11
 *
 * 描述： 新的订单中心
*/
public class OrderCenterActivity extends NewBaseActivity<OrderCenterContact.OrderCenterView, OrderCenterPresenter> implements OrderCenterContact.OrderCenterView {

    @BindView(R.id.b_title_back)
    Button bTitleBack;
    @BindView(R.id.b_title_operation)
    Button bTitleOperation;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_title_order)
    FrameLayout ll_title_order;
    @BindView(R.id.rb_all)
    RadioButton rbAll;
    @BindView(R.id.rb_no_pay)
    RadioButton rbNoPay;
    @BindView(R.id.rb_no_send)
    RadioButton rbNoSend;
    @BindView(R.id.rb_no_receive)
    RadioButton rbNoReceive;
    @BindView(R.id.rb_pay_back)
    RadioButton rbPayBack;
    @BindView(R.id.rg_order_center)
    RadioGroup rgOrderCenter;
    @BindView(R.id.vp)
    ViewPager vp;
    static final String KEY_PAGE_POSITION = "position";
    private List<Fragment> fragmentList;
    private float textSize22 = 22;
    private float textSize14 = 14;
    private int tabIndex = 0;
    private PageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_center);
        ButterKnife.bind(this);
        setFitsSystemWindows(false);
        setTopPadding(ll_title_order, getStatusBarHeight());
        initView();
        presenter.onCreateView();
    }

    @Override
    protected void initView() {
        super.initView();
        if (fragmentList == null) {
            fragmentList = new ArrayList<>();
        }
        tabIndex = getIntent().getIntExtra(KEY_PAGE_POSITION, 0);

        for (int i = 0; i < 5; i++) {
            OrderItemFragment fragment = new OrderItemFragment();
            Bundle args = new Bundle();
            args.putString("orderType", String.valueOf(i));
            args.putInt("tabIndex", i);
            fragment.setArguments(args);
            fragmentList.add(fragment);
        }

        if (adapter == null) {
            adapter = new PageAdapter(getSupportFragmentManager());
        }
        vp.setAdapter(adapter);
        vp.setOffscreenPageLimit(fragmentList.size());
        rgOrderCenter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_all:
                        vp.setCurrentItem(0);
                        rbAll.setTextSize(textSize22);
                        rbNoPay.setTextSize(textSize14);
                        rbNoSend.setTextSize(textSize14);
                        rbNoReceive.setTextSize(textSize14);
                        rbPayBack.setTextSize(textSize14);
                        break;
                    case R.id.rb_no_pay:
                        vp.setCurrentItem(1);
                        rbAll.setTextSize(textSize14);
                        rbNoPay.setTextSize(textSize22);
                        rbNoSend.setTextSize(textSize14);
                        rbNoReceive.setTextSize(textSize14);
                        rbPayBack.setTextSize(textSize14);
                        break;
                    case R.id.rb_no_send:
                        vp.setCurrentItem(2);
                        rbAll.setTextSize(textSize14);
                        rbNoPay.setTextSize(textSize14);
                        rbNoSend.setTextSize(textSize22);
                        rbNoReceive.setTextSize(textSize14);
                        rbPayBack.setTextSize(textSize14);
                        break;
                    case R.id.rb_no_receive:
                        vp.setCurrentItem(3);
                        rbAll.setTextSize(textSize14);
                        rbNoPay.setTextSize(textSize14);
                        rbNoSend.setTextSize(textSize14);
                        rbNoReceive.setTextSize(textSize22);
                        rbPayBack.setTextSize(textSize14);
                        break;
                    case R.id.rb_pay_back:
                        vp.setCurrentItem(4);
                        rbAll.setTextSize(textSize14);
                        rbNoPay.setTextSize(textSize14);
                        rbNoSend.setTextSize(textSize14);
                        rbNoReceive.setTextSize(textSize14);
                        rbPayBack.setTextSize(textSize22);
                        break;
                }
            }
        });
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabIndex = position;
                switch (position) {
                    case 0:
                        rbAll.setChecked(true);
                        break;
                    case 1:
                        rbNoPay.setChecked(true);
                        break;
                    case 2:
                        rbNoSend.setChecked(true);
                        break;
                    case 3:
                        rbNoReceive.setChecked(true);
                        break;
                    case 4:
                        rbPayBack.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                setSwipeBackEnable(tabIndex == 0);
            }
        });
        switch (tabIndex) {
            case 0:
                rbAll.setChecked(true);
                break;
            case 1:
                rbNoPay.setChecked(true);
                break;
            case 2:
                rbNoSend.setChecked(true);
                break;
            case 3:
                rbNoReceive.setChecked(true);
                break;
            case 4:
                rbPayBack.setChecked(true);
                break;
        }
        vp.setCurrentItem(tabIndex);

        int itemWidth = getScreenWidth() / 5;
        if (rbAll.getLayoutParams()==null){
            rbAll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        ViewGroup.LayoutParams layoutParams = rbAll.getLayoutParams();
        layoutParams.width=itemWidth;
        rbAll.setLayoutParams(layoutParams);
        rbNoPay.setLayoutParams(layoutParams);
        rbNoSend.setLayoutParams(layoutParams);
        rbNoReceive.setLayoutParams(layoutParams);
        rbPayBack.setLayoutParams(layoutParams);
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
    public OrderCenterPresenter initPresenter() {
        return new OrderCenterPresenter(context);
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
