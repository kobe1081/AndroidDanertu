package com.danertu.dianping;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.entity.Messagebean;
import com.danertu.tools.AsyncTask;

import org.json.JSONArray;

import java.util.ArrayList;


/**
 * 首页消息中心
 */
public class MessageActivity extends HomeActivity implements OnItemClickListener, View.OnClickListener {

    private ArrayList<Messagebean> dataList = new ArrayList<>();
    private ListView mList;
    private MessageAdapter adapter;
    private TextView tvNoResult;
    private TextView tvTitle;
    private Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        setSystemBarWhite();
        initData();
        findViewById();
        initView();
    }

    Handler mHandler;

    /**
     * 初始化
     */
    private void initData() {
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {

                    default:
                        break;
                }
            }

        };
        showLoadDialog();
        new GetMessageData().execute();
    }

    @Override
    protected void findViewById() {
        tvTitle = ((TextView) findViewById(R.id.tv_title));
        tvTitle.setText("消息中心");
        btnClear = ((Button) findViewById(R.id.b_title_operation));
        tvNoResult = (TextView) findViewById(R.id.noresult_text);
        mList = (ListView) findViewById(R.id.msgList);
        btnClear.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        adapter = new MessageAdapter();
        mList.setAdapter(adapter);
        mList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_title_operation:

                break;
        }
    }

    /**
     * 适配器
     */
    private class MessageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(MessageActivity.this).inflate(R.layout.message_list_item, null, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = ((ViewHolder) convertView.getTag());
            }
            viewHolder.messageTitle.setText(dataList.get(position).getMessageTitle());
            viewHolder.messageTime.setText(dataList.get(position).getModiflyTime().split(" ")[0]);
            viewHolder.messageSub.setText(dataList.get(position).getMessageTitle());
            return convertView;
        }

        class ViewHolder {
            TextView messageTitle;
            TextView messageTime;
            TextView messageSub;

            public ViewHolder(View view) {
                messageTitle = (TextView) view.findViewById(R.id.message_title);
                messageTime = (TextView) view.findViewById(R.id.message_time);
                messageSub = (TextView) view.findViewById(R.id.message_sub);
            }
        }

        public void clearData() {
            dataList.clear();
            adapter.notifyDataSetChanged();
        }
    }

    final String WEB_PAGE_NAME = "AnnouncementDetail.htm";

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent it = new Intent(this, MessageDetail.class);
        Bundle bundle = new Bundle();
//    bundle.putString("url", "http://192.168.1.129:778/AnnouncementDetail.htm");
        bundle.putString("url", Constants.appWebPageUrl + WEB_PAGE_NAME);
        Messagebean bean = dataList.get(position);
        bundle.putString(Messagebean.COL_ID, bean.getId());
        bundle.putString(Messagebean.COL_MESSAGETITLE, bean.getMessageTitle());
        bundle.putString(Messagebean.COL_MODIFLYTIME, bean.getModiflyTime());
        it.putExtras(bundle);
        startActivity(it);
    }


    class GetMessageData extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... param) {

            return appManager.postGetMessage(getUid());
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            hideLoadDialog();
            try {
                ArrayList<Messagebean> results = new ArrayList<>();
                org.json.JSONObject obj = new org.json.JSONObject(result).getJSONObject("messageList");
                JSONArray arr = obj.getJSONArray("messagebean");
                for (int i = 0; i < arr.length(); i++) {
                    org.json.JSONObject oj = arr.getJSONObject(i);
                    Messagebean json = gson.fromJson(oj.toString(), Messagebean.class);
                    results.add(json);
                }

                dataList.addAll(results);
                adapter.notifyDataSetChanged();
                if (dataList.size() <= 0) {
                    mList.setVisibility(View.GONE);
                    tvNoResult.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                if (Constants.isDebug)
                    e.printStackTrace();
                tvNoResult.setVisibility(View.VISIBLE);
            }
        }
    }

}
