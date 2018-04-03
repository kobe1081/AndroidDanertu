package com.danertu.tools;

/**
 * String psw = arg0[0];
 * String uid = arg0[1];
 *
 * @author Administrator
 */
public abstract class CheckPayPswMD5 extends AsyncTask<String, Integer, Boolean> {
    private AppManager appManager;

    public CheckPayPswMD5() {
        appManager = AppManager.getInstance();
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
        try {
            String psw = arg0[0];
            String uid = arg0[1];
            String pswMD5 = MD5Util.MD5(psw);
            String payPswMD5 = appManager.getPayPswMD5(uid);
            return pswMD5.equals(payPswMD5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        postExecute(result);
    }

    public abstract void postExecute(Boolean result);
}

