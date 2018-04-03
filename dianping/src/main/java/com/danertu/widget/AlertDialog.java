package com.danertu.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.webkit.JsResult;
import android.widget.Button;
import android.widget.TextView;

import com.danertu.dianping.R;

public abstract class AlertDialog extends Dialog {
	private TextView tv_title;
	private TextView tv_content;
	private Button b_left, b_right;
	private boolean isCanBack = true;
	private JsResult jsResult;
	private boolean isComfim = false;

	private AlertDialog(Context context) {
		super(context);
	}

	public AlertDialog(Context context, int theme) {
		super(context, theme);
		initDialog();
	}
	
	public AlertDialog(Context context, int theme, JsResult jsResult){
		super(context, theme);
		this.jsResult = jsResult;
		initDialog();
	}

	private void initDialog() {
		this.setContentView(R.layout.dialog_layout_ask);
		tv_title = (TextView) findViewById(R.id.tv_dialog_title);
		tv_title.setGravity(Gravity.CENTER);
		tv_title.setTextColor(Color.RED);
		tv_content = (TextView) findViewById(R.id.tv_dialog_content);
		b_left = (Button) findViewById(R.id.b_dialog_left);
		b_right = (Button) findViewById(R.id.b_dialog_right);
		b_left.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				cancelDialog();
			}
		});
		b_right.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				sure();
			}
		});
	}
	
	public abstract void sure();

	public abstract void cancelDialog();

	public boolean isCanBack() {
		return isCanBack;
	}

	public void setCanBack(boolean isCanBack) {
		this.isCanBack = isCanBack;
	}

	public void setTitle(String title) {
		tv_title.setText(title);
	}

	public void setContent(String content) {
		tv_content.setText(content);
	}

	public void setSureButton(String msg) {
		setSureButton(msg, null);
	}
	
	public void setSureButton(String msg, View.OnClickListener listener) {
		if(TextUtils.isEmpty(msg)){
			b_right.setVisibility(View.GONE);
		}else{
			b_right.setText(msg);
		}
		if(listener != null){
			b_right.setOnClickListener(listener);
		}
	}

	public void setCancelButton(String msg) {
		if(TextUtils.isEmpty(msg)){
			b_left.setVisibility(View.GONE);
		}else{
			b_left.setText(msg);
		}
	}

	private AlertDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	public void onBackPressed() {
		if (isCanBack())
			super.onBackPressed();
	}
	
	public boolean isComfim() {
		return isComfim;
	}

	public void setComfim(boolean isComfim) {
		this.isComfim = isComfim;
	}

	@Override
	public void dismiss() {
		super.dismiss();
		if(jsResult != null){
			if(isComfim){
				jsResult.confirm();
			}else{
				jsResult.cancel();
			}
		}
	}
}
