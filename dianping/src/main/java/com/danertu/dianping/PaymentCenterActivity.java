package com.danertu.dianping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.config.Constants;
import com.danertu.db.DBHelper;
import com.danertu.entity.BaseResultBean;
import com.danertu.entity.ChooseCouponBean;
import com.danertu.entity.FavTicket;
import com.danertu.entity.MyCouponBean;
import com.danertu.entity.PaymentPriceData;
import com.danertu.tools.AccToPay;
import com.danertu.tools.AlipayUtil;
import com.danertu.tools.AppManager;
import com.danertu.tools.ArithUtils;
import com.danertu.tools.Logger;
import com.danertu.tools.MyDialog;
import com.danertu.tools.Result;
import com.danertu.tools.StockAccountPay;
import com.danertu.tools.StockAlipayUtil;
import com.danertu.tools.StockWXPay;
import com.danertu.tools.WXPay;
import com.danertu.widget.CommonTools;
import com.danertu.widget.PayPswDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import static com.danertu.dianping.StockOrderDetailActivity.newHandler;
import static com.danertu.dianping.activity.choosecoupon.ChooseCouponPresenter.REQUEST_CHOOSE_COUPON;


/**
 * 支付页面
 */
public class PaymentCenterActivity extends BaseActivity implements OnClickListener {

    @SuppressWarnings("unused")
    private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "请不要动这些代码，用于";
    public static String translateKeyword = "%E7%94%B5%E8%AF%9D";

    //----------对象------------
    public Context context;
    private ImageLoader imgLoader = null;


    //-----------UI-----------------
    public CheckBox cb_useJLB;
    public LinearLayout ll_cb; //代金券父控件
    public TextView txt_name;
    public TextView txt_mobile;
    public TextView txt_address;
    public TextView txt_buyCount;
    public TextView txt_totalMoney, txt_totaljifen;
    public View simpleView;
    public View noAddressView;
    public TextView txt_orderPrice, invoiceHint, invoiceTitle, txtTitle;
    public TextView txt_submitOrder, remark;
    public TextView et, et2;
    //    优惠价
    private TextView tv_order_1, tv_order_discountPrice;
    private TextView tv_order_2, tv_price;
    private PopupWindow _PopupWindow;
    Button button1, btnclosePop, btnclose;
    RelativeLayout remarklayout, invoicelayout, arrivetimelayout, leavetimelayout;
    public RadioGroup rg_payway;
    public TextView txt_yunfei;
    private LinearLayout ll_proParent = null;
    private EditText et_remark = null;
    private Button b_submit = null;
    private FrameLayout fl_payWay_show;
    //    发货方式
    private RadioGroup rg_payment_ship_time;
    //    马上发货
    private RadioButton rb_payment_ship_now;
    //    囤货后发
    private RadioButton rb_payment_ship_after;
    private TextView tv_ship_after_tip;
    private LinearLayout ll_contact;

    /**
     * 2018年2月2日
     * 单耳兔智慧仓库协议
     */
    private LinearLayout ll_stock_protocol;
    private TextView tv_stock_protocol;

    //-------------常量标识--------------------//
    public static final String KEY_ALLCOUNT = "allCount";
    public static final String KEY_ALLMONEY = "allMoney";
    public static final String KEY_IS_BACK_CALL = "isbackcall";
    public static final int RQF_PAY = 1;
    public static final int RQF_LOGIN = 2;
    public static final int GET_UNION_TN = 89;
    public static final int PLUGIN_VALID = 0;
    public static final int PLUGIN_NOT_INSTALLED = -1;
    public static final int PLUGIN_NEED_UPGRADE = 2;
    public static final int ALL_DATATIME_DIALOG_ID = 0;
    public static final int ARRIVE_TIME_DIALOG_ID = 1;
    public static final int ARRIVE_DATE_DIALOG_ID = 2;
    public static final int LEAVE_TIME_DIALOG_ID = 3;
    public static final int LEAVE_DATE_DIALOG_ID = 4;
    public static final int ARRIVE_DATETIME_DIALOG_ID = 5;


    final String KEY_YUNFEI = "yunfei";
    final String KEY_ATTRS = DBHelper.SHOPCAR_ATTRJSON;
    final String KEY_CREATE_USER = "CreateUser";
    /**
     * 送多少件
     */
    final String KEY_SONG_COUNT = "songCount";
    final String KEY_JOIN_COUNT = "joinCount";
    public static final int REQ_PAYCENTER = 11;
    public static int tMode = 0;


    //---------------变量-----------------------------//
    public Boolean isInserted = false; // 判断是否新增成功
    public String ivTitle, ivContent, myremark, body;
    public String subject = "单耳兔商城购物清单";
    public String outOrderNumber = ""; // 下单订单号
    String score = null;
    double allYunFei = 0;
    double shouldPayMoney = 0;
    boolean isDispatch = true; // 判断物流是否能送达
    boolean existsAgent = false; // 判断订单商品是否包含自营店商品
    boolean isQuanYan = false; // 判断是否泉眼商品
    boolean isCanFav = false;//是否可以有优惠---比如拿货超过十箱
    public int discountCondition = -1;//优惠条件
    /**
     * Unionpay 表示银联支付，Alipay 表示支付宝支付
     */
    public String payWay = null;

    /**
     * 商品抵扣的总金额
     */
    public double zhekouMoney = 0;
    /**
     * 积分能抵扣的金额
     */
    public double canUseMoney = 0;
    /**
     * 可抵扣商品的总价
     */
    public double ckProductMoney = 0;
    public String totalCount = null;
    public String recName = null;
    /**
     * 个人收货地址
     */
    public String recAddress = null;
    public String recMobile = null;
    public String uid = null;
    String intent_allCount = null;
    String intent_allMoney = null;
//	private String productJson = "";
    /**
     * 代金券、积分使用情况记录
     */
    private String tag_cash = "";
    private boolean isCanUseCashTicket = false;
    private String pricedata = "";
    private boolean isCanSubmit = true;
    private String errText = "";
    //是否为后台拿货
    private boolean isBackCall = false;
    //是否为囤货,囤货订单调用的支付类不一样
    private boolean isStock = false;
    private String cbText = "";
    //	private boolean isOnlyTasteWine = false;
//    private List<FavTicket> favTickets;

    private static final int WHAT_CAN_USE_JLB = 777;
    private static final int WHAT_START_CAN_USR_JLB = 778;

//    private String shopId = "";
//    private String productGuid = "";

    private Map<String, List<String>> paramMap;//保存店铺对应的商品 用于获取可用优惠券

    /**
     * 订单类型 -- 囤货、普通后台拿货、退货邮费支付
     */
    private String orderType = "";
    public static final String ORDER_TYPE_STOCK = "warehouse";//囤货--下单后货物放到专属仓库
    public static final String ORDER_TYPE_BACK_CALL = "backcall";//普通后台拿货
    public static final String ORDER_TYPE_ORDER_RETURN = "warehouse_order_return";//退货邮费支付

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        imgLoader = ImageLoader.getInstance();
        setContentView(R.layout.activity_pay_mement_center);
        setSystemBarWhite();
        uid = getUid();
        if (uid == null || uid.equals("")) {
            CommonTools.showShortToast(context, "请先登录！");
            finish();
            return;
        }
        handler = new PaymentCenterHandler(this);
        initHandler();
        findViewById();
        initTitle("结算中心");
        initIntent();
        initData();
        initPswDialog();
    }

    private void initHandler() {
        newHandler = new Handler(this.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_START_CAN_USR_JLB:
                        /**一个订单包含多个商品时，productGuid之间用 , 隔开*/
                        String productId = msg.obj.toString();
                        new CanUseJLB().execute(productId);
                        break;
                    case WHAT_CAN_USE_JLB:
                        String result = msg.obj.toString();
                        if ("true".equals(result) && !isBackCall) {
                            cb_useJLB.setVisibility(View.VISIBLE);
                        } else {
                            cb_useJLB.setVisibility(View.GONE);
                        }

                        hideLoadDialog();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    /**
     * 初始化标题
     *
     * @param title
     */
    private void initTitle(String title) {
        ((TextView) findViewById(R.id.tv_title)).setText(title);
        findViewById(R.id.b_title_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    PaymentPriceData price = new PaymentPriceData();

    public void finish() {
        setResult(CartActivity.REQ_PAYCENTER);
        super.finish();
    }

    private PayPswDialog dialog_psw = null;

    /**
     * 账号支付窗口
     */
    private void initPswDialog() {
        dialog_psw = new PayPswDialog(this, R.style.Dialog) {
            public void cancelDialog() {
                com.danertu.widget.AlertDialog dialog = new com.danertu.widget.AlertDialog(getContext(), R.style.Dialog) {
                    public void sure() {
                        dismiss();
                    }

                    public void cancelDialog() {
                        if (isStock) {
                            dialog_psw.dismiss();
                            dismiss();
                            finish();
                        } else {
                            dialog_psw.dismiss();
                            dismiss();
                            toOrderComplete(outOrderNumber, getString(R.string.payWay_account_tips), price.getTotalPrice(), false);
                        }
                    }
                };
                if (isStock) {
                    //囤货订单
                    dialog.setTitle("取消付款");
                    dialog.setContent("未支付的囤货订单将作废");
                    dialog.setCancelButton("取消付款");
                    dialog.setSureButton("继续付款");
                    dialog.setCanBack(false);
                    dialog.show();
                } else {
                    //常规订单
                    dialog.setTitle("取消付款");
                    dialog.setContent("订单已生成,取消后可到订单中心付款");
                    dialog.setCancelButton("取消付款");
                    dialog.setSureButton("继续付款");
                    dialog.setCanBack(false);
                    dialog.show();
                }
            }

            @Override
            public void passwordRight() {
                String param[] = {uid, outOrderNumber, price.getTotalPrice(), pricedata};
//                String param[] = {uid, outOrderNumber, "0.01", "0.01"};
                if (isStock) {
                    StockAccountPay stockAccountPay = new StockAccountPay(getContext()) {
                        @Override
                        public void paySuccess() {
                            dialog_psw.dismiss();
                            //囤货支付成功操作
//                            jsShowMsg("支付成功");
                            toWarehouse();
                            finish();
                        }

                        @Override
                        public void payFail() {

                        }

                        @Override
                        public void payError() {
                            //支付出错
                            hideLoadDialog();
                        }
                    };
                    stockAccountPay.execute(param);
                } else {
                    AccToPay accToPay = new AccToPay(getContext()) {

                        @Override
                        public void paySuccess() {
                            dialog_psw.dismiss();
                            toOrderComplete(outOrderNumber, getString(R.string.payWay_account_tips), price.getTotalPrice(), true);
                        }

                        @Override
                        public void payFail() {

                        }
                    };
                    accToPay.execute(param);
                }
            }

            @Override
            public void passwordWrong() {
                CommonTools.showShortToast(getContext(), "支付密码不正确！");
            }
        };
    }

    /**
     * 跳转至仓库
     */
    public void toWarehouse() {
        if (StockDetailActivity.isToIn) {
            //是从仓库页面跳过来入货的就不要打开页面了
            return;
        }
        Intent intent = new Intent(context, StockpileActivity.class);
        startActivity(intent);
    }


    /**
     * 添加收货地址
     *
     * @param v
     */
    public void toAddressActivity(View v) {
        if (!isLoading()) {
            if (isQuanYan) {
                jsShowMsg("请直接填写收货人信息");
            } else {
//                CommonTools.showShortToast(context, "请选择收货地址以及收货信息");
                jsStartActivityForResult("AddressActivity", "manageAddress|true", REQ_ADDRESS);
            }
        }
    }

    private RelativeLayout rl_fav_ticket;
    private TextView tv_fav_ticket;
    private RelativeLayout rl_fav_num;
    private TextView tv_fav_num_tips;
    private CheckBox cb_payWay_tag;
    private TextView txt_totalFav;

    @SuppressWarnings("unchecked")
    private void initIntent() {
        Bundle bundle = this.getIntent().getExtras();
        intent_allCount = bundle.getString(KEY_ALLCOUNT);
        intent_allMoney = bundle.getString(KEY_ALLMONEY);
        isBackCall = bundle.getBoolean(KEY_IS_BACK_CALL);
        shoppingCarList = (ArrayList<Map<String, Object>>) getIntent().getSerializableExtra(CartActivity.KEY_SHOPCAR_LIST);
    }

    private void initData() {
        // 初始化数据
        subject = "单耳兔商城" + getString(R.string.pay_tips);
        paramMap = new HashMap<>();
        isDispatch = true;
        existsAgent = false;
        isQuanYan = false;
//		isOnlyTasteWine = false;
        b_submit.setEnabled(true);
        initErrInfo(null);
        outOrderNumber = "";
        if (shoppingCarList == null) {
            shoppingCarList = new ArrayList<>();
        }
        uid = db.GetLoginUid(context);
        Cursor cursor = db.GetDefaultAddress(this, uid);
        initDefaultContactMsg(cursor);

        initCKProPrice();

        showLoadDialog();
        new Thread(new RGetData(shoppingCarList)).start();
    }

    public void initErrInfo(String info) {
        if (info == null || info.replace(" ", "").equals("")) {
            this.isCanSubmit = true;
            this.errText = "";
        } else {
            this.isCanSubmit = false;
            this.errText = info;
        }
    }

    private String[] getAttrIdAndValue(String paramStr) {
        String[] result = {"", ""};
        if (TextUtils.isEmpty(paramStr))
            return result;
        final String[] t = paramStr.split(",");
        for (int i = 0; i < t.length; i++) {
            if (i > 1)
                break;
            result[i] = t[i];
        }
        return result;
    }

    /**
     * 商品列表布局
     *
     * @param shoppingcarlist 购物车列表
     * @return view
     */
    @SuppressWarnings("deprecation")
    private View initProItem(ArrayList<Map<String, Object>> shoppingcarlist) {
        Logger.e("test", "initProItem()--->" + shoppingcarlist.toString());
        String tempcount, yunfei = "0";
        String uri, proName, marketPrice, arriveTime, leaveTime;
        String attrParam = null;
        int song, ji;
        int proCount = 0;
        double proMoney = 0;
        double discountPrice = 0;
        double price = 0;
        Iterator<Map<String, Object>> carListIterator = shoppingcarlist.iterator();
        View v_parent = LayoutInflater.from(context).inflate(R.layout.paycenter_item, null);
        TextView tv_shopName = (TextView) v_parent.findViewById(R.id.tv_payCenter_shopName);
        TextView tv_totalPrice = (TextView) v_parent.findViewById(R.id.tv_payCenter_totalPrice);//商品总价
        TextView tv_yunFei = (TextView) v_parent.findViewById(R.id.tv_payCenter_yunfei);//运费
        LinearLayout ll_itemOne_parent = (LinearLayout) v_parent.findViewById(R.id.ll_payCenter_itemOneParent);
        ll_itemOne_parent.removeAllViews();
//		ll_proParent.removeAllViews();
        while (carListIterator.hasNext()) {
            Map<String, Object> item = carListIterator.next();
            tempcount = item.get("count").toString();
            uri = item.get("imgURL").toString();
            proName = item.get("proName").toString();
            price = Double.parseDouble(item.get("price").toString());
            marketPrice = item.get("marketPrice").toString();
            //后台拿货并且是具有优惠的情况下才会有优惠价
            if (isBackCall) {
                discountCondition = Integer.parseInt(item.get("discountNum").toString());
                discountPrice = Double.parseDouble(item.get("discountPrice").toString());
            }
            arriveTime = item.get("arriveTime").toString();
            leaveTime = item.get("leaveTime").toString();
            yunfei = item.get(KEY_YUNFEI).toString();
            attrParam = item.get(KEY_ATTRS).toString();
            song = (Integer) item.get(KEY_SONG_COUNT);
            ji = (Integer) item.get(KEY_JOIN_COUNT);
            Object shopName = item.get(DBHelper.SHOPCAR_SHOPNAME);
            if (shopName != null) {
                tv_shopName.setText(shopName.toString());
            }
//            计算订单总价
            double tYunFei = 0;
            try {
                int tCount = Integer.parseInt(tempcount);
                if (tCount <= 0 || price < 0) {
                    initErrInfo("下单数据出现异常：" + tempcount + "," + yunfei + "," + price);
                }
                tYunFei = Double.parseDouble(yunfei);
                proMoney += tCount * price + tYunFei;
                proCount += tCount + song;
            } catch (Exception e) {
                initErrInfo("下单数据出现异常：" + tempcount + "," + yunfei + "," + price);
                return null;
            }

            StringBuilder sb = new StringBuilder();
            HashMap<String, String> param = handleParamStr(attrParam);
            if (param != null) {
                Set<String> keys = param.keySet();
                for (String key : keys) {
                    sb.append(key).append(":").append(getAttrIdAndValue(param.get(key))[1]).append("; ");
//                    attrs += key + ":" + getAttrIdAndValue(param.get(key))[1] + "; ";
                }
            }
            String attrs = sb.toString();
            if (tYunFei <= 0) {
                attrs = attrs.length() > 2 ? attrs.substring(0, attrs.length() - 2) : attrs;
            } else {
                attrs += "配送费:" + yunfei;
            }

            View v = LayoutInflater.from(context).inflate(R.layout.activity_my_order_produce_item, null);
            ImageView iv_img = (ImageView) v.findViewById(R.id.iv_order_produce_logo);
            TextView tv_title = (TextView) v.findViewById(R.id.tv_order_produce_title);
            TextView tv_favourable_tip = (TextView) v.findViewById(R.id.tv_item_favourable_tip);//优惠提示----n件更优惠
            TextView tv_dec = (TextView) v.findViewById(R.id.tv_order_produce_dec);
            tv_order_1 = ((TextView) v.findViewById(R.id.tv_order_1));
            tv_order_discountPrice = ((TextView) v.findViewById(R.id.tv_order_discount_price));//优惠价
            tv_order_2 = ((TextView) v.findViewById(R.id.tv_order_2));
            tv_price = (TextView) v.findViewById(R.id.tv_order_produce_price);// 拿货价
            TextView tv_order_proMarketPrice = (TextView) v.findViewById(R.id.tv_order_proMarketPrice);
            tv_order_proMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//给原价加上删除线
            tv_order_proMarketPrice.getPaint().setAntiAlias(true);
            TextView tv_num = (TextView) v.findViewById(R.id.tv_order_produce_num);
            TextView tv_song = (TextView) v.findViewById(R.id.item_joinCount);
            TextView tv_qy_arrTime = (TextView) v.findViewById(R.id.item_arriveTime);
            TextView tv_qy_leaTime = (TextView) v.findViewById(R.id.item_leaveTime);

            tv_yunFei.setText("￥" + CommonTools.formatZero2Str(tYunFei));
            imgLoader.displayImage(uri, iv_img);
            tv_title.setText(proName);
            String priceStr = isCanFav ? "￥" + CommonTools.formatZero2Str(price + discountPrice) : "￥" + CommonTools.formatZero2Str(price);
            tv_price.setText(priceStr);
            tv_order_proMarketPrice.setText("￥" + marketPrice);
            tv_order_discountPrice.setText("￥" + CommonTools.formatZero2Str(price));
            tv_num.setText("x" + tempcount);
            tv_totalPrice.setText("￥" + CommonTools.formatZero2Str(Integer.parseInt(tempcount) * price));//商品总价

            ll_itemOne_parent.addView(v);
            if (ji > 0) {
                tv_song.setVisibility(View.VISIBLE);
                tv_song.setText("买" + ji + "送1");
            }
            if (attrs.length() > 0) {
                tv_dec.setVisibility(View.VISIBLE);
                tv_dec.setText(attrs);
            }

            if (!arriveTime.equals("")) {
                tv_qy_arrTime.setVisibility(View.VISIBLE);
                tv_qy_arrTime.setText("抵达时间:" + arriveTime);
            }
            if (!leaveTime.equals("")) {
                tv_qy_leaTime.setVisibility(View.VISIBLE);
                tv_qy_leaTime.setText("离开时间:" + leaveTime);
            }
            if (isBackCall) {
                //后台拿货显示配送时间
                rg_payment_ship_time.setVisibility(View.VISIBLE);
                if (discountCondition > 0) {
                    tv_favourable_tip.setText(discountCondition + "件更优惠");
                    tv_favourable_tip.setVisibility(View.VISIBLE);
                }
                //如果是后台拿货
                if (isCanFav) {
                    //有优惠
                    tv_order_1.setVisibility(View.VISIBLE);
                    tv_order_1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    tv_order_discountPrice.setVisibility(View.VISIBLE);
                    tv_order_2.setTextColor(getResources().getColor(R.color.gray_text_aaa));
                    tv_order_2.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    tv_price.setTextColor(getResources().getColor(R.color.gray_text_aaa));
                    tv_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//删除线
                    tv_price.getPaint().setAntiAlias(true);
                } else {
                    //无优惠
                    //隐藏优惠价标签
                    tv_order_1.setVisibility(View.GONE);
                    tv_order_discountPrice.setVisibility(View.GONE);
                    //拿货价颜色加深
                    tv_order_2.setTextColor(getResources().getColor(R.color.gray_text));
                    tv_order_2.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    tv_price.setTextColor(getResources().getColor(R.color.gray_text));
                }

            } else {
                //非后台拿货
                //隐藏拿货价标签
                tv_order_2.setVisibility(View.GONE);
                tv_price.setVisibility(View.GONE);
            }
        }

        TextView sumItemMsg = (TextView) v_parent.findViewById(R.id.tv_payCenter_sumMessage);
//        String sumPrice = "共" + proCount + "件商品（含运费）   合计：￥ " + String.format("%.2f", proMoney);
        String sumPrice = "￥ " + String.format("%.2f", proMoney);
        SpannableStringBuilder ssb = new SpannableStringBuilder(sumPrice);
        ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_red)), sumPrice.indexOf("￥"), sumPrice.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sumItemMsg.setText(ssb);
        return v_parent;
    }

    /**
     * 选择支付方式
     */
    private Dialog payWayDialog = null;

    protected void selectPayWay() {
        if (payWayDialog == null) {
            payWayDialog = MyDialog.getDefineDialog(context, R.layout.dialog_payway);
            ViewGroup rg = (ViewGroup) payWayDialog.findViewById(R.id.rg_payWay_group);
            int len = rg.getChildCount();
            if (isQuanYan) {
                payWayDialog.findViewById(R.id.rb_arrivePay).setVisibility(View.VISIBLE);
                payWayDialog.findViewById(R.id.rb_arrivePay_line).setVisibility(View.VISIBLE);
            }
            if (existsAgent) {//美食店铺自营产品
                jsShowMsg(getString(R.string.payWay_selectTag));
            }
            for (int i = 0; i < len; i++) {
                View v = rg.getChildAt(i);
                if (existsAgent) {
                    if (v.getId() == R.id.rb_accountPay) {
                        v.setVisibility(View.VISIBLE);
                    } else {
                        v.setVisibility(View.GONE);
                    }
                }
                if (v instanceof RadioButton && v.getVisibility() == View.VISIBLE) {
                    final RadioButton btn = (RadioButton) v;
                    btn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            payWay = btn.getTag().toString();
                            if (payWay.equals(getString(R.string.payWay_arrivedPay_key)) || isQuanYan) {
                                cb_useJLB.setChecked(false);
                                cb_useJLB.setEnabled(false);
                            } else {
                                cb_useJLB.setEnabled(true);
                            }
                            String select = btn.getText().toString();
                            cb_payWay_tag.setText(select);
                            payWayDialog.dismiss();
                        }
                    });
                }
            }
        }
        payWayDialog.show();
    }

    /**
     * 实例化默认联系方式、地址
     *
     * @param cursor 地址游标
     * @see : 若无默认地址，则取最后一个为收货地址
     */
    private void initDefaultContactMsg(Cursor cursor) {
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int index_name = cursor.getColumnIndex("name");
            int index_mobile = cursor.getColumnIndex("mobile");
            int index_address = cursor.getColumnIndex("adress");
            int index_default_tag = cursor.getColumnIndex("IsDefault");
            recName = cursor.getString(index_name);
            recMobile = cursor.getString(index_mobile);
            recAddress = cursor.getString(index_address);
            String defaultTag = cursor.getString(index_default_tag);
            recMobile = Constants.testedMobile == null ? recMobile : Constants.testedMobile;
//			noAddressView.setVisibility(View.GONE);
            if (defaultTag.equals("1")) {
                break;
            }
        }
    }

    /**
     * 获取购物车列表中醇康商品的总价
     */
    public void initCKProPrice() {
        ckProductMoney = 0;
        for (Map<String, Object> item : shoppingCarList) {
            String agentID = item.get("agentID").toString();
            String SupplierLoginID = item.get("supplierID").toString();
            if ((agentID == null || agentID.equals("")) && (SupplierLoginID == null || SupplierLoginID.equals(""))) {
                double buyPrice = Double.parseDouble(item.get("price").toString());
                double buyCount = Double.parseDouble(item.get("count").toString());
                ckProductMoney += (buyPrice * buyCount);
            }
        }
    }

    public static PaymentCenterHandler handler;

    public void setUseScoreListener() {
        zhekouMoney = ckProductMoney * 0.1; // 抵扣金额为平台商品总价的10%
        zhekouMoney = formatZero2(zhekouMoney);

        cb_useJLB.setChecked(false);
        if (canUseMoney >= zhekouMoney) {
            price.setCanUseJlbMoney(zhekouMoney);
        } else
            price.setCanUseJlbMoney(canUseMoney);

        setCbText();
    }

    private void setCbText() {
        cbText = "金萝卜抵扣(共" + score + "，可抵扣" + price.getFavJlbMoney() + "元)"
                + "用：" + (int) (price.getUseScore() + price.getFavourablePrice() * 100) + "金萝卜";
        cb_useJLB.setText("金萝卜抵扣(共" + score + "，可抵扣" + price.getFavJlbMoney() + "元)\n"
                + "用：" + price.getUseScore() + "金萝卜");
    }

    @Override
    protected void findViewById() {
        cb_useJLB = (CheckBox) findViewById(R.id.cb_payCenter_useJLBtoPay);//金萝卜抵扣
        txt_name = (TextView) findViewById(R.id.tv_payCenter_recName);
        txt_mobile = (TextView) findViewById(R.id.tv_payCenter_recMobile);
        txt_address = (TextView) findViewById(R.id.tv_payCenter_recAddress);
        et_remark = (EditText) findViewById(R.id.et_payCenter_message);
        txt_totalMoney = (TextView) findViewById(R.id.tv_payCenter_allMoney);
        txt_totalFav = (TextView) findViewById(R.id.tv_payCenter_allFav);
        ll_proParent = (LinearLayout) findViewById(R.id.ll_payCenter_proItemParent);
        b_submit = (Button) findViewById(R.id.b_payCenter_submit);//支付按钮
        ll_cb = (LinearLayout) findViewById(R.id.ll_payCenter_bottom);
        fl_payWay_show = (FrameLayout) findViewById(R.id.fl_payWay_show);
        cb_payWay_tag = (CheckBox) findViewById(R.id.cb_payWay_tag);
        rl_fav_ticket = ((RelativeLayout) findViewById(R.id.rl_fav_ticket));//优惠券
        tv_fav_ticket = (TextView) findViewById(R.id.tv_fav_ticket);
        rl_fav_num = (RelativeLayout) findViewById(R.id.rl_fav_num);//优惠码
        tv_fav_num_tips = (TextView) findViewById(R.id.tv_fav_num_tips);
        payWay = getString(R.string.payWay_account_key);
        cb_payWay_tag.setText(getString(R.string.payWay_account_tips));
        rg_payment_ship_time = ((RadioGroup) findViewById(R.id.rg_payment_ship_time));
        rb_payment_ship_now = ((RadioButton) findViewById(R.id.rb_payment_ship_now));
        rb_payment_ship_after = ((RadioButton) findViewById(R.id.rb_payment_ship_after));
        tv_ship_after_tip = ((TextView) findViewById(R.id.tv_ship_after_tip));
        ll_contact = ((LinearLayout) findViewById(R.id.ll_contact));

        ll_stock_protocol = $(R.id.ll_stock_protocol);
        tv_stock_protocol = $(R.id.tv_stock_protocol);
        tv_stock_protocol.setOnClickListener(this);

        rg_payment_ship_time.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_payment_ship_now:
                        tv_ship_after_tip.setVisibility(View.GONE);
                        ll_contact.setVisibility(View.VISIBLE);
                        orderType = ORDER_TYPE_BACK_CALL;
                        ll_stock_protocol.setVisibility(View.GONE);
                        isStock = false;
                        break;
                    case R.id.rb_payment_ship_after:
                        ll_contact.setVisibility(View.GONE);
                        tv_ship_after_tip.setVisibility(View.VISIBLE);
                        orderType = ORDER_TYPE_STOCK;
                        ll_stock_protocol.setVisibility(View.VISIBLE);
                        isStock = true;
                        break;
                }
            }
        });

        initFavNumDialog();
    }

    private Dialog favNumDialog;
    private String favNumGuids;

    private void initFavNumDialog() {
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_fav_num, null);
        favNumDialog = MyDialog.getDefineDialog(context, v);
        final EditText etFavNum = (EditText) v.findViewById(R.id.et_fav_num);
        final Button sure = (Button) v.findViewById(R.id.b_dialog_left);
        sure.setOnClickListener(new View.OnClickListener() {
            boolean isClick = false;
            String info;

            public void onClick(View v) {
                final String codenumber = etFavNum.getText().toString();
//				final String codenumber = "4988472921";
                if (!isClick) {
                    isClick = true;
                    if (TextUtils.isEmpty(codenumber) && !TextUtils.isEmpty(tv_fav_num_tips.getText().toString())) {
                        price.setFavNumMoney(0);
                        setTotalMoneyText();
                        tv_fav_num_tips.setText(codenumber);
                        favNumDialog.dismiss();
                        isClick = false;
                        return;
                    }
                    jsShowMsg("正在检测验证码");
                    new Thread() {
                        public void run() {
                            String state = appManager.postCheckFavNum(codenumber, favNumGuids);
                            try {
                                JSONObject obj = new JSONObject(state);
                                info = obj.getString("info");
                                String favNumMoney = obj.getString("result");
                                price.setFavNumMoney(new BigDecimal(favNumMoney).doubleValue());
                            } catch (JSONException | NumberFormatException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    String num = price.getFavNumMoney() > 0 ? codenumber : "";
                                    setTotalMoneyText();
                                    tv_fav_num_tips.setText(num);
                                    favNumDialog.dismiss();
                                    if (!TextUtils.isEmpty(info))
                                        jsShowMsg(info);
                                    isClick = false;
                                }
                            });
                        }
                    }.start();
                }
            }
        });
        v.findViewById(R.id.b_dialog_right).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                favNumDialog.dismiss();
            }
        });
    }

    private Dialog favTicketDialog;

    public void showFavTicketDialog() {
        if (favTicketDialog != null) {
            if (price.getFavNumMoney() > 0) {
                jsShowMsg("优惠码不能与优惠券同用");
                return;
            }
            favTicketDialog.show();
        }
    }

    FavTicket selectedTicket;

    /**
     * 使用优惠券
     */
    @SuppressWarnings("deprecation")
//    public void setFavTicketDialog() {
//        if (favTickets == null || favTickets.size() <= 0)
//            return;
//        LayoutInflater lif = LayoutInflater.from(context);
//        View v = lif.inflate(R.layout.dialog_fav_ticket, null);
//        favTicketDialog = MyDialog.getDefineDialog(context, v);
//        favTicketDialog.setCanceledOnTouchOutside(true);
//        RadioGroup rg = (RadioGroup) v.findViewById(R.id.rg_fav_tickets);
//        rg.removeAllViews();
//        int id = 0;
//        RadioButton rb;
//        int size = favTickets.size();
//        for (int i = 0; i < size; i++) {
//            FavTicket item = favTickets.get(i);
//            rb = (RadioButton) lif.inflate(R.layout.item_radiobtn_favticket, null);
//            rb.setId(id++);
//            rb.setText(item.getName());
//            rg.addView(rb);
//            if (i == size - 1) {
//                rb.setBackgroundDrawable(null);
//            }
//        }
//        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//
//                if (checkedId >= 0 && checkedId < favTickets.size()) {
//                    selectedTicket = favTickets.get(checkedId);
//                    price.setFavTicket(Double.parseDouble(selectedTicket.getMoney()));
//                    tv_fav_ticket.setText(selectedTicket.getName());
//                    if (cb_useJLB.isChecked() && selectedTicket.getWithOthers().equals("0")) {
//                        cb_useJLB.setChecked(false);
//
//                    }
//                }
//                setTotalMoneyText();
//            }
//        });
//        ((RadioButton) rg.getChildAt(0)).setChecked(true);
//    }

    public Runnable scoreRunnable = new Runnable() {
        public void run() {
            String uid = getUid();
            String useSco = String.valueOf(price.getUseScore());
            AppManager.getInstance().updateUserScore("0084", uid, useSco);
        }
    };

    public void showErrorDialog(String title, String msg) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
                .setPositiveButton("确定", null).show();
    }

    public ArrayList<Map<String, Object>> shoppingCarList;

    protected void initView() {
        initListener();
        BindAddress();

        // 含运费总价
        intent_allMoney = price.getTotalPrice();
        Logger.i("保留两位小数后的总价", intent_allMoney + "");

        setTotalMoneyText();

        checkQuanYan();
//        setFavTicketDialog();

        Set<String> keys = shopGroup.keySet();
        ll_proParent.removeAllViews();
        for (String key : keys) {
            View v = initProItem(shopGroup.get(key));
            if (v != null) {
                ll_proParent.addView(v);
            }
        }
        //是否是后台拿货，是则隐藏优惠券、优惠码、金萝卜抵扣
        if (!isBackCall) {
            rl_fav_ticket.setVisibility(View.VISIBLE);
            rl_fav_num.setVisibility(View.VISIBLE);
//            cb_useJLB.setVisibility(View.VISIBLE);
            //隐藏拿货价标签
            tv_order_2.setVisibility(View.GONE);
            tv_price.setVisibility(View.GONE);
        }
        hideLoadDialog();
    }

    private void initListener() {
        if (isBackCall) {
            cb_useJLB.setVisibility(View.GONE);
        } else {
            cb_useJLB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                    if (selectedTicket != null && selectedTicket.getWithOthers().equals("1")) {
                        price.setUseJLB(false);
                        cb_useJLB.setChecked(false);
                        jsShowMsg("金萝卜不能与此优惠券同用");
                    } else {
                        price.setUseJLB(arg1);
                    }
                    setTotalMoneyText();
                    setCbText();
                }
            });
        }

        cb_payWay_tag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectPayWay();
            }
        });

        setTicketDKListener();
        setUseScoreListener();

        b_submit.setOnClickListener(this);
        rl_fav_num.setOnClickListener(this);
        fl_payWay_show.setOnClickListener(this);
        findViewById(R.id.ib_toSelectAddress).setOnClickListener(this);
        ll_contact.setOnClickListener(this);
        findViewById(R.id.ll_address).setOnClickListener(this);
        tv_fav_ticket.setOnClickListener(this);
    }

    /**
     * 设置支付总价
     */
    private void setTotalMoneyText() {
        double favourablePrice = 0;
        try {
            favourablePrice = price.getPriceSum() - price.getHandleSum();
        } catch (Exception e) {
            e.printStackTrace();
        }
        txt_totalMoney.setText("￥" + price.getTotalPrice());
        if (favourablePrice > 0) {
            txt_totalFav.setVisibility(View.VISIBLE);
            txt_totalFav.setText("(已优惠" + formatZero2Str(favourablePrice) + "元)");
        } else {
            txt_totalFav.setVisibility(View.GONE);
        }
    }

    private void setTicketDKListener() {
        ll_cb.removeAllViews();
        for (final HashMap<String, Object> item : cashTicketList) {
            CheckBox cb = (CheckBox) item.get(KEY_ticketCbox);
            cb.setChecked(false);
            ll_cb.addView(cb);
            try {
                final Integer money = Integer.parseInt(item.get(KEY_ticketMoney).toString());
                cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                        double ticketDikou = (Double) item.get(KEY_ticketDikou);
                        if (arg1) {
                            price.setFavCashTicket(money);
                            ticketDikou = price.getFavCashTicket();
                            setTotalMoneyText();
                        } else {
                            price.setFavCashTicket(0);
                            setTotalMoneyText();
                        }
                        item.put(KEY_ticketDikou, ticketDikou);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkErrorMsg() {
        if (!isDispatch) {
            showErrorDialog("提示", "订单信息中包含物流不能送达的地区,请修改个人中心的默认收货地址！");
            return true;
        }
        if (recName == null || recAddress == null || recMobile == null) {
            CommonTools.showShortToast(context, "请先填写收货人信息");
            return true;
        }
        return false;
    }

    class closePopup implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (_PopupWindow != null && _PopupWindow.isShowing()) {
                _PopupWindow.dismiss();
            }
        }
    }

    /**
     * 绑定地址
     */
    public static final int REQ_ADDRESS = 2;

    private void BindAddress() {
        if (isQuanYan) {
            initAddress("", "", "");
            txt_name.setText(recName);
            txt_mobile.setText(recName);
            txt_address.setText(getString(R.string.tips_o2o));
            txt_address.setTextColor(Color.RED);
            txt_name.setEnabled(true);
            txt_mobile.setEnabled(true);

        } else {
            if (!TextUtils.isEmpty(recName) && !TextUtils.isEmpty(recMobile)) {
                txt_name.setText(recName);
                txt_mobile.setText(recMobile);
                txt_address.setText(recAddress);

            } else {
                // 跳转到选择地址的页面
                initErrInfo("请在顶栏填写正确的收货人信息");
            }
            txt_name.setEnabled(false);
            txt_mobile.setEnabled(false);
        }
    }

    private void initAddress(String recName, String recMobile, String recAddress) {
        this.recName = recName;
        this.recMobile = recMobile;
        this.recAddress = recAddress;
    }

    /**
     * 普通后台拿货以及一般商品结算post
     * 购物车订单列表中全部信息拼凑的json
     *
     * @param carListIterator 购物车迭代器（游标）
     * @see ：只能在子线程中运行
     */
    public String postResult(Iterator<Map<String, Object>> carListIterator) {
        String productID = "";
        String shopID = "", agentID = "";
        body = "";
        String productString = "{'productInfo':[{";

        String buyCount = "1";
        while (carListIterator.hasNext()) {
            Map<String, Object> item = carListIterator.next();
            agentID = item.get("agentID").toString();
            productID = item.get("productID").toString();
            String proName = item.get("proName").toString();
            String price = item.get("price").toString();
            buyCount = item.get("count").toString();
            shopID = item.get("shopID").toString();
            String arriveTime = item.get("arriveTime").toString();
            String leaveTime = item.get("leaveTime").toString();
            String attrParam = item.get(KEY_ATTRS).toString();
            String attrs = "";
            HashMap<String, String> param = handleParamStr(attrParam);

            if (param != null) {
                Set<String> keys = param.keySet();
                String ids = "";
                String names = "";
                for (String key : keys) {
                    String[] idValue = getAttrIdAndValue(param.get(key));
                    ids += idValue[0] + ",";
                    names += idValue[1] + ",";
                }
                attrs = ids.length() > 0 ? ids + ";" + names : "";
            }

            try {
                int song = (Integer) item.get(KEY_SONG_COUNT);
                int tCount = Integer.parseInt(buyCount) + song;
                buyCount = String.valueOf(tCount);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }

            body += proName + ",";
            productString += "'productID':'" + productID + "','arriveTime':'"
                    + arriveTime + "','leaveTime':'" + leaveTime
                    + "','price':'" + price + "','num':'" + buyCount
                    + "','shopID':'" + shopID + "','attribute':'" + attrs + "'},{";
        }
        productString += "}],";
        String infoString = getInfoStr(agentID);
        String postString = (productString + infoString).replace(",{}", "");

        String postResult = null;
        if (!WapActivity.isRecorded && WapActivity.is1YFQ) {
            // 一元抢购订单
            postResult = AppManager.getInstance().postYiYuanOrder("0096", postString, uid, productID);

        } else {
            // 普通订单
            String useTickets = "";
            try {
                useTickets = useCashTicket(cashTicketList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tag_cash = cbText + "; " + useTickets;
            postResult = appManager.postOrder(getIMEI(), getMac(), getDeviceID(), postString, tag_cash, isBackCall);
        }

        return postResult;
    }

    /**
     * 囤货功能post
     *
     * @param carListIterator
     * @return
     */
    public String postStockResult(Iterator<Map<String, Object>> carListIterator) {
        String productID = "";
        String proName = "";
        String price = "";

        double tempPrice = 0;

        body = "";
        int buyCount = 0;

        while (carListIterator.hasNext()) {
            Map<String, Object> item = carListIterator.next();
            productID = item.get("productID").toString();
            proName = item.get("proName").toString();
            price = item.get("price").toString();
            buyCount = Integer.parseInt(item.get("count").toString());
            //计算订单总价
            tempPrice = buyCount * (Float.parseFloat(price));

//            try {
//                int song = (Integer) item.get(KEY_SONG_COUNT);
//                int tCount = Integer.parseInt(buyCount) + song;
//                buyCount = String.valueOf(tCount);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return "";
//            }
            body += proName + ",";
        }
        //0324接口

        String totalPrice = CommonTools.formatZero2Str(tempPrice);
        return appManager.postStockOrder(getUid(), productID, String.valueOf(buyCount), payWay, Constants.deviceType, myremark, getVersionCode() + "", totalPrice);
    }

    /**
     * 使用代金券
     *
     * @param cashTicketList 代金券
     * @return 结果
     * @throws Exception 抛出异常
     */
    public String useCashTicket(ArrayList<HashMap<String, Object>> cashTicketList) throws Exception {
        String ticketNumbers = "";
        int size = cashTicketList.size();
        String result;
        boolean isSuccess;
        String info;
        StringBuilder sb = new StringBuilder();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                HashMap<String, Object> item = cashTicketList.get(i);
                CheckBox cb_item = (CheckBox) item.get(KEY_ticketCbox);
                if (cb_item.isChecked()) {
                    isCanUseCashTicket = true;
                    ticketNumbers += "'" + item.get(KEY_ticketNumber).toString() + "',";
                    sb.append(cb_item.getText().toString()).append(", ");
                }
            }
            if (isCanUseCashTicket) {
                ticketNumbers = ticketNumbers.substring(0, ticketNumbers.length() - 1);
                result = appManager.useTickets(uid, ticketNumbers);
                JSONObject obj = new JSONObject(result);
                isSuccess = Boolean.parseBoolean(obj.getString(KEY_RESULT).toLowerCase());
                info = obj.getString(KEY_RESULT_INFO);
                if (!isSuccess) {
                    price.setFavCashTicket(0);
                    if (!TextUtils.isEmpty(info))
                        Logger.e(TAG, "PaymentCenterActivity useCashTicket info=" + info);
                    CommonTools.showShortToast(context, info);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 判断库存
     *
     * @return 错误信息
     */
    public String judgeStock(String productID, String proName, String buyCount, String arriveTime) {
        String result = "";
        try {
            String rCount = appManager.postGetProductCount("0086", productID, arriveTime);
            int sumCount = Integer.parseInt(rCount);
            int count = Integer.parseInt(buyCount);
            if (count > sumCount) {
                result = proName + " 超出库存：" + (count - sumCount) + "件\n";
            }
        } catch (Exception e) {
            result = e.toString() + "\n";
        }
        return result;
    }

    /**
     * @return 错误信息
     */
    public String judgeStockAndUpdateAttrPrice(Map<String, Object> proItem, String proGuid, String proName, String attrNames, String attrGuids, String buyCount) {
        String result = "";
        try {
            String json = appManager.postGetProAttrCount(proGuid, attrNames, attrGuids);
            JSONObject item = new JSONObject(json).getJSONArray("val").getJSONObject(0);
            int sumCount = Integer.parseInt(item.getString("AttributeCount"));
            int count = Integer.parseInt(buyCount);
            if (count > sumCount) {
                result = proName + " 超出库存：" + (count - sumCount) + "件\n";
            }

            String attrPrice = item.getString("AttributePrice");
            proItem.put(KEY_price, attrPrice);
        } catch (Exception e) {
            result = "商品属性值有误，请联系管理人员\n";
        }

        return result;
    }

    /**
     * 订单基本信息json
     */
    public String getInfoStr(String agentID) {
        String ticketGuid = selectedTicket == null ? "" : selectedTicket.getGuid();
        return "'info':[{'name':'" + recName + "','address':'"
                + recAddress + "','mobile':'" + recMobile + "','uid':'" + uid
                + "','payMoney':'" + price.getTotalPrice() + "','ivTitle':'" + ivTitle
                + "','ivContent':'" + ivContent + "','remark':'" + myremark
                + "','agentID':'" + agentID + "','yunfei':'" + allYunFei
                + "','mobilePayWay':'" + payWay + "','deviceType':'"
                + Constants.deviceType + "','appVersion':'"
                + getVersionCode() + "','ticketGuid':'" + ticketGuid
                + "','codeNumber':'" + tv_fav_num_tips.getText() + "','codeMoney':'" + price.getFavNumMoney() + "'}]}";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_payCenter_submit:
                if (isQuanYan) {
                    String recName = txt_name.getText().toString();
                    String recMobile = txt_mobile.getText().toString();
                    recName = recName.replaceAll("\n|\r| ", "");
                    String recAddress = "";
                    if (TextUtils.isEmpty(recName) || TextUtils.isEmpty(recMobile)) {
                        initErrInfo("请在顶栏填写正确的收货人信息");
                    } else {
                        boolean isMobile = CommonTools.isMobileNO(recMobile);
                        if (!isMobile) {
                            initErrInfo("请输入正确的手机号码");
                        } else {
                            initErrInfo(null);
                            initAddress(recName, recMobile, recAddress);
                        }
                    }
                }
//                后台拿货但是未选择发货时间时提示
                if (isBackCall) {
                    if (!(rb_payment_ship_now.isChecked() || rb_payment_ship_after.isChecked())) {
                        CommonTools.showShortToast(context, "请选择发货时间");
                        return;
                    }
                    submitOrder();
                } else {
                    submitOrder();
                }
                break;

            case R.id.rl_fav_num:
                if (price.getFavTicket() > 0) {
                    jsShowMsg("优惠码不能与优惠券同用");
                    return;
                }
                favNumDialog.show();
                break;
            case R.id.fl_payWay_show:
                cb_payWay_tag.setChecked(!cb_payWay_tag.isChecked());
                break;

            case R.id.ib_toSelectAddress:
            case R.id.ll_contact:
            case R.id.ll_address:
                toAddressActivity(v);
                break;

            case R.id.tv_fav_ticket:
//                showFavTicketDialog();
                /**
                 * 2018年7月11日 修改为新的优惠券页面
                 */
//                jsStartActivityForResult("ChooseCouponActivity", "couponRecordGuid|" + (tv_fav_ticket.getTag() == null ? "" : tv_fav_ticket.getTag().toString()) + ",;shopid|" + shopId.substring(0, shopId.length() - 1) + ",;productGuid|" + productGuid.substring(0, productGuid.length() -1) + ",;totalPrice|" + price.getTotalPrice(), REQUEST_CHOOSE_COUPON);
                try {
                    JSONArray paramJSONArray = new JSONArray();
                    for (String s : paramMap.keySet()) {
                        JSONObject arrayItemJSONObject = new JSONObject();
                        arrayItemJSONObject.put("shopId", s);
                        JSONArray goodsArray = new JSONArray();
                        List<String> list = paramMap.get(s);
                        for (String s1 : list) {
                            goodsArray.put(s1);
                        }
                        arrayItemJSONObject.put("productGuids", goodsArray);
                        paramJSONArray.put(arrayItemJSONObject);
                    }
                    Logger.e(TAG, "拼接的字符串" + paramJSONArray.toString());
                    jsStartActivityForResult("ChooseCouponActivity", "couponRecordGuid|" + (tv_fav_ticket.getTag() == null ? "" : tv_fav_ticket.getTag().toString()) + ",;shopid|" + getShopId() + ",;multiParam|" + paramJSONArray.toString() + ",;totalPrice|" + price.getTotalPrice(), REQUEST_CHOOSE_COUPON);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_stock_protocol:
                Intent intent = new Intent(context, HtmlActivityNew.class);
                intent.putExtra("url", Constants.DANERTU_STOCK_PROTOCOL);
                startActivity(intent);
                break;
        }
    }

    public void alipaySuccess(String code, Result result) {
        if (TextUtils.equals(code, "9000")) {
//			tupdate.start();//支付宝支付成功后需要更新订单状态
            if (isStock) {
                toWarehouse();
                finish();
            } else {
                toOrderComplete(outOrderNumber, getString(R.string.payWay_alipay_tips), price.getTotalPrice(), true);
            }
            jsShowMsg("支付成功");
        } else {
            if (isStock) {
                // 判断resultStatus 为非“9000”则代表可能支付失败
                // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                if (TextUtils.equals(code, "8000")) {
                    jsShowMsg("支付结果确认中");

                } else {
                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                    jsShowMsg("支付失败");
                }
                toWarehouse();
                finish();
            } else {
                // 判断resultStatus 为非“9000”则代表可能支付失败
                // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                if (TextUtils.equals(code, "8000")) {
                    jsShowMsg("支付结果确认中");

                } else {
                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                    jsShowMsg("订单已生成，请到订单中心支付");
                }
                toOrderComplete(outOrderNumber, getString(R.string.payWay_alipay_tips), price.getTotalPrice(), false);
            }
        }
    }

    private void submitOrder() {
        if (isLogined() && !checkErrorMsg() && !isLoading()) {
            if (isCanSubmit) {
                showLoadDialog();
                myremark = et_remark.getText().toString();
                new Thread(rToPay).start();
            } else {
                jsShowMsg(errText);
            }
        }

    }

    /**
     * 支付操作
     */
    public final static String KEY_RESULT_INFO = "info";
    public final static String KEY_RESULT = "result";
    public final static String TAG_RESULT_FAIL = "false";
    public final static String TAG_RESULT_SUCCESS = "true";
    Runnable rToPay = new Runnable() {
        public void run() {
            // 耗时操作
            Iterator<Map<String, Object>> carListIterator = shoppingCarList.iterator();
            if (!carListIterator.hasNext()) {
                sendEmptyMessage(PaymentCenterHandler.WHAT_ORDER_ALREADY_SUBMIT);
                return;
            }

            postOrderMsg(carListIterator);
        }

        private void postOrderMsg(Iterator<Map<String, Object>> carListIterator) {
            String s = "";
            String info = "";
            try {
                String json = "";
                if (isStock) {
                    json = postStockResult(carListIterator);
                } else {
                    json = postResult(carListIterator);
                }
                Logger.e("test", "rToPay--->postOrderMsg--->json=" + json);
                JSONObject jsonObject = new JSONObject(json);
                info = jsonObject.getString(KEY_RESULT_INFO);
                s = jsonObject.getString(KEY_RESULT);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Message msg = new Message();
            if (s.equals(TAG_RESULT_FAIL)) {
                isInserted = false;
                msg.obj = info;
                msg.what = PaymentCenterHandler.WHAT_ORDER_FAIL;
                sendMessage(msg);
            } else {
                if (info.replaceAll(" ", "").length() <= 0) {
                    msg.what = PaymentCenterHandler.WHAT_ORDER_FAIL;
                    msg.obj = "返回的订单号为：" + info;
                    sendMessage(msg);
                } else {
                    outOrderNumber = info;
                    msg.obj = outOrderNumber;
                    removeItem();
                    msg.what = PaymentCenterHandler.WHAT_ORDER_SUCCESS;
                    sendMessage(msg);
                }
            }
        }
    };

    /**
     * 提交订单成功，删除购物车对应的商品
     */
    private void removeItem() {
        // 订单提交成功后删除购车表数据
        // db.delShopCar(context, uid);
        Iterator<Map<String, Object>> iterator = shoppingCarList.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> item = iterator.next();
            String guid = item.get("productID").toString();
            Object param = item.get(DBHelper.SHOPCAR_ATTRJSON);
            String shopid = item.get(DBHelper.SHOPCAR_SHOPID).toString();
            String attrParam = param == null ? null : param.toString();
            db.delProductInCar(getContext(), guid, uid, attrParam, shopid);
            iterator.remove();
        }
    }

    public void onResume() {
        super.onResume();
        uid = getUid();
    }

    public void accountToPay(final String outOrderNumber) {
        dialog_psw.show();
    }

    public void alipayToPay(final String outOrderNumber) {
        new Thread(payRunnable).start();
    }

    private WXPay wxPay;
    private StockWXPay stockWXPay;

    /**
     * 微信支付
     *
     * @param outOrderNumber
     */
    public void wechatToPay(String outOrderNumber) {
        if (isStock) {
            if (stockWXPay == null) {
                stockWXPay = new StockWXPay(context);
            }
            stockWXPay.toPay(body.substring(0, body.length() - 1), outOrderNumber, price.getTotalPrice(), orderType);
        } else {
            if (wxPay == null) {
                wxPay = new WXPay(context);
            }
            wxPay.toPay(body.substring(0, body.length() - 1), outOrderNumber, price.getTotalPrice());
        }
    }

    /**
     * 支付宝支付
     */
    Runnable payRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                subject = body.substring(0, body.length() - 1);
                String orderInfo = "";
                if (isStock) {
                    orderInfo = new StockAlipayUtil(getContext()).getSignPayOrderInfo(outOrderNumber, subject, body, price.getTotalPrice(), orderType);
                } else {
                    orderInfo = new AlipayUtil(getContext()).getSignPayOrderInfo(outOrderNumber, subject, body, price.getTotalPrice(), orderType);
                }
//                String orderInfo = new AlipayUtil(getContext()).getSignPayOrderInfo(outOrderNumber, subject, body, "0.01", orderType);
                PayTask aliPay = new PayTask(PaymentCenterActivity.this);
                String result = aliPay.pay(orderInfo);
                Logger.i("alipayResult", result);
                sendMessage(getMessage(RQF_PAY, result));
            } catch (Exception e) {
                e.printStackTrace();
                jsShowMsg("支付宝获取参数错误");
            }
        }
    };

    public void toOrderComplete(String orderNum, String payWayTag, String totalPrice, boolean isPayed) {
        Constants.clearActData();
        boolean isBooking = false;
        Intent intent1 = new Intent(context, MyOrderCompleteActivity.class);
        intent1.putExtra(MyOrderCompleteActivity.KEY_ORDER_NUMBER, orderNum);
        intent1.putExtra(MyOrderCompleteActivity.KEY_ORDER_PAYWAY, payWayTag);
        intent1.putExtra(MyOrderCompleteActivity.KEY_ORDER_PRICE, totalPrice);
        intent1.putExtra(MyOrderCompleteActivity.KEY_ORDER_ISPAYED, isPayed);
        intent1.putExtra(MyOrderCompleteActivity.KEY_ORDER_BOOKING, isBooking);
        startActivity(intent1);
        finish();
    }


    ArrayList<HashMap<String, Object>> cashTicketList = null;
    final String KEY_isQuanYanProduct = "isQuanYanProduct";
    final String KEY_supplierID = "supplierID";
    final String KEY_selected = "selected";
    final String KEY_proName = "proName";
    final String KEY_price = "price";
    final String KEY_count = "count";
    final String KEY_agentID = "agentID";
    final String KEY_shopID = "shopID";
    final String KEY_arriveTime = "arriveTime";
    final String KEY_leaveTime = "leaveTime";
    final String KEY_imgURL = "imgURL";
    final String KEY_productID = "productID";
    final String KEY_isQuanYanHotel = "isQuanYanHotel";

    final String KEY_ticketNumber = "ticketNumber";
    final String KEY_ticketMoney = "ticketMoney";
    final String KEY_ticketDikou = "ticketDikou";
    final String KEY_ticketCbox = "ticketCbox";

    private LinkedHashMap<String, ArrayList<Map<String, Object>>> shopGroup;

    private class RGetData implements Runnable {
        ArrayList<Map<String, Object>> shoppingcarlist = null;
        private int carCount;//表示有多少种商品

        private RGetData(ArrayList<Map<String, Object>> shoppingcarlist) {
            this.shoppingcarlist = shoppingcarlist;
            cashTicketList = new ArrayList<>();
            carCount = shoppingcarlist.size();
//            favTickets = new ArrayList<>();
            shopGroup = new LinkedHashMap<>();
        }

        public void run() {
            allYunFei = 0;
            existsAgent = false;
            Iterator<Map<String, Object>> carListIterator = shoppingcarlist.iterator();
            double tPriceSum = 0;
            String info = "";
            String shopid = "";
            String yunfei;
            String supid;
            String area;
            String productID;
            String agentID;
            String tempcount;
            String proName;
            String createUser;
            String arriveTime;
            String attrParam;
            boolean isQYPro;
            boolean isQYHotel;
            String favourableParam = "";//满减参数
            pricedata = "";
            String discountNum = "";
            double discountPrice = 0;
            int qyCount = 0;
//            favTickets.clear();
            favNumGuids = "";

            StringBuilder builder = new StringBuilder();

            while (carListIterator.hasNext()) {
                Map<String, Object> item = carListIterator.next();
                yunfei = "0";
                supid = item.get("supplierID").toString();
                area = recAddress;
                productID = item.get("productID").toString();


                builder.append(productID).append(",");
                agentID = item.get("agentID").toString();
                tempcount = item.get("count").toString();
                arriveTime = item.get(KEY_arriveTime).toString();
                proName = item.get(KEY_proName).toString();
                isQYPro = (Boolean) item.get(CartActivity.k_isQuanYanProduct);
                isQYHotel = (Boolean) item.get(CartActivity.k_isQuanYanHotel);
                attrParam = item.get(KEY_ATTRS).toString();
                createUser = item.get(KEY_CREATE_USER).toString();
                shopid = item.get(KEY_shopID).toString();

//                shopId+=shopid;
//                productGuid+=productID;

                List<String> list = paramMap.get(shopid);
                if (list == null) {
                    list = new ArrayList<>();
                    paramMap.put(shopid, list);
                }
                if (!list.contains(productID)) {
                    list.add(productID);
                }

                if (isBackCall) {
                    discountNum = item.get("discountNum").toString();
                    discountCondition = Integer.parseInt(discountNum);
                    discountPrice = Double.parseDouble(item.get("discountPrice").toString());
                    //是否具有优惠
                    isCanFav = Integer.parseInt(tempcount) >= discountCondition && Integer.parseInt(discountNum) > 0;
                }

                ArrayList<Map<String, Object>> item_shopGroup_value = shopGroup.get(shopid);
                if (item_shopGroup_value == null) {
                    item_shopGroup_value = new ArrayList<>();
                }
                item_shopGroup_value.add(item);
                shopGroup.put(shopid, item_shopGroup_value);
                if (isQYPro || isQYHotel) {
                    qyCount++;
                }
                HashMap<String, String> param = handleParamStr(attrParam);
                String attrGuids = "";
                String attrNames = "";
                if (param != null) {
                    Set<String> keys = param.keySet();
                    for (String key : keys) {
                        String[] idValue = getAttrIdAndValue(param.get(key));
                        attrGuids += idValue[0] + ",";
                        attrNames += idValue[1] + ",";
                    }
                }
                if (attrGuids.length() > 0) {
                    info += judgeStockAndUpdateAttrPrice(item, productID, proName, attrNames, attrGuids, tempcount);
                } else {
                    info += judgeStock(productID, proName, tempcount, arriveTime);
                }
                if (!agentID.equals("")) {
                    existsAgent = true; // 订单商品中包含自营店商品
                }
                if (!supid.equals("") && !supid.equals("shopnum1")) {
                    yunfei = AppManager.getInstance().getYunFei("0088", supid, area, productID, tempcount);
//                    Logger.e("test","运费="+yunfei);
                    try {
                        double tYunFei = Double.parseDouble(yunfei);
                        if (tYunFei < 0) {
                            isDispatch = false;
                        }
                        allYunFei += tYunFei;
                    } catch (Exception e) {
                        e.printStackTrace();
                        isDispatch = false;
                    }
                }
                if (productID.equalsIgnoreCase("632095CB-A15E-44A1-9B27-F59709F525A0") && carCount == 1) {//特殊商品
                    yunfei = "10";
                }

                int song = 0;
                int ji = 0;

                try {
                    String joinInfo = AppManager.getInstance().getPJoinedInfo("0082", productID);
                    int count = Integer.parseInt(tempcount);
                    ji = Integer.parseInt(joinInfo);
                    if (ji > 0)
                        song = count / ji;
                } catch (Exception e) {
                    e.printStackTrace();
                }


                String price = item.get(KEY_price).toString();
                double singleItemPrice = 0;
                try {
                    double itemPrice = Double.parseDouble(price);
                    int itemCount = Integer.parseInt(tempcount);
                    favourableParam += getFavourableParamItem(productID, itemPrice, itemCount);
                    singleItemPrice = itemPrice * itemCount;
                    tPriceSum += singleItemPrice;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                createUser = TextUtils.isEmpty(createUser) ? getData("apiid|0134,;proguid|" + productID) : createUser;
                pricedata += createUser + "," + singleItemPrice + "|";

                item.put(KEY_CREATE_USER, createUser);
                item.put(KEY_YUNFEI, yunfei);
                item.put(KEY_SONG_COUNT, song);
                item.put(KEY_JOIN_COUNT, ji);

                //获取可用的优惠券
//                List<FavTicket> listFavTickets = getFavTickets(productID);
//                if (listFavTickets != null && listFavTickets.size() > 0) {
//                    for (FavTicket itemFavTicket : listFavTickets) {
//                        favTickets.add(itemFavTicket);
//                    }
//                }

                favNumGuids += productID + ",";
            }


            //向服务器请求当前订单的商品是否可使用金萝卜
            Message message = newHandler.obtainMessage();
            message.what = WHAT_START_CAN_USR_JLB;
            message.obj = builder.toString().substring(0, builder.toString().length() - 1);
            newHandler.sendMessage(message);

            favNumGuids = favNumGuids.substring(0, favNumGuids.length() - 1);

//            if (favTickets.size() > 0) {
//                favTickets.add(new FavTicket("不使用优惠券", "", "0", "1"));
//            }
            if (qyCount == carCount) {
                isQuanYan = true;
            }

            if (pricedata.length() > 2) {
                pricedata = pricedata.substring(0, pricedata.length() - 1);
            }
            double favourablePrice = 0;

            try {
                tPriceSum += allYunFei;
                price.setPriceSum(Double.parseDouble(formatZero2Str(tPriceSum)));
                favourablePrice = Double.parseDouble(getData("apiid|0176,;strproductlist|" + favourableParam));
            } catch (Exception e) {
                e.printStackTrace();
            }
            price.setFavourablePrice(favourablePrice);

            if (info.length() > 0) {
                Logger.e("test", "PaymentCenterActivity run info=" + info);
                CommonTools.showShortToast(getContext(), info);
                finish();
                return;
            }

            if (carCount == 1) {
                initCashTickets(shopid);
            }

            initJLB();

            int what = PaymentCenterHandler.WHAT_GETYUNFEI_SUCCESS;
            sendEmptyMessage(what);
        }

        /**
         * 获取可使用的优惠券  --0294
         *
         * @param guid
         * @return
         */
        private List<FavTicket> getFavTickets(String guid) {
            List<FavTicket> list = null;
            try {
                String tickets = appManager.getFavTickets(guid, uid, intent_allMoney);
//				String tickets = "[{\"guid\":\"ade4b0f2-7b9b-4a76-87bd-f4ba6faafaea\",\"name\":\"1\",\"money\":\"1\",\"withOthers\":\"0\"}]";
                JSONArray arr = new JSONArray(tickets);
                int len = arr.length();
                list = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String name = obj.getString("name");
                    String tguid = obj.getString("guid");
                    String money = obj.getString("money");
                    String withOthers = obj.getString("withOthers");
                    FavTicket item = new FavTicket(name, tguid, money, withOthers);
                    list.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

        /**
         * 代金券
         *
         * @param shopid
         */
        private void initCashTickets(String shopid) {
            isCanUseCashTicket = true;
            String cashTickets;
            try {
                cashTickets = appManager.getCashPaper(shopid, uid);
                JSONArray arr = new JSONObject(cashTickets).getJSONArray("ticketList");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject item = arr.getJSONObject(i);
                    HashMap<String, Object> one = new HashMap<>();
                    String money = item.getString(KEY_ticketMoney);
                    one.put(KEY_ticketNumber, item.getString(KEY_ticketNumber));
                    one.put(KEY_ticketMoney, money);
                    CheckBox cb = new CheckBox(context);
                    cb.setText("代金券抵扣：" + money);
                    cb.setTextColor(Color.BLACK);
                    one.put(KEY_ticketCbox, cb);
                    one.put(KEY_ticketDikou, 0.0);
                    cashTicketList.add(one);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 金萝卜
         */
        private void initJLB() {
            try {
                score = AppManager.getInstance().postGetMemberScore("0085", uid);
                canUseMoney = Double.parseDouble(score) / 100; // 总积分可抵扣的金额
                canUseMoney = formatZero2(canUseMoney);
            } catch (Exception e) {
                sendEmptyMessage(PaymentCenterHandler.WHAT_GETJLB_FAIL);
            }
        }
    }

    public void sendMessage(Message msg) {
        if (handler != null) {
            handler.sendMessage(msg);
        }
    }

    public void sendEmptyMessage(int what) {
        if (handler != null) {
            handler.sendEmptyMessage(what);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Constants.testedMobile = null;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSE_COUPON:
                if (resultCode != RESULT_CANCELED) {
                    //从领取优惠券页面返回
                    Bundle bundle = data.getExtras();
                    String callBackMethod = bundle.getString("callbackMethod");
                    String isUseCoupon = bundle.getString("isUseCoupon");
                    String bundleString = bundle.getString("data");
                    if ("0".equals(isUseCoupon)) {
                        //不使用优惠券
                        tv_fav_ticket.setText("不使用");
                        tv_fav_ticket.setTag("");
                        price.setFavTicket(0f);
                        selectedTicket = null;
                    } else {
                        //使用优惠券
                        ChooseCouponBean.ValBean bean = gson.fromJson(bundleString, ChooseCouponBean.ValBean.class);
                        String couponName = bean.getCouponName();
                        tv_fav_ticket.setTag(bean.getCouponRecordGuid());
                        tv_fav_ticket.setText(couponName);
                        double discount = 0f;
                        switch (bean.getDiscountType()) {
                            case "0"://优惠金额
                                discount = Double.parseDouble(bean.getDiscountPrice());
                                break;
                            case "1"://优惠折扣
                                double parseDouble = Double.parseDouble(bean.getDiscountPercent());
                                double aDouble = ArithUtils.div(parseDouble, 10, 15);
                                double total = price.getTotalPriceDouble();
                                discount = total - total * aDouble;
                                break;
                        }
                        if (selectedTicket == null) {
                            selectedTicket = new FavTicket();
                        }
                        selectedTicket.setGuid(bean.getCouponRecordGuid());
                        selectedTicket.setMoney(String.valueOf(discount));
                        selectedTicket.setName(bean.getCouponName());
                        //此优惠券是否可用同其他优惠同时使用
                        selectedTicket.setWithOthers(bean.getLimitType());
                        price.setFavTicket(discount);
                        switch (bean.getLimitType()) {
                            case "0"://可与金萝卜一起使用
//                                cb_useJLB.setEnabled(true);
                                break;
                            case "1"://不可与金萝卜一起使用
                                cb_useJLB.setChecked(false);
//                                cb_useJLB.setEnabled(false);
                                break;
                        }

                    }
                    setTotalMoneyText();

                }
                break;
        }
        if (resultCode == REQ_ADDRESS) {
            initData();
        } else if (resultCode == LoginActivity.LOGIN_SUCCESS) {
            initData();
            initTitle("提交订单");
        }
    }

    // 判断订单是否来自泉眼
    private void checkQuanYan() {
        int buyCount1 = 0, buyCount2 = 0;

        for (Map<String, Object> item : shoppingCarList) {
            if (item.get("supplierID").toString().equals("shopnum1")) {
                // 20人团体票购买量判断
                String tuanti20_1 = "4713442b-505a-47a0-b490-09a6b2c6c662";
                String tuanti20_2 = "21ddf0e1-3b22-45b8-9c71-92084b523750";
                String tCount = item.get("count").toString();
                String productID = item.get("productID").toString();
                if (productID.equals(tuanti20_1)) {
                    buyCount1 = Integer.parseInt(tCount);
                } else if (productID.equals(tuanti20_2)) {
                    buyCount2 = Integer.parseInt(tCount);
                }
            }
        }
        less20person(buyCount1, buyCount2);
    }

    private void less20person(int buyCount1, int buyCount2) {
        String errTitle = "购买量不足20";
        String errContent = "您购买的商品为20人团体票，要凑够20张才能下单哦！";
        if (buyCount1 < 20 && buyCount1 != 0) {
            showErrorDialog(errTitle, errContent);
            initErrInfo(errContent);
        }
        if (buyCount2 < 20 && buyCount2 != 0) {
            showErrorDialog(errTitle, errContent);
            initErrInfo(errContent);
        }
    }

    public String getFavourableParamItem(String guid, double price, int num) {
        return guid + "," + price + "," + num + ";";
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean isIsStock() {
        return isStock;
    }


    /**
     * 下单时，界面显示是否可用金萝卜
     * apiid：0339
     * { "result":"false", "info": "1"}
     * true--可用,false--不可用
     */
    class CanUseJLB extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... param) {
            try {
                String productGuid = param[0];
                if (TextUtils.isEmpty(productGuid)) {
                    return "";
                }
                return appManager.postCanUseJLB(productGuid);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            if (TextUtils.isEmpty(result)) {
                jsShowMsg("信息获取失败，请重试");
                finish();
                return;
            }
            BaseResultBean bean = gson.fromJson(result, BaseResultBean.class);
            Message msg = newHandler.obtainMessage();
            msg.what = WHAT_CAN_USE_JLB;
            msg.obj = bean.getResult();
            newHandler.sendMessage(msg);

        }
    }

}
