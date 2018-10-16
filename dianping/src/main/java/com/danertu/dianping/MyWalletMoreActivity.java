package com.danertu.dianping;

import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;

import com.config.Constants;
import com.danertu.widget.CommonTools;
import com.danertu.widget.PayPswDialog;

public class MyWalletMoreActivity extends BaseWebActivity {
	private String WEBPAGE = "Android_wallet_set.html";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("钱包设置", null);
		if(!isLogined()){
			CommonTools.showShortToast(this, "请先登录！");
			return;
		}
		handler = new MyHandler(this);
//		startWebView("file:///android_asset/wallet_set.html");
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

		webView.addJavascriptInterface(this, "app");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		startWebView(Constants.appWebPageUrl+WEBPAGE);
		initPswDialog();
//		startActivity(new Intent(getContext(), AccountToPay.class));
	}
	
	private PayPswDialog dialog_psw;
	private void initPswDialog() {
		dialog_psw = new PayPswDialog(this, R.style.Dialog) {
			@Override
			public void cancelDialog() {
				dismiss();
			}

			@Override
			public void passwordRight() {
				handler.sendEmptyMessage(CANCLE_DIALOG);
				if(tag == CHANGE_BIND_BANKCARD)
					jsStartActivity("BankCard", "title|更换银行卡,;pageName|Android_wallet_changebindcard.html");
				else if(tag == CHANGE_PSW){
					jsStartActivity("MyWalletFirstSet","title|修改支付密码,;pageName|Android_wallet_password1.html");
				}else {
					webView.loadUrl(Constants.IFACE+"passwordRight()");
				}
			}

			@Override
			public void passwordWrong() {
				CommonTools.showShortToast(getContext(), "支付密码不正确！");
				webView.loadUrl(Constants.IFACE+"passwordWrong()");
			}
		};
	}
	
	final int CHANGE_PSW = 1;
	final int CHANGE_BIND_BANKCARD = 2;
	private int tag = 0;
	@JavascriptInterface
	public void jsShowDialog(int tag){
		this.tag = tag;
		handler.sendEmptyMessage(SHOW_DIALOG);
	}

	public final static int SHOW_DIALOG = 110;
	public final static int CANCLE_DIALOG = 111;
	public class MyHandler extends BaseHandler{

		public MyHandler(BaseActivity ui) {
			super(ui);
		}
		
		public void handleMessage(Message msg){
			if(msg.what == SHOW_DIALOG){
				dialog_psw.show();
				
			}else if(msg.what == CANCLE_DIALOG){
				dialog_psw.dismiss();
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.b_title_back2:
			finish();
			break;
		default:
			break;
		}
	}

}
