package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wl.codelibrary.widget.IOSDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.danertu.adapter.SearchProductAdapter;
import com.danertu.adapter.SearchShopAdapter;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;
import com.danertu.widget.SearchTipsGroupView;
import com.danertu.widget.SearchTipsGroupView.OnItemClick;

public class SearchActivityV2 extends HomeActivity {
    private EditText mEditText;
    //    private Button mSearchBtn;
    public RadioGroup mRadioGroup;
    public RadioButton mRadio1, mRadio2;
    /*
        搜索历史词
     */
    private ArrayList<String> searchHistoryList;
    private List<View> mHeaderViews = new ArrayList<>();
    /*
        热门搜索关键词
     */
    private String[] HotList = new String[]{"红酒", "白酒", "养生酒"};
    private static final String HISTORY_URL = "search_history";

    /**
     * 获取热门搜索关键词
     */
    private class GetHotKeyWord extends AsyncTask<String, Integer, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            HashMap<String, String> param = new HashMap<>();
            param.put("apiid", "0158");
            String result = appManager.doPost(param);
            return TextUtils.isEmpty(result) ? null : result.split(",");
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            if (result == null) {
                return;
            }
            initSearchTipsGroupView(ll_search_hot, result);
        }

    }

    /**
     * 获取顶部视图
     *
     * @param paramString
     * @return
     */
    public View getHeaderView(String paramString) {
        LinearLayout localLinearLayout = new LinearLayout(this);
        TextView localTextView = new TextView(this);
        LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        localLayoutParams.leftMargin = CommonTools.dip2px(this, 15.0F);
        localLayoutParams.height = CommonTools.dip2px(this, 50.0F);
        localTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.small_text_size));
        localTextView.setLayoutParams(localLayoutParams);
        localTextView.setText(paramString);
        localTextView.setTextColor(ContextCompat.getColor(this, R.color.light_gray));
        localTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        localTextView.setPadding(0, 0, 10, 0);
        localLinearLayout.addView(localTextView);
        mHeaderViews.add(localLinearLayout);
        return localLinearLayout;
    }

    private void initSearchTipsGroupView(SearchTipsGroupView v, ArrayList<String> list) {
        String[] histories = new String[list.size()];
        initSearchTipsGroupView(v, list.toArray(histories));
    }

    private void initSearchTipsGroupView(SearchTipsGroupView v, final String[] list) {
        if (v.getId() == R.id.ll_search_history) {
            boolean b = (list == null || list.length == 0);
            int visi = b ? View.GONE : View.VISIBLE;
            ll_search_history.setVisibility(visi);
            ll_search_history_tag.setVisibility(visi);
            if (b) return;
        }

        v.removeAllViews();
        v.initViews(list, new OnItemClick() {
            public void onClick(String value) {
                search(value);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_search_layout);
        setSystemBarWhite();
        loadHistory(HISTORY_URL);
        findViewById();
        initView();
        String keyWord = getIntent().getStringExtra("key");
        if (!TextUtils.isEmpty(keyWord))
            search(keyWord);
        new GetHotKeyWord().execute();
    }

    private SearchTipsGroupView ll_search_hot;
    private SearchTipsGroupView ll_search_history;
    private LinearLayout ll_search_history_tag;
    private ViewPager vp_content;

    @Override
    protected void findViewById() {
        mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        mRadio1 = (RadioButton) findViewById(R.id.select_product);
        mRadio2 = (RadioButton) findViewById(R.id.select_shop);
        mEditText = ((EditText) findViewById(R.id.search_edit));

        ll_search_history_tag = (LinearLayout) findViewById(R.id.ll_search_history_tag);
        ll_search_hot = (SearchTipsGroupView) findViewById(R.id.ll_search_hot);
        ll_search_history = (SearchTipsGroupView) findViewById(R.id.ll_search_history);

        vp_content = (ViewPager) findViewById(R.id.vp_content);
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        private int index = 0;

        @Override
        public void onPageSelected(int arg0) {
            index = arg0;
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            if (arg0 == 0) {
                RadioButton rb = (RadioButton) mRadioGroup.getChildAt(index);
                if (!rb.isChecked())
                    rb.setChecked(true);
                if (index == 0)
                    setSwipeBackEnable(true);
                else
                    setSwipeBackEnable(false);
            }
        }
    };

    public void onDestroy() {
        super.onDestroy();
        vp_content.removeOnPageChangeListener(pageChangeListener);
        if (searchShop != null && !searchShop.isCancelled()) searchShop.cancel(true);
        if (searchProduct != null && !searchProduct.isCancelled()) searchProduct.cancel(true);
    }

    @Override
    protected void initView() {

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            private IOSDialog dialog;

            public void onClick(View v) {
                if (dialog == null) {
                    dialog = new IOSDialog(getContext());
                    dialog.setMessage("确定删除所有历史记录？");
                    dialog.setPositiveButton("取消", new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    dialog.setNegativeButton("确定", new View.OnClickListener() {
                        public void onClick(View v) {
                            removeHistory(HISTORY_URL);
                            ll_search_history.setVisibility(View.GONE);
                            ll_search_history_tag.setVisibility(View.GONE);
                            dialog.cancel();
                        }
                    });
                }
                dialog.show();
            }
        });

        ArrayList<View> views = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            View v = LayoutInflater.from(this).inflate(R.layout.item_search_vp, null);
            ListView lv = (ListView) v.findViewById(R.id.lv_search_content);
            if (i == 0) {//product
                lv.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> item = (Map<String, Object>) parent.getItemAtPosition(position);
                        String guid = String.valueOf(item.get("guid"));
                        String shopId = getShopId();
                        Logger.e(TAG,"shopId="+shopId);
                        jsStartActivity("ProductDetailsActivity2", "guid|" + guid + ",;shopid|" + shopId);
                    }
                });
            } else if (i == 1) {
                lv.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> item = (Map<String, Object>) parent.getItemAtPosition(position);
                        String shopid = String.valueOf(item.get("shopid"));
                        String levelType = item.get("leveltype").toString();
                        if (shopid.equals("dlts")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("shopid", shopid);
                            bundle.putString("shopName", String.valueOf(item.get("shopName")));
                            bundle.putString("mobile", String.valueOf(item.get("mobile")));
                            Intent intent = new Intent(getContext(), DetailActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            jsStartActivity("IndexActivity", "shopid|" + shopid + ",;shoptype|" + levelType);
                            //发出刷新首页请求，更新shopid等
//                            jsSendRefreshBroadcast();
                        }
                    }
                });
            }
            views.add(v);
        }
        vp_content.setAdapter(new SearchContentAdapter(views));
        vp_content.addOnPageChangeListener(pageChangeListener);

        initSearchTipsGroupView(ll_search_hot, HotList);
        initSearchTipsGroupView(ll_search_history, searchHistoryList);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.select_product:
                        vp_content.setCurrentItem(0, true);
                        break;
                    case R.id.select_shop:
                        vp_content.setCurrentItem(1, true);
                        break;
                }
//                if (checkedId == mRadio1.getId()) {
//                    vp_content.setCurrentItem(0, true);
//                } else if (checkedId == mRadio2.getId()) {
//                    vp_content.setCurrentItem(1, true);
//                }
            }
        });
        mRadioGroup.check(R.id.select_product);
        // 搜索框虚拟键盘输入方法
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.KEYCODE_SOFT_LEFT)) {
                    if (isEmpty(mEditText.getText().toString().trim())) {
                        return true;
                    }
                    String keyword = mEditText.getText().toString().trim();
                    search(keyword);
                    return true;
                }
                return false;
            }
        });
//        final Drawable dLeft = ContextCompat.getDrawable(this, R.drawable.ic_search);
        final Drawable dRight = ContextCompat.getDrawable(this, R.drawable.ic_close);
//        dLeft.setBounds(0, 0, dLeft.getIntrinsicWidth(), dLeft.getIntrinsicHeight());
        dRight.setBounds(0, 0, dRight.getIntrinsicWidth(), dRight.getIntrinsicHeight());
        mEditText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    setSearchState(false);
                    initSearchTipsGroupView(ll_search_history, searchHistoryList);
                } else {
                    mEditText.setCompoundDrawables(null, null, dRight, null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        mEditText.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = mEditText.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > mEditText.getWidth() - mEditText.getPaddingRight() - drawable.getIntrinsicWidth()) {
                    mEditText.setText("");
                }
                return false;
            }
        });

    }

    private class SearchContentAdapter extends PagerAdapter {
        private List<View> views;

        SearchContentAdapter(List<View> views) {
            super();
            this.views = views;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

//        @Override
//        public void destroyItem(View arg0, int arg1, Object arg2) {
//            ((ViewPager) arg0).removeView(views.get(arg1));
//        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

//        @Override
//        public Object instantiateItem(View arg0, int arg1) {
//            ((ViewPager) arg0).addView(views.get(arg1));
//            return views.get(arg1);
//        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return views.size();
        }
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    // 加载搜索历史列表
    private void loadHistory(String field) {
        SharedPreferences sp = getSharedPreferences(field, Context.MODE_PRIVATE);
        searchHistoryList = new ArrayList<>();
        int size = sp.getInt("Status_size", 0);
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                searchHistoryList.add(sp.getString("Status_" + i, ""));
            }
        }
    }

    /**
     * 把指定的字符串内容保存到sharedPreference中指定的字符段
     *
     * @param field 保存在sharedPreference中的字段名
     * @param text  要保存的字符串
     */
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

    private void setSearchState(boolean isSearch) {
        int visibility = isSearch ? View.GONE : View.VISIBLE;
        int visibility1 = isSearch ? View.VISIBLE : View.GONE;
        findViewById(R.id.ll_search_tips).setVisibility(visibility);
        vp_content.setVisibility(visibility1);
        findViewById(R.id.radiogroup).setVisibility(visibility1);
    }

    /**
     * 搜索方法
     */
    private SearchResultActivity.SearchShop searchShop;
    private SearchProductResultActivity.SearchProduct searchProduct;

    private void search(String keyword) {
        if (keyword.equals("") || isLoading()) {
            return;
        }
        mEditText.setText(keyword);
        mEditText.setSelection(keyword.length());
        setSearchState(true);
        showLoadDialog();
        /**
         * 搜索产品
         */
        searchProduct = new SearchProductResultActivity.SearchProduct(keyword) {
            private SearchProductAdapter adapter;

            public void content(List<Map<String, Object>> result) {
                hideLoadDialog();
                View v = vp_content.getChildAt(0);
                ListView lv = (ListView) v.findViewById(R.id.lv_search_content);
                TextView empty_tips = (TextView) v.findViewById(R.id.tips_empty);
                v.findViewById(R.id.tips_loading).setVisibility(View.GONE);
                int visi = result == null ? View.VISIBLE : View.GONE;
                int visi1 = result == null ? View.GONE : View.VISIBLE;
                empty_tips.setVisibility(visi);
                lv.setVisibility(visi1);

                if (result != null) {
                    if (adapter == null) {
                        adapter = new SearchProductAdapter(getContext(), result);
                        lv.setAdapter(adapter);
                    } else {
                        adapter.setData(result);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        };
        searchProduct.execute();
        /**
         * 搜索商店
         */
        searchShop = new SearchResultActivity.SearchShop(keyword) {
            private SearchShopAdapter adapter;

            public void content(List<Map<String, Object>> result) {
                hideLoadDialog();
                View v = vp_content.getChildAt(1);
                ListView lv = (ListView) v.findViewById(R.id.lv_search_content);
                TextView empty_tips = (TextView) v.findViewById(R.id.tips_empty);
                v.findViewById(R.id.tips_loading).setVisibility(View.GONE);
                int visi = result == null ? View.VISIBLE : View.GONE;
                int visi1 = result == null ? View.GONE : View.VISIBLE;
                empty_tips.setVisibility(visi);
                lv.setVisibility(visi1);
                if (result != null) {
                    if (adapter == null) {
                        adapter = new SearchShopAdapter(getContext(), result);
                        lv.setAdapter(adapter);
                    } else {
                        adapter.setData(result);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        };
        searchShop.execute();
        searchHistoryList.remove(keyword);
        searchHistoryList.add(0, keyword);
        if (searchHistoryList.size() > 20) {
            searchHistoryList.remove(20);
        }
        saveHistory(HISTORY_URL, keyword);
    }
}