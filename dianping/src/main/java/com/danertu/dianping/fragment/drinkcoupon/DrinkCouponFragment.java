package com.danertu.dianping.fragment.drinkcoupon;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.base.NewBaseActivity;
import com.danertu.base.NewBaseFragment;
import com.danertu.dianping.HtmlActivityNew;
import com.danertu.dianping.R;
import com.danertu.entity.CouponBean;
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
import me.grantland.widget.AutofitTextView;

/**
 * 酒水优惠券列表
 * 通用优惠卷跳转首页
 * <p>
 * 泉眼优惠卷跳泉眼页面
 * <p>
 * 酒水优惠卷跳酒水页面
 * <p>
 * 下单页面返回至下单页面
 */
public class DrinkCouponFragment extends NewBaseFragment<DrinkCouponContact.DrinkCouponView, DrinkCouponPresenter> implements DrinkCouponContact.DrinkCouponView, XListView.IXListViewListener, SwipeRefreshLayout.OnRefreshListener {
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
        isShowDescriptionMap = new HashMap<>();
        unbinder = ButterKnife.bind(this, view);
        presenter.onCreateView();
        return view;
    }

    public void setListener(CouponCountCallBackListener listener) {
        this.listener = listener;
    }

    @Override
    public DrinkCouponPresenter initPresenter() {
        return new DrinkCouponPresenter(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void initList(List<CouponBean.CouponListBean> list) {
        adapter = new CouponAdapter(list);
        lvCoupon.setPullLoadEnable(true);
        lvCoupon.setPullRefreshEnable(true);
        lvCoupon.setAdapter(adapter);
        swipeRefresh.setEnabled(false);
        lvCoupon.setXListViewListener(this);
        swipeRefresh.setOnRefreshListener(this);
    }

    @Override
    public void notifyChange(int listSize, String totalCount) {
        adapter.notifyDataSetChanged();
        if (listener != null) {
            listener.countCallBack(2, totalCount);
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

    @Override
    public void toAgentShop(int levelType, String shopId) {
        jsStartActivity("com.danertu.dianping.IndexActivity", "shopid|" + shopId + ",;shoptype|" + levelType);
    }

    class CouponAdapter extends BaseAdapter {
        List<CouponBean.CouponListBean> list;
        private Drawable drawableUp = null;
        private Drawable drawableDown = null;

        public CouponAdapter(List<CouponBean.CouponListBean> list) {
            this.list = list;
            int dp12 = CommonTools.dip2px(context, 12);
            drawableUp = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_coupon_up));
            drawableUp.setBounds(0, 0, dp12, dp12);
            drawableDown = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_coupon_down));
            drawableDown.setBounds(0, 0, dp12, dp12);
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
                convertView = inflater.inflate(R.layout.item_coupon_center, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final CouponBean.CouponListBean bean = list.get(position);
            holder.tvCouponName.setText(bean.getCouponName());
            /**
             * 0-优惠金额，1-优惠折扣
             */
            if ("0".equals(bean.getDiscountType())) {
                holder.tvCouponMoney.setText(setStyleForUnSignNumLeft("￥" + bean.getDiscountPrice()));
            } else {
                String discountPercent = bean.getDiscountPercent();
                if (discountPercent.endsWith("0")) {
                    discountPercent = discountPercent.substring(0, discountPercent.length() - 1);
                }
                holder.tvCouponMoney.setText(setStyleForUnSignNumRight(discountPercent + "折"));
            }
            /**
             * 0-无限制，1-满减价格
             */
            if ("0".equals(bean.getUseCondition())) {
                holder.tvCouponCondition.setText("无限制");
            } else {
                holder.tvCouponCondition.setText("消费满" + bean.getUseConditionLimitPrice() + "即可使用");
            }
            holder.tvCouponLast.setText("剩余：" + bean.getRemainCount());
            Logger.e(TAG,"次日："+bean.getUseFromTomorrow());
            String validityPeriod="";
            switch (bean.getUseValidityType()) {
                case "0":
                    String[] startTimes = bean.getGetStartTime().split(" ");
                    String[] endTimes = bean.getGetEndTime().split(" ");
                    validityPeriod=startTimes[0].replace("/", ".") + "-" + endTimes[0].replace("/", ".");
                    break;
                case "1":
                    validityPeriod="领取后次日"+bean.getUseFromTomorrow()+"天内有效";
                    break;
                case "2":
                    validityPeriod="领取后"+bean.getUseFromToday()+"天内有效";
                    break;
            }
            holder.tvCouponDate.setText(validityPeriod);
            holder.llCouponDescription.removeAllViews();
            String[] descriptionList = bean.getDescription().split("@@");
            int length = descriptionList.length;
            for (int i = 0; i < length; i++) {
                TextView textView = (TextView) inflater.inflate(R.layout.item_coupon_limit, holder.llCouponDescription, false);
                textView.setText(descriptionList[i]);
                holder.llCouponDescription.addView(textView);
                if (i > 0) {
                    textView.setVisibility(View.GONE);
                }
            }
            if (isShowDescriptionMap.get(position)) {
                showLimit(holder.llCouponDescription, true);
            } else {
                showLimit(holder.llCouponDescription, false);
            }
            holder.tvCouponMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(holder.llCouponDescription.getTag().toString()) == 1) {
                        isShowDescriptionMap.put(position, true);
                        holder.llCouponDescription.setTag(0);
                        showLimit(holder.llCouponDescription, true);
                        holder.tvCouponMore.setCompoundDrawables(null, null, drawableDown, null);
                    } else {
                        holder.llCouponDescription.setTag(1);
                        isShowDescriptionMap.put(position, false);
                        showLimit(holder.llCouponDescription, false);
                        holder.tvCouponMore.setCompoundDrawables(null, null, drawableUp, null);
                    }
                }
            });

            //空是没领，0-领了未使用，1-领了并且用了

            if ("0".equals(bean.getIsUsed())) {
                holder.tvCouponGet.setText("去使用");
                holder.tvCouponGet.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.tvCouponGet.setBackgroundResource(R.drawable.bg_coupon_btn_solid);
            } else {
                holder.tvCouponGet.setText("立即领取");
                holder.tvCouponGet.setTextColor(ContextCompat.getColor(context, R.color.coupon_text_red));
                holder.tvCouponGet.setBackgroundResource(R.drawable.bg_coupon_use);
            }

            holder.tvCouponGet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (holder.tvCouponGet.getText().toString()) {
                        case "去使用":
                            switch (bean.getUseScope()) {
                                case "0"://全平台--点击使用跳转至首页。
                                    ((NewBaseActivity) getActivity()).backToHome();
                                    break;
                                case "1"://温泉--点击使用跳转至温泉预订页面。
                                    //app.jsStartActivity('HtmlActivityNew', 'url|' + sp.qyyd + '?agentid=' + shopid + '&platform=android&timestamp=' + Date.parse(new Date()));
                                    Intent intent = new Intent(context, HtmlActivityNew.class);
                                    intent.putExtra("url", Constants.SPRING_URL+"?agentid=" + getUid() + "&platform=android&timestamp=" + System.currentTimeMillis());
                                    startActivity(intent);
                                    break;
                                case "2"://酒水 --跳转至酒水列表  cateid=779
                                    //app.jsStartActivity('CategoryActivity', 'cateid|' + categoryId + ',;shopid|' + shopid);
                                    jsStartActivity("com.danertu.dianping.CategoryActivity", "cateid|779,;shopid|" + getShopId());
                                    break;
                                case "3"://指定代理商 --当优惠券指定代理商时，点击使用跳转至该代理商店铺首页，不可指定多个代理商。
                                    //app.jsStartActivity('DetailActivity', 'shopid|' + shopid + ',;shoptype|2');
                                    presenter.toAgentShopIndex(bean.getUseAgentAppoint());
                                    break;
                                case "4"://指定商品 --指定商品，当指定特定商品时，跳转至该商品详情。 当同时指定多个商品时，跳转至首页。（后续可指定商品集合表）
                                    String[] guids = bean.getUseProductAppointGuid().split(",");
                                    if (guids.length > 1) {
                                        //跳转至列表  目前仅跳首页
                                        ((NewBaseActivity) getActivity()).backToHome();
                                    } else {
                                        //只有一个的时候跳转至商品详情
                                        //app.jsStartActivity('ProductDetailsActivity2', 'guid|' + guid + ',;shopid|' + shopid);
                                        jsStartActivity("com.danertu.dianping.ProductDetailsActivity2", "guid|" + guids[0] + ",;shopid|" + getShopId());
                                    }
                                    break;
                                case "5"://除指定商品--指定商品除外跳转至首页。
                                    ((NewBaseActivity) getActivity()).backToHome();
                                    break;
                            }
                            break;
                        case "立即领取":
                            //立即领取
                            presenter.getCoupon(position, bean.getGuid());
                            break;
                    }
                }
            });
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            return convertView;
        }

        void showLimit(LinearLayout llLimit, boolean show) {
            if (show){
                llLimit.setVisibility(View.VISIBLE);
            }else {
                llLimit.setVisibility(View.GONE);
            }
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

            @BindView(R.id.tv_coupon_money)
            AutofitTextView tvCouponMoney;
            @BindView(R.id.tv_coupon_condition)
            TextView tvCouponCondition;
            @BindView(R.id.tv_coupon_get)
            TextView tvCouponGet;
            @BindView(R.id.tv_coupon_last)
            TextView tvCouponLast;
            @BindView(R.id.ll_right_right)
            LinearLayout llRightRight;
            @BindView(R.id.tv_coupon_name)
            TextView tvCouponName;
            @BindView(R.id.tv_coupon_date)
            TextView tvCouponDate;
            @BindView(R.id.tv_coupon_more)
            TextView tvCouponMore;
            @BindView(R.id.ll_coupon_description)
            LinearLayout llCouponDescription;
            @BindView(R.id.root)
            LinearLayout root;

            public ViewHolder(View view) {
//                R.layout.item_coupon_center
                ButterKnife.bind(this, view);
            }
        }
    }
}