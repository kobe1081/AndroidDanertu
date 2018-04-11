package com.danertu.dianping.fragment.warehouse;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.danertu.base.BaseFragment;
import com.danertu.dianping.R;
import com.danertu.dianping.StockDetailActivity;
import com.danertu.entity.WarehouseBean;
import com.danertu.listener.LoadingListener;
import com.danertu.widget.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Viz on 2017/11/9.
 */

public class WarehouseFragment extends BaseFragment<WarehouseView, WarehousePresenter> implements WarehouseView, View.OnClickListener, XListView.IXListViewListener {

    private View view;
    private LinearLayout ll_stock;
    private TextView tv_all_produce;
    private TextView tv_stock;
    private TextView tv_sort;
    private TextView tv_null_text;
    private XListView lv_product;
    private ListAdapter adapter;
    public static final int REQUEST_DETAIL = 11;
    private LoadingListener loadingListener;
    private ImageLoader imageLoader;
    private boolean listSort = true;

    public void setLoadingListener(LoadingListener loadingListener) {
        this.loadingListener = loadingListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = ImageLoader.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_warehouse, container, false);
        findView(view);
        presenter.onCreateView();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            if (loadingListener != null)
                loadingListener.hideLoading();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public WarehousePresenter initPresenter() {
        return new WarehousePresenter(context);
    }

    private void findView(View view) {
        ll_stock = ((LinearLayout) view.findViewById(R.id.ll_stock));
        tv_all_produce = ((TextView) view.findViewById(R.id.tv_all_produce));
        tv_stock = ((TextView) view.findViewById(R.id.tv_stock));
        tv_sort = ((TextView) view.findViewById(R.id.tv_sort));
        tv_null_text = ((TextView) view.findViewById(R.id.tv_order_null_text));
        lv_product = (XListView) view.findViewById(R.id.lv_product);
        tv_sort.setOnClickListener(this);
        tv_stock.setOnClickListener(this);
        tv_all_produce.setOnClickListener(this);
    }

    @Override
    public void initListView(List<WarehouseBean.WareHouseListBean> productList) {
        adapter = new ListAdapter(productList);
        lv_product.setAdapter(adapter);
        lv_product.setPullRefreshEnable(true);
        lv_product.setPullLoadEnable(true);
        lv_product.setXListViewListener(this);
    }

    @Override
    public void updateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showStockPopup(List<WarehouseBean.ProductCategoryBean> categoryList) {
        //服务器返回的分类中没有全部，手动添加
        int totalCount = 0;
        boolean isContainAll = false;
        for (WarehouseBean.ProductCategoryBean productCategoryBean : categoryList) {
            if (productCategoryBean.getBrandName().contains("全部产品")) {
                isContainAll = true;
            }
            totalCount += Integer.parseInt(productCategoryBean.getTotal());
        }
        if (!isContainAll) {
            WarehouseBean.ProductCategoryBean bean = new WarehouseBean.ProductCategoryBean();
            bean.setBrandName("全部产品");
            bean.setProductCategoryId("");
            bean.setTotal(totalCount + "");
            categoryList.add(0, bean);
        }
        //设置contentView
        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_stock_stock, null);
        LinearLayout ll_root = (LinearLayout) contentView.findViewById(R.id.ll_root);
        final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(contentView);
        for (final WarehouseBean.ProductCategoryBean productCategoryBean : categoryList) {
            final TextView textView = new TextView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setBackgroundColor(R.color.white);
            textView.setText(productCategoryBean.getBrandName() + "（" + productCategoryBean.getTotal() + "）");
            textView.setTextColor(R.color.solid_while);
            textView.setTextSize(18);
            textView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_bottom_gray_line_1));
            textView.setPadding(15, 25, 15, 25);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_all_produce.setText(textView.getText());
                    //重新筛选数据
                    presenter.filterData(productCategoryBean.getProductCategoryId());
                    popupWindow.dismiss();
                }
            });
            ll_root.addView(textView);
        }

        contentView.findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        //显示PopupWindow
        popupWindow.setBackgroundDrawable(new BitmapDrawable());//注意这里如果不设置，下面的setOutsideTouchable(true);允许点击外部消失会失效
        popupWindow.setOutsideTouchable(true);
//        popupWindow.setAnimationStyle(R.style.AnimationBottomFade);
        popupWindow.showAsDropDown(tv_all_produce, 0, 0, Gravity.CENTER);

    }

    @Override
    public void showLoading() {
        if (loadingListener != null)
            loadingListener.showLoading();
    }

    @Override
    public void hideLoading() {
        if (loadingListener != null)
            loadingListener.hideLoading();
    }

    @Override
    public void updateView(int size) {
        if (size > 0) {
            ll_stock.setVisibility(View.VISIBLE);
            lv_product.setVisibility(View.VISIBLE);
            tv_null_text.setVisibility(View.GONE);
        } else {
            ll_stock.setVisibility(View.GONE);
            lv_product.setVisibility(View.GONE);
            tv_null_text.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void stopLoadMore() {
        lv_product.stopLoadMore();
    }

    @Override
    public void noMoreData() {
        lv_product.stopLoadMore();
        lv_product.setPullLoadEnable(false);
    }

    @Override
    public void stopFresh() {
        lv_product.stopRefresh();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_all_produce:
                presenter.showStockPopup();
                break;
            case R.id.tv_stock:
                //按照库存排序
                presenter.loadData("1");
                break;
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_DETAIL:
                    //刷新
                    presenter.refresh();
                    break;
            }
        }
    }


    class ListAdapter extends BaseAdapter {
        private List<WarehouseBean.WareHouseListBean> list;

        public ListAdapter(List<WarehouseBean.WareHouseListBean> productList) {
            list = productList;
        }

        @Override
        public int getCount() {
            return list.size();
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
                convertView = LayoutInflater.from(context).inflate(R.layout.item_stock, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = ((ViewHolder) convertView.getTag());
            }
            final WarehouseBean.WareHouseListBean bean = list.get(position);
            imageLoader.displayImage(getStockSmallImgPath(bean.getSmallImage()), holder.iv_product);
            holder.tv_product_name.setText(bean.getProductName());
            holder.tv_valuation.setText("估值：￥" + bean.getTotalPrice());
            holder.tv_stock_count.setText(bean.getStock());
            //囤货详细
            holder.rl_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StockDetailActivity.class);
                    intent.putExtra("guid", bean.getGuid());
                    intent.putExtra("productGuid", bean.getProductGuid());
                    intent.putExtra("img", bean.getSmallImage());
                    intent.putExtra("productName", bean.getProductName());
                    intent.putExtra("shopPrice", "");
                    intent.putExtra("marketPrice", "");
                    intent.putExtra("totalPrice", bean.getTotalPrice());
                    intent.putExtra("stockCount", bean.getStock());
                    startActivityForResult(intent, REQUEST_DETAIL);
                }
            });
            return convertView;
        }

        class ViewHolder {
            private RelativeLayout rl_root;
            private ImageView iv_product;
            private TextView tv_product_name;
            private TextView tv_valuation;
            private TextView tv_stock_count;

            public ViewHolder(View itemView) {
                rl_root = ((RelativeLayout) itemView.findViewById(R.id.rl_root));
                iv_product = ((ImageView) itemView.findViewById(R.id.iv_product));
                tv_product_name = ((TextView) itemView.findViewById(R.id.tv_product_name));
                tv_valuation = ((TextView) itemView.findViewById(R.id.tv_valuation));
                tv_stock_count = ((TextView) itemView.findViewById(R.id.tv_stock_count));
            }
        }

    }

}
