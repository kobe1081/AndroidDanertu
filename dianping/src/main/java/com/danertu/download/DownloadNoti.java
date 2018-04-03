package com.danertu.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.danertu.dianping.APKDownload;
import com.danertu.dianping.R;

public class DownloadNoti {
	private static DownloadNoti noti;
	Context context;

	private DownloadNoti(Context context){
		this.context = context;
	}

	public static DownloadNoti getInstance(Context context){
		if(noti == null)
			noti = new DownloadNoti(context);
		return noti;
	}

	private final int id = 111;
	private NotificationManager manager;
	private Notification notification;
	private int max;

	private boolean isCancled = false;
	public void createNoti(){
		isCancled = false;
		manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		RemoteViews rvs = new RemoteViews(context.getPackageName(), R.layout.download_notifi);
		notification = new Notification();
		initNotiContentIntent();
		notification.contentView = rvs;
		notification.flags = Notification.FLAG_NO_CLEAR;
		notification.icon = R.drawable.icon;
		notification.tickerText = context.getString(R.string.title_apkUpload);
		notification.when = System.currentTimeMillis();
		manager.notify(id, notification);
	}

	public void cancleNoti(){
		if(manager != null){
			manager.cancel(id);
			isCancled = true;
		}
	}

	private void initNotiContentIntent(){
		if(notification != null){
			notification.contentIntent = getPIntent();
		}
	}

	public void updateNoti(int progress, String tips){
		if(manager == null || notification == null || isCancled)
			return;
		notification.contentView.setTextViewText(R.id.tv_down_tips, "已下载："+tips);
		notification.contentView.setProgressBar(R.id.pb_down_tips, max, progress, false);
		manager.notify(id, notification);
	}

	private Intent intent;
	private PendingIntent getPIntent(){
		if(intent == null){
			intent = new Intent(context, APKDownload.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		PendingIntent contentIntent = PendingIntent.getActivity(context, 222, intent, PendingIntent.FLAG_ONE_SHOT);
		return contentIntent;
	}

	public int getId() {
		return id;
	}

	public void setMax(int maxProgress){
		this.max = maxProgress;
	}

	public int getMax() {
		return max;
	}
}
