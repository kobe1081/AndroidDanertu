package com.danertu.dianping;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.config.Constants;
import com.danertu.tools.AppUtil;

public class BaseHandler extends Handler {
	WeakReference<BaseActivity> wAct = null;
	protected BaseActivity ui = null;
	
	public BaseHandler (BaseActivity ui) {
		wAct = new WeakReference<>(ui);
		this.ui = wAct.get();
	}
	
	public BaseHandler (Looper looper) {
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
					} else if (!AppUtil.isEmptyInt(taskId)) {
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
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ui.toast(e.getMessage());
		}
	}
	
}