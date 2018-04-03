package com.danertu.entity;

import java.util.List;

import com.danertu.dianping.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CNAddressAdapter extends BaseAdapter {

	private Context context;
	private List<CNAddressListItem> myList;

	public CNAddressAdapter(Context context, List<CNAddressListItem> myList) {
		this.context = context;
		this.myList = myList;
	}

	public int getCount() {
		return myList.size();
	}

	public Object getItem(int position) {
		return myList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		CNAddressListItem myListItem = myList.get(position);
		return new MyAdapterView(this.context, myListItem);
	}

	class MyAdapterView extends LinearLayout {
		public static final String LOG_TAG = "MyAdapterView";

		public MyAdapterView(Context context, CNAddressListItem myListItem) {
			super(context);
			this.setOrientation(HORIZONTAL);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(10, 15, 1, 15);

			TextView name = new TextView(context);
			name.setTextSize(getResources().getDimension(
					R.dimen.micro_text_size));
			name.setTextColor(getResources().getColor(R.color.gray));
			name.setText(myListItem.getName());
			addView(name, params);

			LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params2.setMargins(1, 1, 1, 1);

			TextView pcode = new TextView(context);
			pcode.setText(myListItem.getPcode());
			addView(pcode, params2);
			pcode.setVisibility(GONE);

		}

	}

}