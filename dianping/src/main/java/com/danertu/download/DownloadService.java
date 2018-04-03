package com.danertu.download;

import java.io.File;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.danertu.dianping.APKDownload;
import com.danertu.tools.Logger;

public class DownloadService extends IntentService {
    public static final String start_fileSize = "start_fileSize";
    public static final String update_downSize = "update_downSize";
    public static final String update_downSpeed = "update_downSpeed";
    public static final String update_downPercent = "update_downPercent";
    public static final String end = "end";

    private updateUIThread mUpdateUIThread;
    private int max;
    private Handler handler = new Handler() {
        ;

        public void handleMessage(Message msg) {
            Message m = Message.obtain();
            m.what = msg.what;
            Bundle data = new Bundle();

            DownloadNoti noti = DownloadNoti.getInstance(getApplicationContext());
            switch (msg.what) {
                case FileUtil.startDownloadMeg:
                    max = mUpdateUIThread.getFileSize();
                    noti.setMax(max);
                    noti.updateNoti(0, "0%");
                    data.putInt(start_fileSize, max);
                    break;

                case FileUtil.updateDownloadMeg:
                    APKDownload.state = msg.what;
                    noti.updateNoti(mUpdateUIThread.getDownloadSize(), mUpdateUIThread.getDownloadPercent() + "%");

                    data.putInt(start_fileSize, max);
                    data.putInt(update_downPercent, mUpdateUIThread.getDownloadPercent());
                    data.putInt(update_downSize, mUpdateUIThread.getDownloadSize());
                    data.putInt(update_downSpeed, mUpdateUIThread.getDownloadSpeed());

                    break;

                case FileUtil.endDownloadMeg:
                    APKDownload.state = msg.what;
                    noti.updateNoti(max, "100%");
                    data.putBoolean(end, true);
                    if (APKDownload.handler == null) {
                        Intent i = new Intent(getApplicationContext(), APKDownload.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                    break;
            }
            if (APKDownload.handler != null) {
                m.setData(data);
                APKDownload.handler.sendMessage(m);
            }
            super.handleMessage(msg);
        }
    };

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        String url = intent.getStringExtra("url");
        Logger.e("DownloadService", url);
        String filename = FileUtil.getFileName(url);
        String str = "/myfile/" + filename;
        String fileName = Environment.getExternalStorageDirectory() + str;
        File file = new File(fileName);
        FileUtil.deleteFiles(file);

        mUpdateUIThread = new updateUIThread(handler, url, FileUtil.setMkdir(this) + File.separator, FileUtil.getFileName(url));
        mUpdateUIThread.start();
    }

}
