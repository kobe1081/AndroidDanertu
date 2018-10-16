package com.danertu.dianping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.danertu.tools.AppManager;
import com.danertu.tools.Logger;
import com.danertu.widget.AlertDialog;
import com.danertu.widget.CommonTools;

public class AddressManageActivity extends BaseActivity implements OnClickListener {
    Context context;
    EditText et_name, et_pNum, et_postalCode, et_address;
    CheckBox cb_setDefault;
    Button b_delete, b_save, b_title;
    //	private TextView tv_title;
    String name, pNum, postalCode, address, sguid;
    boolean isDefault;
    private final String TITLE = "收货地址管理";
    public static final String KEY_NAME = "keyName";
    public static final String KEY_PNUM = "keyPNum";
    public static final String KEY_POSTAL_CODE = "keyPostalCode";
    public static final String KEY_ADDRESS = "keyAddress";
    public static final String KEY_ISDEFAULT = "keyIsdefault";
    public static final String KEY_ADDRESS_GUID = "keyAddress_guid";
//	DBManager db = new DBManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        this.setContentView(R.layout.activity_address_manage);
        initIntentMsg();
        findViewById();
        initView();
    }

    private void initIntentMsg() {
        Intent intent = this.getIntent();
        name = intent.getStringExtra(KEY_NAME);
        pNum = intent.getStringExtra(KEY_PNUM);
        postalCode = intent.getStringExtra(KEY_POSTAL_CODE);
        address = intent.getStringExtra(KEY_ADDRESS);
        isDefault = intent.getBooleanExtra(KEY_ISDEFAULT, false);
        sguid = intent.getStringExtra(KEY_ADDRESS_GUID);
        Logger.e("msg---", pNum + " , " + address + " , " + isDefault + " , " + sguid);
    }

    @Override
    protected void findViewById() {
        b_title = (Button) findViewById(R.id.b_title_back5);
//		tv_title = (TextView)findViewById(R.id.tv_title5);
        et_address = (EditText) findViewById(R.id.et_addressManage_address);
        et_name = (EditText) findViewById(R.id.et_addressManage_pName);
        et_pNum = (EditText) findViewById(R.id.et_addressManage_pNum);
        et_postalCode = (EditText) findViewById(R.id.et_addressManage_postalCode);
        b_delete = (Button) findViewById(R.id.b_addressManage_delete);
        b_save = (Button) findViewById(R.id.b_addressManage_save);
        cb_setDefault = (CheckBox) findViewById(R.id.cb_addressManage_setDefault);
    }

    @Override
    protected void initView() {
        b_title.setText(TITLE);
        et_address.setText(address);
        et_name.setText(name);
        et_pNum.setText(pNum);
        if (postalCode == null) {
            findViewById(R.id.rl_addressManage_postalCode).setVisibility(View.GONE);
        } else {
            et_postalCode.setText(postalCode);
        }
        b_title.setOnClickListener(this);
        b_delete.setOnClickListener(this);
        b_save.setOnClickListener(this);
        cb_setDefault.setChecked(isDefault);
        if (isDefault)
            cb_setDefault.setEnabled(false);
    }

    //	删除收货地址
    private Thread tDeleteUserAddress = new Thread(new Runnable() {
        public void run() {
            // 耗时操作
            AppManager.getInstance().postDeleteUserAddress("0014", sguid,getUid());
        }
    });

    private AlertDialog dialog;

    @Override
    public void onClick(View v) {
        Logger.i("cb_state", isDefault + "");
        switch (v.getId()) {
            case R.id.b_addressManage_delete:
                if (dialog == null) {
                    dialog = new AlertDialog(this, R.style.Dialog) {

                        @Override
                        public void sure() {
                            isDefault = cb_setDefault.isChecked();
                            if (isDefault) {
                                CommonTools.showShortToast(context, "默认地址不能删除");
                                return;
                            }
                            db.deleteAddress(context, sguid);
                            tDeleteUserAddress.start();
                            setResult(AddressActivity.REQUEST_ADDRESS);
                            finish();
                        }

                        @Override
                        public void cancelDialog() {
                            dismiss();
                        }
                    };
                    dialog.setCanBack(true);
                    dialog.setTitle("删除地址！");
                    dialog.setContent("确定删除此地址信息？？？");
                    dialog.setSureButton("是");
                    dialog.setCancelButton("否");
                }
                dialog.show();
                break;

            case R.id.b_addressManage_save:
                isDefault = cb_setDefault.isChecked();
                // CommonTools.showShortToast(add.this,
                // "登录失败，请确认账号与密码的正确性");
                String tName = et_name.getText().toString().replaceAll(" ", "");
                String tAddress = et_address.getText().toString().replaceAll(" ", "");
                String mobile = et_pNum.getText().toString();
                String name = tName.replaceAll("\n", "").replace("\r", "");
                String address = tAddress.replaceAll("\n", "").replace("\r", "");
                if (tName.length() <= 0 || mobile.length() <= 0 || tAddress.length() <= 0) {
                    CommonTools.showShortToast(context, "信息不能为空！");
                    return;
                }

//			db.SetDefaultAddress(context, sguid);
                db.SetAddress(context, name, mobile, address, sguid, isDefault);
//			Cursor cursor = db.GetDefaultAddress(context, db.GetLoginUid(context));
                new Thread(tSetUserDefaultAddress).start();
                setResult(AddressActivity.REQUEST_ADDRESS);//更新地址Activity信息
                finish();
                break;

            case R.id.b_title_back5:
                finish();
                break;
            default:
                break;
        }
    }

    // 设置服务器上的默认收货地址
    private Runnable tSetUserDefaultAddress = new Runnable() {
        public void run() {
            // 设置默认收货地址
            String json = AppManager.getInstance().postSetAddressIsDefault("0031", sguid, db.GetLoginUid(context));
            judgeIsTokenException(json,"您的登录信息已过期，请重新登录",-1);
        }

    };
}
