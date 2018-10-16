package com.danertu.dianping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.danertu.entity.Address;
import com.danertu.entity.CNAddressAdapter;
import com.danertu.entity.CNAddressListItem;
import com.danertu.tools.AppManager;
import com.danertu.widget.CommonTools;

public class AddressOperateActivity extends BaseActivity implements OnClickListener {

    private Button save_address_button, b_title;
    //	private TextView tv_title;
    private EditText address_name_edit, address_mobile_edit, address_detail_edit/*, address_postal_code*/;
    //	private Boolean isInserted = false; //判断是否新增成功
    private CheckBox cbIsDefault;
    private final String TITLE = "新增收货地址";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adress_operate);
        findViewById();
        initView();
    }

    //	private CNAddressDBManager dbm;
//	private SQLiteDatabase sDatabase;
    private JSONObject areaJson;

    /**
     * 解析raw存放的地址数据
     *
     * @param table
     * @param pcode
     * @param provinceKey
     * @param cityKey
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<CNAddressListItem> getCNAddressData(String table, String pcode, String provinceKey, String cityKey) {
        List<CNAddressListItem> list = new ArrayList<>();
        try {
            if (areaJson == null) {
                InputStream in = getResources().openRawResource(R.raw.area);
//				InputStream in = getAssets().open("area.json");
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                areaJson = new JSONObject(new String(out.toByteArray(), "utf-8"));
            }
            Iterator<String> keys = null;
            JSONArray arr = null;
            if (isProvinces) {
                keys = areaJson.keys();
            } else if (isCity) {
                keys = areaJson.getJSONArray(provinceKey).getJSONObject(0).keys();
            } else if (isDistrict) {
                arr = areaJson.getJSONArray(provinceKey).getJSONObject(0).getJSONArray(cityKey);
            }
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    String key = arr.getString(i);
                    CNAddressListItem myListItem = new CNAddressListItem();
                    myListItem.setName(key);
                    myListItem.setPcode(key);
                    list.add(myListItem);
                }
            } else if (keys != null) {
                while (keys.hasNext()) {
                    String key = keys.next();
                    CNAddressListItem myListItem = new CNAddressListItem();
                    myListItem.setName(key);
                    myListItem.setPcode(key);
                    list.add(myListItem);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


//		if(dbm == null)
//			dbm = new CNAddressDBManager(this);
//		dbm.openDatabase();
//		sDatabase = dbm.getDatabase();
//	 	List<CNAddressListItem> list = new ArrayList<CNAddressListItem>();
//		
//	 	try {
//	 		String sql;
//	 		if(TextUtils.isEmpty(pcode)){
//	 			sql = "select * from "+table;
//	 		}else{
//	        	sql = "select * from "+table+" where pcode='"+pcode+"'";
//	 		}
//	        Cursor cursor = sDatabase.rawQuery(sql,null);  
//	        
//	        for (cursor.moveToFirst();!cursor.isAfterLast(); cursor.moveToNext()){ 
//		        String code=cursor.getString(cursor.getColumnIndex("code")); 
//		        byte bytes[]=cursor.getBlob(2); 
//		        String name=new String(bytes,"gbk");
//		        name = name.trim();
//		        
//		        if(isProvinces){
//		        	if(!name.contains("省")){
//		        		name = name.contains("市") ? name.replace("市", "省") : (name + "省");
//		        	}
//		        }else if(isCity){
//		        	name = name.contains("市") ? name : (name + "市");
//		        }else if(isDistrict){
//		        	if(!name.contains("区")){
//		        		name = name.contains("县") ? name : (name + "区");
//		        	}
//		        }
//		        
//		        CNAddressListItem myListItem=new CNAddressListItem();
//		        myListItem.setName(name);
//		        myListItem.setPcode(code);
//		        list.add(myListItem);
//	        }
//	        
//	    } catch (Exception e) {  
//	    } 
//	 	dbm.closeDatabase();
//	 	sDatabase.close();
        return list;
    }

    private List<CNAddressListItem> provinces;
    private boolean isProvinces, isCity, isDistrict;

    private void setAreaTag(boolean isProvinces, boolean isCity, boolean isDistrict) {
        this.isProvinces = isProvinces;
        this.isCity = isCity;
        this.isDistrict = isDistrict;
    }

    /**
     * 获取省级列表
     *
     * @return
     */
    public List<CNAddressListItem> getProvince() {
        setAreaTag(true, false, false);
        if (provinces == null)
            provinces = getCNAddressData("province", null, null, null);
        return provinces;
    }


    private String province = null;

    /**
     * 获取市级列表
     */
    public List<CNAddressListItem> getCity(String pcode) {
        setAreaTag(false, true, false);
        province = pcode;
        return getCNAddressData("city", pcode, pcode, null);
    }

    /**
     * 获取区/县级列表
     *
     * @param pcode
     * @return
     */
    public List<CNAddressListItem> getDistrict(String pcode) {
        setAreaTag(false, false, true);
        return getCNAddressData("district", pcode, province, pcode);
    }

    private RelativeLayout rl_addressAdd_area;
    private DrawerLayout sd_address;
    private ListView lv_addressAdd_cnSelect;
    private TextView tv_addressAdd_selectTips;
    private TextView tv_addressAdd_area;

    @Override
    protected void findViewById() {
        b_title = (Button) findViewById(R.id.b_title_back5);
        save_address_button = (Button) findViewById(R.id.b_title_operation5);
//		cancel_address_button = (Button) findViewById(R.id.cancel_address_button);
        address_name_edit = (EditText) findViewById(R.id.et_addressAdd_name);
        address_mobile_edit = (EditText) findViewById(R.id.et_addressAdd_pNum);
//		address_postal_code = (EditText) findViewById(R.id.et_addressAdd_postalcode);
        address_detail_edit = (EditText) findViewById(R.id.et_addressAdd_areaDetail);
        cbIsDefault = (CheckBox) findViewById(R.id.cb_addressAdd_setDefault);
        //	address_tel_edit = (EditText) findViewById(R.id.add_address_tel_edit);
//		address_detail_edit = (EditText) findViewById(R.id.add_address_detail_edit);
//		ckIsDefault = (CheckBox) findViewById(R.id.ckIsDefault);

        rl_addressAdd_area = (RelativeLayout) findViewById(R.id.rl_addressAdd_area);
        sd_address = (DrawerLayout) findViewById(R.id.dl_parent);
        lv_addressAdd_cnSelect = (ListView) findViewById(R.id.lv_addressAdd_cnSelect);
        tv_addressAdd_selectTips = (TextView) findViewById(R.id.tv_addressAdd_selectTips);
        tv_addressAdd_area = (TextView) findViewById(R.id.tv_addressAdd_area);
    }

    @Override
    protected void initView() {
        save_address_button.setText("保存");
        save_address_button.setOnClickListener(this);
        b_title.setText(TITLE);
        b_title.setOnClickListener(this);
        rl_addressAdd_area.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//				sd_address.animateOpen();
                sd_address.openDrawer(Gravity.RIGHT);
            }
        });
        lv_addressAdd_cnSelect.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                CNAddressListItem item = (CNAddressListItem) parent.getAdapter().getItem(position);
                String name = tv_addressAdd_selectTips.getText().toString();
                name = name.equals(item.getName()) ? name : (name + item.getName());
                setTips(name);
                List<CNAddressListItem> list = null;
                if (selectCount == 1) {
                    list = getCity(item.getPcode());
                    if (list.size() > 0) {
                        lv_addressAdd_cnSelect.setAdapter(new CNAddressAdapter(getContext(), list));
                    } else {
                        tv_addressAdd_area.setText(tv_addressAdd_selectTips.getText());
                        sd_address.closeDrawers();
                    }
                } else if (selectCount == 2) {
                    list = getDistrict(item.getPcode());
                    if (list.size() > 0) {
                        lv_addressAdd_cnSelect.setAdapter(new CNAddressAdapter(getContext(), list));
                    } else {
                        tv_addressAdd_area.setText(tv_addressAdd_selectTips.getText());
                        sd_address.closeDrawers();
                    }
                } else if (selectCount >= 3) {
                    tv_addressAdd_area.setText(tv_addressAdd_selectTips.getText());
                    sd_address.closeDrawers();
                }
            }
        });
        sd_address.addDrawerListener(new DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                CNAddressAdapter adapter = new CNAddressAdapter(getContext(), getProvince());
                lv_addressAdd_cnSelect.setAdapter(adapter);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                setTips(null);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
//		sd_address.setDrawerListener(new DrawerListener() {
//			public void onDrawerStateChanged(int arg0) {
//			}
//			public void onDrawerSlide(View arg0, float arg1) {
//			}
//			public void onDrawerOpened(View arg0) {
//				CNAddressAdapter adapter = new CNAddressAdapter(getContext(), getProvince());
//				lv_addressAdd_cnSelect.setAdapter(adapter);
//			}
//			public void onDrawerClosed(View arg0) {
//				setTips(null);
//			}
//		});
//		sd_address.setOnDrawerOpenListener(new OnDrawerOpenListener() {
//			public void onDrawerOpened() {
//				sd_address.getHandle().setBackgroundResource(R.drawable.arrow_normal);
//			}
//		});
//		sd_address.setOnDrawerCloseListener(new OnDrawerCloseListener() {
//			public void onDrawerClosed() {
//				sd_address.getHandle().setBackground(null);
//				setTips(null);
//			}
//		});
//		cancel_address_button.setOnClickListener(this);
    }

    private int selectCount = 0;

    public void setTips(String name) {
        if (name == null) {
            if (tv_addressAdd_selectTips != null) {
                tv_addressAdd_selectTips.setText("");
            }
            selectCount = 0;
            return;
        }
        tv_addressAdd_selectTips.setText(name);
        selectCount++;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_title_operation5:

                String msg = checkData();
                if (!msg.equals("")) {
                    CommonTools.showShortToast(AddressOperateActivity.this, msg);
                    return;
                } else {
                    new Thread(tAddAddress).start();
                }
                break;
            case R.id.b_title_back5:
                finish();
                break;
//		case R.id.cancel_address_button:
//			Intent mIntent=new Intent(AddressOperateActivity.this, AddressActivity.class);
//			startActivity(mIntent);
            default:
                break;
        }
    }

    /**
     * 上传地址至服务器
     */
    private Runnable tAddAddress = new Runnable() {
        @Override
        public void run() {
            if (isLoading())
                return;
            showLoadDialog();
            // 耗时操作
            String uid = db.GetLoginUid(AddressOperateActivity.this);
            String apiId = "0015";
            String uName = address_name_edit.getText().toString();
            String mobile = address_mobile_edit.getText().toString();
            String area = "";
            try {
                area = new String(tv_addressAdd_area.getText().toString().getBytes(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String detailAddress = area + address_detail_edit.getText().toString();
            String isDefault = cbIsDefault.isChecked() ? "1" : "0";
            String guid = UUID.randomUUID().toString();

            if (!TextUtils.isEmpty(uName))
                uName = uName.replaceAll("\n|\r| ", "");
            if (!TextUtils.isEmpty(detailAddress))
                detailAddress = detailAddress.replaceAll(" |\n|\r", "");

            String s = appManager.postInsertUserAddress(apiId, uid, uName, mobile, detailAddress, isDefault, guid);
            if (s.equals("true")) {
                Address addressEntity = new Address();
                addressEntity.setName(uName);
                addressEntity.setMobile(mobile);
                addressEntity.setAddress(detailAddress);
                addressEntity.setModifyTime(new Date(System.currentTimeMillis()).toString());
                addressEntity.setIsDefault(isDefault);
                addressEntity.setGuid(guid);
                db.InsertAddress(AddressOperateActivity.this, addressEntity);
                jsShowMsg("添加成功");
                setResult(AddressActivity.REQUEST_ADDRESS_ADD);
                finish();
            } else {
                judgeIsTokenException(s,"您的登录信息已过期，请重新登录",-1);
                jsShowMsg("添加失败，请检查信息是否存在特殊符号！");
            }
            hideLoadDialog();
        }

    };

    /**
     * 数据（姓名/地址/手机号码/）格式检查
     *
     * @return 判断结果
     */
    private String checkData() {
        String msg = "";
        int nameSize = address_name_edit.getText().toString().trim().length();
        String mobile = address_mobile_edit.getText().toString();
        String address = address_detail_edit.getText().toString().replaceAll(" ", "");
        String area = tv_addressAdd_area.getText().toString();
        if (nameSize < 2 || nameSize > 10) {
            msg = "请输入正确的名字";
        } else if (!CommonTools.isMobileNO(mobile)) {
            msg = "请输入正确的手机号码";
        } else if (TextUtils.isEmpty(area)) {
            msg = "请选择地区";
        } else if (address.length() < 5 || address.length() > 100) {
            msg = "请输入正确的地址";
        }
        return msg;
    }

}
