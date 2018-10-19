package com.danertu.dianping;

import java.lang.ref.WeakReference;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.config.Constants;

import static com.danertu.dianping.BaseActivity.WHAT_TO_LOGIN;

public class BaseHandler extends Handler {
    WeakReference<BaseActivity> wAct = null;
    protected BaseActivity ui = null;

    public BaseHandler(BaseActivity ui) {
        wAct = new WeakReference<>(ui);
        this.ui = wAct.get();
    }

    public BaseHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        try {
            int taskId;
            String result;
            switch (msg.what) {
                case BaseTask.TASK_COMPLETE:
                    ui.hideLoadDialog();
                    taskId = msg.getData().getInt("task");
                    result = msg.getData().getString("data");
                    if (result != null) {
                        ui.onTaskComplete(taskId, result);
                    } else if (!(new Integer(taskId) == null)) {
                        ui.onTaskComplete(taskId);
                    } else {
                        ui.toast(Constants.err.MESSAGE);
                    }
                    break;
                case BaseTask.NETWORK_ERROR:
                    ui.hideLoadDialog();
                    taskId = msg.getData().getInt("task");
                    ui.onNetworkError(taskId);
                    break;
                case BaseTask.SHOW_LOADBAR:
                    ui.showLoadDialog();
                    break;
                case BaseTask.HIDE_LOADBAR:
                    ui.hideLoadDialog();
                    break;
                case BaseTask.SHOW_TOAST:
                    ui.hideLoadDialog();
                    result = msg.getData().getString("data");
                    ui.toast(result);
                    break;
                case WHAT_TO_LOGIN:
                    removeMessages(WHAT_TO_LOGIN);
                    if (ui != null) {
                        if (msg.obj != null) {
                            ui.jsShowMsg(msg.obj.toString());
                        }
                        ui.quitAccount();
                        Intent intent = new Intent(ui, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        if (msg.arg1 == -1) {
                            ui.startActivity(intent);
                            ui.finish();
                        } else {
                            ui.startActivityForResult(intent, msg.arg1);
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ui.toast(e.getMessage());
        }
    }

}