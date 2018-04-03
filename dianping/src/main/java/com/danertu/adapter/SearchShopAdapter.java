package com.danertu.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.danertu.dianping.ActivityUtils;
import com.danertu.dianping.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SearchShopAdapter extends SearchAdapter {// 自己定义的构造函数

    public SearchShopAdapter(Context context, List<Map<String, Object>> data) {
        super(context, data);
    }

    @Override
    public View getView(int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.search_result_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = ((ViewHolder) view.getTag());
        }
        String imgName = getItem(position).get("titleImg").toString();
        String shopid = getItem(position).get("shopid").toString();
        String imgUrl = ActivityUtils.getImgUrl(imgName, shopid, null);
        Map<String, Object> item = getItem(position);
        viewHolder.shopName.setText(item.get("shopName").toString());
        viewHolder.address.setText(item.get("address").toString());
//		mobile.setText(item.get("mobile").toString());
        viewHolder.jyfw.setText(item.get("jyfw").toString());
        ImageLoader.getInstance().displayImage(imgUrl, viewHolder.imgView);
        return view;
    }

    class ViewHolder {
        ImageView imgView;
        TextView shopName;
        TextView address;
        TextView jyfw;

        //		TextView mobile = (TextView) view.findViewById(R.id.mobile);
        public ViewHolder(View view) {
            imgView = (ImageView) view.findViewById(R.id.titleImg);
            shopName = (TextView) view.findViewById(R.id.shopName);
            address = (TextView) view.findViewById(R.id.address);
            jyfw = (TextView) view.findViewById(R.id.jyfw);
//		 mobile = (TextView) view.findViewById(R.id.mobile);
        }
    }

}
