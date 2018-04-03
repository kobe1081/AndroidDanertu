package com.danertu.dianping.frag.stockorder;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.danertu.base.BaseFragment;
import com.danertu.dianping.R;
import com.danertu.dianping.StockOrderDetailActivity;
import com.danertu.entity.WareHouseOrderBean;
import com.danertu.listener.LoadingListener;
import com.danertu.tools.Logger;
import com.danertu.widget.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Viz on 2017/11/9.
 */

public class StockOrderFragment extends BaseFragment<StockOrderView, StockOrderPresenter> implements View.OnClickListener, XListView.IXListViewListener, StockOrderView {

    private View view;
    private LinearLayout ll_order;
    private TextView tv_all_product;
    private TextView tv_order_state;
    private TextView tv_order_null_text;
    private TextView tv_sort;
    private XListView lv_order;
    private ListAdapter adapter;
    public static final int REQUEST_TO_ORDER_DETAIL = 122;
    private LoadingListener loadingListener;
    private ImageLoader imageLoader;

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
        view = inflater.inflate(R.layout.fragment_stock_order, container, false);
        findView(view);
        presenter.onCreateView();
        return view;
    }

    private void findView(View view) {
        ll_order = ((LinearLayout) view.findViewById(R.id.ll_order));
        tv_all_product = ((TextView) view.findViewById(R.id.tv_all_produce));
        tv_order_state = ((TextView) view.findViewById(R.id.tv_order_state));
        tv_order_null_text = ((TextView) view.findViewById(R.id.tv_order_null_text));
        tv_sort = ((TextView) view.findViewById(R.id.tv_sort));
        lv_order = ((XListView) view.findViewById(R.id.lv_order));
        tv_all_product.setOnClickListener(this);
        tv_order_state.setOnClickListener(this);
        tv_sort.setOnClickListener(this);
        lv_order.setXListViewListener(this);
    }


    @Override
    public StockOrderPresenter initPresenter() {
        return new StockOrderPresenter(context);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_all_produce:
                break;
            case R.id.tv_order_state:
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

    /**
     * 订单状态筛选，不做
     *
     * @param list
     */
    public void showStatePopup(List<String> list) {
        //设置contentView
        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_stock_stock, null);
        LinearLayout ll_root = (LinearLayout) contentView.findViewById(R.id.ll_root);
        final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(contentView);
        for (String s : list) {
            final TextView textView = new TextView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setBackgroundColor(R.color.white);
            textView.setText(s);
            textView.setTextColor(R.color.solid_while);
            textView.setTextSize(18);
            textView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_bottom_gray_line_1));
            textView.setPadding(15, 25, 15, 25);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //重新筛选数据
                    tv_all_product.setText(textView.getText());
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
        popupWindow.showAsDropDown(tv_all_product, 0, 0, Gravity.CENTER);

    }

    /**
     * 用不到的
     *
     * @param list
     */
    public void showStockPopup(List<String> list) {
        //设置contentView
        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_stock_stock, null);
        LinearLayout ll_root = (LinearLayout) contentView.findViewById(R.id.ll_root);
        final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(contentView);
        for (String s : list) {
            final TextView textView = new TextView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setBackgroundColor(R.color.white);
            textView.setText(s);
            textView.setTextColor(R.color.solid_while);
            textView.setTextSize(18);
            textView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_bottom_gray_line_1));
            textView.setPadding(15, 25, 15, 25);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //重新筛选数据
                    tv_all_product.setText(textView.getText());
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
        popupWindow.showAsDropDown(tv_all_product, 0, 0, Gravity.CENTER);

    }

    @Override
    public void updateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void updateView(int listSize) {
        adapter.notifyDataSetChanged();
        if (listSize > 0) {
            ll_order.setVisibility(View.VISIBLE);
            lv_order.setVisibility(View.VISIBLE);
            tv_order_null_text.setVisibility(View.GONE);
        } else {
            ll_order.setVisibility(View.GONE);
            lv_order.setVisibility(View.GONE);
            tv_order_null_text.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initList(List<WareHouseOrderBean.WareHouseOrderListBean> list) {
        adapter = new ListAdapter(list);
        lv_order.setAdapter(adapter);
        lv_order.setPullRefreshEnable(true);
        lv_order.setPullLoadEnable(true);
        lv_order.setXListViewListener(this);
    }

    @Override
    public void stopLoadMore() {
        lv_order.stopLoadMore();
    }

    @Override
    public void noMoreData() {
        lv_order.stopLoadMore();
        lv_order.setPullLoadEnable(false);
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
    public void stopRefresh() {
        lv_order.stopRefresh();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.e(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 列表adapter
     */
    class ListAdapter extends BaseAdapter {
        List<WareHouseOrderBean.WareHouseOrderListBean> list;

        public ListAdapter(List<WareHouseOrderBean.WareHouseOrderListBean> list) {
            this.list = list;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_stock_order, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = ((ViewHolder) convertView.getTag());
            }

            final WareHouseOrderBean.WareHouseOrderListBean bean = list.get(position);
            imageLoader.displayImage(getStockSmallImgPath(bean.getSmallImage()), holder.iv_order_produce);
            holder.tv_order_number.setText("订单号：" + bean.getOrderNumber());
            holder.tv_order_num.setText("x" + bean.getBuyNumber());
            holder.tv_order_name.setText(bean.getProductName());
            holder.tv_shop_price.setText("￥" + bean.getShopPrice());
            holder.tv_market_price.setText("￥" + bean.getMarketPrice());
            holder.tv_market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//给原价加上删除线
            holder.tv_market_price.getPaint().setAntiAlias(true);

            holder.btn_order_detail.setVisibility(View.VISIBLE);
            holder.btn_order_return.setVisibility(View.GONE);
            //orderState==  0 未确认，1 已确认，2 已取消，3 已作废
            //shipmentState==  0 未发货，1 已发货，2 已收货，3 配货，4 退货中，5 已退货
            final String orderState = bean.getOrderStatus();
            final String shipmentState = bean.getShipmentStatus();

            String state = "";
            switch (orderState) {
                case "2":
                    state = "已取消";
                    break;
                case "3":
                    state = "已作废";
                    break;
                case "4":
                    state = "请退货";
                    break;
                case "5":
                    state = "交易成功";
                    break;
            }

            switch (shipmentState) {
                case "0":
                    //未发货
                    state = "待发货";
                    break;
                case "1":
                    //已发货
                    state = "待收货";
                    break;
                case "2":
                    //已收货
                    state = "已收货";
                    break;
                case "3":
                    //配货
                    break;
                case "4":
                    //退货
                    state = "退货中";
                    holder.btn_order_return.setVisibility(View.VISIBLE);
                    break;
                case "5":
                    state = "已退货";
//                    holder.btn_order_return.setVisibility(View.VISIBLE);

                    break;
            }

            holder.tv_order_state.setText(state);
            holder.ll_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //订单详细
                    toOrderDetail(bean.getGuid(), bean.getOrderNumber(), orderState, shipmentState, position);
                }
            });
            holder.btn_order_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //订单详细
                    toOrderDetail(bean.getGuid(), bean.getOrderNumber(), orderState, shipmentState, position);
                }
            });
            holder.btn_order_return.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //退货处理
                    toOrderDetail(bean.getGuid(), bean.getOrderNumber(), orderState, shipmentState, position);
                }
            });
            return convertView;
        }

        //订单详情
        public void toOrderDetail(String guid, String orderNumber, String orderState, String shipmentState, int position) {
            Intent intent = new Intent(context, StockOrderDetailActivity.class);
            intent.putExtra("guid", guid);
            intent.putExtra("orderNumber", orderNumber);
            intent.putExtra("orderState", orderState);
            intent.putExtra("shipmentState", shipmentState);
            intent.putExtra("position", position);

            startActivityForResult(intent, REQUEST_TO_ORDER_DETAIL);
        }

        class ViewHolder {
            private LinearLayout ll_root;
            private ImageView iv_order_produce;
            private TextView tv_shop_price;
            private TextView tv_market_price;
            private TextView tv_order_num;
            private TextView tv_order_number;
            private TextView tv_order_name;
            private TextView tv_order_state;
            private TextView tv_order_produce_dec;
            private Button btn_order_return;
            private Button btn_order_detail;

            public ViewHolder(View itemView) {
                ll_root = ((LinearLayout) itemView.findViewById(R.id.ll_root));
                iv_order_produce = ((ImageView) itemView.findViewById(R.id.iv_order_produce));
                tv_shop_price = ((TextView) itemView.findViewById(R.id.tv_shop_price));
                tv_market_price = ((TextView) itemView.findViewById(R.id.tv_market_price));
                tv_order_num = ((TextView) itemView.findViewById(R.id.tv_order_num));
                tv_order_number = ((TextView) itemView.findViewById(R.id.tv_order_number));
                tv_order_name = ((TextView) itemView.findViewById(R.id.tv_order_name));
                tv_order_state = ((TextView) itemView.findViewById(R.id.tv_order_trade_state));
                tv_order_produce_dec = ((TextView) itemView.findViewById(R.id.tv_order_produce_dec));
                btn_order_return = ((Button) itemView.findViewById(R.id.btn_order_return));
                btn_order_detail = ((Button) itemView.findViewById(R.id.btn_order_detail));
            }
        }

    }
}
