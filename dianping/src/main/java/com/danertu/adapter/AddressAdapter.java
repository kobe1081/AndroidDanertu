package com.danertu.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.danertu.db.DBManager;
import com.danertu.dianping.R;
import com.danertu.listui.AddressItemUI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class AddressAdapter extends BaseAdapter{

	private ArrayList<HashMap<String, Object>> data;
	private LayoutInflater layoutInflater;
	private Context context;


	public AddressAdapter(Context context,ArrayList<HashMap<String, Object>> data) {
		this.context = context;
		this.data = data;
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position ) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int p = position;
		AddressItemUI addressUi = null;
		if(convertView==null){
			addressUi = new AddressItemUI();
			// 获取组件布局
			convertView = layoutInflater.inflate(R.layout.address_item, null);
//			addressUi.adress_name = (TextView) convertView.findViewById(R.id.adress_name);
//			addressUi.adress_mobile = (TextView) convertView.findViewById(R.id.adress_mobile);
//			addressUi.adress_Adress = (TextView) convertView.findViewById(R.id.adress_Adress);
//			addressUi.btn_isdefault = (Button) convertView.findViewById(R.id.btn_isdefault);
//			addressUi.btn_isdefault = (Button) convertView.findViewById(R.id.btn_isdefault);
//			addressUi.address_tv_isdefault = (TextView) convertView.findViewById(R.id.address_tv_isdefault);
//			convertView = layoutInflater.inflate(R.layout.adress_item, null);
//			addressUi.adress_name = (TextView) convertView.findViewById(R.id.adress_name);
//			addressUi.adress_mobile = (TextView) convertView.findViewById(R.id.adress_mobile);
//			addressUi.adress_Adress = (TextView) convertView.findViewById(R.id.adress_Adress);
//			addressUi.btn_isdefault = (Button) convertView.findViewById(R.id.btn_isdefault);
//			addressUi.btn_isdefault = (Button) convertView.findViewById(R.id.btn_isdefault);
//			addressUi.address_tv_isdefault = (TextView) convertView.findViewById(R.id.address_tv_isdefault);
			// 这里要注意，是使用的tag来存储数据的。
			convertView.setTag(addressUi);
		}
		else {
			addressUi = (AddressItemUI) convertView.getTag();
		}
		// 绑定数据、以及事件触发
		addressUi.adress_name.setText((String)data.get(position).get("adress_name"));
		addressUi.adress_mobile.setText((String)data.get(position).get("adress_mobile"));
		addressUi.adress_Adress.setText((String)data.get(position).get("adress_Adress"));
		if(data.get(position).get("adress_isdefault").toString().equals("1"))
		{
			addressUi.address_tv_isdefault.setVisibility(View.VISIBLE);
			addressUi.btn_isdefault.setVisibility(Button.GONE);
		}else
		{
			addressUi.address_tv_isdefault.setVisibility(View.GONE);
			addressUi.btn_isdefault.setVisibility(Button.VISIBLE);
		}
		addressUi.btn_isdefault.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				//showInfo();
				String sguid =data.get(p).get("adress_Guid").toString();
				//	CommonTools.showShortToast(add.this, "登录失败，请确认账号与密码的正确性");
//				DBManager db = new DBManager();
				DBManager db = DBManager.getInstance();
				db.SetDefaultAddress(context, sguid);

			}

		});
		return convertView;
	}

	public void refresh(ArrayList<HashMap<String, Object>> list) {
		data = list;
		notifyDataSetChanged();
	}

}
