package com.danertu.dianping;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
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
import com.danertu.tools.NoticeManager;
import com.danertu.widget.CommonTools;

public class MessageCenterActivity extends BaseActivity implements OnItemClickListener {

    private ArrayList<Messagebean> dataList = new ArrayList<>();
    private ListView mList;
    private MessageAdapter adapter;
    private TextView noResult;
    private Button btnBack;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        setSystemBarColor("#ff3333");
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
                    case NoticeManager.GETMSG_COMPLETE:
                        hideLoadDialog();
                        if (NoticeManager.getInstance().hasNewMsg()) {
                            NoticeManager.getInstance().resetUnsetMsgCount();
                            dataList = NoticeManager.getInstance().getMsgLists();
                            adapter.notifyDataSetChanged();
                        } else {
                            noResult.setVisibility(View.VISIBLE);
                        }
                        break;

                    default:
                        break;
                }
            }

        };
        showLoadDialog();
        //传入一个handler, 异步任务完成时通知主线程处理
        NoticeManager.getInstance().setHandler(mHandler);
        String memberid = getIntent().getStringExtra("memberid");
        if (TextUtils.isEmpty(memberid)) {
            NoticeManager.getInstance().undateMsg();
        } else {
            NoticeManager.getInstance().updateMsg(memberid);
        }
    }

    @Override
    protected void findViewById() {
        tvTitle = ((TextView) findViewById(R.id.tv_title));
        tvTitle.setText("新闻中心");
        tvTitle.setTextColor(ContextCompat.getColor(this,R.color.white));
        findViewById(R.id.b_title_operation) .setVisibility(View.GONE);
        noResult = (TextView) findViewById(R.id.noresult_text);
        btnBack = (Button) findViewById(R.id.b_title_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        mList = (ListView) findViewById(R.id.msgList);
    }

    @Override
    protected void initView() {
        adapter = new MessageAdapter();
        mList.setAdapter(adapter);
        mList.setOnItemClickListener(this);
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
                convertView = LayoutInflater.from(MessageCenterActivity.this).inflate(R.layout.message_list_item, null, false);
                viewHolder=new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder= ((ViewHolder) convertView.getTag());
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
        public void clearData(){
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


}
