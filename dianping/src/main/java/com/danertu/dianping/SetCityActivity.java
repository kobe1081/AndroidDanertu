package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.entity.CityModel;
import com.danertu.tools.MyLetterListView;
import com.danertu.tools.MyLetterListView.OnTouchingLetterChangedListener;

public class SetCityActivity extends BaseActivity implements OnClickListener, OnTouchListener {
    private ListAdapter adapter;

    private ArrayList<CityModel> mCityNames;
    private ListView mCityLit;
    private MyLetterListView letterListView;
    private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private String[] sections;// 存放存在的汉语拼音首字母
    private TextView overlay;
    private Handler handler;
    private OverlayThread overlayThread;
    private View headView;
    private EditText searchText;
    private TextView current_city;
    private TextView hotCity1, hotCity2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_city);
        findById();
        initView();

    }

    final String TITLE = "单耳兔";

    private void findById() {
        Button b_title = (Button) findViewById(R.id.b_order_title_back);
        b_title.setText(TITLE);
        b_title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
        current_city = (TextView) findViewById(R.id.current_city);
        mCityLit = (ListView) findViewById(R.id.city_list);
        headView = getLayoutInflater().inflate(R.layout.city_head, null);
        headView.setOnClickListener(null);
        hotCity1 = (TextView) headView.findViewById(R.id.hotcity1);
        hotCity1.setOnClickListener(this);
        hotCity1.setOnTouchListener(this);
        hotCity2 = (TextView) headView.findViewById(R.id.hotcity2);
        hotCity2.setOnClickListener(this);
        hotCity2.setOnTouchListener(this);
        letterListView = (MyLetterListView) findViewById(R.id.cityLetterListView);
        searchText = (EditText) findViewById(R.id.edit_keywords);

        overlay = (TextView) findViewById(R.id.overlay_text);
    }

    protected void initView() {

        mCityNames = getCityNames();
        if (mCityNames == null) {
            jsShowMsg("数据加载出错");
            return;
        }
        alphaIndexer = new HashMap<>();
        handler = new Handler();
        overlayThread = new OverlayThread();
        mCityLit.addHeaderView(headView);
        adapter = new ListAdapter(SetCityActivity.this, mCityNames);
        mCityLit.setAdapter(adapter);
        mCityLit.setOnItemClickListener(new CityListOnItemClick());
        current_city.setText(Constants.getCityName());

        letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
        searchText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hotcity1:
                Constants.setCityName("中山市");
                // startActivity(intent);
                setResult(SET_CITY_SUCCESS);
                finish();
                break;
            case R.id.hotcity2:
                Constants.setCityName("珠海市");
                // startActivity(intent);
                setResult(SET_CITY_SUCCESS);
                finish();
                break;
            default:
                break;
        }

    }

    private ArrayList<CityModel> getCityNames() {
        Cursor cursor = db.getCitys(SetCityActivity.this);
        boolean flag = cursor == null;
        if (flag) {
            return null;
        }
        ArrayList<CityModel> names = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            CityModel cityModel = new CityModel();
            cityModel.setCityName(cursor.getString(cursor.getColumnIndex("CityName")));
            cityModel.setNameSort(cursor.getString(cursor.getColumnIndex("SortName")));
            names.add(cityModel);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return names;

    }

    private class ListAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<CityModel> list;

        public ListAdapter(Context context, List<CityModel> list) {

            this.inflater = LayoutInflater.from(context);
            this.list = list;
            alphaIndexer = new HashMap<>();
            sections = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                // 当前汉语拼音首字母
                // getAlpha(list.get(i));
                String currentStr = list.get(i).getNameSort();
                // 上一个汉语拼音首字母，如果不存在为“ ”
                String previewStr = (i - 1) >= 0 ? list.get(i - 1).getNameSort() : " ";
                if (!previewStr.equals(currentStr)) {
                    String name = list.get(i).getNameSort();
                    alphaIndexer.put(name, i);
                    sections[i] = name;
                }
            }

        }

        public void updateListView(List<CityModel> filterList) {
            this.list = filterList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.city_list_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(list.get(position).getCityName());
            String currentStr = list.get(position).getNameSort();
            String previewStr = (position - 1) >= 0 ? list.get(position - 1).getNameSort() : " ";
            if (!previewStr.equals(currentStr)) {
                holder.alpha.setVisibility(View.VISIBLE);
                holder.alpha.setText(currentStr);
            } else {
                holder.alpha.setVisibility(View.GONE);
            }
            return convertView;
        }

        private class ViewHolder {
            TextView alpha;
            TextView name;

            public ViewHolder(View view) {
                alpha = (TextView) view.findViewById(R.id.alpha);
                name = (TextView) view.findViewById(R.id.name);
            }
        }

    }

    private class LetterListViewListener implements
            OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                mCityLit.setSelection(position);
                overlay.setText(sections[position]);
                overlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(overlayThread);
                // 延迟一秒后执行，让overlay为不可见
                handler.postDelayed(overlayThread, 1500);
            }
        }

    }

    // 设置overlay不可见
    private class OverlayThread implements Runnable {

        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }

    }

    public static final int SET_CITY_SUCCESS = 10;

    private class CityListOnItemClick implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            CityModel cityModel = (CityModel) mCityLit.getAdapter().getItem(position);
            Constants.setCityName(cityModel.getCityName());
            setResult(SET_CITY_SUCCESS);
            finish();
        }

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr 过滤值
     */
    private void filterData(String filterStr) {
        List<CityModel> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            mCityLit.addHeaderView(headView);
            filterDateList = mCityNames;
        } else {
            mCityLit.removeHeaderView(headView);
            filterDateList.clear();
            filterStr = filterStr.trim();
            for (CityModel cityModel : mCityNames) {
                String sort = cityModel.getNameSort();
                String name = cityModel.getCityName();
                if (filterStr.equalsIgnoreCase(sort) || name.contains(filterStr)) {
                    filterDateList.add(cityModel);
                }
            }

        }
        adapter.updateListView(filterDateList);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setBackgroundResource(R.drawable.cart_btn_normal);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.setBackgroundResource(R.drawable.citybg);
        }

        return false;
    }

    @Override
    protected void findViewById() {
    }

}
