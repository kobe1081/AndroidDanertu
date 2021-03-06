package com.danertu.dianping;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.config.Constants;
import com.danertu.base.NewBaseActivity;
import com.danertu.dianping.activity.mycoupon.MyCouponContact;
import com.danertu.dianping.activity.mycoupon.MyCouponPresenter;
import com.danertu.entity.MyCouponBean;
import com.danertu.tools.DateTimeUtils;
import com.danertu.widget.CommonTools;
import com.danertu.widget.XListView;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.danertu.dianping.activity.mycoupon.MyCouponPresenter.REQUEST_GET_COUPON;
import static com.danertu.dianping.fragment.mallcoupon.MallCouponFragment.REQUEST_COUPON_DETAIL;

/**
 * 通用优惠卷跳转首页
 * <p>
 * 泉眼优惠卷跳泉眼页面
 * <p>
 * 酒水优惠卷跳酒水页面
 * <p>
 * 下单页面返回至下单页面
 * <p>
 * 当优惠券的使用日期未到时不可点击
 * <p>
 * <p>
 * shopId：当用户为店主时shopId为用户的shopId，不是店主则为上级的shoId
 * 同时领券中心的shopId从本页面传递，不需要再重新获取
 */
public class MyCouponActivity extends NewBaseActivity<MyCouponContact.MyCouponView, MyCouponPresenter> implements MyCouponContact.MyCouponView, SwipeRefreshLayout.OnRefreshListener, XListView.IXListViewListener {
    @BindView(R.id.b_title_back)
    Button bTitleBack;
    @BindView(R.id.b_title_operation)
    Button bTitleOperation;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.personal_top_layout)
    FrameLayout personalTopLayout;
    @BindView(R.id.lv_coupon)
    XListView lvCoupon;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.tv_coupon_history)
    TextView tvCouponHistory;
    @BindView(R.id.tv_coupon_get_more)
    TextView tvCouponGetMore;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.ll_no_data)
    LinearLayout llNoData;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.tv_try_again)
    TextView tvTryAgain;
    private MyCouponAdapter adapter;
    private Map<Integer, Boolean> isShowDescriptionMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupon);
        setSystemBarWhite();
        ButterKnife.bind(this);
        tvTitle.setText(getResources().getString(R.string.title_my_coupon));
        isShowDescriptionMap = new HashMap<>();
        presenter.onCreateView();
    }

    @OnClick(R.id.b_title_back)
    public void onBackClick(View view) {
        jsFinish();
    }

    @OnClick(R.id.tv_coupon_history)
    public void onTvCouponHistoryClick(View view) {
        if (!isClickMoreTimesShortTime()) {
            return;
        }
        jsStartActivity("com.danertu.dianping.CouponHistoryActivity");
    }

    @OnClick({R.id.tv_coupon_get_more, R.id.tv_no_data})
    public void onTvMoreClick(View view) {
        if (!isClickMoreTimesShortTime()) {
            return;
        }
        jsStartActivityForResult("com.danertu.dianping.CouponCenterActivity", "shopid|" + getShopId(), REQUEST_GET_COUPON);
    }

    @OnClick(R.id.tv_try_again)
    public void onTvTryAgainClick(View view) {
        presenter.refresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public MyCouponPresenter initPresenter() {
        return new MyCouponPresenter(context);
    }

    @Override
    public void initList(List<MyCouponBean.CouponRecordListBean> list) {
        adapter = new MyCouponAdapter(list);
        swipeRefresh.setEnabled(false);
        lvCoupon.setPullLoadEnable(true);
        lvCoupon.setPullRefreshEnable(true);
        swipeRefresh.setOnRefreshListener(this);
        lvCoupon.setXListViewListener(this);
        lvCoupon.setAdapter(adapter);
    }

    @Override
    public void notifyChange(int listSize) {
        adapter.notifyDataSetChanged();
        if (listSize > 0) {
            swipeRefresh.setEnabled(false);
            lvCoupon.setVisibility(View.VISIBLE);
            llNoData.setVisibility(View.GONE);
            tvNoData.setVisibility(View.GONE);
            tvTryAgain.setVisibility(View.GONE);
        } else {
            swipeRefresh.setEnabled(true);
            lvCoupon.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.VISIBLE);
            tvTryAgain.setVisibility(View.GONE);
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
    public void getDataFail() {
        swipeRefresh.setEnabled(true);
        lvCoupon.setVisibility(View.GONE);
        tvNoData.setVisibility(View.GONE);
        llNoData.setVisibility(View.VISIBLE);
        tvTryAgain.setVisibility(View.VISIBLE);
    }

    @Override
    public void toAgentShop(int levelType, String shopId) {
        jsStartActivity("com.danertu.dianping.IndexActivity", "shopid|" + shopId + ",;shoptype|" + levelType);
    }

    @Override
    public void setRefresh(boolean isRefresh) {
        lvCoupon.setPullRefreshEnable(isRefresh);
    }

    @Override
    public void canLoadMore(boolean loadMore) {
        lvCoupon.setPullLoadEnable(loadMore);
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
        List<MyCouponBean.CouponRecordListBean> list;
        private Bitmap bitmapUp = null;
        private Bitmap bitmapDown = null;

        public MyCouponAdapter(List<MyCouponBean.CouponRecordListBean> list) {
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
                convertView = inflater.inflate(R.layout.item_my_coupon, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final MyCouponBean.CouponRecordListBean bean = list.get(position);
            /** 0-优惠金额，1-优惠折扣
             */
            holder.tvMyCouponName.setText(bean.getCouponName());
            if ("0".equals(bean.getDiscountType())) {
                String discountPrice = bean.getDiscountPrice();
                discountPrice = discountPrice.substring(0, discountPrice.indexOf("."));
                holder.tvMyCouponMoney.setText(setStyleForUnSignNumLeft(getResources().getString(R.string.rmb) + discountPrice));
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
//                    isCanUse(holder.tvMyCouponUse, useStartTime);
                    break;
                case "1":
                    //领取后次日N天内可用
//                    String useFromTomorrowStart = bean.getUseFromTomorrowStart();
                    String[] splitTomorrowStart = bean.getUseFromTomorrowStart().replace("/", ".").replace("-", ".").split(" ");
                    String[] splitTomorrowEnd = bean.getUseFromTomorrowEnd().replace("/", ".").replace("-", ".").split(" ");
//                    useDate = "领取后次日" + bean.getUseFromTomorrow() + "天内有效";
                    useDate = splitTomorrowStart[0] + "-" + splitTomorrowEnd[0];
//                    isCanUse(holder.tvMyCouponUse, useFromTomorrowStart);
                    break;
                case "2":
                    //领取后当日N天内可用
//                    String useFromTodayStart = bean.getUseFromTodayStart();
                    String[] splitTodayStart = bean.getUseFromTodayStart().replace("/", ".").replace("-", ".").split(" ");
                    String[] splitTodayEnd = bean.getUseFromTodayEnd().replace("/", ".").replace("-", ".").split(" ");
//                    useDate = "领取后" + bean.getUseFromToday() + "天内有效";
                    useDate = splitTodayStart[0] + "-" + splitTodayEnd[0];
//                    isCanUse(holder.tvMyCouponUse, useFromTodayStart);
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
                        isShowDescriptionMap.put(position, true);
                        holder.llMyCouponLimit.setTag(0);
                        showLimit(holder.llMyCouponLimit, true);
//                        holder.ivMyCouponMore.setImageBitmap(bitmapDown);
                        holder.ivMyCouponMore.setImageBitmap(bitmapUp);
                    } else {
                        holder.llMyCouponLimit.setTag(1);
                        isShowDescriptionMap.put(position, false);
                        showLimit(holder.llMyCouponLimit, false);
//                        holder.ivMyCouponMore.setImageBitmap(bitmapUp);
                        holder.ivMyCouponMore.setImageBitmap(bitmapDown);
                    }
                }
            });

//            holder.tvMyCouponUse.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    switch (bean.getUseScope()) {
//                        case "0"://全平台--点击使用跳转至首页。
//                            backToHome();
//                            break;
//                        case "1"://温泉--点击使用跳转至温泉预订页面。
//                            //app.jsStartActivity('HtmlActivityNew', 'url|' + sp.qyyd + '?agentid=' + shopid + '&platform=android&timestamp=' + Date.parse(new Date()));
//                            Intent intent = new Intent(context, HtmlActivityNew.class);
//                            intent.putExtra("url", Constants.SPRING_URL + "?agentid=" + getUid() + "&platform=android&timestamp=" + System.currentTimeMillis());
//                            startActivity(intent);
//                            break;
//                        case "2"://酒水 --跳转至酒水列表  cateid=779
//                            //app.jsStartActivity('CategoryActivity', 'cateid|' + categoryId + ',;shopid|' + shopid);
//                            jsStartActivity("com.danertu.dianping.CategoryActivity", "cateid|779,;shopid|" + getShopId());
//                            break;
//                        case "3"://指定代理商 --当优惠券指定代理商时，点击使用跳转至该代理商店铺首页，不可指定多个代理商。
//                            presenter.toAgentShopIndex(bean.getUseAgentAppoint());
//                            break;
//                        case "4"://指定商品 --指定商品，当指定特定商品时，跳转至该商品详情。 当同时指定多个商品时，跳转至首页。（后续可指定商品集合表）
//                            String[] guids = bean.getUseProductAppointGuid().split(",");
//                            if (guids.length > 1) {
//                                //跳转至列表  目前仅跳首页
//                                backToHome();
//                            } else {
//                                //只有一个的时候跳转至商品详情
//                                //app.jsStartActivity('ProductDetailsActivity2', 'guid|' + guid + ',;shopid|' + shopid);
//                                jsStartActivity("com.danertu.dianping.ProductDetailsActivity2", "guid|" + guids[0] + ",;shopid|" + getShopId());
//                            }
//                            break;
//                        case "5"://除指定商品--指定商品除外跳转至首页。
//                            backToHome();
//                            break;
//                    }
//                }
//            });


            holder.tvMyCouponUse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断优惠券是否在有效期
                    boolean isCanUse = false;
                    switch (bean.getUseValidityType()) {
                        case "0"://自定义日期
                            try {
                                String useStartTime = bean.getUseStartTime().replace("/", "-");
                                String useEndTime = bean.getUseEndTime().replace("/", "-");
                                //比较两个日期,如果日期相等返回0；小于0，参数date1就是在date2之后,大于0，参数date1就是在date2之前
                                isCanUse = DateTimeUtils.compareDate(DateTimeUtils.getDateToyyyyMMddHHmmss(), useStartTime) < 0 && DateTimeUtils.compareDate(DateTimeUtils.getDateToyyyyMMddHHmmss(), useEndTime) >= 0;
                            } catch (Exception e) {
                                isCanUse = false;
//                                        e.printStackTrace();
                            }
                            break;
                        case "1"://领取后次日N天内可用
                            try {
                                String useStartTime = bean.getUseFromTomorrowStart().replace("/", "-");
                                String useEndTime = bean.getUseFromTomorrowEnd().replace("/", "-");
                                //比较两个日期,如果日期相等返回0；小于0，参数date1就是在date2之后,大于0，参数date1就是在date2之前
                                isCanUse = DateTimeUtils.compareDate(DateTimeUtils.getDateToyyyyMMddHHmmss(), useStartTime) < 0 && DateTimeUtils.compareDate(DateTimeUtils.getDateToyyyyMMddHHmmss(), useEndTime) >= 0;
                            } catch (Exception e) {
                                isCanUse = false;
//                                        e.printStackTrace();
                            }
                            break;
                        case "2"://领取后当日N天内可用
                            try {
                                String useStartTime = bean.getUseFromTodayStart().replace("/", "-");
                                String useEndTime = bean.getUseFromTodayEnd().replace("/", "-");
                                //比较两个日期,如果日期相等返回0；小于0，参数date1就是在date2之后,大于0，参数date1就是在date2之前
                                isCanUse = DateTimeUtils.compareDate(DateTimeUtils.getDateToyyyyMMddHHmmss(), useStartTime) < 0 && DateTimeUtils.compareDate(DateTimeUtils.getDateToyyyyMMddHHmmss(), useEndTime) >= 0;
                            } catch (Exception e) {
                                isCanUse = false;
//                                        e.printStackTrace();
                            }
                            break;
                    }

                    if (!isCanUse) {
                        jsShowMsg("当前日期不可用");
                        return;
                    }
                    switch (bean.getJumpType()) {
                        case "0"://--独立页面 --温泉/酒店
                            Intent intent = new Intent(context, HtmlActivityNew.class);
//                            intent.putExtra("url", bean.getWenQuanUrl() +  "&platform=android&timestamp=" + System.currentTimeMillis());
                            intent.putExtra("url", bean.getWenQuanUrl().contains("agentid") ? bean.getWenQuanUrl() : (bean.getWenQuanUrl() + "agentid=" + bean.getShopId()) + "&platform=android&timestamp=" + System.currentTimeMillis());
                            startActivity(intent);
                            break;
                        case "1"://--原生产品列表
                            String[] guids = bean.getUseProductAppointGuid().split(",");
                            if (guids.length > 1) {
                                //跳去新页面,请求新接口展示数据
                                jsStartActivity("com.danertu.dianping.CouponProductsActivity", "shopid|" + bean.getShopId() + ",;couponGuid|" + bean.getCouponGuid());
                            } else {
                                //只有一个的时候跳转至商品详情
                                //app.jsStartActivity('ProductDetailsActivity2', 'guid|' + guid + ',;shopid|' + shopid);
                                if (TextUtils.isEmpty(bean.getAppointProductUrl())) {
                                    jsStartActivity("com.danertu.dianping.ProductDetailsActivity2", "guid|" + guids[0] + ",;shopid|" + bean.getShopId());
                                } else {
                                    //门票/客房   AppointProductType  1-成人票、儿童票  2-团体票  3-客房
                                    switch (bean.getAppointProductType()) {
                                        case "1":
                                        case "2":
                                            jsStartActivity("com.danertu.dianping.HtmlActivity", "pageName|" + "android/" + bean.getAppointProductUrl() + "&platform=android&timestamp=" + System.currentTimeMillis() + ",;guid|" + guids[0] + ",;shopid|" + bean.getShopId() + ",;productCategory|" + bean.getAppointProductType());
                                            break;
                                        case "3":
                                            jsStartActivity("com.danertu.dianping.ProductDetailsActivity2", "guid|" + guids[0] + ",;shopid|" + bean.getShopId());
                                            break;
                                        default:
                                            jsStartActivity("com.danertu.dianping.ProductDetailsActivity2", "guid|" + guids[0] + ",;shopid|" + bean.getShopId());
                                            break;
                                    }
                                }
                            }
                            break;
                        case "2"://app页面,
                            Intent intent2 = new Intent(context, HtmlActivityNew.class);
                            intent2.putExtra("url", bean.getAppUrl());
                            startActivity(intent2);
                            break;
                        case "3"://原生的分类页面
                            //比如 酒水 --779
                            jsStartActivity("com.danertu.dianping.CategoryActivity", "cateid|" + bean.getProductCategoryID() + ",;shopid|" + bean.getShopId());
                            break;
                        case "4"://跳转至店铺首页
                            presenter.toAgentShopIndex(bean.getUseAgentAppoint());
                            break;
                        case "5":
                            presenter.toAgentShopIndex(bean.getShopId());
                            break;
                        default:
                            backToHome();
                            break;

                    }
                }
            });

            holder.llRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isClickMoreTimesShortTime()) {
                        return;
                    }
                    Intent intent = new Intent(context, CouponDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("shopid", getShopId());
                    bundle.putInt("position", position);
                    bundle.putString("couponGuid", bean.getCouponGuid());
                    bundle.putString("couponRecordGuid", bean.getCouponRecordGuid());
                    bundle.putString("isUsed", "0");
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUEST_COUPON_DETAIL);
                }
            });
            holder.tvCouponShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isClickMoreTimesShortTime()) {
                        return;
                    }
                    try {
                        if ("3".equals(bean.getUseScope())) {
                            shareImgWithQRCode(bean.getImageUrl(), bean.getCouponShareUrl()+ "&shopid=" + bean.getUseAgentAppoint(), Float.parseFloat(bean.getImageX()), Float.parseFloat(bean.getImageY()), Integer.parseInt(bean.getImageWidth()), "Wechat&WechatMoments");
                        } else {
                            shareImgWithQRCode(bean.getImageUrl(), bean.getCouponShareUrl() + "&shopid=" + getShopId(), Float.parseFloat(bean.getImageX()), Float.parseFloat(bean.getImageY()), Integer.parseInt(bean.getImageWidth()), "Wechat&WechatMoments");
                        }
//                        shareImgWithQRCode(bean.getImageUrl(), Constants.COUPON_SHARE_URL+bean.getCouponGuid()+"&shopid="+getShopId(), Float.parseFloat(bean.getImageX()), Float.parseFloat(bean.getImageY()), Integer.parseInt(bean.getImageWidth()), "Wechat&WechatMoments");
                    } catch (Exception e) {
                        jsShowMsg("分享失败");
                        if (Constants.isDebug) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return convertView;
        }

        void isCanUse(TextView textView, String startDate) {
            if (DateTimeUtils.isAfterToday(startDate)) {
                textView.setEnabled(false);
            } else {
                textView.setEnabled(true);
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
            @BindView(R.id.tv_coupon_share)
            TextView tvCouponShare;

            public ViewHolder(View view) {
//                R.layout.item_my_coupon
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
