package com.danertu.dianping;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 取消预定
 */
public class PayBackFragment extends Fragment {
    private PayBackActivity base;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        base = (PayBackActivity) getActivity();
        base.setSystemBarWhite();
        View v = LayoutInflater.from(base).inflate(R.layout.payback_hotel, null);
        LinearLayout ll = (LinearLayout) v.findViewById(R.id.parent_reason);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View item = ll.getChildAt(i);
            item.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    click(v);
                }
            });
        }
        return v;
    }

    public void click(View v) {
        if (v instanceof ImageButton) {
            base.finish();
        } else if (v instanceof TextView) {
            PayBackConfirmFragment pbcf = new PayBackConfirmFragment();
            String reason = ((TextView) v).getText().toString();
            Bundle b = new Bundle();
            b.putString("reason", reason);
            b.putString("remark", "");
            pbcf.setArguments(b);
            FragmentManager manager = base.getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            String tag = base.TAG_REASON_CONFIRM;
            transaction.add(R.id.container, pbcf, tag);
            transaction.addToBackStack(tag);
            transaction.commit();
        }
    }
}
