package com.danertu.tools;

import android.content.Context;

import com.danertu.dianping.PaymentCenterActivity;
import com.danertu.widget.CommonTools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 2018年1月3日
 * 退货运费账号余额支付,调用0332接口
 * 返回格式为：   {"result":"","info":""}
 *
 * uid = param[0]; outOrderNumber = param[1]; totalprice = param[2]; pricedata =
 * param[3];//createUser,price|createUser1,price1|.......
 *
 *
 *
 * @author haungyeliang
 */
public abstract class StockOrderReturnAccountPay extends AsyncTask<String, Integer, String> {
    private String uid;
    private String outOrderNumber;
    private String totalprice;
    private AppManager appManager;
    private AccountUtil accUtil;
    private Context context;
    private LoadingDialog loadDialog = null;

    public StockOrderReturnAccountPay(Context context) {
        this.context = context;
        appManager = AppManager.getInstance();
        try {
            accUtil = new AccountUtil();
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadDialog = new LoadingDialog(context);
    }

    @Override
    protected String doInBackground(String... param) {
        uid = param[0];
        outOrderNumber = param[1];
        totalprice = param[2];
        String result = null;
        try {
            result = appManager.payReturnCost(accUtil.getPayInfo(uid, outOrderNumber, totalprice,"android"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
		Logger.e("账户支付结果", result+"");
        if (result == null || result.equals("")) {
            CommonTools.showShortToast(context, "支付出错了");
            payError();
            return;
        }
        JSONObject obj;
        try {
            obj = new JSONObject(result);
            String key_result = obj.getString(PaymentCenterActivity.KEY_RESULT);
            String info = obj.getString(PaymentCenterActivity.KEY_RESULT_INFO);
            if (key_result.equals(PaymentCenterActivity.TAG_RESULT_SUCCESS)) {
                paySuccess();
            } else {
                payFail();
            }
            CommonTools.showShortToast(context, info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadDialog.dismiss();
        loadDialog = null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loadDialog.show();
    }

    public abstract void paySuccess();

    public abstract void payFail();

    public abstract void payError();
}
