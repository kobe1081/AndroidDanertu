package com.danertu.dianping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.db.DBHelper;
import com.danertu.entity.ShopCar;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import wl.codelibrary.widget.IOSDialog;

import static com.baidu.location.d.g.S;
import static com.baidu.location.d.g.b;
import static com.baidu.location.d.g.l;

public class CartActivity extends HomeActivity {

    // private Button cart_market, goShopping, cart_go;
    private TextView tvEdit;
    private LinearLayout llNoData;
    private TextView tvNoData;
    private ScrollView slCar;
    private LinearLayout llCar;
    private CheckBox cbSelectAll;
    private TextView tvTotalPrice;
    private TextView tvOPtion;
    private LinearLayout llTotalPrice;
    private RelativeLayout rlBottom;
    // private View layout_noProduct;
    private Intent mIntent;

    // private ListView lv;
    List<ShopCar> carEntity;
    // private GoodsCollectAdapter adp1;
    public ArrayList<Map<String, Object>> list = new ArrayList<>();
    // private String givecount;
    private int totalgivecount = 0;
    private int curselectcount = 0;

    View foot;
    public static final String KEY_SHOPCAR_LIST = "ShoppingCarList";
    public static final int REQ_PAYCENTER = 1;
    private final int REQ_LOGIN = 102;

    public static final String TAG_ITEM = "item";
    public static final String TAG_SHOP_CHECKBOX = "item_shop";
    public static final String TAG_ITEM_CHECKBOX = "item_check";
    public static final String TAG_ITEM_TOP = "item_top";
    public static final String TAG_ITEM_EDIT = "item_edit";
    public static final String TAG_ITEM_PRODUCT = "item_product";
    public static final String TAG_ITEM_PRODUCT_INFO = "item_product_info";
    public static final String TAG_ITEM_PRODUCT_INFO_PRICES = "item_product_info_price";
    Map<Integer, Boolean> shopSelectMap = new HashMap<>();

    private boolean isEditMode = false;

    private boolean isNotCancelAllSelect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);
//        String proAct = getIntent().getStringExtra("isProductDetail");
//        boolean isProAct = !TextUtils.isEmpty(proAct) && Boolean.parseBoolean(proAct);
//        if (isProAct)
        initSystemBar();
        setSystemBarColor("#F7F7F7");
        setTabVisibility(View.GONE);
        findViewById();
        tvNoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsStartIndexActivity();
            }
        });
        if (!isLogin()) {
            jsStartActivityForResult("LoginActivity", "", REQ_LOGIN);
            return;
        }
    }


    @Override
    protected void findViewById() {
        super.findViewById();
        llNoData = ((LinearLayout) findViewById(R.id.ll_no_data));
        tvNoData = ((TextView) findViewById(R.id.tv_no_data));
        slCar = ((ScrollView) findViewById(R.id.sl_shop_car));
        llCar = ((LinearLayout) findViewById(R.id.ll_car_data));
        tvEdit = ((TextView) findViewById(R.id.tv_edit));
        cbSelectAll = ((CheckBox) findViewById(R.id.cbSelectAll));
        tvTotalPrice = ((TextView) findViewById(R.id.tv_shopcar_total_price));
        tvOPtion = ((TextView) findViewById(R.id.tv_shopcar_option));
        llTotalPrice = ((LinearLayout) findViewById(R.id.ll_total_price));
        rlBottom = ((RelativeLayout) findViewById(R.id.rl_bottom));
        tvEdit.setText("编辑");
        //编辑
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.e(TAG, "购物车按钮：" + tvEdit.getText().toString());
                int childCount = llCar.getChildCount();
                if (tvEdit.getText().toString().equals("编辑")) {
                    isEditMode = true;
                    for (int i = 0; i < childCount; i++) {
                        LinearLayout llRoot = (LinearLayout) llCar.getChildAt(i);
                        int childCount1 = llRoot.getChildCount();
                        for (int i1 = 0; i1 < childCount1; i1++) {
                            View childAt = llRoot.getChildAt(i1);
                            if (childAt instanceof LinearLayout) {
                                LinearLayout llProduct = (LinearLayout) childAt;
                                if (llProduct != null) {
                                    LinearLayout llInfo = (LinearLayout) llProduct.findViewWithTag(TAG_ITEM_PRODUCT_INFO);
                                    if (llInfo != null) {
                                        LinearLayout llEdit = llInfo.findViewWithTag(TAG_ITEM_EDIT);
                                        llEdit.setVisibility(View.VISIBLE);
                                        LinearLayout llPrice = llInfo.findViewWithTag(TAG_ITEM_PRODUCT_INFO_PRICES);
                                        llPrice.setVisibility(View.GONE);
                                        llInfo.requestLayout();
                                    }
                                }
                            }
                        }

                    }
                    llTotalPrice.setVisibility(View.GONE);
                    tvEdit.setText("完成");
                    tvOPtion.setText("删除");
                } else {
                    isEditMode = false;
                    for (int i = 0; i < childCount; i++) {
                        LinearLayout llRoot = (LinearLayout) llCar.getChildAt(i);
                        int childCount1 = llRoot.getChildCount();
                        for (int i1 = 0; i1 < childCount1; i1++) {
                            View childAt = llRoot.getChildAt(i1);
                            if (childAt instanceof LinearLayout) {
                                LinearLayout llProduct = (LinearLayout) childAt;
                                if (llProduct != null) {
                                    LinearLayout llInfo = (LinearLayout) llProduct.findViewWithTag(TAG_ITEM_PRODUCT_INFO);
                                    if (llInfo != null) {
                                        LinearLayout llEdit = llInfo.findViewWithTag(TAG_ITEM_EDIT);
                                        llEdit.setVisibility(View.GONE);
                                        LinearLayout llPrice = llInfo.findViewWithTag(TAG_ITEM_PRODUCT_INFO_PRICES);
                                        llPrice.setVisibility(View.VISIBLE);
                                        llInfo.requestLayout();
                                    }
                                }
                            }
                        }

                    }
                    llTotalPrice.setVisibility(View.VISIBLE);
                    tvEdit.setText("编辑");
                    tvOPtion.setText("支付");
                }

            }
        });

        cbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Logger.e(TAG, "cbSelectAll.setOnCheckedChangeListener");
                if (list.size() <= 0) {
                    jsShowMsg("购物车空空的，去逛逛吧");
                    cbSelectAll.setChecked(false);
                    return;
                }

                if (isNotCancelAllSelect) {
                    return;
                }

                shopSelectMap.clear();

                int childCount = llCar.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    LinearLayout llTop = (LinearLayout) llCar.getChildAt(i).findViewWithTag(TAG_ITEM_TOP);
                    if (llTop != null) {
                        int llTopChildCount = llTop.getChildCount();
                        for (int i1 = 0; i1 < llTopChildCount; i1++) {
                            CheckBox box = (CheckBox) llTop.getChildAt(i1).findViewWithTag(TAG_SHOP_CHECKBOX);
                            if (box != null)
                                box.setChecked(isChecked);
                        }
//                        CheckBox box = (CheckBox) llTop.findViewWithTag(TAG_ITEM_CHECKBOX);
//                        if (box != null) {
//                            box.setChecked(isChecked);
//                        }
                    }
                }

                /**
                 * 取消全选时，总价格归零
                 */
                if (!isChecked) {
                    tvTotalPrice.setText(CommonTools.formatZero2Str(0));
                }

            }
        });

        tvOPtion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvOPtion.getText().equals("删除")) {
                    deleteProItem();
                } else {
                    int selectCount = 0;
                    for (Map<String, Object> map : list) {
                        boolean selected = Boolean.parseBoolean(map.get("selected").toString());
                        if (selected) {
                            selectCount++;
                        }
                    }
                    //结算
                    if (selectCount > 0) {
                        jsStartPayMementCenterActivity();
                    } else {
                        jsShowMsg("请选择要购买的商品");
                    }
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_LOGIN && resultCode == LoginActivity.LOGIN_FAILURE) {
            finish();
        }
    }

    public void onResume() {
        super.onResume();
        initShopCar(false);
        cbSelectAll.setChecked(false);
    }

    private void initShopCar(boolean isShowEdit) {
        tvTotalPrice.setText(CommonTools.formatZero2Str(0));
        if (isLogin()) {
            list.clear();
            BindData();
            initTitle("购物车（" + list.size() + "）");

            if (list.size() > 0) {
                llNoData.setVisibility(View.GONE);
                slCar.setVisibility(View.VISIBLE);
                rlBottom.setVisibility(View.VISIBLE);
                llCar.removeAllViews();
                initDataList(isShowEdit);

            } else {
                rlBottom.setVisibility(View.GONE);
                llNoData.setVisibility(View.VISIBLE);
                slCar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 生成列表
     */
    private void initDataList(boolean isShowEdit) {
        LinearLayout.MarginLayoutParams llRootLayoutParams = new LinearLayout.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams wrapContentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.MarginLayoutParams marginMatch = new LinearLayout.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.MarginLayoutParams marginWrap = new LinearLayout.MarginLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        int dp85 = CommonTools.dip2px(context, 85);
        int dp10 = CommonTools.dip2px(context, 10);
        int dp3 = CommonTools.dip2px(context, 3);
        int dp5 = CommonTools.dip2px(context, 5);
        int dp20 = CommonTools.dip2px(context, 20);
        int dp30 = CommonTools.dip2px(context, 30);
        int dp1 = CommonTools.dip2px(context, 1);

        int firstPosition = 1;
        String lastShopName = "";

        int size = list.size();

        for (int i = 0; i < size; i++) {
            Map<String, Object> map = list.get(i);
            String imgURL = map.get("imgURL").toString();
            boolean selected = Boolean.parseBoolean(map.get("selected").toString());
            String discountPrice = map.get("discountPrice").toString();
            String leaveTime = map.get("leaveTime").toString();
            String arriveTime = map.get("arriveTime").toString();
            String marketPrice = map.get("marketPrice").toString();
            String count = map.get("count").toString();
            String proName = map.get("proName").toString();
            String price = map.get("price").toString();
            String shopName = map.get("shopName").toString();
            String supplierID = map.get("supplierID").toString();
            String discountNum = map.get("discountNum").toString();
            String agentID = map.get("agentID").toString();
            String isQuanYanProduct = map.get("isQuanYanProduct").toString();
            String productID = map.get("productID").toString();
            String isQuanYanHotel = map.get("isQuanYanHotel").toString();
            String attrJson = map.get("attrJson").toString();
            String CreateUser = map.get("CreateUser").toString();
            String shopID = map.get("shopID").toString();

            boolean isSetTopMargin = false;

            final LinearLayout llItemRoot = new LinearLayout(context);
            LinearLayout.MarginLayoutParams ll = new LinearLayout.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llItemRoot.setLayoutParams(ll);
            llItemRoot.setGravity(Gravity.CENTER_HORIZONTAL);
            llItemRoot.setOrientation(LinearLayout.VERTICAL);
            llItemRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            llItemRoot.setTag(TAG_ITEM);
            llItemRoot.setPadding(dp10, dp5, dp10, dp5);


            LinearLayout llItemTop = new LinearLayout(context);
            llItemTop.setLayoutParams(llRootLayoutParams);
            llItemTop.setGravity(Gravity.CENTER_VERTICAL);
            llItemTop.setTag(TAG_ITEM_TOP);
            llItemTop.setOrientation(LinearLayout.HORIZONTAL);

            llItemRoot.addView(llItemTop);

            final CheckBox cbShop = new CheckBox(context);
            cbShop.setLayoutParams(wrapContentLayoutParams);
            cbShop.setButtonDrawable(R.drawable.bg_shopcar_cb);
            cbShop.setTag(TAG_SHOP_CHECKBOX);
            llItemTop.addView(cbShop);

            //店铺名
            TextView tvShopName = new TextView(context);
            tvShopName.setLayoutParams(wrapContentLayoutParams);
            Drawable drawable = getResources().getDrawable(R.drawable.ic_shop_logo);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvShopName.setCompoundDrawables(drawable, null, null, null);
            tvShopName.setCompoundDrawablePadding(dp5);
            tvShopName.setTextColor(ContextCompat.getColor(context, R.color.text_gray_999));
            tvShopName.setText(shopName);
            tvShopName.setTextSize(16);
            tvShopName.setGravity(Gravity.CENTER_VERTICAL);
            llItemTop.addView(tvShopName);
            tvShopName.setPadding(dp10, dp10, 0, dp10);
            //分割线
            View view = new View(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp1));
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.diver_color));

            final LinearLayout llProduct = new LinearLayout(context);
            llProduct.setLayoutParams(llRootLayoutParams);
            llProduct.setOrientation(LinearLayout.HORIZONTAL);
//            llProduct.setTag(TAG_ITEM_PRODUCT);
            llProduct.setGravity(Gravity.CENTER_VERTICAL);


            //设置tag，方便选择时操作数据
            llProduct.setTag(i);

            if (shopName.equals(lastShopName)) {
                //同一家店铺产品
                llItemRoot.removeView(llItemTop);
                ((LinearLayout) llCar.getChildAt(i - firstPosition).findViewWithTag(TAG_ITEM)).addView(llProduct);
                firstPosition++;
            } else {
                firstPosition = 1;
                isSetTopMargin = true;
                llItemRoot.addView(view);
                llItemRoot.addView(llProduct);
            }

            lastShopName = shopName;

            final CheckBox cbProduct = new CheckBox(context);
            cbProduct.setLayoutParams(wrapContentLayoutParams);
            cbProduct.setButtonDrawable(R.drawable.bg_shopcar_cb);
            cbProduct.setTag(TAG_ITEM_CHECKBOX);
            llProduct.addView(cbProduct);

            //产品图片
            ImageView imageView = new ImageView(context);
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(dp85, dp85);
            imageView.setLayoutParams(params);

            ImageLoader.getInstance().displayImage(imgURL, imageView);
            llProduct.addView(imageView);


            LinearLayout llProductInfo = new LinearLayout(context);
            llProductInfo.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            llProductInfo.setGravity(Gravity.CENTER_VERTICAL);
            llProductInfo.setOrientation(LinearLayout.VERTICAL);
            llProductInfo.setTag(TAG_ITEM_PRODUCT_INFO);
            llProductInfo.setBackgroundResource(R.drawable.bg_bottom_line);
            llProductInfo.setPadding(0, 0, 0, dp20);

            llProduct.addView(llProductInfo);


            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(marginMatch);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            llProductInfo.addView(linearLayout);

            //商品名
            TextView tvProductName = new TextView(context);
            tvProductName.setLayoutParams(marginMatch);
            tvProductName.setMaxLines(2);
            tvProductName.setGravity(Gravity.CENTER_VERTICAL);
            tvProductName.setTextColor(ContextCompat.getColor(context, R.color.gray_text));
            tvProductName.setText(proName);
            linearLayout.addView(tvProductName);

            TextView tvProductTips = new TextView(context);
            tvProductTips.setLayoutParams(marginMatch);
            tvProductTips.setTextSize(12);
            tvProductTips.setTextColor(ContextCompat.getColor(context, R.color.gray_text));

            tvProductTips.setText(" ");
//            tvProductTips.setVisibility(View.GONE);

            linearLayout.addView(tvProductTips);

            LinearLayout llPriceAndNum = new LinearLayout(context);
            llPriceAndNum.setLayoutParams(marginMatch);
            llPriceAndNum.setOrientation(LinearLayout.HORIZONTAL);
            llPriceAndNum.setTag(TAG_ITEM_PRODUCT_INFO_PRICES);

            linearLayout.addView(llPriceAndNum);

            //商品价
            final TextView tvShopPrice = new TextView(context);
            tvShopPrice.setLayoutParams(marginWrap);
            tvShopPrice.setTextColor(ContextCompat.getColor(context, R.color.red_text1));
            tvShopPrice.setText("￥" + price);

            llPriceAndNum.addView(tvShopPrice);

//            //市场价
//            TextView tvMarketPrice = new TextView(context);
//            tvMarketPrice.setLayoutParams(marginWrap);
//            setMargins(tvMarketPrice, dp20, 0, 0, 0);
//            tvMarketPrice.setText("￥" + marketPrice);
//            tvMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//            tvMarketPrice.getPaint().setAntiAlias(true);
//
//            llPriceAndNum.addView(tvMarketPrice);

            //购买数量
            final TextView tvBuyNum = new TextView(context);
            tvBuyNum.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            tvBuyNum.setGravity(Gravity.RIGHT);
            tvBuyNum.setText("x" + count);

            llPriceAndNum.addView(tvBuyNum);

            LinearLayout llEdit = new LinearLayout(context);
            llEdit.setLayoutParams(marginMatch);
            llEdit.setTag(TAG_ITEM_EDIT);
            llEdit.setGravity(Gravity.CENTER_VERTICAL);
            llEdit.setOrientation(LinearLayout.HORIZONTAL);


            final ImageView ivReduce = new ImageView(context);
            ivReduce.setLayoutParams(marginWrap);
            ivReduce.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_reduce));

            llEdit.addView(ivReduce);

            final TextView tvCount = new TextView(context);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            tvCount.setLayoutParams(params1);
            tvCount.setBackgroundResource(R.drawable.bg_shopcar_edit);
            tvCount.setGravity(Gravity.CENTER);
            tvCount.setText(count);
            llEdit.addView(tvCount);
            tvCount.setPadding(dp3, dp3, dp3, dp3);

            ImageView ivAdd = new ImageView(context);
            ivAdd.setLayoutParams(marginWrap);
            ivAdd.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_add_new));
            llEdit.addView(ivAdd);

            if (isShowEdit) {
                llPriceAndNum.setVisibility(View.GONE);
                llEdit.setVisibility(View.VISIBLE);
            } else {
                llPriceAndNum.setVisibility(View.VISIBLE);
                llEdit.setVisibility(View.GONE);
            }

            llProductInfo.addView(llEdit);

            llCar.addView(llItemRoot);

            //设置margin放在最后，否则不起作用
            setMargins(llProduct, 0, dp5, 0, 0);
            setMargins(imageView, dp10, dp10, dp10, dp10);
            setMargins(tvProductName, 0, 0, dp30, 0);
            setMargins(tvProductTips, 0, dp3, dp30, 0);
            setMargins(llPriceAndNum, 0, dp3, 0, 0);
            setMargins(llEdit, 0, dp10, dp30, 0);
            setMargins(ivReduce, 0, 0, dp10, 0);
            setMargins(ivAdd, dp10, 0, 0, 0);
            setMargins(tvProductName, dp10, 0, 0, 0);
            if (isSetTopMargin) {
                setMargins(llItemRoot, 0, dp20, 0, 0);
            }

            //各个按钮触发事件

            //单个商品选择
            final int finalI = i;
            cbProduct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        curselectcount++;
                    } else {
                        curselectcount--;
                    }
                    boolean isShopAllSelect = false;
                    int selectCount = 0;
                    LinearLayout lRoot = ((LinearLayout) buttonView.getParent().getParent());
                    int childCount = lRoot.getChildCount();
                    for (int i1 = 0; i1 < childCount; i1++) {
                        View childAt = lRoot.getChildAt(i1);
                        if (childAt instanceof LinearLayout) {
                            LinearLayout lProduct = (LinearLayout) childAt;
                            if (lProduct != null) {
                                CheckBox box = (CheckBox) lProduct.findViewWithTag(TAG_ITEM_CHECKBOX);
                                if (box != null) {
                                    if (box.isChecked()) {
                                        selectCount++;
                                    } else {
                                        selectCount--;
                                    }
                                }
                            }
                        }
                    }
                    isShopAllSelect = selectCount == childCount - 2;
                    CheckBox box = (CheckBox) lRoot.findViewWithTag(TAG_ITEM_TOP).findViewWithTag(TAG_SHOP_CHECKBOX);

                    if (box != null) {
                        shopSelectMap.put(finalI, isShopAllSelect);
                        box.setChecked(isShopAllSelect);
                    }

                    Map<String, Object> objectMap = list.get(finalI);
                    objectMap.put("selected", isChecked);
                    calculateTotalPrice(objectMap, isChecked, tvCount.getText().toString());

                    /**如果已经是全选状态，那么再点击任意一项，都要取消全选按钮的选中状态*/
                    if (cbSelectAll.isChecked()) {
                        if (!isChecked) {
                            //是取消某一项状态
                            isNotCancelAllSelect = true;
                            cbSelectAll.setChecked(false);
                        }
                    }
                    isSelectAll();

                }
            });

            //店铺全选
            cbShop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (shopSelectMap.get(finalI) != null && !shopSelectMap.get(finalI) && !isChecked) {
                        return;
                    }
                    int childCount = ((LinearLayout) llCar.getChildAt(finalI)).getChildCount();
                    for (int i1 = 1; i1 < childCount; i1++) {
                        CheckBox cb = (llItemRoot.getChildAt(i1)).findViewWithTag(TAG_ITEM_CHECKBOX);
                        if (cb != null)
                            cb.setChecked(isChecked);
                    }
                }
            });

            //数量-
            ivReduce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String buyNumText = tvCount.getText().toString();
                    if (buyNumText.equals("1")) {
                        jsShowMsg("已经不能再减了");
                        return;
                    }
                    try {
                        int parseInt = Integer.parseInt(buyNumText);
                        parseInt--;
                        tvBuyNum.setText("x" + parseInt);
                        tvCount.setText("" + parseInt);

                        list.get(finalI).put("count", parseInt);

                        LinearLayout lProduct = (LinearLayout) ivReduce.getParent().getParent().getParent();
                        if (lProduct != null) {
                            CheckBox box = (CheckBox) lProduct.findViewWithTag(TAG_ITEM_CHECKBOX);
                            if (box != null && box.isChecked()) {
                                calculateTotalPrice(list.get(finalI), false, 1);
                            }
                        }
                        jsUpdateProductCount(finalI, parseInt);
                    } catch (Exception e) {
                        e.printStackTrace();
                        jsShowMsg("出错了，请重试");
                    }
                }
            });

            ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String buyNumText = tvCount.getText().toString();
                    try {
                        int parseInt = Integer.parseInt(buyNumText);
                        parseInt++;
                        tvBuyNum.setText("x" + parseInt);
                        tvCount.setText("" + parseInt);

                        list.get(finalI).put("count", parseInt);

                        LinearLayout lProduct = (LinearLayout) ivReduce.getParent().getParent().getParent();
                        if (lProduct != null) {
                            CheckBox box = (CheckBox) lProduct.findViewWithTag(TAG_ITEM_CHECKBOX);
                            if (box != null && box.isChecked()) {
                                calculateTotalPrice(list.get(finalI), true, 1);
                            }
                        }
                        jsUpdateProductCount(finalI, parseInt);
                    } catch (Exception e) {
                        e.printStackTrace();
                        jsShowMsg("出错了，请重试");
                    }

                }
            });

        }
    }

    /**
     * 计算是否已经全选
     */
    private void isSelectAll() {
        int selectCount = 0;
        for (Map<String, Object> map : list) {
            if (map.get("selected").toString().equals("true")) {
                selectCount++;
            }
        }
        if (selectCount == list.size() && selectCount != 0) {
            isNotCancelAllSelect = false;
            cbSelectAll.setChecked(true);
        }
    }

    /**
     * 计算总价
     *
     * @param objectMap
     * @param isChecked
     */
    private void calculateTotalPrice(Map<String, Object> objectMap, boolean isChecked, String count) {
        try {
            boolean selected = Boolean.parseBoolean(objectMap.get("selected").toString());
            float price = Float.parseFloat(objectMap.get("price").toString());
            float totalPrice = Float.parseFloat(tvTotalPrice.getText().toString());
            int buyCunt = Integer.parseInt(count);

            if (isChecked) {
                //选中为+
                if (selected) {
                    tvTotalPrice.setText(CommonTools.formatZero2Str((price * buyCunt) + totalPrice));
                }
            } else {
                //否则为-
                tvTotalPrice.setText(CommonTools.formatZero2Str(totalPrice - (price * buyCunt)));
            }


//            for (Map<String, Object> stringObjectMap : list) {
//                String selected = stringObjectMap.get("selected").toString();
//                if (selected.equals("true")) {
//                    float price = Float.parseFloat(stringObjectMap.get("price").toString());
//                    float totalPrice = Float.parseFloat(tvTotalPrice.getText().toString());
//                    tvTotalPrice.setText(CommonTools.formatZero2Str(price + totalPrice));
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateTotalPrice(Map<String, Object> objectMap, boolean isChecked, int count) {
        try {
            boolean selected = Boolean.parseBoolean(objectMap.get("selected").toString());
            float price = Float.parseFloat(objectMap.get("price").toString());
            float totalPrice = Float.parseFloat(tvTotalPrice.getText().toString());

            if (isChecked) {
                //选中为+
                if (selected) {
                    tvTotalPrice.setText(CommonTools.formatZero2Str((price * count) + totalPrice));
                }
            } else {
                //否则为-
                tvTotalPrice.setText(CommonTools.formatZero2Str(totalPrice - (price * count)));
            }


//            for (Map<String, Object> stringObjectMap : list) {
//                String selected = stringObjectMap.get("selected").toString();
//                if (selected.equals("true")) {
//                    float price = Float.parseFloat(stringObjectMap.get("price").toString());
//                    float totalPrice = Float.parseFloat(tvTotalPrice.getText().toString());
//                    tvTotalPrice.setText(CommonTools.formatZero2Str(price + totalPrice));
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Gson gson = new Gson();

    @JavascriptInterface
    public void jsLoadShoppingCarList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String jsonStr = gson.toJson(list);
                Logger.i("购物车数据", jsonStr + "");
                webView.loadUrl(Constants.IFACE + "javaLoadProductList('" + jsonStr + "')");
            }
        });
    }

    @JavascriptInterface
    public void jsUpdatePrice(int id, double price) {
        jsUpdatePrice(id, price, null);
    }

    @JavascriptInterface
    public void jsUpdatePrice(int id, double price, String attrJson) {
        HashMap<String, Object> item = (HashMap<String, Object>) list.get(id);
        final String guid = String.valueOf(item.get(k_productID).toString());
        final String shopID = item.get(DBHelper.SHOPCAR_SHOPID).toString();
        Object att = item.get(DBHelper.SHOPCAR_ATTRJSON);
        attrJson = att == null ? null : att.toString();
        db.updateProductPriceInCar(getContext(), guid, getUid(), price, attrJson, shopID);
        item.put(k_price, price);
    }

    @JavascriptInterface
    public void jsUpdateProductCount(final int id, int count) {
        jsUpdateProductCount(id, count, null);
    }

    @JavascriptInterface
    public void jsUpdateProductCount(final int id, int count, String attrJson) {
        HashMap<String, Object> item = (HashMap<String, Object>) list.get(id);
        final String guid = String.valueOf(item.get("productID").toString());
        final String shopID = item.get(DBHelper.SHOPCAR_SHOPID).toString();
        Object att = item.get(DBHelper.SHOPCAR_ATTRJSON);
        attrJson = att == null ? null : att.toString();
        db.updateProductCountInCar(getContext(), guid, getUid(), count, attrJson, shopID);
        item.put("count", count);
    }

    @JavascriptInterface
    public void jsSetProductSelect(int id, int status) {
        if (status == 1) {
            list.get(id).put("selected", true);
        } else if (status == 0) {
            list.get(id).put("selected", false);
        }
    }

    @JavascriptInterface
    public void jsSetArriveTime(int id, String date) {
        if (date.length() > 10) {
            list.get(id).put("arriveTime", date);
        }
    }

    @JavascriptInterface
    public void jsSetLeaveTime(int id, String date) {
        if (date.length() > 10) {
            list.get(id).put("leaveTime", date);
        }
    }

    @JavascriptInterface
    public void jsUpdateSelectCount(int count) {
        curselectcount = count;
    }

    @JavascriptInterface
    public void jsStartIndexActivity() {
        application.backToActivity("IndexActivity");
    }

    boolean isToPay = false;

    @JavascriptInterface
    public boolean jsStartPayMementCenterActivity() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (isToPay)
                    return;
                if (!checkErrorMsg()) {
                    int allCount = 0;
                    double totalMoney = 0;

                    Iterator<Map<String, Object>> iter = list.iterator();
                    while (iter.hasNext()) {
                        Map<String, Object> item = iter.next();
                        if ((Boolean) item.get("selected")) {
                            double price = Double.parseDouble(item.get("price").toString());
                            String agentid = item.get("agentID").toString();
                            int count = Integer.parseInt(item.get("count").toString());
                            allCount += count;
                            if (agentid != null && agentid.equals("")) {
                                totalMoney += (price * count);
                            }
                        } else {
                            iter.remove();
                        }
                    }

                    mIntent = new Intent();
                    mIntent.setClassName(getApplicationContext(), "com.danertu.dianping.PaymentCenterActivity");
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // Bundle类用作携带数据，它类似于Map，用于存放key-value名值对形式的值
                    Bundle b = new Bundle();
                    b.putString("shopid", getShopId());
                    b.putString("allCount", String.valueOf(allCount + totalgivecount));
                    b.putString("allMoney", String.valueOf(totalMoney));
                    // 此处使用putExtras，接受方就响应的使用getExtra
                    mIntent.putExtra(KEY_SHOPCAR_LIST, list);
                    mIntent.putExtras(b);
                    startActivityForResult(mIntent, REQ_PAYCENTER);
                    isToPay = true;
                    // finish();
                } else
                    isToPay = false;
            }
        });
        return isToPay;
    }

    private boolean checkErrorMsg() {
        if (curselectcount <= 0) {
            showErrorDialog("提示", "请至少选择一项商品！");
            return true;
        }
        for (Map<String, Object> item : list) {
            if ((Boolean) item.get("selected") && (Boolean) item.get("isQuanYanProduct")) {
                if (TextUtils.isEmpty(item.get("arriveTime").toString())) {
                    showErrorDialog("提示", "请确认已选定 预计抵达时间！");
                    return true;
                }
                if ((Boolean) item.get("isQuanYanHotel")) {
                    if (TextUtils.isEmpty(item.get("leaveTime").toString())) {
                        showErrorDialog("提示", "请确认已选定 预计抵达时间 和 预计离开时间！");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @JavascriptInterface
    public void showErrorDialog(String title, String msg) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("确定", null).show();
    }

    public void initTitle(String title) {
        TextView tv_title = (TextView) findViewById(R.id.tv_title2);
        tv_title.setText(title);
        findViewById(R.id.b_title_back2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
//				jsStartIndexActivity();
            }
        });
    }

    @JavascriptInterface
    public void deleteProItem() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (curselectcount <= 0) {
                    CommonTools.showShortToast(getContext(), "请选择你要删除的商品");
                    return;
                }

                final IOSDialog iosDialog = new IOSDialog(context);
                iosDialog.setTitle("删除商品");
                iosDialog.setMessage("确定要从购物车删除所有选择商品？");
                iosDialog.setNegativeButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iosDialog.dismiss();
                        String uid = getUid();
                        Iterator<Map<String, Object>> iter = list.iterator();
                        while (iter.hasNext()) {
                            Map<String, Object> item = iter.next();
                            if ((Boolean) item.get("selected")) {
                                String guid = item.get("productID").toString();
                                Object param = item.get(DBHelper.SHOPCAR_ATTRJSON);
                                String shopid = item.get(DBHelper.SHOPCAR_SHOPID).toString();
                                String attrParam = param == null ? null : param.toString();
                                db.delProductInCar(CartActivity.this, guid, uid, attrParam, shopid);
                                iter.remove();
                            }
                        }
                        initShopCar(true);
                        if (list.size() <= 0) {
                            llCar.setVisibility(View.GONE);
                            llTotalPrice.setVisibility(View.GONE);
                        }
                        initShopCarCount();
                        //删除成功后总价格清零
                        tvTotalPrice.setText(CommonTools.formatZero2Str(0));
                    }
                });
                iosDialog.setPositiveButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iosDialog.dismiss();
                    }
                });
                iosDialog.show();
            }
        });
    }


    public final static String k_isQuanYanProduct = "isQuanYanProduct";
    public final static String k_isQuanYanHotel = "isQuanYanHotel";
    public final static String k_productID = "productID";
    public final static String k_proName = "proName";
    public final static String k_price = "price";
    public final static String k_count = "count";
    public final static String k_agentID = "agentID";
    public final static String k_imgURL = "imgURL";
    public final static String k_supplierID = "supplierID";
    public final static String k_shopID = "shopID";
    public final static String k_selected = "selected";
    public final static String k_arriveTime = "arriveTime";
    public final static String k_leaveTime = "leaveTime";

    private void BindData() {
        isToPay = false;
        Cursor cursor = db.GetShopCar(getContext(), db.GetLoginUid(getContext()));
        if (cursor == null)
            return;
        list = new ArrayList<>();
        int size = cursor.getCount();

        for (int i = 0; i < size; i++) {
            cursor.moveToPosition(i);
            String productID = cursor.getString(0);
            String proName = cursor.getString(1);
            String price = cursor.getString(3);
            String marketPrice = cursor.getString(4);
            String count = cursor.getString(5);
            String agentID = cursor.getString(2);
            String imgName = cursor.getString(4);
            String supplierID = cursor.getString(7);
            String shopID = cursor.getString(8);
            String attrJson = cursor.getString(cursor.getColumnIndex(DBHelper.SHOPCAR_ATTRJSON));
            String createUser = cursor.getString(cursor.getColumnIndex(DBHelper.SHOPCAR_CREATEUSER));
            String shopName = cursor.getString(cursor.getColumnIndex(DBHelper.SHOPCAR_SHOPNAME));
//( isSelect,  productID,  buycount,  imgName,  supplierID, shopID,  agentID,  proName,  price,  marketPrice,  createUser,  attrJson,  arriveTime,  leaveTime,  shopName,  discountNum,  discountPrice)
            HashMap<String, Object> dataMap = ActivityUtils.getShopCarItem(false, productID, count, imgName, supplierID, shopID, agentID, proName, price, "", createUser, attrJson, "", "", shopName, "", "");
            list.add(dataMap);
        }
        Collections.sort(list, new CarComparator());
    }

    /**
     * 购物车比较器，用于归类购物车同一店铺的产品
     */
    class CarComparator implements Comparator<Map<String, Object>> {

        @Override
        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
            String shopName1 = o1.get(DBHelper.SHOPCAR_SHOPNAME).toString();
            String shopName2 = o2.get(DBHelper.SHOPCAR_SHOPNAME).toString();
            return shopName1.equals(shopName2) ? -1 : 0;
        }
    }
}
