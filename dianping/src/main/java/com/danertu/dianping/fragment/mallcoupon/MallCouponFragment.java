package com.danertu.dianping.fragment.mallcoupon;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.base.NewBaseActivity;
import com.danertu.base.NewBaseFragment;
import com.danertu.dianping.CouponDetailActivity;
import com.danertu.dianping.HtmlActivityNew;
import com.danertu.dianping.R;
import com.danertu.entity.CouponBean;
import com.danertu.listener.CouponCountCallBackListener;
import com.danertu.tools.DateTimeUtils;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;
import com.danertu.widget.XListView;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.grantland.widget.AutofitTextView;

import static com.danertu.dianping.activity.coupondetail.CouponDetailPresenter.RESULT_COUPON_GET;

/**
 * 商城优惠券页面
 * <p>
 * 通用优惠卷跳转首页
 * <p>
 * 泉眼优惠卷跳泉眼页面
 * <p>
 * 酒水优惠卷跳酒水页面
 * <p>
 * 下单页面返回至下单页面
 * <p>
 * <p>
 * 优惠券使用跳转规则。
 * 1、当优惠券无限制使用时，点击使用跳转至首页。
 * 2、当优惠券近限温泉时，点击使用跳转至温泉预订页面。
 * 3、当优惠券指定代理商时，点击使用跳转至该代理商店铺首页，不可指定多个代理商。
 * 4、指定商品，当指定特定商品时，跳转至该商品详情。 当同时指定多个商品时，跳转至首页。（后续可指定商品集合表）
 * 5、指定商品除外跳转至首页。
 */
public class MallCouponFragment extends NewBaseFragment<MallCouponContact.MallCouponView, MallCouponPresenter> implements MallCouponContact.MallCouponView, SwipeRefreshLayout.OnRefreshListener, XListView.IXListViewListener {
    public static final int REQUEST_COUPON_DETAIL = 213;
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
    public MallCouponPresenter initPresenter() {
        return new MallCouponPresenter(context);
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

    @Override
    public void toAgentShop(int levelType, String shopId) {
        jsStartActivity("com.danertu.dianping.IndexActivity", "shopid|" + shopId + ",;shoptype|" + levelType);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_COUPON_DETAIL:
                if (resultCode == RESULT_COUPON_GET) {
                    Bundle bundle = data.getExtras();
                    Logger.e(TAG, "数据=" + bundle.toString());
                    int position = Integer.parseInt(bundle.getString("position", "-1"));
                    if (position == -1) {
                        return;
                    }
                    String isUsed = bundle.getString("isUsed");
                    String couponRecordGuid = bundle.getString("couponRecordGuid");
                    String shopId = bundle.getString("shopId");
                    if (adapter != null) {
                        adapter.updateUsed(position, isUsed, couponRecordGuid, shopId);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                String discountPrice = bean.getDiscountPrice();
                discountPrice = discountPrice.substring(0, discountPrice.indexOf("."));
                holder.tvCouponMoney.setText(setStyleForUnSignNumLeft(getResources().getString(R.string.rmb) + discountPrice));
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
            String validityPeriod = "";
            switch (bean.getUseValidityType()) {
                case "0":
                    String[] startTimes = bean.getUseStartTime().split(" ");
                    String[] endTimes = bean.getUseEndTime().split(" ");
                    validityPeriod = startTimes[0].replace("/", ".") + "-" + endTimes[0].replace("/", ".");
                    break;
                case "1":
                    if ("0".equals(bean.getIsUsed())) {
                        //已领取未使用
                        String[] splitTomorrowStart = bean.getGetTime().replace("/", ".").replace("-", ".").split(" ");
                        String specifiedDayAfter = DateTimeUtils.getSpecifiedDayAfter(splitTomorrowStart[0]);
                        if (TextUtils.isEmpty(bean.getEndTime())) {
                            bean.setEndTime(DateTimeUtils.getSpecifiedDayAfterN(specifiedDayAfter, Integer.parseInt(bean.getUseFromTomorrow())));
                        }
                        String[] splitTomorrowEnd = bean.getEndTime().replace("/", ".").replace("-", ".").split(" ");
                        validityPeriod = specifiedDayAfter + "-" + splitTomorrowEnd[0];
                    } else {
                        //未领取
                        validityPeriod = "领取后次日" + bean.getUseFromTomorrow() + "天内有效";
                    }
                    break;
                case "2":
                    if ("0".equals(bean.getIsUsed())) {
                        String[] splitTodayStart = bean.getGetTime().replace("/", ".").replace("-", ".").split(" ");
                        if (TextUtils.isEmpty(bean.getEndTime())) {
                            bean.setEndTime(DateTimeUtils.getSpecifiedDayAfterN(bean.getGetTime(), Integer.parseInt(bean.getUseFromToday())));
                        }
                        String[] splitTodayEnd = bean.getEndTime().replace("/", ".").replace("-", ".").split(" ");
                        validityPeriod = splitTodayStart[0] + "-" + splitTodayEnd[0];
                    } else {
                        validityPeriod = "领取后" + bean.getUseFromToday() + "天内有效";
                    }
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
                        holder.tvCouponMore.setCompoundDrawables(null, null, drawableUp, null);
                    } else {
                        holder.llCouponDescription.setTag(1);
                        isShowDescriptionMap.put(position, false);
                        showLimit(holder.llCouponDescription, false);
                        holder.tvCouponMore.setCompoundDrawables(null, null, drawableDown, null);
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
                            boolean isCanUse = false;
                            //检查优惠券是否已经到了使用日期
                            switch (bean.getUseValidityType()) {
                                case "0"://自定义日期
                                    try {
                                        String useStartTime = bean.getUseStartTime().replace("/", "-");
                                        String useEndTime = bean.getUseEndTime().replace("/", "-");
                                        //比较两个日期,如果日期相等返回0；小于0，参数date1就是在date2之后,大于0，参数date1就是在date2之前
                                        isCanUse = DateTimeUtils.compareDate(DateTimeUtils.getDateToyyyyMMddHHmmss(), useStartTime) <= 0 && DateTimeUtils.compareDate(DateTimeUtils.getDateToyyyyMMddHHmmss(), useEndTime) > 0;
                                    } catch (Exception e) {
                                        isCanUse = false;
//                                        e.printStackTrace();
                                    }
                                    break;
                                case "1"://领取后次日N天内可用
                                    try {
                                        String getTimeStr = bean.getGetTime();
                                        String[] split = getTimeStr.split(" ");
                                        String nextDayStr = DateTimeUtils.getSpecifiedDayAfter(split[0]) + " " + split[1];
                                        isCanUse = DateTimeUtils.compareDate(DateTimeUtils.getDateToyyyyMMddHHmmss().replace("/", "-"), nextDayStr.replace("/", "-")) <= 0;
                                    } catch (Exception e) {
                                        isCanUse = false;
//                                        e.printStackTrace();
                                    }
                                    break;
                                case "2"://领取后当日N天内可用
                                    try {
                                        String getTime = bean.getGetTime();
                                        String[] split = getTime.split(" ");
                                        String deadLineDay = DateTimeUtils.getSpecifiedDayAfterN(split[0], Integer.parseInt(bean.getUseFromToday())) + " " + split[1];//截止日期
                                        isCanUse = DateTimeUtils.compareDate(DateTimeUtils.getDateToyyyyMMddHHmmss().replace("/", "-"), deadLineDay.replace("/", "-")) >= 0;
                                    } catch (Exception e) {
                                        isCanUse = false;
//                                        e.printStackTrace();
                                    }
                                    break;
                            }
                            if (!isCanUse) {
                                jsShowMsg("此优惠券暂不可使用");
                                return;
                            }
                            switch (bean.getJumpType()) {
                                case "0"://--独立页面 --温泉/酒店
                                    Intent intent = new Intent(context, HtmlActivityNew.class);
                                    intent.putExtra("url", bean.getWenQuanUrl().contains("agentid") ? bean.getWenQuanUrl() : (bean.getWenQuanUrl() + "agentid=" + bean.getShopId()) + "&platform=android&timestamp=" + System.currentTimeMillis());
                                    startActivity(intent);
                                    break;
                                case "1"://--原生产品列表
                                    String[] guids = bean.getUseProductAppointGuid().split(",");
                                    if (guids.length > 1) {
                                        //跳去新页面,请求新接口展示数据
                                        jsStartActivity("com.danertu.dianping.CouponProductsActivity", "shopid|" + bean.getShopId() + ",;couponGuid|" + bean.getGuid());
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
                    if (!isClickMoreTimesShortTime()) {
                        return;
                    }
                    Intent intent = new Intent(context, CouponDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("shopid", getShopId());
                    bundle.putInt("position", position);
                    bundle.putString("couponGuid", bean.getGuid());
                    bundle.putString("couponRecordGuid", bean.getCouponRecordGuid());
                    bundle.putString("isUsed", bean.getIsUsed());
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
                            shareImgWithQRCode(bean.getImageUrl(), bean.getCouponShareUrl() + "&shopid=" + bean.getUseAgentAppoint(), Float.parseFloat(bean.getImageX()), Float.parseFloat(bean.getImageY()), Integer.parseInt(bean.getImageWidth()), "Wechat&WechatMoments");
                        } else {
                            shareImgWithQRCode(bean.getImageUrl(), bean.getCouponShareUrl() + "&shopid=" + getShopId(), Float.parseFloat(bean.getImageX()), Float.parseFloat(bean.getImageY()), Integer.parseInt(bean.getImageWidth()), "Wechat&WechatMoments");
                        }
//                        shareImgWithQRCode(bean.getImageUrl(), Constants.COUPON_SHARE_URL + bean.getGuid()+"&shopid="+getShopId(), Float.parseFloat(bean.getImageX()), Float.parseFloat(bean.getImageY()), Integer.parseInt(bean.getImageWidth()), "Wechat&WechatMoments");
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

        void showLimit(LinearLayout llLimit, boolean show) {
            if (show) {
                llLimit.setVisibility(View.VISIBLE);
            } else {
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

        public void updateUsed(int position, String isUsed, String couponRecordGuid, String shopId) {
            if (position > list.size()) {
                return;
            }
            CouponBean.CouponListBean bean = list.get(position);
            bean.setIsUsed(isUsed);
            bean.setShopId(shopId);
            bean.setCouponRecordGuid(couponRecordGuid);
            notifyDataSetChanged();
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
            @BindView(R.id.tv_coupon_share)
            TextView tvCouponShare;

            public ViewHolder(View view) {
//                R.layout.item_coupon_center
                ButterKnife.bind(this, view);
            }
        }
    }
}
