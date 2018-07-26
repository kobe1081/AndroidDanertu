package com.danertu.dianping;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.tools.AppManager;
import com.danertu.tools.AudioRecorder;
import com.danertu.tools.Logger;
import com.danertu.tools.MyDialog;
import com.danertu.tools.RecodingAsyncTask;
import com.danertu.tools.RecordThread;
import com.joker.annotation.PermissionsCustomRationale;
import com.joker.annotation.PermissionsDenied;
import com.joker.annotation.PermissionsGranted;
import com.joker.annotation.PermissionsNonRationale;
import com.joker.annotation.PermissionsRationale;
import com.joker.annotation.PermissionsRequestSync;
import com.joker.api.Permissions4M;

import wl.codelibrary.widget.IOSDialog;

/**
 * 2017年9月12日
 * 添加运行时获取录音以及读取手机状态权限
 */
@PermissionsRequestSync(
        permission = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },
        value = {
                Constants.READ_PHONE_CODE,
                Constants.WRITE_STORAGE_CODE,
                Constants.RECORD_CODE

        }
)
public class HuDongActivity extends Activity implements OnGestureListener {

    RecordThread rThread = null;
    MediaPlayer mp;
    public static boolean imgisrun = true;
    public ImageView imageView1;
    private ViewFlipper flipper;
    public int imagenum = 1;
    private MyDialog dialog;
    private int score = 0;

    private View mainlayout;
    // private TextView txtClick;

    private Button record;

    private Thread recordThread;

    private double maxvoiceValue = 0.0; // 记录最大音量值

    private String showInfo = "";
    TextView tvHelp;
    private Handler imgHandle;
    private IOSDialog iosDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgHandle = new Handler(imgHandlerCallBack);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_hu_dong);
        //同步获取多种权限
        Permissions4M.get(this).requestSync();
        flipper = (ViewFlipper) this.findViewById(R.id.ViewFlipper01);
        imageView1 = new ImageView(this);
        imageView1.setBackgroundResource(R.drawable.t_01);
        flipper.addView(imageView1);
        mainlayout = (View) findViewById(R.id.main_layout);
        // txtClick = (TextView) findViewById(R.id.txtClick);
        initTitle();
        record = (Button) this.findViewById(R.id.record);
        // 录音
        record.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (RECODE_STATE != RECORD_ING) {
                            scanOldFile();
                            mr = new AudioRecorder("voice");
                            RECODE_STATE = RECORD_ING;
                            showVoiceDialog();
                            try {
                                mr.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            recordThread = new Thread(ImgThread);
                            recordThread.start();
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        if (RECODE_STATE == RECORD_ING) {
                            RECODE_STATE = RECODE_ED;
                            if (ydialog.isShowing()) {
                                ydialog.dismiss();
                            }
                            try {
                                mr.stop();
                                voiceValue = 0.0;
                                if (maxvoiceValue > 31000) {
                                    RecodingAsyncTask asyncTask = new RecodingAsyncTask(imageView1, getPics(2), rThread);
                                    asyncTask.execute(12);
                                    score = 100;

                                } else if (maxvoiceValue > 15000
                                        && maxvoiceValue <= 31000) {
                                    RecodingAsyncTask asyncTask = new RecodingAsyncTask(imageView1, getPics(1), rThread);
                                    asyncTask.execute(12);
                                    score = 50;

                                } else {
                                    score = 50;
                                }

                                Message msg = new Message();
                                msg.what = 2;
                                imgHandle.sendMessage(msg);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (recodeTime < MIX_TIME) {

                            } else {

                            }
                        }

                        break;
                }
                return false;
            }
        });
    }


    @PermissionsGranted({Constants.READ_PHONE_CODE, Constants.WRITE_STORAGE_CODE, Constants.RECORD_CODE})
    public void granted(int code) {
        switch (code) {
            case Constants.READ_PHONE_CODE:
                Logger.e("Permission", "获取读取手机状态权限成功");
                break;
            case Constants.WRITE_STORAGE_CODE:
                Logger.e("Permission", "获取外存写入权限成功");
                break;
            case Constants.RECORD_CODE:
                Logger.e("Permission", "获取录音权限成功");
                break;

        }
    }

    @PermissionsDenied({Constants.READ_PHONE_CODE, Constants.WRITE_STORAGE_CODE, Constants.RECORD_CODE,})
    public void denied(int code) {
        switch (code) {
            case Constants.READ_PHONE_CODE:
                Logger.e("Permission", "获取读取手机状态权限失败");
                break;
            case Constants.WRITE_STORAGE_CODE:
                Logger.e("Permission", "获取外存写入权限回调失败");
                break;
            case Constants.RECORD_CODE:
                Logger.e("Permission", "获取录音权限回调失败");
                break;

        }
    }

    @PermissionsRationale({Constants.READ_PHONE_CODE, Constants.WRITE_STORAGE_CODE, Constants.RECORD_CODE})
    public void rationale(int code) {
        switch (code) {
            case Constants.READ_PHONE_CODE:
                Toast.makeText(HuDongActivity.this, "请给予应用读取手机状态权限", Toast.LENGTH_SHORT).show();
                break;
            case Constants.WRITE_STORAGE_CODE:
                Toast.makeText(HuDongActivity.this, "请给予应用读取/写入媒体文件状态权限", Toast.LENGTH_SHORT).show();
                break;
            case Constants.RECORD_CODE:
                Toast.makeText(HuDongActivity.this, "请给予应用录音权限", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @PermissionsCustomRationale({Constants.READ_PHONE_CODE, Constants.WRITE_STORAGE_CODE, Constants.RECORD_CODE})
    public void cusRationale(int code) {
        switch (code) {
            case Constants.READ_PHONE_CODE:
//                new AlertDialog.Builder(HuDongActivity.this)
//                        .setMessage("请给予应用获取手机状态权限，否则您无法进行活动")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Permissions4M.get(HuDongActivity.this)
//                                        .requestOnRationale()
//                                        .requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                        .requestCode(Constants.READ_PHONE_CODE)
//                                        .request();
//                            }
//                        })
//                        .onCreate()
//                        .show();
                showiOSDialog("", "请给予应用获取手机状态权限，否则您无法进行活动",
                        "确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Permissions4M.get(HuDongActivity.this)
                                        .requestOnRationale()
                                        .requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .requestCode(Constants.READ_PHONE_CODE)
                                        .request();
                                iosDialog.dismiss();
                            }
                        });
                break;

            case Constants.WRITE_STORAGE_CODE:
//                new AlertDialog.Builder(HuDongActivity.this)
//                        .setMessage("请给予应用读取/写入多媒体权限，否则您无法进行活动")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Permissions4M.get(HuDongActivity.this)
//                                        .requestOnRationale()
//                                        .requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                        .requestCode(Constants.WRITE_STORAGE_CODE)
//                                        .request();
//                            }
//                        })
//                        .onCreate()
//                        .show();
                showiOSDialog("", "请给予应用读取/写入多媒体权限，否则您无法进行活动",
                        "确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Permissions4M.get(HuDongActivity.this)
                                        .requestOnRationale()
                                        .requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .requestCode(Constants.WRITE_STORAGE_CODE)
                                        .request();
                                iosDialog.dismiss();
                            }
                        });
                break;
            case Constants.RECORD_CODE:
//                new AlertDialog.Builder(HuDongActivity.this)
//                        .setMessage("请给予应用录音权限，否则您无法进行活动")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Permissions4M.get(HuDongActivity.this)
//                                        .requestOnRationale()
//                                        .requestPermission(Manifest.permission.RECORD_AUDIO)
//                                        .requestCode(Constants.RECORD_CODE)
//                                        .request();
//                            }
//                        })
//                        .onCreate()
//                        .show();
                showiOSDialog("", "请给予应用录音权限，否则您无法进行活动",
                        "确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Permissions4M.get(HuDongActivity.this)
                                        .requestOnRationale()
                                        .requestPermission(Manifest.permission.RECORD_AUDIO)
                                        .requestCode(Constants.RECORD_CODE)
                                        .request();
                                iosDialog.dismiss();
                            }
                        });
                break;
        }
    }

    /**
     * 用户拒绝授权时提示
     *
     * @param code
     * @param intent
     */
    @PermissionsNonRationale({Constants.READ_PHONE_CODE, Constants.WRITE_STORAGE_CODE, Constants.RECORD_CODE})
    public void nonRationale(int code, final Intent intent) {
        showiOSDialog("", "请给予应用读取手机状态、录音以及文件写入权限，否则您将无法进行活动",
                "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /**
                         * 应使用startActivityForResult，否则在进入设置授权后返回app仍提示未授权
                         */
                        startActivityForResult(intent,1);
                        iosDialog.dismiss();
                    }
                });
//        switch (code) {
//            case Constants.READ_PHONE_CODE:
//                new AlertDialog.Builder(HuDongActivity.this)
//                        .setMessage("请给予应用获取手机状态权限，否则您无法进行活动")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                startActivity(intent);
//                            }
//                        })
//                        .onCreate()
//                        .show();
//                break;
//
//            case Constants.WRITE_STORAGE_CODE:
//                new AlertDialog.Builder(HuDongActivity.this)
//                        .setMessage("请给予应用读取/写入多媒体权限，否则您无法进行活动")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                startActivity(intent);
//                            }
//                        })
//                        .onCreate()
//                        .show();
//                break;
//            case Constants.RECORD_CODE:
//                new AlertDialog.Builder(HuDongActivity.this)
//                        .setMessage("请给予应用录音权限，否则您无法进行活动")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                startActivity(intent);
//                            }
//                        })
//                        .onCreate()
//                        .show();
//                break;
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Permissions4M.onRequestPermissionsResult(HuDongActivity.this, requestCode, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showiOSDialog(String title, String message, String btnText, View.OnClickListener btnListener) {
        if (iosDialog == null) {
            iosDialog = new IOSDialog(HuDongActivity.this);
        }
        iosDialog.setTitle(title);
        iosDialog.setMessage(message);
        iosDialog.setSingleButton(btnText,btnListener);
        iosDialog.setCanceledOnTouchOutside(true);
        iosDialog.show();

    }

    /**
     * 设置标题栏
     */
    private void initTitle() {
        Button b_back = (Button) findViewById(R.id.b_title_back2);
        Button b_operation = (Button) findViewById(R.id.b_title_operation2);
        b_back.setText("吼一吼");
        b_operation.setText("游戏规则");
        b_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
        b_operation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                showAlertDialog();
            }
        });
    }

    @Override
    public boolean onDown(MotionEvent arg0) {

        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        if (e1.getX() - e2.getX() > 120) {
            imagenum++;
            if (imagenum > 6) {
                imagenum = 1;
            }
            Logger.e("imagenum", "imagenum：-----------------------> " + imagenum);
            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
            this.flipper.showNext();
            return true;
        } else if (e1.getX() - e2.getX() < -120) {
            imagenum--;
            if (imagenum < 1) {
                imagenum = 6;
            }
            Logger.e("imagenum", "imagenum：-----------------------> " + imagenum);
            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
            this.flipper.showPrevious();
            return true;
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent arg0) {

    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
                            float arg3) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {

    }

    private Dialog ydialog;
    private ImageView dialog_img;

    // 录音时显示Dialog
    void showVoiceDialog() {
        ydialog = new Dialog(HuDongActivity.this, R.style.DialogStyle);
        ydialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ydialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ydialog.setContentView(R.layout.my_dialog);
        dialog_img = (ImageView) ydialog.findViewById(R.id.dialog_img);
        ydialog.show();
    }

    private static int MAX_TIME = 15; // 最长录制时间，单位秒，0为无时间限制
    private static int MIX_TIME = 1; // 最短录制时间，单位秒，0为无时间限制，建议设为1

    private static int RECORD_ING = 1; // 正在录音
    private static int RECODE_ED = 2; // 完成录音

    private static int RECODE_STATE = 0; // 录音的状态

    private static float recodeTime = 0.0f; // 录音的时间
    private AudioRecorder mr;
    // 录音线程
    private Runnable ImgThread = new Runnable() {
        private boolean isLoading = false;

        @Override
        public void run() {
            if (isLoading)
                return;
            isLoading = true;
            recodeTime = 0.0f;
            while (RECODE_STATE == RECORD_ING) {
                if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
                    imgHandle.sendEmptyMessage(0);
                } else {
                    try {
                        Thread.sleep(200);
                        recodeTime += 0.2;
                        if (RECODE_STATE == RECORD_ING) {
                            voiceValue = mr.getAmplitude();
                            if (voiceValue > maxvoiceValue) {
                                maxvoiceValue = voiceValue;
                            }
                            imgHandle.sendEmptyMessage(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            isLoading = false;
        }
    };

    public Handler.Callback imgHandlerCallBack = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    // 录音超过15秒自动停止
                    if (RECODE_STATE == RECORD_ING) {
                        RECODE_STATE = RECODE_ED;
                        if (ydialog.isShowing()) {
                            ydialog.dismiss();
                        }
                        mr.stop();
                        voiceValue = 0.0;

                    }
                    break;
                case 1:
                    setDialogImage();
                    break;
                case 2:
                    Thread thread = new Thread(runnable);
                    thread.start();
                    break;
                case 3:
                    if (!isFinishing()) {
                        dialog = new MyDialog(HuDongActivity.this, mainlayout, showInfo);
                        dialog.show();
                    }
                    break;
            }
            return true;
        }
    };

    // 删除老文件
    void scanOldFile() {
        File file = new File(Environment.getExternalStorageDirectory(), "my/voice.amr");
        if (file.exists()) {
            boolean delete = file.delete();
        }
    }

    private static double voiceValue = 0.0; // 麦克风获取的音量值

    // 录音Dialog图片随声音大小切换
    void setDialogImage() {
        if (voiceValue < 200.0) {
            dialog_img.setImageResource(R.drawable.record_animate_01);
        } else if (voiceValue > 200.0 && voiceValue < 400) {
            dialog_img.setImageResource(R.drawable.record_animate_02);
        } else if (voiceValue > 400.0 && voiceValue < 800) {
            dialog_img.setImageResource(R.drawable.record_animate_03);
        } else if (voiceValue > 800.0 && voiceValue < 1600) {
            dialog_img.setImageResource(R.drawable.record_animate_04);
        } else if (voiceValue > 1600.0 && voiceValue < 3200) {
            dialog_img.setImageResource(R.drawable.record_animate_05);
        } else if (voiceValue > 3200.0 && voiceValue < 5000) {
            dialog_img.setImageResource(R.drawable.record_animate_06);
        } else if (voiceValue > 5000.0 && voiceValue < 7000) {
            dialog_img.setImageResource(R.drawable.record_animate_07);
        } else if (voiceValue > 7000.0 && voiceValue < 10000.0) {
            dialog_img.setImageResource(R.drawable.record_animate_08);
        } else if (voiceValue > 10000.0 && voiceValue < 14000.0) {
            dialog_img.setImageResource(R.drawable.record_animate_09);
        } else if (voiceValue > 14000.0 && voiceValue < 17000.0) {
            dialog_img.setImageResource(R.drawable.record_animate_10);
        } else if (voiceValue > 17000.0 && voiceValue < 20000.0) {
            dialog_img.setImageResource(R.drawable.record_animate_11);
        } else if (voiceValue > 20000.0 && voiceValue < 24000.0) {
            dialog_img.setImageResource(R.drawable.record_animate_12);
        } else if (voiceValue > 24000.0 && voiceValue < 28000.0) {
            dialog_img.setImageResource(R.drawable.record_animate_13);
        } else if (voiceValue > 28000.0) {
            dialog_img.setImageResource(R.drawable.record_animate_14);
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        return false;
    }

    @Override
    protected void onDestroy() {
        if (mp != null) {
            mp.stop();
        }
        if (imgHandle != null) {
            imgHandle.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    public void playM(int rs) {
        mp = new MediaPlayer();
        mp.reset();
        try {
            mp = MediaPlayer.create(this, rs);
            if (mp != null) {
                mp.stop();
            }
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("SimpleDateFormat")
    Runnable runnable = new Runnable() {
        boolean isLoading = false;

        @Override
        public void run() {
            if (isLoading)
                return;
            isLoading = true;
            DBManager db = DBManager.getInstance();
            String versionNo = getPhoneID();
            String uid = db.GetLoginUid(HuDongActivity.this);
            String forUid = "";
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                forUid = bundle.getString("foruid");
            }
            Date date = new Date();
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            String playdate = f.format(date);
            String result = AppManager.getInstance().postCheckPlayData("0047", versionNo, playdate);
            if (result.equals("false")) {
                String istrue = AppManager.getInstance().postInsertHudongScore("0046", versionNo, String.valueOf(score), uid, forUid);
                if (istrue.equals("true")) {
                    db.SethudongScore(HuDongActivity.this, versionNo, score, forUid, uid);
                    showInfo = "你获得" + score + "积分！";
                }
            } else {
                showInfo = "今天已无法继续获得积分，请明天再来！";
            }
            imgHandle.sendEmptyMessage(3);
            isLoading = false;
        }
    };

    public int[] getPics(int yinliang) {
        int pic[] = null;
        if (yinliang == 1) {
            pic = a1;
            // playM(R.raw.voice_suzumori_park_1);
        }
        if (yinliang == 2) {
            pic = a2;
            // playM(R.raw.voice_suzumori_park_2);
        }
        if (yinliang == 3) {
            pic = a1;
            // playM(R.raw.voice_suzumori_park_3);
        }
        return pic;
    }

    public int a1[] = {R.drawable.t_01, R.drawable.t_02, R.drawable.t_03,
            R.drawable.t_04, R.drawable.t_05, R.drawable.t_06
    };
    public int a2[] = {R.drawable.t_01, R.drawable.t_02, R.drawable.t_03,
            R.drawable.t_04, R.drawable.t_05, R.drawable.t_06, R.drawable.t_07,
            R.drawable.t_08, R.drawable.t_09, R.drawable.t_10, R.drawable.t_11
    };

    // 获取手机设备号
    // liujun 7-29
    private String getPhoneID() {

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public void showAlertDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        //	builder.setMessage("这个就是自定义的提示框");
        builder.setTitle("游戏规则");
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //设置你的操作事项
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();

    }
    // public int
    // a3[]={R.drawable.suzumori_park_00111,R.drawable.suzumori_park_00113,R.drawable.suzumori_park_00115,
    // R.drawable.suzumori_park_00117,R.drawable.suzumori_park_00119,R.drawable.suzumori_park_00121,
    // R.drawable.suzumori_park_00123,R.drawable.suzumori_park_00125,R.drawable.suzumori_park_00127,
    // R.drawable.suzumori_park_00129,R.drawable.suzumori_park_00131,R.drawable.suzumori_park_00133,
    // R.drawable.suzumori_park_00135,R.drawable.suzumori_park_00137,R.drawable.suzumori_park_00139,
    // R.drawable.suzumori_park_00141,R.drawable.suzumori_park_00143,R.drawable.suzumori_park_00145,
    // R.drawable.suzumori_park_00147,R.drawable.suzumori_park_00149,R.drawable.suzumori_park_00151,
    // R.drawable.suzumori_park_00153,R.drawable.suzumori_park_00155,R.drawable.suzumori_park_00157,
    // R.drawable.suzumori_park_00159,R.drawable.suzumori_park_00161,R.drawable.suzumori_park_00163,
    // R.drawable.suzumori_park_00165,R.drawable.suzumori_park_00167,R.drawable.suzumori_park_00169,R.drawable.suzumori_park};
}
