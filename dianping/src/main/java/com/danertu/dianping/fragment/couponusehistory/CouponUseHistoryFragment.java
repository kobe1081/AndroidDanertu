package com.danertu.dianping.fragment.couponusehistory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.danertu.base.NewBaseFragment;
import com.danertu.dianping.R;
import com.danertu.entity.MyCouponBean;
import com.danertu.listener.CouponCountCallBackListener;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;
import com.danertu.widget.XListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 优惠券使用记录
 */
public class CouponUseHistoryFragment extends NewBaseFragment<CouponUseHistoryContact.CouponUseHistoryView, CouponUseHistoryPresenter> implements CouponUseHistoryContact.CouponUseHistoryView, XListView.IXListViewListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.lv_coupon)
    XListView lvCoupon;
    Unbinder unbinder;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    private View view;
    private CouponCountCallBackListener listener;
    private CouponAdapter adapter;
    private Map<Integer, Boolean> isShowDescriptionMap;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_coupon, container, false);
        }
        unbinder = ButterKnife.bind(this, view);
        isShowDescriptionMap = new HashMap<>();
        presenter.onCreateView();
        return view;
    }

    public void setListener(CouponCountCallBackListener listener) {
        this.listener = listener;
    }

    @Override
    public CouponUseHistoryPresenter initPresenter() {
        return new CouponUseHistoryPresenter(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void initList(List<MyCouponBean.CouponRecordListBean> list) {
        adapter = new CouponAdapter(list);
        lvCoupon.setPullLoadEnable(true);
        lvCoupon.setPullRefreshEnable(true);
        lvCoupon.setAdapter(adapter);
        swipeRefresh.setEnabled(false);
        lvCoupon.setXListViewListener(this);
        swipeRefresh.setOnRefreshListener(this);
    }

    @Override
    public void notifyChange(int listSize,String totalCount) {
        adapter.notifyDataSetChanged();
        if (listener != null) {
            listener.countCallBack(0, totalCount);
        }
        if (listSize > 0) {
            swipeRefresh.setEnabled(false);
            if (lvCoupon.getVisibility() == View.GONE) {
                lvCoupon.setVisibility(View.VISIBLE);
            }
            if (tvNoData.getVisibility() == View.VISIBLE) {
                tvNoData.setVisibility(View.GONE);
            }
        } else {
            swipeRefresh.setEnabled(true);
            if (lvCoupon.getVisibility() == View.VISIBLE) {
                lvCoupon.setVisibility(View.GONE);
            }
            if (tvNoData.getVisibility() == View.GONE) {
                tvNoData.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void stopRefresh() {
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
        lvCoupon.stopRefresh();
    }

    @Override
    public void stopLoadMore() {
        lvCoupon.stopLoadMore();
    }

    @Override
    public void noMoreData() {
        lvCoupon.stopLoadMore();
        lvCoupon.setPullLoadEnable(false);
    }

    @Override
    public void onRefresh() {
        presenter.refresh();
    }

    @Override
    public void onLoadMore() {
        presenter.loadMore();
    }

    class CouponAdapter extends BaseAdapter {
        List<MyCouponBean.CouponRecordListBean> list;
        private Bitmap bitmapUp = null;
        private Bitmap bitmapDown = null;

        public CouponAdapter(List<MyCouponBean.CouponRecordListBean> list) {
            this.list = list;
            int dp12 = CommonTools.dip2px(context, 12);
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
                convertView = inflater.inflate(R.layout.item_my_coupon, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvMyCouponMoney.setEnabled(false);
            holder.tvMyCouponName.setEnabled(false);
            holder.tvMyCouponUse.setText("已使用");
            holder.tvMyCouponUse.setEnabled(false);
            MyCouponBean.CouponRecordListBean bean = list.get(position);
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
                    String[] startTimes = bean.getUseStartTime().split(" ");
                    String[] endTimes = bean.getUseEndTime().split(" ");
                    useDate = startTimes[0].replace("/", ".") + "-" + endTimes[0].replace("/", ".");
                    break;
                case "1":
                    //领取后次日N天内可用
                    useDate = "领取后次日"+bean.getUseFromTomorrow()+"天内有效";
                    break;
                case "2":
                    //领取后当日N天内可用
                    useDate="领取后"+bean.getUseFromToday()+"天内有效";
                    break;
            }

            holder.tvMyCouponDate.setText(useDate);
            holder.llMyCouponLimit.removeAllViews();
            String[] descriptionList = bean.getDescription().split("@@");
            Logger.e(TAG,"描述："+descriptionList.toString());
            int length = descriptionList.length;
            for (int i = 0; i < length; i++) {
                TextView textView = (TextView) inflater.inflate(R.layout.item_coupon_limit, holder.llMyCouponLimit, false);
                textView.setText(descriptionList[i]);
                holder.llMyCouponLimit.addView(textView);
                if (i > 0) {
                    textView.setVisibility(View.GONE);
                }
            }

            holder.ivMyCouponMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(holder.llMyCouponLimit.getTag().toString()) == 1) {
                        isShowDescriptionMap.put(position, true);
                        holder.llMyCouponLimit.setTag(0);
                        showLimit(holder.llMyCouponLimit,true);
                        holder.ivMyCouponMore.setImageBitmap(bitmapDown);
                    } else {
                        holder.llMyCouponLimit.setTag(1);
                        isShowDescriptionMap.put(position, false);
                        showLimit(holder.llMyCouponLimit,false);
                        holder.ivMyCouponMore.setImageBitmap(bitmapUp);
                    }
                }
            });
            holder.llRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            return convertView;
        }
        void showLimit(LinearLayout llLimit,boolean show){
            int childCount = llLimit.getChildCount();
            for (int i = 1; i < childCount; i++) {
                View childAt = llLimit.getChildAt(i);
                if (show){
                    childAt.setVisibility(View.VISIBLE);
                }else {
                    childAt.setVisibility(View.GONE);
                }
            }
        }
        class ViewHolder {
            @BindView(R.id.tv_my_coupon_money)
            TextView tvMyCouponMoney;
            @BindView(R.id.tv_my_coupon_condition)
            TextView tvMyCouponCondition;
            @BindView(R.id.tv_my_coupon_name)
            TextView tvMyCouponName;
            @BindView(R.id.tv_my_coupon_date)
            TextView tvMyCouponDate;
            @BindView(R.id.tv_my_coupon_use)
            TextView tvMyCouponUse;
            @BindView(R.id.ll_my_coupon_limit)
            LinearLayout llMyCouponLimit;
            @BindView(R.id.iv_my_coupon_more)
            ImageView ivMyCouponMore;
            @BindView(R.id.ll_root)
            LinearLayout llRoot;

            public ViewHolder(View view) {
//                R.layout.item_my_coupon
                ButterKnife.bind(this, view);
            }
        }
    }
}
