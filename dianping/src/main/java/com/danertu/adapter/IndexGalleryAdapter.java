package com.danertu.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.danertu.entity.IndexGalleryItemData;
import com.nostra13.universalimageloader.core.ImageLoader;

public class IndexGalleryAdapter extends BaseAdapter {

	Context context;
	int layoutId;
	int to[];
	List<IndexGalleryItemData> listData;
	ImageLoader imageLoader; 

	public IndexGalleryAdapter(Context context, int layoutId,
			List<IndexGalleryItemData> listData, int to[]) {
		this.context = context;
		this.layoutId = layoutId;
		this.listData = listData;
		this.to = to;
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listData.size() == 0 ? 0 : listData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(layoutId, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) convertView.findViewById(to[0]);

			
			viewHolder.textView = (TextView) convertView.findViewById(to[1]);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			resetViewHolder(viewHolder);
		}

		// ImageLoader.getInstance().displayImage(listData.get(pos).getImageUrl(),
		// viewHolder.imageView);
		String netUrl = "";
		if(listData.size() == 1 && position < 1){
			initView(netUrl, viewHolder, position, listData);
		}else if(listData.size() == 2 && position < 2){
			initView(netUrl, viewHolder, position, listData);
		}else if(listData.size() > 2){
			initView(netUrl, viewHolder, position, listData);
		}

		return convertView;

	}
	
	public void initView(String netUrl, ViewHolder viewHolder, int position, List<IndexGalleryItemData> listData){
		netUrl = listData.get(position).getImageUrl();
		imageLoader.displayImage(netUrl, viewHolder.imageView);
		viewHolder.textView.setText(listData.get(position).getPrice());
	}

	static class ViewHolder {
		ImageView imageView;
		TextView textView;
	}

	protected void resetViewHolder(ViewHolder viewHolder) {
		viewHolder.imageView.setImageBitmap(null);
		viewHolder.textView.setText("");
	}
}
