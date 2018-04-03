package com.danertu.dianping;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.danertu.widget.CommonTools;

/**
 * 2017年7月26日
 * huangyeliang
 * HistoryAdapter添加ViewHolder类
 */
public class SearchActivity extends BaseActivity {

    private EditText mEditText = null;
    private Button mImageButton = null;
    private ListView mListView = null;
    private HistoryAdapter historyListAdapter;
    private ArrayList<String> searchHistoryList = new ArrayList<>();
    private List<View> mHeaderViews = new ArrayList<>();
    private static final String HISTORY_URL = "find_history";
    private String[] AreaList = new String[]{"中山", "珠海"};

    public View getHeaderView(String paramString) {
        LinearLayout localLinearLayout = new LinearLayout(this);
        TextView localTextView = new TextView(this);
        LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0F);
        localLayoutParams.leftMargin = CommonTools.dip2px(this, 15.0F);
        localLayoutParams.height = CommonTools.dip2px(this, 50.0F);
        localTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
                .getDimensionPixelSize(R.dimen.small_text_size));
        localTextView.setLayoutParams(localLayoutParams);
        localTextView.setText(paramString);
        localTextView.setTextColor(ContextCompat.getColor(this, R.color.light_gray));
        localTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        localLinearLayout.addView(localTextView);
        mHeaderViews.add(localLinearLayout);
        return localLinearLayout;
    }

    private View getHotWordView(String[] list) {
        LinearLayout localLinearLayout = new LinearLayout(this);
//        localLinearLayout.setOrientation(0);
        localLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        int i = CommonTools.dip2px(this, 15.0F);
        localLinearLayout.setPadding(i, 0, i, 0);
        for (i = 0; i < list.length; i++) {
            TextView localTextView = getTextView(list[i]);
            localTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            localTextView.setTextSize(0, getResources().getDimensionPixelSize(
                    R.dimen.small_text_size));
            localTextView.setBackgroundResource(R.drawable.background_round_textview);
            LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) localTextView.getLayoutParams();
            localLayoutParams.height = CommonTools.dip2px(this, 38.0F);
            localLinearLayout.addView(localTextView);
        }
        mHeaderViews.add(localLinearLayout);
        return localLinearLayout;
    }

    private TextView getTextView(final String paramKeyword) {
        TextView localTextView = new TextView(this);
        localTextView.setText(paramKeyword);
        localTextView.setTextColor(ContextCompat.getColor(this, R.color.deep_black));
        localTextView.setTextSize(0, getResources().getDimensionPixelSize(R.dimen.small_text_size));
        localTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                search(paramKeyword);
            }
        });
        localTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0F));
        localTextView.setBackgroundResource(R.drawable.background_round_textview);
        return localTextView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        loadHistory(HISTORY_URL);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        mEditText = (EditText) findViewById(R.id.edit_search);
        mImageButton = (Button) findViewById(R.id.search);
        mListView = (ListView) findViewById(R.id.search_history_list);
        mListView.addHeaderView(getHeaderView("搜索地区"), null, false);
        mListView.addHeaderView(getHotWordView(AreaList));
        if (searchHistoryList.size() > 0) {
            mListView.addHeaderView(getHeaderView("搜索历史"), null, false);
        }
    }

    @Override
    protected void initView() {
        mEditText.requestFocus();
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.KEYCODE_SOFT_LEFT)) {
                    String keyword = mEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(keyword)) {
                        return true;
                    }
                    search(keyword);
                    return true;
                }
                return false;
            }
        });
        mImageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String keyword = mEditText.getText().toString();
                search(keyword);
            }
        });

        mListView.setOnItemClickListener(mOnClickListener);
        historyListAdapter = new HistoryAdapter(this);
        mListView.setAdapter(historyListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((mHeaderViews.size() == 2) && (searchHistoryList.size() > 0)) {
            mListView.addHeaderView(getHeaderView("搜索历史"), null, false);
        }
        historyListAdapter.notifyDataSetChanged();
    }

    private class HistoryAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;

        private HistoryAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
            if (searchHistoryList.size() > 0) {
                searchHistoryList.add("清除搜索历史");
            }
        }

        public int getCount() {
            // return this.stringList.size();
            return searchHistoryList.size();
        }

        public Object getItem(int paramInt) {
            // return this.stringList.get(paramInt);
            return paramInt;
        }

        public long getItemId(int paramInt) {
            return paramInt;
        }

        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
            ViewHolder holder;
            if (paramView == null) {
                paramView = mInflater.inflate(R.layout.search_list_item, null);
                holder = new ViewHolder(paramView);
                paramView.setTag(holder);
            } else {
                holder = ((ViewHolder) paramView.getTag());
            }
            holder.localTextView.setText(searchHistoryList.get(paramInt));
            if (paramInt == -1 + getCount()) {
                holder.divider.setVisibility(View.GONE);
                holder.list_view_end_divider.setVisibility(View.VISIBLE);
                holder.list_view_start_divider.setVisibility(View.VISIBLE);
                holder.localTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            } else {
                holder.divider.setVisibility(View.VISIBLE);
                holder.list_view_end_divider.setVisibility(View.GONE);
                holder.list_view_start_divider.setVisibility(View.GONE);
                holder.localTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            }
            return paramView;
        }

        class ViewHolder {
            TextView localTextView;
            View divider;
            View list_view_start_divider;
            View list_view_end_divider;

            public ViewHolder(View view) {
                localTextView = (TextView) view.findViewById(R.id.text1);
                divider = view.findViewById(R.id.divider);
                list_view_end_divider = view.findViewById(R.id.list_view_end_divider);
                list_view_start_divider = view.findViewById(R.id.list_view_start_divider);
            }
        }
    }

    // 历史列表点击事件方法
    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {
            if (id == (-1 + searchHistoryList.size())) {
                removeHistory(HISTORY_URL);
                mListView.removeHeaderView(mHeaderViews.get(mHeaderViews.size() - 1));
                mHeaderViews.remove(mHeaderViews.size() - 1);
                historyListAdapter.notifyDataSetChanged();
            } else {
                String keyword = searchHistoryList.get((int) id);
                search(keyword);
            }
        }
    };

    // @Override
    // protected void onResume() {
    // super.onResume();
    // searchHistoryList.add("清除搜索历史");
    // }

    // 加载搜索历史列表
    private void loadHistory(String field) {
        SharedPreferences sp = getSharedPreferences(field, Context.MODE_PRIVATE);
        int size = sp.getInt("Status_size", 0);
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                searchHistoryList.add(sp.getString("Status_" + i, ""));
            }
        }
    }

    private void saveHistory(String field, String text) {
        SharedPreferences sp = getSharedPreferences(field, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit = sp.edit();
        mEdit.putInt("Status_size", searchHistoryList.size());
        for (int i = 0; i < searchHistoryList.size(); i++) {
            mEdit.remove("Status_" + i);
            mEdit.putString("Status_" + i, searchHistoryList.get(i));
        }
        mEdit.apply();
    }

    // 清空搜索历史
    private void removeHistory(String field) {
        SharedPreferences sp = getSharedPreferences(field, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.clear();
        editor.apply();
        searchHistoryList.clear();
    }

    private void search(String keyword) {
        if (keyword.equals("")) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("key", keyword);
        bundle.putString("act", "IndexActivity");
        Intent intent = new Intent();
        intent.setClassName(getApplicationContext(), "com.danertu.dianping.SearchResultActivity");
        intent.putExtras(bundle);
        startActivity(intent);
        searchHistoryList.remove(keyword);
        searchHistoryList.remove("清除搜索历史");
        searchHistoryList.add(0, keyword);
        if (searchHistoryList.size() == 11) {
            searchHistoryList.remove(10);
        }
        saveHistory(HISTORY_URL, keyword);
        searchHistoryList.add("清除搜索历史");
    }
}
