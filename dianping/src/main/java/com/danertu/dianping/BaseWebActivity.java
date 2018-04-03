package com.danertu.dianping;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.db.DBHelper;
import com.danertu.download.FileUtil;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.Base64;
import com.danertu.tools.FWorkUtil;
import com.danertu.tools.Logger;
import com.danertu.widget.MWebChromeClient;
import com.danertu.widget.MWebViewClient;
import com.google.gson.Gson;

/**
 * 2017年7月27日
 * huangyeliang
 * 将选择图片的弹窗方式更改为popupWindow，方法：showPopupWindow()，原方法为showSelectDialog()
 */
public class BaseWebActivity extends BaseActivity implements OnClickListener {
    private static final int MAX_PROGRESS = 100;

    private final int IMAGE_REQUEST_CODE = 123;
    private final int IMAGE_REQUEST_CROP_CODE = 124;
    private static final int IMAGE_REQUEST_TAKEPHOTO = 125;

    protected int mProgress = 0;
    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initWebView();
    }

    /**
     * @since versionCode 60
     */
    public void onResume() {
        super.onResume();
        webView.loadUrl(Constants.IFACE + "onResume()");
    }

    /**
     * @since versionCode 60
     */
    public void onPause() {
        super.onPause();
        webView.loadUrl(Constants.IFACE + "onPause()");
    }

    /**
     * 设置标题，和注册了标题控件的点击事件监听
     *
     * @param title     标题 不能为空
     * @param operation 为空时表示隐藏操作按钮,否则显示
     * @return 按钮数组，下标为0 的是“标题按钮”，1为“操作按钮”
     */
    public Button[] setTitle(String title, String operation) {

        Button[] vs = new Button[2];
        if (title == null) {
            return null;
        }
        Button back = (Button) findViewById(R.id.b_title_back2);
        back.setText(title);
        Button opera = (Button) findViewById(R.id.b_title_operation2);
        if (operation == null || operation.equals("")) {
            opera.setVisibility(View.GONE);
        } else {
            opera.setVisibility(View.VISIBLE);
            opera.setText(operation);
            opera.setOnClickListener(this);
        }
        back.setOnClickListener(this);
        vs[0] = back;
        vs[1] = opera;
        return vs;
    }

    protected void findViewById() {
    }

    protected void initView() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == IMAGE_REQUEST_CODE) {
                startPhotoZoom(data.getData());

            } else if (requestCode == IMAGE_REQUEST_TAKEPHOTO) {
                startPhotoZoom(Uri.fromFile(tempFile));

            } else if (requestCode == IMAGE_REQUEST_CROP_CODE) {
                new GenCropImg().execute(data.getData());

            } else {
                String result = "";
                if (data != null)
                    result = bundleToJson(data.getExtras());
                webView.loadUrl(Constants.IFACE + "onActivityResult(" + requestCode + "," + resultCode + ",'" + result + "')");
            }
        }
    }

    /**
     * 解析系统返回的图片图片
     */
    private class GenCropImg extends AsyncTask<Uri, Void, String> {
        protected String doInBackground(Uri... param) {
            String path = param[0] == null ? pathName : param[0].getPath();
            Bitmap photo = BitmapFactory.decodeFile(path);
            File f = new File(path);
            if (f.exists()) {
                boolean delete = f.delete();
            }
            if (photo == null) {
                return "";
            }
            upBitmapHashMap.put(upBitmapName, photo);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //在这里设置图片质量，0-100
            photo.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] byteArray = stream.toByteArray();
            return Base64.encode(byteArray);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            webView.loadUrl(Constants.IFACE + "callBack('data:image/jpg;base64," + result + "')");//传到页面的经过base64加密的图片流数据
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.stopLoading();
            Logger.i(TAG, "停止加载数据");
        }
    }

    private boolean isAutoHide = true;

    protected void setAutoHideDialog(boolean isAutoHide) {
        this.isAutoHide = isAutoHide;
    }

    protected boolean isAutoHideDialog() {
        return isAutoHide;
    }

    private void initWebView() {
        this.webView = (WebView) findViewById(R.id.wv_container);
        this.webView.setWebChromeClient(new MWebChromeClient(this) {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                mProgress = progress;
                if (mProgress >= MAX_PROGRESS && isAutoHideDialog()) {
                    hideLoadDialog();
                }

            }

        });

        webView.setWebViewClient(new MWebViewClient(this, "app"));
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        v.requestFocus(View.FOCUS_DOWN);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                }
                return false;
            }
        });
        initWebSettings();
    }

    /**
     * WebView属性设置
     */
    @SuppressLint("SetJavaScriptEnabled")
    protected void initWebSettings() {
        WebSettings mWebSettings;
        mWebSettings = webView.getSettings();
        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setTextZoom(100);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        //不允许缩放
//        mWebSettings.setSupportZoom(false);
//        mWebSettings.setBuiltInZoomControls(false);
//        //不显示缩放按钮
        mWebSettings.setDisplayZoomControls(false);
    }

    public WebView getWebView() {
        return this.webView;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    public void startWebView(String url) {
        showLoadDialog();
        Logger.e(TAG," startWebView url="+url);
        webView.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (canBack && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @JavascriptInterface
    public void jsExecSQL(String sql) {
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.execSQL(sql);
    }

    @JavascriptInterface
    public String jsQuerySQL(String table, String orderBy, String limit) {
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (orderBy.equals(""))
            orderBy = null;
        if (limit.equals(""))
            limit = null;
        Cursor cursor = db.query(table, null, null, null, null, null, orderBy, limit);
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            HashMap<String, String> item = new HashMap<>();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String name = cursor.getColumnName(i);
                String value = cursor.getString(cursor.getColumnIndex(name));
                item.put(name, value);
            }
            list.add(item);
        }
        if (gson == null)
            gson = new Gson();
        return gson.toJson(list);
    }

    /**
     * 非通用方法
     *
     * @param picName  当直接使用不带参数的 openImg() 方法时需要传正确的值。
     * @param shopType 标识普通店铺跟供应商店铺图片
     */
    @JavascriptInterface
    public boolean uploadImg(String picName, String shopType) {
        if (upBitmapHashMap == null || upBitmapHashMap.isEmpty()) {
            return false;
        }
        Set<String> sets = upBitmapHashMap.keySet();
        String agentid = getUid();
        for (String item : sets) {
            Bitmap upBitmap = upBitmapHashMap.get(item);
            if (item.equals("head")) {
                HashMap<String, String> param = new HashMap<>();
                param.put("agentid", agentid);
                param.put("shopType", shopType);
                item = picName;
                uploadFile(item, upBitmap, param);
            } else {
                uploadFile(item, upBitmap, "apiid|0172,;agentid|" + agentid);
            }
        }
        upBitmapHashMap.clear();
        return true;
    }

    /**
     * 非通用方法
     *
     * @param shopType 标识普通店铺跟供应商店铺图片
     * @param quality  图片质量0-100，供图片压缩时使用，100质量最好，一般默认90就好。
     * @since versionCode 66
     */
    @JavascriptInterface
    public boolean uploadImgTo246(String shopType, int quality) {//单纯传到246图片服务器上
        if (upBitmapHashMap == null || upBitmapHashMap.isEmpty()) {
            return false;
        }
        Set<String> sets = upBitmapHashMap.keySet();
        String agentid = getUid();
        for (String item : sets) {
            Bitmap upBitmap = upBitmapHashMap.get(item);
            HashMap<String, String> param = new HashMap<>();
            param.put("agentid", agentid);
            param.put("shopType", shopType);
            uploadFile(item, upBitmap, param, quality);
        }
        upBitmapHashMap.clear();
        return true;
    }

    @JavascriptInterface
    public void setCanBack(boolean canBack) {
        this.canBack = canBack;
    }

    private boolean canBack = true;

    /**
     * versionCode:74
     */
    private boolean isBackListen = false;

    @JavascriptInterface
    public void setBackListener(boolean isBackListen) {
        this.isBackListen = isBackListen;
    }

    @JavascriptInterface
    public void goBack() {
        if (webView != null) webView.goBack();
    }

    @Override
    public void onBackPressed() {
        if (!canBack)
            return;
        else if (isBackListen && webView != null) {
            webView.loadUrl(Constants.IFACE + "javaBackListener()");
            return;
        }
        super.onBackPressed();
    }

    /**
     * @since versionCode 54
     */
    @JavascriptInterface
    public void openImg() {
        openImg("head", 1, 1, 300, 300);
    }

    private AlertDialog.Builder builder;
    private File tempFile = null;
    private String upBitmapName;
    private HashMap<String, Bitmap> upBitmapHashMap;

    /**
     * @param upBitmapName 图片名
     * @param aspectX      宽的比例
     * @param aspectY      高的比例
     * @param outputX      图片像素值X
     * @param outputY      图片像素值Y
     * @since versionCode 60
     */
    @JavascriptInterface
    public void openImg(String upBitmapName, int aspectX, int aspectY, int outputX, int outputY) {
        this.upBitmapName = upBitmapName;
        this.aspectX = aspectX;
        this.aspectY = aspectY;
        this.outputX = outputX;
        this.outputY = outputY;
        if (upBitmapHashMap == null) {
            upBitmapHashMap = new HashMap<>();
        }
//        showSelectDialog();
        showPopupWindow();
    }

    /**
     * 选择图片popupWindow
     */
    private void showPopupWindow() {
        //设置contentView
        View contentView = LayoutInflater.from(this).inflate(R.layout.popup_base_web_select_photo, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(contentView);
        TextView tvSelectCamera = (TextView) contentView.findViewById(R.id.tv_select_camera);
        TextView tvSelectGallery = (TextView) contentView.findViewById(R.id.tv_select_gallery);
        TextView tvSelectCancel = (TextView) contentView.findViewById(R.id.tv_select_cancel);
//        TextView tvSelectShow = (TextView) contentView.findViewById(R.id.tv_select_show);
        View view = contentView.findViewById(R.id.view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        // 在相册中选取
        tvSelectGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*"); // 设置文件类型
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
                popupWindow.dismiss();
            }
        });
        //调用相机
        tvSelectCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempFile = initPicFile();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(intent, IMAGE_REQUEST_TAKEPHOTO);
                popupWindow.dismiss();
            }
        });

        tvSelectCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        //显示PopupWindow
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.AnimationBottomFade);
        //得到当前activity的rootView
        View rootView = ((ViewGroup) BaseWebActivity.this.findViewById(android.R.id.content)).getChildAt(0);
        //底部弹出
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 选择图片dialog
     */
    private void showSelectDialog() {
        if (builder == null) {
            builder = new AlertDialog.Builder(getContext());
            String[] items = new String[]{"拍照", "本地图片"};
            builder.setTitle("选择图片方式");
            builder.setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent;
                    switch (which) {
                        case 0:
                            tempFile = initPicFile();
                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                            startActivityForResult(intent, IMAGE_REQUEST_TAKEPHOTO);
                            break;
                        case 1:
                            intent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*"); // 设置文件类型
                            startActivityForResult(intent, IMAGE_REQUEST_CODE);
                            break;
                    }
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create();
        }
        builder.show();
    }

    @SuppressLint("SimpleDateFormat")
    private File initPicFile() {
        String path = FileUtil.setMkdir(this);
        path += "/photos";
        File file_path = new File(path);
        boolean mkdir = file_path.mkdir();

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        String picName = dateFormat.format(date) + ".jpg";
        return new File(file_path, picName);
    }

    private int aspectX = 1;
    private int aspectY = 1;
    private int outputX = 300;
    private int outputY = 300;
    private FWorkUtil fwutil;
    //备用的变量
    private String pathName = null;

    /**
     * 裁剪图片方法实现
     *
     * @param uri 图片Uri
     */
    @JavascriptInterface
    public void startPhotoZoom(Uri uri) {
        fwutil = fwutil == null ? new FWorkUtil(this) : fwutil;
        pathName = fwutil.startPhotoZoom(uri, aspectX, aspectY, outputX, outputY, IMAGE_REQUEST_CROP_CODE, true);
    }

    public void onClick(View v) {
    }

    /**
     * 收藏商品
     * @param guid
     * @param proName
     * @param agentId
     * @param price
     * @param imgName
     * @param supId
     * @param detail
     */
    @JavascriptInterface
    public void jsCollectPro(String guid, String proName, String agentId, String price, String imgName, String supId, String detail) {
        if (!isLogined()) {
            jsShowMsg("请先登录！");
            openActivity(LoginActivity.class);
            return;
        }
        try {
            Cursor cursor = db.allreadyCollectProduct(getContext(), guid, getUid());
            double tPrice = Double.parseDouble(price);
            if (tPrice == 0.1 || tPrice == 1 || guid.equals(Constants.guid01)) {
                jsShowMsg("此商品不允许收藏！");
            } else if (cursor.getCount() > 0) {
                jsShowMsg("您已经收藏过该商品了！");
            } else {
                db.InsertCollectProduct(getContext(), guid, proName, agentId, price,
                        imgName, getUid(), supId, detail);
                jsShowMsg("商品收藏成功！");
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @JavascriptInterface
    public String jsGetCollectPro() {
        return jsGetCollectPro(null);
    }

    /**
     * 获取用户收藏的商品
     * @param uid 用户uid
     * @return
     */
    @JavascriptInterface
    public String jsGetCollectPro(String uid) {
        uid = TextUtils.isEmpty(uid) ? getUid() : uid;
        StringBuilder sb =new StringBuilder();
        String json="";
        if (TextUtils.isEmpty(uid)) {
            return json;
        }
        try {
            Cursor cursor = db.GetCollectProductGuid(getContext(), getUid());
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//                json += cursor.getString(0) + ",";
                sb.append(cursor.getString(0)).append(",");
            }
            json=sb.toString();
            json = json.length() > 0 ? json.substring(0, json.length() - 1) : "";

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return json;
    }

    /**
     * 取消商品收藏
     * @param guid 商品id
     * @return
     */
    @JavascriptInterface
    public boolean jsDeleteCollectPro(String guid) {
        return db.deleteCollectProItem(getContext(), guid);
    }

    /**
     * 收藏商品
     * @param guid 商品id
     * @return
     */
    @JavascriptInterface
    public boolean isCollectedPro(String guid) {
        return db.isCollectedPro(getContext(), getUid(), guid);
    }


}