package com.danertu.widget;

import com.danertu.dianping.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;


public class ExitView extends PopupWindow {


    private Button btn_setting_exit, btn_cancel;
    private View mMenuView;

    public ExitView(Activity context, OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.exit_dialog_from_settings, null);
        btn_setting_exit = (Button) mMenuView.findViewById(R.id.btn_exit);

        btn_cancel = (Button) mMenuView.findViewById(R.id.btn_cancel);
        //取锟斤拷钮
        btn_cancel.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                //锟斤拷俚锟斤拷锟斤拷锟?
                dismiss();
            }
        });
        //锟斤拷锟矫帮拷钮锟斤拷锟斤拷
        btn_setting_exit.setOnClickListener(itemsOnClick);

        //锟斤拷锟斤拷SelectPicPopupWindow锟斤拷View
        this.setContentView(mMenuView);
        //锟斤拷锟斤拷SelectPicPopupWindow锟斤拷锟斤拷锟斤拷锟斤拷目锟?
        this.setWidth(LayoutParams.MATCH_PARENT);
        //锟斤拷锟斤拷SelectPicPopupWindow锟斤拷锟斤拷锟斤拷锟斤拷母锟?
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //锟斤拷锟斤拷SelectPicPopupWindow锟斤拷锟斤拷锟斤拷锟斤拷傻锟斤拷
        this.setFocusable(true);

        //实锟斤拷一锟斤拷ColorDrawable锟斤拷色为锟斤拷透锟斤拷
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //锟斤拷锟斤拷SelectPicPopupWindow锟斤拷锟斤拷锟斤拷锟斤拷谋锟斤拷锟?
        this.setBackgroundDrawable(dw);
        //mMenuView锟斤拷锟絆nTouchListener锟斤拷锟斤拷锟叫断伙拷取锟斤拷锟斤拷位锟斤拷锟斤拷锟斤拷锟窖★拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷俚锟斤拷锟斤拷锟?
        mMenuView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

}
