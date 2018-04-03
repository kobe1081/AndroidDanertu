package wl.codelibrary.widget;

import wl.codelibrary.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class IOSDialog extends Dialog {
	private TextView title, content;
	private Button left, right;
	private View line;
	private boolean isComfim;
	private JsResult jsResult;
	private Context context;
	public IOSDialog(Context context) {
		this(context, R.style.Dialog);
	}
	
	public IOSDialog(Context context, JsResult jsResult) {
		this(context, R.style.Dialog, jsResult);
	}
	
	public IOSDialog(Context context, int theme, JsResult jsResult) {
		this(context, theme);
		this.jsResult = jsResult;
	}

	public IOSDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		init();
	}
	
	public void init(){
		setContentView(R.layout.dialog_ios_style);
		title = (TextView) findViewById(R.id.dialog_ios_title);
		content = (EditText) findViewById(R.id.dialog_ios_content);
		left = (Button) findViewById(R.id.b_dialog_left);
		right = (Button) findViewById(R.id.b_dialog_right);
		title.setVisibility(View.GONE);
		line = findViewById(R.id.line);
		setWindowAnimations(R.style.PopupAnimation);
	}

	public void setTitle(CharSequence title) {
		// TODO Auto-generated method stub
		this.title.setVisibility(View.VISIBLE);
		this.title.setText(title);
	}

	public void setMessage(CharSequence message) {
		// TODO Auto-generated method stub
		this.content.setText(message);
	}

	public void setPositiveButton(CharSequence text, View.OnClickListener listener) {
		// TODO Auto-generated method stub
		left.setText(text);
		left.setOnClickListener(listener);
	}

	public void setNegativeButton(CharSequence text, View.OnClickListener listener) {
		// TODO Auto-generated method stub
		right.setText(text);
		right.setOnClickListener(listener);
	}

	public void setSingleButton(CharSequence text, View.OnClickListener listener){
		right.setVisibility(View.GONE);
		line.setVisibility(View.GONE);
		left.setBackgroundResource(R.drawable.dialog_ios_btn_one_selector);
		left.setText(text);
		left.setOnClickListener(listener);
	}

	public boolean isComfim() {
		return isComfim;
	}

	public void setComfim(boolean isComfim) {
		this.isComfim = isComfim;
	}
	
	public void setWindowAnimations(int resId){
		getWindow().setWindowAnimations(resId);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		if(context instanceof Activity){
			Activity act = (Activity)context;
			if(act.isFinishing()){
				Log.e("IOSDialog_err", "activity already finished");
				return;
			}
		}
		super.show();
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
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
