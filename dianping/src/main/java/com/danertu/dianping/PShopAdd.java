package com.danertu.dianping;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import wl.codelibrary.widget.CircleImageView;
import wl.codelibrary.widget.IOSDialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.danertu.download.FileUtil;
import com.danertu.entity.TokenExceptionBean;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.FWorkUtil;
import com.danertu.tools.GoodsEditTWatcher;
import com.danertu.tools.LoadingDialog;
import com.danertu.widget.CommonTools;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 2017年7月26日
 * huangyeliang
 * Base类添加ViewHolder
 */
public class PShopAdd extends BaseActivity implements OnClickListener {
    private Button b_title, b_opera;
    private CircleImageView ib_add_pic;
    int picWidth, picHeight;
    EditText et_name, et_price, et_marketPrice, et_stock;
    private LoadingDialog dialog;
    private File tempFile = null;
    private FWorkUtil fwUtil = null;

    /**
     * 请求码
     */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int IMAGE_REQUEST_TAKEPHOTO = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    public static final int REQ_SELECT_PHOTOS = 3;
    private final String KEY_ISCHECKED = "cb_isCheck";

    //变量---------------------
    private ArrayList<HashMap<String, Object>> pics = null;
    public final String KEY_PHOTO = "photo";
    private int shopType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_p_shop_add);
        if (!isLogined()) {
            CommonTools.showShortToast(this, "请先登录！");
            finish();
            return;
        }
        newHandler = new Handler(hCallback);
        fwUtil = new FWorkUtil(getContext());
        dialog = new LoadingDialog(getContext());
        pics = new ArrayList<>();
        init();
    }

    /**
     * 是否为编辑商品
     */
    boolean isEdit = false;

    public void init() {
        String result = initIntent();
        if (TextUtils.isEmpty(result)) {
            isEdit = true;
        }
        initView();
    }

    private Button b_editGood;
    private Button b_deleteGood;
    private IOSDialog aDialog;

    private final int GOOD_PUTDOWN = 1;
    private final int GOOD_PUTUP = 0;
    /**
     * 1为可下架商品，0为可上架商品
     */
    private int type;
    private String productName;
    private String proImg;
    private double shopPrice;
    private double marketPrice;
    private int repertoryCount;
    private String sPrice, sMarketPrice;
    private String guid;
    private String detail;
    private String detailPics;

    private String initIntent() {
        try {
            Intent i = getIntent();
            String tempType = i.getStringExtra("shopType");
            if (!TextUtils.isEmpty(tempType))
                shopType = Integer.parseInt(tempType);
            sPrice = i.getStringExtra("shopPrice");
            sMarketPrice = i.getStringExtra("marketPrice");
            shopPrice = Double.parseDouble(sPrice);
            marketPrice = Double.parseDouble(sMarketPrice);
            repertoryCount = Integer.parseInt(i.getStringExtra("repertoryCount"));
            type = Integer.parseInt(i.getStringExtra("type"));
            productName = i.getStringExtra("productName");
            proImg = i.getStringExtra("proImg");
            guid = i.getStringExtra("guid");
            detail = i.getStringExtra("detail");
            detailPics = i.getStringExtra("detailPics");
            String result = null;
            if (TextUtils.isEmpty(guid)) {
                result = "guid can't null";
            } else if (TextUtils.isEmpty(productName)) {
                result = "productName can't null";
            } else if (TextUtils.isEmpty(proImg)) {
                result = "proImg can't null";
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    public String getFormatCurrentTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String picName = dateFormat.format(date);
        Log.i("picName", picName + "");
        return picName;
    }

    final int WHAT_SUBMIT_SUCCESS = 3;
    final int WHAT_SUBMIT_FAIL = -3;
    private TextView tv_title;

    private class RSubmit implements Runnable {
        private String name;
        private double shopPrice;
        private double marketPrice;
        private int repertoryCount;
        private String uid;
        private int type;

        public RSubmit(String uid, String name, double shopPrice, double marketPrice,
                       int repertoryCount, int type) {
            this.uid = uid;
            this.name = name;
            this.shopPrice = shopPrice;
            this.marketPrice = marketPrice;
            this.repertoryCount = repertoryCount;
            this.type = type;
        }

        private String guid;
        private String imgName;

        public RSubmit(String guid, String name, double shopPrice, double marketPrice,
                       int repertoryCount) {
            this.guid = guid;
            this.name = name;
            this.shopPrice = shopPrice;
            this.marketPrice = marketPrice;
            this.repertoryCount = repertoryCount;
            imgName = proImg.substring(proImg.lastIndexOf("/") + 1, proImg.length());
        }

        public void submitEdit() throws Exception {
            if (!isEdit)
                return;
            boolean isChangedBanner = false;
            Bitmap upBitmap = null;
            String picName = null;
            if (pics.size() <= 0) {
                picName = imgName;
                isChangedBanner = false;
            } else {
                for (HashMap<String, Object> item : pics) {
                    picName = getFormatCurrentTime() + "_" + (count++) + ".jpg";
                    upBitmap = (Bitmap) item.get(KEY_PHOTO);
                    isChangedBanner = true;
                }
            }
            String[] names = null;
            int proPhotosSize = picDatas.size();
            String proPhotos = "";
            if (proPhotosSize > 0) {
                names = new String[proPhotosSize];
                for (int i = 0; i < proPhotosSize; i++) {
                    if (picDatas.get(i).startsWith("http")) {
                        names[i] = FileUtil.getFileName(picDatas.get(i));
                        if (i == 0) {
                            proPhotos = names[i];
                        } else {
                            proPhotos += "," + names[i];
                        }
                    } else {
                        names[i] = getFormatCurrentTime() + "_" + (count++) + ".jpg";
                        if (i == 0) {
                            proPhotos = names[i];
                        } else {
                            proPhotos += "," + names[i];
                        }
                    }
                }
            }
            final String result = appManager.postEditGoods(guid, name, picName, shopPrice, marketPrice, repertoryCount, proDetail, proPhotos, uid);
            if (judgeIsTokenException(result)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TokenExceptionBean tokenExceptionBean = com.alibaba.fastjson.JSONObject.parseObject(result, TokenExceptionBean.class);
                            jsShowMsg(tokenExceptionBean.getInfo());
                            quitAccount();
                            finish();
                            jsStartActivity("LoginActivity", "");
                        } catch (Exception e) {
                            newHandler.sendMessage(getMessage(WHAT_SUBMIT_FAIL, "编辑商品失败"));
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                JSONObject obj = new JSONObject(result);
                if (Boolean.parseBoolean(obj.getString("result"))) {
                    if (isChangedBanner && upBitmap != null) {
                        upload(picName, upBitmap);
                    }
                    if (proPhotosSize > 0) {
                        for (int i = 0; i < proPhotosSize; i++) {
                            String itemName = picDatas.get(i);
                            if (!itemName.startsWith("http")) {
                                Bitmap proItem = BitmapFactory.decodeFile(itemName);
                                upload(names[i], proItem);
                                deleteCropImg(itemName);

                            }
                        }
                    }
                    newHandler.sendMessage(getMessage(WHAT_SUBMIT_SUCCESS, "编辑商品成功"));
                } else {
                    newHandler.sendMessage(getMessage(WHAT_SUBMIT_FAIL, "编辑商品失败"));
                }
            }
        }

        public void editGood() {
            if (!isEdit)
                return;
            try {
                submitEdit();
            } catch (Exception e) {
                newHandler.sendMessage(getMessage(WHAT_SUBMIT_FAIL, e.toString()));
                e.printStackTrace();
            }
        }

        public void addGood() {
            if (isEdit)
                return;
            try {
                String result = "";
                for (HashMap<String, Object> item : pics) {
                    String picName = getFormatCurrentTime() + "_" + (count++) + ".jpg";
                    String[] names = null;
                    if (proDesImgs != null) {
                        int len = proDesImgs.length;
                        names = new String[len];
                        for (int i = 0; i < len; i++) {
                            String name = getFormatCurrentTime() + "_" + (count++) + ".jpg";
                            names[i] = name;
                            proDetail = proDetail.replace(proDesImgs[i], getImgUrl(name, uid, ""));
                        }
                        proDetail = gson.toJson(proDetail);
                    }
                    int proPhotosSize = picDatas.size();
                    StringBuilder sb = new StringBuilder();
                    if (proPhotosSize > 0) {
                        names = new String[proPhotosSize];
                        names[0] = getFormatCurrentTime() + "_" + (count++) + ".jpg";
                        sb.append(names[0]);
//						proPhotos = names[0];
                        for (int i = 1; i < proPhotosSize; i++) {
                            names[i] = getFormatCurrentTime() + "_" + (count++) + ".jpg";
                            sb.append(",").append(names[i]);
//							proPhotos += "," + names[i];
                        }
                    }
                    String proPhotos = sb.toString();
                    result = appManager.postAddGoods(uid, name, picName, shopPrice, marketPrice, repertoryCount, type, proDetail, proPhotos, uid);
                    if (judgeIsTokenException(result)) {
                        final String finalResult = result;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    TokenExceptionBean tokenExceptionBean = com.alibaba.fastjson.JSONObject.parseObject(finalResult, TokenExceptionBean.class);
                                    jsShowMsg(tokenExceptionBean.getInfo());
                                    quitAccount();
                                    finish();
                                    jsStartActivity("LoginActivity", "");
                                } catch (Exception e) {
                                    newHandler.sendMessage(getMessage(WHAT_SUBMIT_FAIL, "添加商品失败，请重新添加"));
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        JSONObject obj = new JSONObject(result);
                        if (Boolean.parseBoolean(obj.getString("result"))) {
                            Bitmap upBitmap = (Bitmap) item.get(KEY_PHOTO);
                            upload(picName, upBitmap);
                            Thread.sleep(100);
                            if (proDesImgs != null) {
                                for (int i = 0; i < proDesImgs.length; i++) {
                                    String itemName = proDesImgs[i].substring(7);//去掉"file://"前缀
                                    Bitmap proItem = BitmapFactory.decodeFile(itemName);
                                    upload(names[i], proItem);
                                    deleteCropImg(itemName);
                                    Thread.sleep(100);
                                }
                            }
                            if (proPhotosSize > 0) {
                                for (int i = 0; i < proPhotosSize; i++) {
                                    String itemName = picDatas.get(i);
                                    Bitmap proItem = BitmapFactory.decodeFile(itemName);
                                    upload(names[i], proItem);
                                    deleteCropImg(itemName);
                                }
                            }
                            newHandler.sendMessage(getMessage(WHAT_SUBMIT_SUCCESS, "添加商品成功"));
                        } else {
                            newHandler.sendMessage(getMessage(WHAT_SUBMIT_FAIL, "添加商品失败，请重新添加"));
                        }
                        count++;
                    }
                }
            } catch (Exception e) {
                newHandler.sendMessage(getMessage(WHAT_SUBMIT_FAIL, e.toString()));
                e.printStackTrace();
            }
        }

        int count = 1;

        public void run() {
            editGood();
            addGood();
        }
    }

    private boolean deleteCropImg(String path) {
        File f = new File(path);
        return f.exists() && f.delete();
    }

    public void upload(String picName, Bitmap bitmap) {
        HashMap<String, String> param = new HashMap<>();
        param.put("agentid", getUid());
        param.put("shopType", String.valueOf(shopType));
        uploadFile(picName, bitmap, param);
    }

    private Handler.Callback hCallback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (aDialog != null && aDialog.isShowing()) {
                aDialog.dismiss();
            }
            dialog.dismiss();
            if (msg.what == WHAT_SUBMIT_SUCCESS) {
                jsShowMsg(msg.obj.toString());
                setResult(3);
                finish();
            } else if (msg.what == WHAT_SUBMIT_FAIL) {
                jsShowMsg(msg.obj.toString());
            }
            return true;
        }
    };

    public Handler newHandler = null;
    private CheckBox cb_putaway;

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    private EditText et_proDes;
    private GridView gv_introduction;
    private LinearLayout ll_edit;

    @Override
    protected void findViewById() {
        tv_title = (TextView) findViewById(R.id.tv_title4);
        b_title = (Button) findViewById(R.id.b_title_back4);
        b_opera = (Button) findViewById(R.id.b_title_operation4);
        b_opera.setVisibility(View.VISIBLE);
        ib_add_pic = (CircleImageView) findViewById(R.id.ib_pShopAdd_pic);
        et_name = (EditText) findViewById(R.id.et_pShopAdd_proName);
        et_price = (EditText) findViewById(R.id.et_pShopAdd_proPrice);
        et_marketPrice = (EditText) findViewById(R.id.et_pShopAdd_proMarketPrice);
        et_stock = (EditText) findViewById(R.id.et_pShopAdd_proNum);
        cb_putaway = (CheckBox) findViewById(R.id.cb_putaway);
        picWidth = ib_add_pic.getWidth();
        picHeight = ib_add_pic.getHeight();
        Log.i("图片宽高", picWidth + " , " + picHeight);
        et_proDes = (EditText) findViewById(R.id.et_pshop_pro_detail);
        gv_introduction = (GridView) findViewById(R.id.gv_pshop_selected_photo);

        ll_edit = (LinearLayout) findViewById(R.id.ll_pShop_edit);
        b_editGood = (Button) ll_edit.findViewById(R.id.b_pShop_editGood);
        b_deleteGood = (Button) findViewById(R.id.b_pShop_deleteGood);
    }

    ArrayList<String> picDatas;
    final String PIC_BITMAP = "bitmap";
    final String PIC_PATH = "bitmapPath";
    private Base adapter = null;

    @Override
    protected void initView() {
        findViewById();
        String title = null;
        if (isEdit) {
            title = "编辑商品";
        } else {
            title = "添加商品";
        }
        tv_title.setText(title);
        b_opera.setText("完成");

        ib_add_pic.setBorderWidth(CommonTools.dip2px(getContext(), 2));
        ib_add_pic.setBorderColor(Color.parseColor("#dddddd"));
        ib_add_pic.setForgroundResource(R.drawable.add_img_down);
        b_title.setOnClickListener(this);
        b_opera.setOnClickListener(this);
        ib_add_pic.setOnClickListener(this);
        et_name.addTextChangedListener(new GoodsEditTWatcher(et_name));
        CommonTools.addMoneyLimit(et_price);
        CommonTools.addMoneyLimit(et_marketPrice);
        sp = getSharedPreferences("pshop.danertu", MODE_PRIVATE);
        cb_putaway.setChecked(sp.getBoolean(KEY_ISCHECKED, true));
        picDatas = new ArrayList<>();
        adapter = new Base();
        gv_introduction.setAdapter(adapter);
        gv_introduction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (position == adapter.getCount() - 2) {
                    showDialog(4 - picDatas.size(), REQ_SELECT_DESCPTION_PIC, REQ_TAKE_DESCPTION_PIC);
                } else if (position == adapter.getCount() - 1) {
                    adapter.setCanDelete(!adapter.isCanDelete());
                    adapter.notifyDataSetChanged();
                } else {
                    if (adapter.isCanDelete()) {
                        picDatas.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        initEditView();
    }

    private void initEditView() {
        if (isEdit) {
            et_proDes.setText(detail);
            if (!TextUtils.isEmpty(detailPics.trim())) {
                String[] dps = detailPics.split(",");
                for (String dp : dps) {
                    picDatas.add(getImgUrl(dp, getUid(), null));
                }
                adapter.notifyDataSetChanged();

            }
            findViewById(R.id.line_shadow).setVisibility(View.VISIBLE);
            ll_edit.setVisibility(View.VISIBLE);
            b_editGood.setOnClickListener(this);
            b_deleteGood.setOnClickListener(this);
            findViewById(R.id.fl_pro_state).setVisibility(View.GONE);
            findViewById(R.id.fl_pro_state_line).setVisibility(View.GONE);
            et_name.setText(productName);
            et_marketPrice.setText(sMarketPrice);
            et_price.setText(sPrice);
            et_stock.setText(String.valueOf(repertoryCount));
            if (type == GOOD_PUTDOWN) {
                b_deleteGood.setText("商品下架");
                b_editGood.setVisibility(View.GONE);
            } else if (type == GOOD_PUTUP) {
                b_editGood.setText("商品上架");
            }
            ImageLoader.getInstance().displayImage(proImg, ib_add_pic);
        }

    }

    private void editGood(final String guid, boolean isDelete) {
        if (!isEdit || guid == null)
            return;
        int editType = 0;
        String content = "";
        if (isDelete) {//删除商品
            editType = 2;
            content = "确定要删除此商品？";
        } else if (type == GOOD_PUTDOWN) {//下架
            editType = 1;
            content = "确定要下架此商品？";
        } else if (type == GOOD_PUTUP) {//上架
            editType = 3;
            content = "确定要上架此商品？";
        }
        if (editType == 1 || editType == 2 || editType == 3) {
            final int eType = editType;
            if (aDialog == null) aDialog = new IOSDialog(this);
            aDialog.setMessage(content);
            aDialog.setPositiveButton("取消", new View.OnClickListener() {
                public void onClick(View v) {
                    aDialog.dismiss();
                }
            });
            aDialog.setNegativeButton("确定", new View.OnClickListener() {
                public void onClick(View v) {
                    String param[] = {guid, String.valueOf(eType)};
                    new TEditGood().execute(param);
                }
            });
            aDialog.show();
        } else {
            jsShowMsg("参数出错");
        }
    }

    public class TEditGood extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... param) {
            String result = "";
            try {
                String guid = param[0];
                String type = param[1];

                result = appManager.postEditGoods(guid, getUid(), type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (judgeIsTokenException(result)) {
                try {
                    TokenExceptionBean tokenExceptionBean = com.alibaba.fastjson.JSONObject.parseObject(result, TokenExceptionBean.class);
                    jsShowMsg(tokenExceptionBean.getInfo());
                    quitAccount();
                    finish();
                    jsStartActivity("LoginActivity", "");
                } catch (Exception e) {
                    aDialog.dismiss();
                    setResult(3);
                    finish();
                    e.printStackTrace();
                }
            } else {
                aDialog.dismiss();
                setResult(3);
                finish();
            }
        }
    }

    final int REQ_SELECT_DESCPTION_PIC = 30;
    final int REQ_TAKE_DESCPTION_PIC = 31;
    final int REQ_CROP_DESCPTION_PIC = 32;

    private class Base extends BaseAdapter {
        private boolean isCanDelete = false;

        public boolean isCanDelete() {
            return isCanDelete;
        }

        public void setCanDelete(boolean isCanDelete) {
            this.isCanDelete = isCanDelete;
        }

        @SuppressWarnings("deprecation")
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.photo_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = ((ViewHolder) convertView.getTag());
            }
            viewHolder.fl.setBackgroundDrawable(null);

            viewHolder.iv.setBackgroundDrawable(null);
            viewHolder.iv_tag.setBackgroundDrawable(null);
            if (position >= getCount() - 2) {
                if (position == getCount() - 2) {
                    viewHolder.iv.setImageResource(R.drawable.icon_add);
                } else if (position == getCount() - 1) {
                    viewHolder.iv.setImageResource(R.drawable.icon_reduce);
                }
                viewHolder.iv.setBackgroundResource(R.drawable.b_color_selector);
            } else {
                String imgPath = getItem(position);
                if (!imgPath.startsWith("http")) {
                    imgPath = "file://" + imgPath;
                }
                ImageLoader.getInstance().displayImage(imgPath, viewHolder.iv);
                viewHolder.iv_tag.setBackgroundResource(R.drawable.photo_selector_cancle);
                viewHolder.iv_tag.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT));
                if (isCanDelete) {
                    viewHolder.iv_tag.setSelected(true);
                } else {
                    viewHolder.iv_tag.setSelected(false);
                }
            }
            return convertView;
        }

        class ViewHolder {
            FrameLayout fl;
            ImageView iv;
            ImageView iv_tag;

            public ViewHolder(View view) {
                fl = (FrameLayout) view.findViewById(R.id.fl_photo_item);
                iv = (ImageView) view.findViewById(R.id.iv_photo_item);
                iv_tag = (ImageView) view.findViewById(R.id.iv_photo_item_tag);
            }
        }


        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public String getItem(int position) {
            return picDatas.get(position);
        }

        @Override
        public int getCount() {
            return picDatas.size() + 2;
        }
    }

    final int picCount = 1;
    /**
     * { "选择本地图片","拍照上传" }
     */
    private String[] items = new String[]{"选择本地图片", "拍照上传"};
    private SharedPreferences sp;
    private String proDetail = "";
    private String[] proDesImgs;

    /**
     * 显示选择对话框
     */
    private void showDialog() {
        showDialog(1, REQ_SELECT_PHOTOS, IMAGE_REQUEST_TAKEPHOTO);
    }

    /**
     * 选择图片窗口
     *
     * @param count        共能选取多少张图片
     * @param reqSelectPic 选择图片的请求码
     * @param reqTakePhoto 照相的请求码
     */
    public void showDialog(final int count, final int reqSelectPic, final int reqTakePhoto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("商品图片");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                if (count < 1) {
                    CommonTools.showShortToast(getContext(), "图片数量已达上限");
                    return;
                }
                switch (which) {
                    case 0:
                        intent = new Intent(getContext(), PhotoActivity.class);
                        intent.putExtra(PhotoActivity.KEY_SUMCOUNT, count);
                        startActivityForResult(intent, reqSelectPic);
                        break;
                    case 1:// 拍照
                        tempFile = initPicFile();
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                        startActivityForResult(intent, reqTakePhoto);
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
     * 保存裁剪之后的图片数据
     *
     * @param data
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            ib_add_pic.setImageBitmap(photo);
            HashMap<String, Object> ps = new HashMap<>();
            ps.put(KEY_PHOTO, photo);
            pics.add(ps);
        }
    }

    public void startBannerCrop(Uri uri) {
        if (uri != null && fwUtil != null) {
            fwUtil.startPhotoZoom(uri, 150, 150, RESULT_REQUEST_CODE, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {
            ArrayList<String> paths = null;
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    startBannerCrop(data.getData());
                    break;

                case IMAGE_REQUEST_TAKEPHOTO:
                    startBannerCrop(Uri.fromFile(tempFile));
                    break;

                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;

                case REQ_SELECT_PHOTOS:
                    paths = data.getStringArrayListExtra(PhotoActivity.KEY_PATH);
                    initBanner(paths);
                    break;

                case REQ_SELECT_DESCPTION_PIC:
                    paths = data.getStringArrayListExtra(PhotoActivity.KEY_PATH);
                    for (String path : paths) {
                        String cropPath = fwUtil.startPhotoZoom(Uri.parse("file://" + path), 640, 640, REQ_CROP_DESCPTION_PIC, true);
                        picDatas.add(cropPath);
                    }
                    break;

                case REQ_TAKE_DESCPTION_PIC:
                    String cropPath = fwUtil.startPhotoZoom(Uri.fromFile(tempFile), 640, 640, REQ_CROP_DESCPTION_PIC, true);
                    picDatas.add(cropPath);
                    break;

                case REQ_CROP_DESCPTION_PIC:
                    if (adapter != null) {
                        adapter = new Base();
                        gv_introduction.setAdapter(adapter);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initBanner(ArrayList<String> paths) {
        try {
            for (String path : paths) {
                startBannerCrop(Uri.parse("file://" + path));
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == ib_add_pic) {
            showDialog();

        } else if (v == b_title) {
            finish();

        } else if (v == b_opera) {
            submit();

        } else if (v == b_deleteGood) {
            if (type == GOOD_PUTDOWN)
                editGood(guid, false);
            else
                editGood(guid, true);

        } else if (v == b_editGood) {
            editGood(guid, false);

        }
    }

    private void submit() {
        String name = et_name.getText().toString();
        double price = 0;
        double marketPrice = 0;
        int stock = 0;
        try {
            price = Double.parseDouble(et_price.getText().toString());
            marketPrice = Double.parseDouble(et_marketPrice.getText().toString());
            stock = Integer.parseInt(et_stock.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (!isEdit && pics.size() < 1) {
            jsShowMsg("请选择商品图片");
        } else if (TextUtils.isEmpty(name.replaceAll(" ", ""))) {
            jsShowMsg(et_name.getHint().toString());
        } else if (price <= 0) {
            jsShowMsg(et_price.getHint().toString());
        } else if (marketPrice <= 0) {
            jsShowMsg(et_marketPrice.getHint().toString());
        } else if (stock <= 0) {
            jsShowMsg(et_stock.getHint().toString());
        } else {
            proDetail = et_proDes.getText().toString();
            submitPost(name, price, marketPrice, stock);
        }
    }

    private void submitPost(final String name, double price, double marketPrice, int stock) {
        if (isEdit) {
            String detailPics = "";
            for (int i = 0; i < picDatas.size(); i++) {
                if (i == 0) {
                    detailPics = FileUtil.getFileName(picDatas.get(i));
                } else {
                    detailPics += "," + FileUtil.getFileName(picDatas.get(i));
                }
            }
            if (isChanged(name, price, marketPrice, stock, proDetail, detailPics)) {
                final double tPrice = price;
                final double tMarketPrice = marketPrice;
                final int tStock = stock;

                if (aDialog == null) aDialog = new IOSDialog(this);
                aDialog.setMessage("确认修改商品信息");
                aDialog.setPositiveButton("取消", new View.OnClickListener() {
                    public void onClick(View v) {
                        aDialog.dismiss();
                    }
                });
                aDialog.setNegativeButton("确定", new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.show();
                        new Thread(new RSubmit(guid, name, tPrice, tMarketPrice, tStock)).start();
                    }
                });
                aDialog.show();

            } else {
                jsShowMsg("您还没有做任何修改呢！");
            }
        } else {
            dialog.show();
            int type = 0;
            if (cb_putaway.isChecked()) {
                type = 1;
            } else {
                type = 0;
            }
            Editor editor = sp.edit();
            editor.putBoolean(KEY_ISCHECKED, cb_putaway.isChecked());
            editor.apply();
            new Thread(new RSubmit(getUid(), name, price, marketPrice, stock, type)).start();
        }
    }

    private boolean isChanged(String name, double price, double marketPrice2, int stock, String detail, String detailPics) {
        if (!name.equals(productName)) {
            return true;
        } else if (marketPrice2 != marketPrice) {
            return true;
        } else if (price != shopPrice) {
            return true;
        } else if (stock != repertoryCount) {
            return true;
        } else if (!detail.equals(this.detail)) {
            return true;
        } else if (!detailPics.equals(this.detailPics)) {
            return true;
        }
        return false;
    }

    private File initPicFile() {
        String path = FileUtil.setMkdir(this);
        path += "/photos";
        File file_path = new File(path);
        boolean mkdir = file_path.mkdir();
        return new File(file_path, getPhotoFileName());
    }

}