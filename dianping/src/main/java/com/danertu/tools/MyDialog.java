package com.danertu.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.danertu.dianping.R;

public class MyDialog extends Dialog implements OnClickListener {

    private View gameview;
    private Context context;

    public MyDialog(Context context, View gameview, String msg) {
        super(context, R.style.dialog);
        this.gameview = gameview;
        this.context = context;
        this.setContentView(R.layout.dialog_view);
        TextView text_msg = (TextView) findViewById(R.id.text_message);
        TextView btn_next = (TextView) findViewById(R.id.next_imgbtn);
        text_msg.setText(msg);
        btn_next.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        this.dismiss();
        switch (v.getId()) {
            case R.id.next_imgbtn:
                Dialog dialog = new AlertDialog.Builder(context).create();
                dialog.dismiss();

                break;


        }
    }

    public static Dialog getDefineDialog(Context context, String title, String content) {
        Dialog dialog = getDefineDialog(context, R.layout.dialog_layout_ask);
        TextView dialog_title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
        TextView dialog_content = (TextView) dialog.findViewById(R.id.tv_dialog_content);
        dialog_title.setText(title);
        dialog_content.setText(content);
        return dialog;
    }

    public static Dialog getDefineDialog(Context context, int layoutID) {
        return getDefineDialog(context, LayoutInflater.from(context).inflate(layoutID, null));
    }

    public static Dialog getDefineDialog(Context context, View v) {
        Dialog dialog = new Dialog(context, R.style.Dialog);
        dialog.setContentView(v);
        return dialog;
    }
}
