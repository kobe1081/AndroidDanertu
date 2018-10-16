package com.danertu.dianping;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.tools.AppManager;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;
import com.danertu.widget.MyBaseAdapter;
import com.danertu.widget.MyGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 2017年7月31日
 * huangyeliang
 * adapter添加holder类
 */
public class ShopToComment extends BaseActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopcomment);
        initData();
        initTitle("评论", "发布");
        findViewById();
        handler = new Handler(callback);
        showLoadDialog();
        new Thread(tGetshopdetails).start();
    }

    private void initTitle(String title, String opera) {
        Button b_title = (Button) findViewById(R.id.b_title_back2);
        Button b_opera = (Button) findViewById(R.id.b_title_operation2);
        b_title.setText(title);
        b_opera.setText(opera);
        b_title.setOnClickListener(this);
        b_opera.setOnClickListener(this);
    }

    /**
     * UI
     */
    private ImageView iv_banner;
    private TextView tv_shopTitle;
    private TextView tv_shopDes;
    private RatingBar rb_star;
    private TextView tv_starTips;
    private EditText et_comment;
    private TextView tv_etWordCount;
    // private LinearLayout ll_picParent;
    private Button b_isAnonymity;
    private MyGridView gv_pics;
    private PhotoAdapter picAdapter = null;

    /**
     * tag
     */
    private boolean isSubmited = false;
    private boolean isAnonymity = false;
    public final String KEY_PHOTO = "photo";
    public final String KEY_SELECT = "isSelect";
    public final String KEY_PHOTO_NAME = "photoName";

    private DisplayMetrics metrics = null;
    private ArrayList<HashMap<String, Object>> pics = null;

    private void initData() {
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Resources res = getResources();
        starTips = new String[]{res.getString(R.string.shopComment_star1),
                res.getString(R.string.shopComment_star2),
                res.getString(R.string.shopComment_star3),
                res.getString(R.string.shopComment_star4),
                res.getString(R.string.shopComment_star5)};
        bm_cameraAdd = zoomBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.camera_add), 300, 300);
        Intent i = getIntent();
        shopid = i.getStringExtra(DetailActivity.KEY_SHOP_ID);
        shopJson = i.getStringExtra(ShopCommentActivity.KEY_SHOP_JSON);
        isSubmited = false;
        isAnonymity = false;
        pics = new ArrayList<>();
        picAdapter = new PhotoAdapter(getContext(), pics.size());
    }

    public Handler.Callback callback = new Handler.Callback() {

        public boolean handleMessage(Message msg) {
            hideLoadDialog();
//            if (msg.what == WHAT_SHOPDETAIL_SUCCESS) {
//                initView();
//            } else if (msg.what == WHAT_SUBMITCOMMENT_SUCCESS) {
//                CommonTools.showShortToast(getContext(), "评论提交成功");
//                setResult(ShopCommentActivity.REQ_COMMENT);
//                finish();
//
//            } else if (msg.what == WHAT_SUBMITCOMMENT_FAIL) {
//                CommonTools.showShortToast(getContext(), "提交失败,请重新提交");
//            }
            switch (msg.what) {
                case WHAT_SHOPDETAIL_SUCCESS:
                    initView();
                    break;
                case WHAT_SUBMITCOMMENT_SUCCESS:
                    CommonTools.showShortToast(getContext(), "评论提交成功");
                    setResult(ShopCommentActivity.REQ_COMMENT);
                    finish();
                    break;
                case WHAT_SUBMITCOMMENT_FAIL:
                    CommonTools.showShortToast(getContext(), "提交失败,请重新提交");
                    break;
            }
            return true;
        }
    };

    private String shopid = null;
    private String shopJson = null;
    private HashMap<String, String> shopData = null;
    private final int MAX_PHOTO_COUNT = 6;
    private Bitmap bm_cameraAdd = null;

    private class PhotoAdapter extends MyBaseAdapter {

        PhotoAdapter(Context context, int count) {
            super(context, count);
        }

        public int getCount() {
            if (pics.size() < MAX_PHOTO_COUNT)
                count = pics.size() + 1;
            else
                count = MAX_PHOTO_COUNT;
            return count;
        }

        @Override
        public View getView(int p, View v, ViewGroup parent) {
            ViewHolder holder;
            if (v == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.photo_item, null);
                holder = new ViewHolder(v);
                v.setTag(holder);
            } else {
                holder = ((ViewHolder) v.getTag());
            }
            holder.iv.destroyDrawingCache();
            holder.iv.setImageBitmap(bm_cameraAdd);
            holder.iv.setBackgroundColor(getResources().getColor(R.color.white));
            if (count - 1 < MAX_PHOTO_COUNT && p != count - 1) {
                setImage(p, holder.iv);
            } else if (pics.size() == MAX_PHOTO_COUNT && p == count - 1) {
                setImage(p, holder.iv);
            }
            return v;
        }

        private void setImage(int p, ImageView iv) {
            HashMap<String, Object> item = pics.get(p);
            boolean isSelected = (Boolean) item.get(KEY_SELECT);
            Bitmap photo = (Bitmap) item.get(KEY_PHOTO);
            iv.setImageBitmap(photo);
            iv.setBackgroundResource(R.drawable.selector_del);
            if (isSelected)
                iv.setSelected(true);
            else
                iv.setSelected(false);
        }

        class ViewHolder {
            ImageView iv;

            public ViewHolder(View view) {
                iv = (ImageView) view.findViewById(R.id.iv_photo_item);
            }
        }
    }

    /**
     * { "选择本地图片","拍照上传" }
     */
    private String[] items = new String[]{"选择本地图片", "拍照上传"};

    /**
     * 显示选择对话框
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("商品图片");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                switch (which) {
                    case 0:
                        intent = new Intent(getContext(), PhotoActivity.class);
                        intent.putExtra(PhotoActivity.KEY_SUMCOUNT, MAX_PHOTO_COUNT - pics.size());
                        startActivityForResult(intent, REQ_SELECT_PHOTOS);
                        break;
                    case 1:// 拍照
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                        startActivityForResult(intent, IMAGE_REQUEST_TAKEPHOTO);
                        break;
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri uri
     */
    public void startPhotoZoom(Uri uri) {
        Log.i("图片路径", uri.toString());
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param data data
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Bitmap pic300_300 = zoomBitmap(photo, 300, 300);
            HashMap<String, Object> item = new HashMap<>();
            item.put(KEY_PHOTO, pic300_300);
            item.put(KEY_SELECT, false);
            pics.add(item);
            picAdapter.notifyDataSetChanged();
        }
    }

    private File tempFile = null;

    private void initPicFile() {
        File file_path;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
            file_path = Environment.getRootDirectory();
        } else {
            file_path = Environment.getExternalStorageDirectory();
        }
        tempFile = new File(file_path, getPhotoFileName());
    }

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".png";
    }

    final int WHAT_SUBMITCOMMENT_SUCCESS = 3;
    final int WHAT_SUBMITCOMMENT_FAIL = -3;

    private class RSubmit implements Runnable {
        String uid, score, comment;

        RSubmit(String uid, String score, String comment) {
            this.uid = uid;
            this.score = score;
            this.comment = comment;
        }

        public void run() {
            StringBuilder picNameList = new StringBuilder();
            int count = 1;
            for (HashMap<String, Object> item : pics) {
                String picName = getFormatCurrentTime() + "_" + count + ".jpg";
                Bitmap upBitmap = (Bitmap) item.get(KEY_PHOTO);
                uploadFile(picName, upBitmap);
                picNameList.append(picName).append(",");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
            }
            if (submitComment(uid, score, comment, picNameList.toString())) {
                Log.i("提交评论成功",
                        uid + " , " + score + " , " + picNameList.toString()
                                + " , " + comment);
                handler.sendEmptyMessage(WHAT_SUBMITCOMMENT_SUCCESS);
            } else {
                isSubmited = false;
                Log.i("提交评论失败", "");
                handler.sendEmptyMessage(WHAT_SUBMITCOMMENT_FAIL);
            }
        }
    }


    /**
     * 请求码
     */
    private static final int IMAGE_REQUEST_TAKEPHOTO = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    public static final int REQ_SELECT_PHOTOS = 3;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {

            switch (requestCode) {
                case IMAGE_REQUEST_TAKEPHOTO:
                    try {
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } catch (Exception e) {
                        String err = e.toString();
                        CommonTools.showShortToast(getContext(), "载入图片失败:" + err);
                        Log.e("err_line603", err);
                    }
                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;
                case REQ_SELECT_PHOTOS:
                    ArrayList<String> paths = data
                            .getStringArrayListExtra(PhotoActivity.KEY_PATH);
                    for (String path : paths) {
                        try {
                            Bitmap pic300_300 = getScalePic(path, 300);
                            HashMap<String, Object> item = new HashMap<>();
                            item.put(KEY_PHOTO, pic300_300);
                            item.put(KEY_SELECT, false);
                            pics.add(item);
                        } catch (OutOfMemoryError e) {
                            Log.e("err_line624", e.toString());
                        }
                    }
                    picAdapter.notifyDataSetChanged();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getFormatCurrentTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String picName = dateFormat.format(date);
        Log.i("picName", picName + "");
        return picName;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_title_back2:
                finish();
                break;

            case R.id.b_title_operation2:
                if (!isLogined()) {
                    CommonTools.showShortToast(getContext(), "请先登录!");
                    openActivity(LoginActivity.class);
                } else if (!isSubmited) {
                    String uid = db.GetLoginUid(getContext());
                    int star = (int) rb_star.getRating();
                    star = star <= 0 ? 1 : star;
                    String score = String.valueOf(star);
                    String comment = et_comment.getText().toString();
                    if (comment.length() < 15) {
                        CommonTools.showShortToast(getContext(), "字数过少。");
                    } else if (comment.length() > 300) {
                        CommonTools.showShortToast(getContext(), "字数超出范围。");
                    } else {
                        showLoadDialog();
                        new Thread(new RSubmit(uid, score, comment)).start();
                        isSubmited = true;
                    }
                }
                break;

            case R.id.b_shopComment_isAnonymity:
                if (!isAnonymity) {
                    b_isAnonymity.setSelected(true);
                    isAnonymity = true;
                } else {
                    b_isAnonymity.setSelected(false);
                    isAnonymity = false;
                }
                break;

            default:
                break;
        }
    }

    final static int WHAT_SHOPDETAIL_SUCCESS = 11;
    /**
     * 获取店铺详细信息
     */
    private Runnable tGetshopdetails = new Runnable() {
        @Override
        public void run() {
            // 耗时操作
            String result;
            try {
                result = TextUtils.isEmpty(shopJson) ? AppManager.getInstance().postGetShopDetails("0041", shopid) : shopJson;
                shopData = ActivityUtils.getInstance().analyzeShopJson(result);
                String imgName = shopData.get(ActivityUtils.SHOP_BANNER);
                Bitmap bitmap = null;
                try {
                    URL url = new URL(getImgUrl(imgName, shopid, null));
                    bitmap = BitmapFactory.decodeStream(url.openStream());
                } catch (FileNotFoundException e) {
                    shopData.put(ActivityUtils.SHOP_BANNER, shopData.get(ActivityUtils.SHOP_ENTITYIMAGE));
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                } finally {
                    if (bitmap != null && !bitmap.isRecycled())
                        bitmap.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Log.e("店铺详细", result);
            handler.sendEmptyMessage(WHAT_SHOPDETAIL_SUCCESS);
        }

    };

    @Override
    protected void findViewById() {
        iv_banner = (ImageView) findViewById(R.id.iv_shopComment);
        tv_shopTitle = (TextView) findViewById(R.id.tv_shopComment_shopName);
        tv_shopDes = (TextView) findViewById(R.id.tv_shopComment_shopDes);
        rb_star = (RatingBar) findViewById(R.id.rb_shopComment_star);
        tv_starTips = (TextView) findViewById(R.id.tv_shopComment_starMsg);
        et_comment = (EditText) findViewById(R.id.et_shopComment);
        tv_etWordCount = (TextView) findViewById(R.id.tv_shopComment_etWordCount);
        b_isAnonymity = (Button) findViewById(R.id.b_shopComment_isAnonymity);
        gv_pics = (MyGridView) findViewById(R.id.gv_shopComment_pics);

        b_isAnonymity.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        initPicFile();
        et_comment.addTextChangedListener(watcher);
        rb_star.setOnRatingBarChangeListener(orbcl);
        gv_pics.setAdapter(picAdapter);
        gv_pics.setOnItemClickListener(oicl);

        String imgName = shopData.get(ActivityUtils.SHOP_BANNER);
        imgName = imgName.replaceAll(" ", "");
        imgName = imgName.equals("") ? shopData.get(ActivityUtils.SHOP_ENTITYIMAGE) : imgName;
        String picURL = getImgUrl(imgName, shopid, null);
        ImageLoader.getInstance().displayImage(picURL, iv_banner);
        tv_shopTitle.setText(shopData.get(ActivityUtils.SHOP_NAME));
        tv_shopDes.setText(shopData.get(ActivityUtils.SHOP_DETAILS));
        rb_star.setRating(5);
    }

    public OnItemClickListener oicl = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            int count = position + 1;
            if (count == gv_pics.getCount() && pics.size() != MAX_PHOTO_COUNT) {
                showDialog();
            } else {
                HashMap<String, Object> item = pics.get(position);
                boolean isSelected = (Boolean) item.get(KEY_SELECT);
                if (!isSelected) {
                    item.put(KEY_SELECT, true);
                } else {
                    pics.remove(position);
                }
                picAdapter.notifyDataSetChanged();
            }
        }
    };

    public boolean submitComment(String uid, String score, String comment, String imgStr) {
        // shopid = "15019909394";// 带我走店铺
        // uid = "15113347438";
        // score = "5";
        // comment = "非常好好好";
        String nim = isAnonymity ? "1" : "0";
        String result = "";
        try {
            result = appManager.postShopToComment(shopid, uid, score, comment, imgStr, nim);
            return Boolean.parseBoolean(result);
        } catch (Exception e) {
            judgeIsTokenException(result, "您的登录信息已过期，请重新登录", -1);
            if (Constants.isDebug) {
                e.printStackTrace();
            }
            Logger.e("ShopCommentActivity_err", e.toString());
        }
        return false;
    }

    String starTips[] = null;
    public OnRatingBarChangeListener orbcl = new OnRatingBarChangeListener() {
        public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
            int index = (int) arg1 - 1;
            if (index >= 0 && index < starTips.length)
                tv_starTips.setText(starTips[index]);
            else if (index < 0) {
                arg0.setRating(1);
            }
        }
    };

    public TextWatcher watcher = new TextWatcher() {
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            tv_etWordCount.setText(String.valueOf(et_comment.length()));
        }

        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        public void afterTextChanged(Editable arg0) {
        }
    };

}
