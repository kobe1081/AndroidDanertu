package com.danertu.dianping;

import java.util.Set;

import wl.codelibrary.widget.IOSDialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class SchemeBase extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();
        try {
            String actName = null;
            Intent i = new Intent();
            String scheme = uri.getScheme();

            if (scheme.equals("danertu")) {
                actName = uri.getHost();
            } else {
                actName = "com.danertu.dianping." + uri.getQueryParameter("actName");
            }
            Set<String> names = uri.getQueryParameterNames();
            if (names != null && names.size() > 0) {
                Bundle b = new Bundle();
                for (String key : names) {
                    b.putString(key, uri.getQueryParameter(key));
                }
                i.putExtras(b);
            }

            if (isPermissionDenied(actName)) {
                showError("权限不足，您不能打开" + actName);
                return;
            }
            i.setClass(this, Class.forName(actName));
            startActivity(i);
        } catch (Exception e) {
            Log.e("Error", "param error " + e.toString());
            startActivity(new Intent(this, SplashActivity.class));
        }
        finish();
    }

    public boolean isPermissionDenied(String actName) {
        String name = PaymentCenterActivity.class.getName();
        return name.equals(actName);
    }

    public void showError(String msg) {
        final IOSDialog dialog = new IOSDialog(this);
        dialog.setMessage(msg);
        dialog.show();
        dialog.setSingleButton("确定", new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
    }
}
