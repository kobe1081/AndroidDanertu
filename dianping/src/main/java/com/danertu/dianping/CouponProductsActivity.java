package com.danertu.dianping;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.danertu.base.NewBaseActivity;
import com.danertu.dianping.activity.couponproducts.CouponProductsContact;
import com.danertu.dianping.activity.couponproducts.CouponProductsPresenter;
import com.danertu.entity.CouponProductsBean;
import com.danertu.widget.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 优惠券指定的可用商品列表
 */
public class CouponProductsActivity extends NewBaseActivity<CouponProductsContact.CouponProductsView, CouponProductsPresenter> implements CouponProductsContact.CouponProductsView, SwipeRefreshLayout.OnRefreshListener, XListView.IXListViewListener {

    @BindView(R.id.b_title_back)
    Button bTitleBack;
    @BindView(R.id.b_title_operation)
    Button bTitleOperation;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.personal_top_layout)
    FrameLayout personalTopLayout;
    @BindView(R.id.lv_product)
    XListView lvProduct;
    @BindView(R.id.tv_try_again)
    TextView tvTryAgain;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    private CouponProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_products);
        setSystemBarWhite();
        ButterKnife.bind(this);
        presenter.onCreateView(getIntent());
    }

    @Override
    public CouponProductsPresenter initPresenter() {
        return new CouponProductsPresenter(context);
    }

    @OnClick(R.id.b_title_back)
    public void onBackClick(View view) {
        finish();
    }

    @OnClick(R.id.tv_try_again)
    public void onTryAgainClick(View view) {
        presenter.loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {
        presenter.loadData();
    }

    @Override
    public void initList(List<CouponProductsBean.ProductListBean> list) {
        lvProduct.setXListViewListener(this);
        swipeRefresh.setOnRefreshListener(this);
        lvProduct.setPullLoadEnable(false);
        lvProduct.setPullRefreshEnable(true);
        adapter = new CouponProductsAdapter(list);
        lvProduct.setAdapter(adapter);
    }

    @Override
    public void notifyChange(int listSize) {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (listSize > 0) {
            lvProduct.setVisibility(View.VISIBLE);
            swipeRefresh.setEnabled(false);
            tvTryAgain.setVisibility(View.GONE);
        } else {
            swipeRefresh.setEnabled(true);
            lvProduct.setVisibility(View.GONE);
            tvTryAgain.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void stopRefresh() {
        lvProduct.stopRefresh();
    }

    class CouponProductsAdapter extends BaseAdapter {
        List<CouponProductsBean.ProductListBean> list;

        public CouponProductsAdapter(List<CouponProductsBean.ProductListBean> list) {
            this.list = list;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_coupon_product, null, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final CouponProductsBean.ProductListBean bean = list.get(position);
            holder.tvProductName.setText(bean.getName());
            holder.tvProductShopPrice.setText("￥" + String.valueOf(bean.getShopPrice()));
            holder.tvProductMarketPrice.setText("原价:￥" + String.valueOf(bean.getMarketPrice()));

            ImageLoader.getInstance().displayImage(getImgUrl(bean.getSmallImage(), bean.getAgentID() == null ? "" : bean.getAgentID(), bean.getSupplierLoginID() == null ? "" : bean.getSupplierLoginID()), holder.ivProductImg);

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (bean.getProductJumpType()) {
                        case "0"://一般商品
                            jsStartActivity("com.danertu.dianping.ProductDetailsActivity2", "guid|" + bean.getGuid() + ",;shopid|" + getShopId());
                            break;
                        case "1"://温泉

                            switch (bean.getAppointProductType()) {
                                case "1":case "2":
                                    jsStartActivity("com.danertu.dianping.HtmlActivity", "pageName|"+"android/"+bean.getAppointProductUrl()+ "&platform=android&timestamp=" + System.currentTimeMillis()+",;guid|" + bean.getGuid() + ",;shopid|" + bean.getShopId()+",;productCategory|"+bean.getAppointProductType());
                                    break;
                                case "3":
                                    jsStartActivity("com.danertu.dianping.ProductDetailsActivity2", "guid|" + bean.getGuid() + ",;shopid|" + bean.getShopId());
                                    break;
                                default:
                                    jsStartActivity("com.danertu.dianping.ProductDetailsActivity2", "guid|" + bean.getGuid() + ",;shopid|" + bean.getShopId());
                                    break;
                            }

                            break;
                        default:
                            jsStartActivity("com.danertu.dianping.ProductDetailsActivity2", "guid|" + bean.getGuid() + ",;shopid|" + getShopId());
                            break;
                    }
                }
            });

            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.iv_product_img)
            ImageView ivProductImg;
            @BindView(R.id.tv_product_name)
            TextView tvProductName;
            @BindView(R.id.root)
            RelativeLayout root;
            @BindView(R.id.tv_product_market_price)
            TextView tvProductMarketPrice;
            @BindView(R.id.tv_product_shop_price)
            TextView tvProductShopPrice;

            public ViewHolder(View view) {
//                R.layout.item_coupon_product
                ButterKnife.bind(this, view);
            }
        }
    }
}
