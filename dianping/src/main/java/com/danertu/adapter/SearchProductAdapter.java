package com.danertu.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.danertu.db.DBManager;
import com.danertu.dianping.ActivityUtils;
import com.danertu.dianping.R;
import com.danertu.widget.CommonTools;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SearchProductAdapter extends SearchAdapter {
    private Context context;
    private DBManager dbManager;
    public SearchProductAdapter(Context context, List<Map<String, Object>> data) {
        super(context, data);
        this.context=context;
        dbManager=DBManager.getInstance();
    }

    @Override
    public View getView(int position, View v, ViewGroup arg2) {
        ViewHolder holder;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.productlist_item, null);
            holder = new ViewHolder(v);
            v.setTag(holder);
        } else {
            holder = ((ViewHolder) v.getTag());
        }

        String imgName = getItem(position).get("img").toString();
        String agentID = getItem(position).get("agentID").toString();
        String supplierID = getItem(position).get("supplierID").toString();
        String marketPrice = getItem(position).get("marketPrice").toString();
        String ss = ActivityUtils.getImgUrl(imgName, agentID, supplierID,dbManager.GetLoginUid(context));

        String proFormat = getItem(position).get("proFormat").toString();
        proFormat = proFormat.replaceAll(" ", "");
        if (proFormat.equals("")) {
            holder.tv_format.setVisibility(View.GONE);
        } else {
            holder.tv_format.setVisibility(View.VISIBLE);
            holder.tv_format.setText(proFormat);
        }
        holder.proName.setText(getItem(position).get("proName").toString());
        SpannableStringBuilder ssb = new SpannableStringBuilder("￥" + getItem(position).get("price").toString());
        ssb.setSpan(new AbsoluteSizeSpan(CommonTools.sp2px(context, 13)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.proPrice.setText(ssb);
        holder.tv_marketPrice.setText("原价: ￥" + marketPrice);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(ss, holder.imgView);

        return v;
    }

    class ViewHolder {
        ImageView imgView;
        TextView proName;
        TextView tv_format;
        TextView proPrice;
        TextView tv_marketPrice;

        public ViewHolder(View v) {
            imgView = (ImageView) v.findViewById(R.id.productIcon);
            proName = (TextView) v.findViewById(R.id.proName);
            tv_format = (TextView) v.findViewById(R.id.proNameDetails);
            proPrice = (TextView) v.findViewById(R.id.proPrice);
            tv_marketPrice = (TextView) v.findViewById(R.id.mpromarketprice);
        }
    }
}
