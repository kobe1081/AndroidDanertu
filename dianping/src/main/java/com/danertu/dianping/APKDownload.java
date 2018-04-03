package com.danertu.dianping;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bobo.utils.PatchUtils;
import com.danertu.download.DownloadNoti;
import com.danertu.download.DownloadService;
import com.danertu.download.FileUtil;
import com.danertu.tools.DemoApplication;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;

public class APKDownload extends Activity implements OnClickListener {

    static {
        /**
         *
         * 2017年11月6日
         *
         * 经测试添加try-catch后应用在64位cpu机子上仍闪退
         * 所以不能在jniLibs中添加64位so库文件夹
         *
         */
        try {
            System.loadLibrary("diff");
        } catch (Exception e) {
            Logger.e("APKDownload","64位so库");
            e.printStackTrace();
        }
    }

    public static final String k_isComplete = "complete";
    /**
     * 1表示首次进入会默认开始下载，2表示下载进行中，3表示下载完毕
     */
    public static final String k_state = "state";
    protected static final String TAG = "APKDownload";
    private Button button;
    private ProgressBar pb;
    private TextView tv_tips;
    private ImageView[] ivs = new ImageView[3];
    private AnimationSet[] aniSets = new AnimationSet[3];
    private int iv_ids[] = {R.id.iv_icon1, R.id.iv_icon2, R.id.iv_icon3};

    private String url = null;
    /**
     * 是否下载完整app安装包
     */
    public static boolean isComplete = true;
    /**
     * 1表示首次进入会默认开始下载，2表示下载进行中，3表示下载完毕
     */
    public static int state = 1;
    private final int ANIMATIONEACHOFFSET = 600;
    private int what = 0;
    DemoApplication dApplication = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download);
        dApplication = (DemoApplication) getApplication();
        String tComplete = getIntent().getStringExtra(k_isComplete);
        if (!TextUtils.isEmpty(tComplete)) {
            isComplete = Boolean.parseBoolean(tComplete);
        }
        if (isComplete)
            url = "http://www.danertu.com/download/danertu.apk";
        else
            url = "http://www.danertu.com/download/danertu-patch.apk";

        Logger.e("APKDownload", "isComplete:" + isComplete + ", state:" + state);
        button = (Button) this.findViewById(R.id.button);
        tv_tips = (TextView) findViewById(R.id.tv_tips);
        pb = (ProgressBar) this.findViewById(R.id.pb);
        button.setOnClickListener(this);

        handler = new MHandler();
        for (int i = 0; i < ivs.length; i++) {
            ivs[i] = (ImageView) findViewById(iv_ids[i]);
            aniSets[i] = getNewAnimationSet();
        }
        if (state == 1) {
            state = 2;
            Intent intent = new Intent(this, DownloadService.class);
            intent.putExtra("url", url);
            startService(intent);
        } else if (state == 3) {
            downEndInitWidget();
        }

        if (dApplication.getActivitySize() < 1) {
            InitFinishData();
        }
    }

    private void InitFinishData() {
        // TODO Auto-generated method stub
        SharedPreferences sharedPreferences = getSharedPreferences("myActivityName", MODE_PRIVATE);
        isFirstRun = sharedPreferences.getBoolean("isFirstIn", true);
        String keyVCode = "versionCode";
        int spVCode = sharedPreferences.getInt(keyVCode, 0);
        int vCode = CommonTools.getVersionCode(this);
        isFirstRun = (vCode > spVCode);
    }

    public void finish() {
        if (dApplication.getActivitySize() < 1)
            judgeFirstEnter();
        super.finish();
    }

    boolean isFirstRun = false;

    public void judgeFirstEnter() {
        // 启动homeactivity，相当于Intent
        if (isFirstRun == true) {
            startActivity(new Intent(this, AppShowActivity.class));
        } else {
            startActivity(new Intent(this, IndexActivity.class));
        }
    }

    public void onResume() {
        super.onResume();
        DownloadNoti.getInstance(getApplicationContext()).cancleNoti();
        if (state != 3) {
            showWaveAnimation();
        }
    }

    public void onPause() {
        super.onPause();
        cancalWaveAnimation();
        DownloadNoti noti = DownloadNoti.getInstance(getApplicationContext());
        noti.createNoti();
        if (state == 3) {
            noti.updateNoti(noti.getMax(), "100%");
        }
    }

    private AnimationSet getNewAnimationSet() {
        AnimationSet as = new AnimationSet(true);
        ScaleAnimation sa = new ScaleAnimation(1f, 2.3f, 1f, 2.3f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(ANIMATIONEACHOFFSET * 3);
        sa.setRepeatCount(-1);// 设置循环
        AlphaAnimation aniAlp = new AlphaAnimation(1, 0.1f);
        aniAlp.setRepeatCount(-1);// 设置循环
        as.setDuration(ANIMATIONEACHOFFSET * 3);
        as.addAnimation(sa);
        as.addAnimation(aniAlp);
        return as;
    }

    private void showWaveAnimation() {
        for (int i = 0; i < ivs.length; i++) {
            final int j = i;
            handler.postDelayed(new Runnable() {
                public void run() {
                    ivs[j].startAnimation(aniSets[j]);
                }
            }, ANIMATIONEACHOFFSET * j);
        }

    }

    private void cancalWaveAnimation() {
        for (ImageView iv : ivs) {
            iv.clearAnimation();
        }
    }

    int count = 0;

    @Override
    public void onClick(View v) {
        if (v == button) {
            if (state == 3 || what == FileUtil.endDownloadMeg) {
                /* apk安装界面跳转 */
                String filename = FileUtil.getFileName(url);
                String str = "/myfile/" + filename;
                String fileName = Environment.getExternalStorageDirectory() + str; // 下载的补丁
                install(isComplete, fileName);
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        handler = null;
    }

    private int max;
    public static Handler handler = null;

    public class MHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            what = msg.what;
            Bundle data = msg.getData();
            switch (what) {
                case FileUtil.startDownloadMeg:
                    max = data.getInt(DownloadService.start_fileSize);
                    pb.setMax(max); // 开始
                    button.setText("开始下载");
                    break;
                case FileUtil.updateDownloadMeg:
                    max = data.getInt(DownloadService.start_fileSize);
                    pb.setMax(max); // 开始
                    pb.setProgress(data.getInt(DownloadService.update_downSize));
                    button.setText("努力下载中");
                    tv_tips.setText("下载速度：" + data.getInt(DownloadService.update_downSpeed)
                            + "k/秒\n已下载："
                            + data.getInt(DownloadService.update_downPercent) + "%");
                    break;
                case FileUtil.endDownloadMeg:
                    downEndInitWidget();
                    Toast.makeText(APKDownload.this, "下载完成,马上安装", Toast.LENGTH_SHORT).show();

				/* apk安装界面跳转 */
                    String filename = FileUtil.getFileName(url);
                    String str = "/myfile/" + filename;
                    String fileName = Environment.getExternalStorageDirectory() + str; // 下载的补丁
                    install(isComplete, fileName);

                    break;
            }
            super.handleMessage(msg);
        }
    }

    ;

    private void downEndInitWidget() {
        cancalWaveAnimation();
        button.setText("点击安装");
        tv_tips.setText("下载完成");
        pb.setProgress(pb.getMax());
    }

    private void install(boolean isComplete, String fileName) {
        if (isComplete) {
            fileInstall(new File(fileName));
            return;
        }
        String oldapk = "";
        try {
            oldapk = APKDownload.this.getPackageManager().getApplicationInfo("com.danertu.dianping", 0).sourceDir;
            String complexapk = "mnt/sdcard/danertu.apk";// 合成的新apk
            /**
             * 将增量包与旧APK合并成新版本安装包
             */
            PatchUtils.patch(oldapk, complexapk, fileName);
            fileInstall(new File(complexapk));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fileInstall(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}