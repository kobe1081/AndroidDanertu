package com.danertu.dianping;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PayBackTipsFragment extends Fragment {
    private PayBackActivity base;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        base = (PayBackActivity) getActivity();
        base.setSystemBarWhite();
        View v = LayoutInflater.from(base).inflate(R.layout.payback_hotel_tips, null);
        TextView tips = (TextView) v.findViewById(R.id.tips);
        tips.setText("您的退款￥" + base.getTotalPrice() + "将在3个工作日内返回到您的账户上");
        v.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                base.finish();
            }
        });
        return v;
    }
}
