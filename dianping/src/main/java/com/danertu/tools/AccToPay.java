package com.danertu.tools;

import com.danertu.dianping.PaymentCenterActivity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.danertu.widget.CommonTools;

/**
 * uid = param[0]; outOrderNumber = param[1]; totalprice = param[2]; pricedata =
 * param[3];//createUser,price|createUser1,price1|.......
 *
 * @author dengweilin
 */
public abstract class AccToPay extends AsyncTask<String, Integer, String> {
    private String uid;
    private String outOrderNumber;
    private String totalprice;
    private String pricedata;
    private AppManager appManager;
    private AccountUtil accUtil;
    private Context context;
    private LoadingDialog loadDialog = null;

    public AccToPay(Context context) {
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
        pricedata = param[3];
        String result = null;
        try {
            result = appManager.accountToBuy(accUtil.getPayInfo(uid, outOrderNumber, totalprice), pricedata);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
//		Log.e("账户支付结果", result+"");
        if (result == null || result.equals("")) {
            CommonTools.showShortToast(context, "支付出错了");
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
}
