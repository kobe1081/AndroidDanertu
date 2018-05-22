package com.danertu.dianping.fragment.orderitem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.danertu.base.NewBaseFragment;
import com.danertu.dianping.R;
import com.danertu.widget.XListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OrderItemFragment extends NewBaseFragment<OrderItemContact.OrderItemView, OrderItemPresenter> implements OrderItemContact.OrderItemView {

    @BindView(R.id.tv_order_null_text)
    TextView tvOrderNullText;
    @BindView(R.id.xlv_order)
    XListView xlvOrder;
    Unbinder unbinder;
    private View view;

    @Override
    public OrderItemPresenter initPresenter() {
        return new OrderItemPresenter(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_orer_item, container, false);
        unbinder = ButterKnife.bind(this, view);
        presenter.onCreateView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        presenter.onDestroy();
    }

    class OrderAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }

        class ViewHolder {
            public ViewHolder(View view) {
            }
        }
    }
}
