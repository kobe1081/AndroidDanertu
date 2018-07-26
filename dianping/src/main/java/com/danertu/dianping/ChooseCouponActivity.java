package com.danertu.dianping;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.danertu.base.NewBaseActivity;
import com.danertu.dianping.activity.choosecoupon.ChooseCouponContact;
import com.danertu.dianping.activity.choosecoupon.ChooseCouponPresenter;
import com.danertu.entity.ChooseCouponBean;
import com.danertu.tools.DateTimeUtils;
import com.danertu.widget.CommonTools;
import com.danertu.widget.XListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.grantland.widget.AutofitTextView;

/**
 * 如果以选择了优惠券，在本界面就会显示选中的优惠券
 * 点击某一张优惠券后返回上一界面
 * <p>
 * 需要传递以下参数：
 * couponRecordGuid：      --优惠券guid，已经选择使用优惠券后传递，为空则是未选择过
 * callBackMethod：  --页面回调方法名，页面调起本页面时需要传递
 */
public class ChooseCouponActivity extends NewBaseActivity<ChooseCouponContact.ChooseCouponView, ChooseCouponPresenter> implements ChooseCouponContact.ChooseCouponView, XListView.IXListViewListener {

    @BindView(R.id.b_title_back)
    Button bTitleBack;
    @BindView(R.id.b_title_operation)
    Button bTitleOperation;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.personal_top_layout)
    FrameLayout personalTopLayout;
    @BindView(R.id.tv_use_coupon)
    TextView tvUseCoupon;
    @BindView(R.id.lv_coupon)
    XListView lvCoupon;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    private Map<Integer, Boolean> isShowDescriptionMap;
    private MyCouponAdapter adapter;
    private String couponRecordGuid;
    private String callBackMethod;
    private String totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_coupon);
        ButterKnife.bind(this);
        setSystemBarWhite();
        isShowDescriptionMap = new HashMap<>();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        couponRecordGuid = extras.getString("couponRecordGuid", "");
        callBackMethod = extras.getString("callBackMethod", "");
        totalPrice = extras.getString("totalPrice");
        tvUseCoupon.setSelected(TextUtils.isEmpty(couponRecordGuid));
        presenter.onCreate(intent);
    }

    @Override
    public ChooseCouponPresenter initPresenter() {
        return new ChooseCouponPresenter(context);
    }

    @OnClick(R.id.b_title_back)
    public void onBackClick(View view) {
        finish();
    }

    @Override
    public void initList(List<ChooseCouponBean.ValBean> list) {
        lvCoupon.setPullLoadEnable(false);
        lvCoupon.setPullRefreshEnable(true);
        lvCoupon.setXListViewListener(this);
        adapter = new MyCouponAdapter(list);
        lvCoupon.setAdapter(adapter);
        tvUseCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tvUseCoupon.isSelected()) {
                    tvUseCoupon.setSelected(true);
                }
                presenter.chooseCoupon(0, callBackMethod, null);
            }

        });
    }

    @Override
    public void notifyChange(int listSize) {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (listSize > 0) {
            lvCoupon.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        } else {
            lvCoupon.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void stopRefresh() {
        lvCoupon.stopRefresh();
    }

    @Override
    public void stopLoadMore() {
        lvCoupon.stopLoadMore();
    }

    @Override
    public void onRefresh() {
        presenter.refresh();
    }

    @Override
    public void onLoadMore() {
        presenter.loadMore();
    }


    class MyCouponAdapter extends BaseAdapter {
        List<ChooseCouponBean.ValBean> list;
        private Bitmap bitmapUp = null;
        private Bitmap bitmapDown = null;

        public MyCouponAdapter(List<ChooseCouponBean.ValBean> list) {
            this.list = list;
//            int dp12 = CommonTools.dip2px(context, 12);
            bitmapUp = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_coupon_up);
//            bitmapUp.setWidth(dp12);
//            bitmapUp.setHeight(dp12);
            bitmapDown = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_coupon_down);
//            bitmapDown.setHeight(dp12);
//            bitmapDown.setWidth(dp12);
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            if (isShowDescriptionMap.size() == 0 || isShowDescriptionMap.get(position) == null) {
                isShowDescriptionMap.put(position, false);
            }
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_use_coupon, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ChooseCouponBean.ValBean bean = list.get(position);
            /** 0-优惠金额，1-优惠折扣
             */
            holder.tvMyCouponName.setText(bean.getCouponName());
            if ("0".equals(bean.getDiscountType())) {
                holder.tvMyCouponMoney.setText(setStyleForUnSignNumLeft("￥" + bean.getDiscountPrice()));
            } else {
                String discountPercent = bean.getDiscountPercent();
                if (discountPercent.endsWith("0")) {
                    discountPercent = discountPercent.substring(0, discountPercent.length() - 1);
                }
                holder.tvMyCouponMoney.setText(setStyleForUnSignNumRight(discountPercent + "折"));
            }
            /**
             * 0-无限制，1-满减价格
             */
            if ("0".equals(bean.getUseCondition())) {
                holder.tvMyCouponCondition.setText("无限制");
            } else {
                holder.tvMyCouponCondition.setText("消费满" + bean.getUseConditionLimitPrice() + "即可使用");
            }
            String useDate = "";

            switch (bean.getUseValidityType()) {
                case "0":
                    // 自定义时间
                    String useStartTime = bean.getUseStartTime().replace("/", ".").replace("-", ".");
                    String[] startTimes = useStartTime.split(" ");
                    String[] endTimes = bean.getUseEndTime().replace("/", ".").replace("-", ".").split(" ");
                    useDate = startTimes[0] + "-" + endTimes[0];
                    isCanUse(holder.llRoot, useStartTime);
                    break;
                case "1":
                    //领取后次日N天内可用
                    String useFromTomorrowStart = bean.getUseFromTomorrowStart();
                    useDate = "领取后次日" + bean.getUseFromTomorrow() + "天内有效";
                    isCanUse(holder.llRoot, useFromTomorrowStart);
                    break;
                case "2":
                    //领取后当日N天内可用
                    String useFromTodayStart = bean.getUseFromTodayStart();
                    useDate = "领取后" + bean.getUseFromToday() + "天内有效";
                    isCanUse(holder.llRoot, useFromTodayStart);
                    break;
            }


            holder.tvMyCouponDate.setText(useDate);

            holder.llMyCouponLimit.removeAllViews();
            String[] descriptionList = bean.getDescription().split("@@");
            int length = descriptionList.length;
            for (int i = 0; i < length; i++) {
                TextView textView = (TextView) inflater.inflate(R.layout.item_coupon_limit, holder.llMyCouponLimit, false);
                textView.setText(descriptionList[i]);
                holder.llMyCouponLimit.addView(textView);
                if (i > 0) {
                    textView.setVisibility(View.GONE);
                }
            }
            if (isShowDescriptionMap.get(position)) {
                showLimit(holder.llMyCouponLimit, true);
            } else {
                showLimit(holder.llMyCouponLimit, false);
            }
            holder.ivMyCouponMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(holder.llMyCouponLimit.getTag().toString()) == 1) {
//                        isShowDescriptionMap.put(position, true);
                        holder.llMyCouponLimit.setTag(0);
//                        showLimit(holder.llMyCouponLimit, true);
                        holder.ivMyCouponMore.setImageBitmap(bitmapDown);
                    } else {
                        holder.llMyCouponLimit.setTag(1);
                        isShowDescriptionMap.put(position, false);
                        showLimit(holder.llMyCouponLimit, false);
                        holder.ivMyCouponMore.setImageBitmap(bitmapUp);
                    }
                }
            });


            final double doubleTotalPrice = Double.parseDouble(totalPrice);
            final double limitPrice = Double.parseDouble(bean.getUseConditionLimitPrice());

            switch (bean.getUseCondition()) {
                case "0"://无限制
                    holder.tvMyCouponMoney.setEnabled(true);
                    holder.tvMyCouponName.setEnabled(true);
                    holder.ivCouponUse.setEnabled(true);
                    break;
                case "1"://总价达到一定金额
                    if (doubleTotalPrice >= limitPrice) {
                        holder.ivCouponUse.setEnabled(true);
                        holder.tvMyCouponMoney.setEnabled(true);
                        holder.tvMyCouponName.setEnabled(true);
                    } else {
                        holder.tvMyCouponMoney.setEnabled(false);
                        holder.ivCouponUse.setEnabled(false);
                        holder.tvMyCouponName.setEnabled(false);
                    }
                    break;
            }

            if (!TextUtils.isEmpty(couponRecordGuid) && couponRecordGuid.equals(bean.getCouponRecordGuid())&&holder.ivCouponUse.isEnabled()) {
                holder.ivCouponUse.setSelected(true);
                tvUseCoupon.setSelected(false);
            } else {
                holder.ivCouponUse.setSelected(false);
//                holder.ivCouponUse.setSelected(true);
            }


            holder.llRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (bean.getUseCondition()) {
                        case "0"://无限制
                            presenter.chooseCoupon(1, callBackMethod, bean);
                            tvUseCoupon.setSelected(false);
                            break;
                        case "1"://总价达到一定金额
                            if (doubleTotalPrice >= limitPrice) {
                                presenter.chooseCoupon(1, callBackMethod, bean);
                                tvUseCoupon.setSelected(false);
                            } else {
                                jsShowMsg("未满足使用条件,无法使用此优惠券");
                            }
                            break;
                    }

                }
            });
            return convertView;
        }

        void isCanUse(View view, String startDate) {
            if (DateTimeUtils.isAfterToday(startDate)) {
                view.setEnabled(false);
            } else {
                view.setEnabled(true);
            }
        }

        void showLimit(LinearLayout llLimit, boolean show) {
            int childCount = llLimit.getChildCount();
            for (int i = 1; i < childCount; i++) {
                View childAt = llLimit.getChildAt(i);
                if (show) {
                    childAt.setVisibility(View.VISIBLE);
                } else {
                    childAt.setVisibility(View.GONE);
                }
            }
        }

        class ViewHolder {

            @BindView(R.id.tv_my_coupon_money)
            AutofitTextView tvMyCouponMoney;
            @BindView(R.id.tv_my_coupon_condition)
            TextView tvMyCouponCondition;
            @BindView(R.id.tv_my_coupon_name)
            TextView tvMyCouponName;
            @BindView(R.id.tv_my_coupon_date)
            TextView tvMyCouponDate;
            @BindView(R.id.iv_coupon_use)
            ImageView ivCouponUse;
            @BindView(R.id.ll_my_coupon_limit)
            LinearLayout llMyCouponLimit;
            @BindView(R.id.iv_my_coupon_more)
            ImageView ivMyCouponMore;
            @BindView(R.id.ll_root)
            LinearLayout llRoot;

            public ViewHolder(View view) {
//                R.layout.item_use_coupon
                ButterKnife.bind(this, view);
            }
        }
    }


    /**
     * 修改第一个字体大小
     *
     * @param text
     * @return
     */
    public SpannableStringBuilder setStyleForUnSignNumLeft(String text) {
        SpannableStringBuilder unSignNumBuilder = new SpannableStringBuilder(text);
        unSignNumBuilder.setSpan(new AbsoluteSizeSpan(CommonTools.dip2px(context, 12)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return unSignNumBuilder;
    }

    /**
     * 修改最后一个字体大小
     *
     * @param text
     * @return
     */
    public SpannableStringBuilder setStyleForUnSignNumRight(String text) {
        SpannableStringBuilder unSignNumBuilder = new SpannableStringBuilder(text);
        unSignNumBuilder.setSpan(new AbsoluteSizeSpan(CommonTools.dip2px(context, 12)), text.length() - 1, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return unSignNumBuilder;
    }

}
