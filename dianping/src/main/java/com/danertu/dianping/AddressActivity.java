package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.danertu.tools.AsyncTask;
import com.danertu.tools.LoadingDialog;
import com.danertu.tools.Logger;
import com.danertu.widget.AlertDialog;
import com.danertu.widget.MyListView;

public class AddressActivity extends BaseActivity implements OnClickListener {

    private Button b_title;
    private Button b_opera;
    private Button b_addAddress;
    private ListView addressList;
    private TextView tvNoAddressTips;
    private final String TITLE = "我的收货地址";

    AddressAdapter adapter = null;
    ArrayList<HashMap<String, Object>> data = new ArrayList<>();

    public static final int REQUEST_ADDRESS = 2;
    public static final int REQUEST_ADDRESS_ADD = 3;
    LoadingDialog loadDialog = null;

    public final int ADDRESS_SET_DEFAULT = 5;
    public final int ADDRESS_DELETE = 6;
    private Handler handleAddress = null;
    private Handler.Callback callbackAddress = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == ADDRESS_SET_DEFAULT) {
                //执行接收到的通知，更新UI 此时执行的顺序是按照队列进行，即先进先出
//				jsShowMsg("设置成功");
            } else if (msg.what == ADDRESS_DELETE) {
                jsShowMsg("删除成功");
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
            bindListData();
            loadDialog.dismiss();
            return true;
        }

    };

    private boolean isManageAddress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        String mana = getIntent().getStringExtra("manageAddress");
        isManageAddress = mana != null && Boolean.parseBoolean(mana);
        uid = getUid();
        handleAddress = new Handler(callbackAddress);
        loadDialog = new LoadingDialog(this);
        findViewById();
        initView();
    }

    public void bindListData() {
        if (data == null) {
            data = new ArrayList<>();
        } else {
            data.clear();
        }
        Cursor cursor = db.GetUserAddress(AddressActivity.this, uid);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("adress_name", cursor.getString(0));
            item.put("adress_tel", cursor.getString(1));
            item.put("adress_mobile", cursor.getString(2));
            item.put("adress_isdefault", cursor.getString(3));
            item.put("adress_Adress", cursor.getString(4));
            item.put("adress_Guid", cursor.getString(5));
            item.put("adress_time", cursor.getString(6));
            data.add(item);
        }
        Logger.e("address", data.toString());

        if (adapter == null) {
            adapter = new AddressAdapter(this);
            addressList.setAdapter(adapter);
        }
        if (data.size() > 0) {
            adapter.notifyDataSetChanged();
            tvNoAddressTips.setVisibility(View.GONE);
        } else {
            addressList.setVisibility(View.VISIBLE);
            tvNoAddressTips.setVisibility(View.VISIBLE);
            b_addAddress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_title_back5:
                finish();
                break;

            case R.id.b_title_operation5:
                isManageAddress = !isManageAddress;
                int visible = isManageAddress ? View.VISIBLE : View.GONE;
                b_addAddress.setVisibility(visible);
                adapter.notifyDataSetChanged();
                break;

            case R.id.b_address_add:
                Intent mIntent = new Intent(this, AddressOperateActivity.class);
                startActivityForResult(mIntent, REQUEST_ADDRESS_ADD);
                break;
            default:
                break;
        }
    }

    public void finish() {
        setResult(PaymentCenterActivity.REQ_ADDRESS);
        super.finish();
    }

    protected void findViewById() {
        addressList = (ListView) findViewById(R.id.lv_address_content);
        b_title = (Button) findViewById(R.id.b_title_back5);
        b_opera = (Button) findViewById(R.id.b_title_operation5);
        b_addAddress = (Button) findViewById(R.id.b_address_add);
        tvNoAddressTips= ((TextView) findViewById(R.id.tv_no_address_tip));
        b_title.setOnClickListener(this);
        b_addAddress.setOnClickListener(this);
        b_opera.setOnClickListener(this);
    }

    protected void initView() {
        bindListData();
        b_title.setText(TITLE);

        int visible = isManageAddress ? View.VISIBLE : View.GONE;
        b_addAddress.setVisibility(visible);
        if (isManageAddress) {
            b_opera.setVisibility(View.GONE);
        } else {
            b_opera.setVisibility(View.VISIBLE);
            b_opera.setText("管理");
        }
    }

    public void startAddressManage(String name, String pNum, String address, String isDefault, String addressGuid) {
        Intent intent = new Intent(this, AddressManageActivity.class);
        intent.putExtra(AddressManageActivity.KEY_NAME, name);
        intent.putExtra(AddressManageActivity.KEY_ADDRESS, address);
        intent.putExtra(AddressManageActivity.KEY_ISDEFAULT, isDefault.equals("1"));
        intent.putExtra(AddressManageActivity.KEY_PNUM, pNum);
        intent.putExtra(AddressManageActivity.KEY_ADDRESS_GUID, addressGuid);
        startActivityForResult(intent, REQUEST_ADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUEST_ADDRESS) {
            bindListData();
        } else if (resultCode == REQUEST_ADDRESS_ADD) {
            bindListData();
        }
    }

    public static final String KEY_ADDRESS_UID = "adress_uid";
    public static final String KEY_ADDRESS_NAME = "adress_name";
    public static final String KEY_ADDRESS_TEL = "adress_tel";
    public static final String KEY_ADDRESS_MOBILE = "adress_mobile";
    public static final String KEY_ADDRESS_MSG = "adress_Adress";
    public static final String KEY_ADDRESS_ISDEFAULT = "adress_isdefault";
    public static final String KEY_ADDRESS_TIME = "adress_time";
    public static final String KEY_ADDRESS_GUID = "adress_Guid";

    private String uid = null;

    // AsyncTask异步任务
    class RefreshTask extends AsyncTask<Integer, Integer, String> {
        MyListView listview;

        public RefreshTask(MyListView listView) {
            super();
            this.listview = listView;
            data = new ArrayList<>();
        }

        protected String doInBackground(Integer... params) {
            try {
                String result = appManager.postGetUserAddress("0030", uid);
                try {
                    JSONObject jsonObject = new JSONObject(result).getJSONObject("adress");
                    JSONArray jsonArray = jsonObject.getJSONArray("adresslist");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject oj = jsonArray.getJSONObject(i);
                        // Address adressEntity = new Address();
                        HashMap<String, Object> item = new HashMap<>();
                        item.put("adress_uid", uid);
                        item.put("adress_name", oj.getString("name"));
                        item.put("adress_tel", oj.getString("tel"));
                        item.put("adress_mobile", oj.getString("mobile"));
                        item.put("adress_Adress", oj.getString("adress"));
                        item.put("adress_isdefault", oj.getString("ck"));
                        item.put("adress_time", oj.getString("time"));
                        item.put("adress_guid", oj.getString("guid"));
                        data.add(item);
                    }
                    db.TogetherPcUserAddress(AddressActivity.this, uid, data);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    // String s ="123";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 在data最前添加数据
            // data.addFirst("刷新后的内容");
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            adapter.notifyDataSetChanged();
            // listView.onRefreshComplete();
        }
    }

    private AlertDialog dialog;

    private class AddressAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private Context context;

        AddressAdapter(Context context) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return data.size();
        }

        public Object getItem(int position) {
            return data.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        class AddressItemUI {
            TextView address_name, address_mobile, address_Address;
            TextView tv_edit, tv_delete;
            RadioButton cb_set_default;
            FrameLayout fl_editAddress;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            AddressItemUI ui = null;
            if (v == null) {
                ui = new AddressItemUI();
                // 获取组件布局
                v = layoutInflater.inflate(R.layout.address_item, null);
                ui.address_name = (TextView) v.findViewById(R.id.tv_address_pName);
                ui.address_mobile = (TextView) v.findViewById(R.id.tv_address_pNum);
                ui.address_Address = (TextView) v.findViewById(R.id.tv_address_receive);
                ui.cb_set_default = (RadioButton) v.findViewById(R.id.set_default);
                ui.tv_delete = (TextView) v.findViewById(R.id.tv_delete);
                ui.tv_edit = (TextView) v.findViewById(R.id.tv_edit);
                ui.fl_editAddress = (FrameLayout) v.findViewById(R.id.fl_editAddress);
                // 这里要注意，是使用的tag来存储数据的。
                v.setTag(ui);
            } else {
                ui = (AddressItemUI) v.getTag();
            }
            HashMap<String, Object> item = data.get(position);
            final String name = item.get(KEY_ADDRESS_NAME).toString();
            final String mobile = item.get(KEY_ADDRESS_MOBILE).toString();
            final String address = item.get(KEY_ADDRESS_MSG).toString();
            final String isDefault = item.get(KEY_ADDRESS_ISDEFAULT).toString();
            final String addressGuid = item.get(KEY_ADDRESS_GUID).toString();

            if (isManageAddress) {
                ui.fl_editAddress.setVisibility(View.VISIBLE);
            } else {
                ui.fl_editAddress.setVisibility(View.GONE);
            }
            // 绑定数据、以及事件触发
            ui.address_name.setText(name);
            ui.address_mobile.setText(mobile);
            SpannableString ssAddress = null;
            String ad = "收货地址: " + address;
            if (isDefault.equals("1")) {
                String df = "[默认地址]";
                ssAddress = formatStr(df + ad, df.length());
                ui.cb_set_default.setChecked(true);
            } else {
                ssAddress = formatStr(ad, 0);
                ui.cb_set_default.setChecked(false);
            }
            ui.address_Address.setText(ssAddress);
            ui.cb_set_default.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!isDefault.equals("1")) {
                        loadDialog.show();
                        new Thread(new SetDefaultAddress(addressGuid)).start();
                        db.SetAddress(context, name, mobile, address, addressGuid, true);
                    }
                }
            });
            ui.tv_edit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startAddressManage(name, mobile, address, isDefault, addressGuid);
                }
            });
            ui.tv_delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (dialog == null) {
                        dialog = new AlertDialog(context, R.style.Dialog) {
                            public void sure() {
                            }

                            public void cancelDialog() {
                                dismiss();
                            }
                        };
                    }
                    dialog.setCanBack(true);
                    dialog.setTitle("删除地址！");
                    dialog.setContent("确定删除此地址信息？？？");
                    dialog.setSureButton("是", new View.OnClickListener() {
                        public void onClick(View v) {
                            if (isDefault.equals("1")) {
                                jsShowMsg("默认地址不能删除");
                                return;
                            }
                            loadDialog.show();
                            new Thread(new DeleteAddress(addressGuid)).start();
                            db.deleteAddress(context, addressGuid);
                        }
                    });
                    dialog.setCancelButton("否");
                    dialog.show();
                }
            });

            return v;
        }

        SpannableString formatStr(String text, int len) {
            SpannableString sp = new SpannableString(text);
//			ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.red_light));
            ForegroundColorSpan fcs = new ForegroundColorSpan(ContextCompat.getColor(this.context, R.color.red_light));
            sp.setSpan(fcs, 0, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sp;
        }

        // 主线程Handler负责更新UI，Handler与 Thread通过Message通信
        private class SetDefaultAddress implements Runnable {
            private String sguid;

            SetDefaultAddress(String sguid) {
                this.sguid = sguid;
            }

            @Override
            public void run() {
                // 耗时操作
                String result = appManager.postSetAddressIsDefault("0031", sguid, uid);
                Logger.e(TAG,"设置默认地址结果==="+result);

                Message msg = Message.obtain();
                msg.what = ADDRESS_SET_DEFAULT;
                handleAddress.sendMessage(msg);
            }

        }

        private class DeleteAddress implements Runnable {
            private String sguid;

            DeleteAddress(String sguid) {
                this.sguid = sguid;
            }

            public void run() {
                appManager.postDeleteUserAddress("0014", sguid);
                Message msg = Message.obtain();
                msg.what = ADDRESS_DELETE;
                handleAddress.sendMessage(msg);
            }
        }
    }

}
