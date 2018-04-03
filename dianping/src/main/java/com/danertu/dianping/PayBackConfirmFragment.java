package com.danertu.dianping;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * 退款
 */
public class PayBackConfirmFragment extends Fragment {
    private PayBackActivity base;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        base = (PayBackActivity) getActivity();
        base.setSystemBarWhite();
        View v = LayoutInflater.from(base).inflate(R.layout.payback_hotel_confirm, null);
        TextView price = (TextView) v.findViewById(R.id.price);
        TextView price_total = (TextView) v.findViewById(R.id.price_total);
        Button payBack = (Button) v.findViewById(R.id.payBack_confirm);
        String priceStr = "￥" + base.getTotalPrice();
        price.setText(priceStr);
        price_total.setText(priceStr);
        payBack.setText("确定退款" + priceStr);
        payBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = getArguments();
                base.payBack(b.getString("reason"), b.getString("remark"), true);
            }
        });
        v.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager manager = base.getSupportFragmentManager();
                manager.popBackStack();
            }
        });
        return v;
    }

}
