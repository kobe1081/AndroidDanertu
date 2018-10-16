package com.danertu.dianping;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.base.NewBaseActivity;
import com.danertu.dianping.activity.coupondetail.CouponDetailContact;
import com.danertu.dianping.activity.coupondetail.CouponDetailPresenter;
import com.danertu.entity.CouponBean;
import com.danertu.entity.CouponProductsBean;
import com.danertu.tools.DateTimeUtils;
import com.danertu.widget.CommonTools;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.grantland.widget.AutofitTextView;

/**
 * 作者:  Viz
 * 日期:  2018/8/1 11:20
 * <p>
 * 描述： 优惠券详情
 */
public class CouponDetailActivity extends NewBaseActivity<CouponDetailContact.CouponDetailView, CouponDetailPresenter> implements CouponDetailContact.CouponDetailView {

    @BindView(R.id.b_title_back)
    Button bTitleBack;
    @BindView(R.id.b_title_operation)
    Button bTitleOperation;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.personal_top_layout)
    FrameLayout personalTopLayout;
    @BindView(R.id.tv_coupon_money)
    AutofitTextView tvCouponMoney;
    @BindView(R.id.tv_coupon_name)
    TextView tvCouponName;
    @BindView(R.id.ll_product_parent)
    LinearLayout llProductParent;
    @BindView(R.id.ll_point_product)
    LinearLayout llPointProduct;
    @BindView(R.id.ll_description_parent)
    LinearLayout llDescriptionParent;
    @BindView(R.id.tv_option)
    TextView tvOption;
    @BindView(R.id.tv_coupon_date)
    TextView tvCouponDate;
    @BindView(R.id.tv_coupon_condition)
    TextView tvCouponCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_detail);
        ButterKnife.bind(this);
        initSystemBar();
        setSystemBarWhite();
        bTitleOperation.setText(getResources().getString(R.string.coupon_detail_title_share));
        tvTitle.setText(getResources().getString(R.string.coupon_detail_title));
        bTitleOperation.setTextColor(ContextCompat.getColor(context, R.color.blue1));
        presenter.onCreate(getIntent());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @OnClick(R.id.b_title_back)
    public void onBackClick(View view) {
        finish();
    }

    @Override
    public CouponDetailPresenter initPresenter() {
        return new CouponDetailPresenter(context);
    }

    @Override
    public void showCoupon(final CouponBean.CouponListBean bean, CouponProductsBean productsBean) {
        /**
         * 0-无限制，1-满减价格
         */
        if ("0".equals(bean.getUseCondition())) {
            tvCouponCondition.setText("无限制");
        } else {
            tvCouponCondition.setText("消费满" + bean.getUseConditionLimitPrice() + "可用");
        }
        if ("0".equals(bean.getDiscountType())) {
            String discountPrice = bean.getDiscountPrice();
            discountPrice = discountPrice.substring(0, discountPrice.indexOf("."));
            tvCouponMoney.setText(setStyleForUnSignNumRight(discountPrice + "元"));
        } else {
            String discountPercent = bean.getDiscountPercent();
            if (discountPercent.endsWith("0")) {
                discountPercent = discountPercent.substring(0, discountPercent.length() - 1);
            }
            tvCouponMoney.setText(setStyleForUnSignNumRight(discountPercent + "折"));
        }
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
        tvCouponDate.setText("使用有效期：" + validityPeriod);
        tvCouponName.setText(bean.getCouponName());
        llDescriptionParent.removeAllViews();
        String[] descriptionList = bean.getDescription().split("@@");
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        for (String aDescriptionList : descriptionList) {
            TextView textView = (TextView) layoutInflater.inflate(R.layout.item_coupon_limit, llDescriptionParent, false);
            textView.setText(aDescriptionList);
            llDescriptionParent.addView(textView);
        }

        llProductParent.removeAllViews();
        if (productsBean == null) {
            llPointProduct.setVisibility(View.GONE);
        } else {
            int imageWidth = getScreenWidth() / 4 - CommonTools.dip2px(context, 15);
            List<CouponProductsBean.ProductListBean> productList = productsBean.getProductList();
            int length = productList.size() > 4 ? 4 : productList.size();
            for (int i = 0; i < length; i++) {
                final CouponProductsBean.ProductListBean listBean = productList.get(i);
                ImageView imageView = (ImageView) layoutInflater.inflate(R.layout.item_coupon_product_img, llProductParent, false);
                if (imageView.getLayoutParams() == null) {
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.width = imageWidth;
                layoutParams.height = imageWidth;
                imageView.setLayoutParams(layoutParams);
                ImageLoader.getInstance().displayImage(getSmallImgPath(listBean.getSmallImage(), listBean.getAgentID(), listBean.getSupplierLoginID()), imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (listBean.getProductJumpType()) {
                            case "0"://一般商品
                                jsStartActivity("com.danertu.dianping.ProductDetailsActivity2", "guid|" + listBean.getGuid() + ",;shopid|" + getShopId());
                                break;
                            case "1"://温泉
                                switch (listBean.getAppointProductType()) {
                                    case "1":
                                    case "2":
                                        jsStartActivity("com.danertu.dianping.HtmlActivity", "pageName|" + "android/" + listBean.getAppointProductUrl() + "&platform=android&timestamp=" + System.currentTimeMillis() + ",;guid|" + listBean.getGuid() + ",;shopid|" + listBean.getShopId() + ",;productCategory|" + listBean.getAppointProductType());
                                        break;
                                    case "3":
                                        jsStartActivity("com.danertu.dianping.ProductDetailsActivity2", "guid|" + listBean.getGuid() + ",;shopid|" + listBean.getShopId());
                                        break;
                                    default:
                                        jsStartActivity("com.danertu.dianping.ProductDetailsActivity2", "guid|" + listBean.getGuid() + ",;shopid|" + listBean.getShopId());
                                        break;
                                }

                                break;
                            default:
                                jsStartActivity("com.danertu.dianping.ProductDetailsActivity2", "guid|" + listBean.getGuid() + ",;shopid|" + getShopId());
                                break;
                        }
                    }
                });
                llProductParent.addView(imageView);
            }

            llPointProduct.setVisibility(View.VISIBLE);
        }

        //空是没领，0-领了未使用，1-领了并且用了
        if ("0".equals(bean.getIsUsed())) {
            tvOption.setText("去使用");
        } else {
            tvOption.setText("立即领取");
        }


        tvOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (tvOption.getText().toString()) {
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
                                backToHome();
                                break;
                        }
                        break;
                    case "立即领取":
                        presenter.getCoupon(bean.getGuid(), getUid(), getShopId());
                        break;
                }
            }
        });

        bTitleOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClickMoreTimesShortTime()) {
                    return;
                }
                try {
                    if ("3".equals(bean.getUseScope())) {
                        shareImgWithQRCode(bean.getImageUrl(), bean.getCouponShareUrl() + "&shopid=" + bean.getUseAgentAppoint(), Float.parseFloat(bean.getImageX()), Float.parseFloat(bean.getImageY()), Integer.parseInt(bean.getImageWidth()), "Wechat&WechatMoments");
                    } else {
                        shareImgWithQRCode(bean.getImageUrl(), bean.getCouponShareUrl()+ "&shopid=" + getShopId(), Float.parseFloat(bean.getImageX()), Float.parseFloat(bean.getImageY()), Integer.parseInt(bean.getImageWidth()), "Wechat&WechatMoments");
                    }
                } catch (Exception e) {
                    jsShowMsg("分享失败");
                    if (Constants.isDebug) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public void updateCouponState(String isUsed) {
        if ("0".equals(isUsed)) {
            tvOption.setText("去使用");
        } else {
            tvOption.setText("立即领取");
        }
    }

    @Override
    public void toAgentShop(String levelType, String shopId) {
        jsStartActivity("com.danertu.dianping.IndexActivity", "shopid|" + shopId + ",;shoptype|" + levelType);
    }

    public SpannableStringBuilder setStyleForUnSignNumRight(String text) {
        SpannableStringBuilder unSignNumBuilder = new SpannableStringBuilder(text);
        unSignNumBuilder.setSpan(new AbsoluteSizeSpan(CommonTools.dip2px(context, 16)), text.length() - 1, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return unSignNumBuilder;
    }
}
