package com.danertu.tools;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.danertu.dianping.R;

public class LoadingDialog extends Dialog {

    private ImageView mImageView;
    //	private Context context;
    private AnimationDrawable anim = null;

    public LoadingDialog(Context context) {
        super(context, R.style.loadingDialogStyle);
//		this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        setCanceledOnTouchOutside(false); // 设置点击屏幕Dialog不消失
        mImageView = ((ImageView) findViewById(R.id.loading_rotate));
        mImageView.setImageResource(R.drawable.loading_anim);
        anim = ((AnimationDrawable) mImageView.getDrawable());
        // anim.start();
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                anim.start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isShowing())
            return;
        super.onBackPressed();
    }

}
