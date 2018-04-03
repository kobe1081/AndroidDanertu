package com.danertu.dianping;

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

import java.util.ArrayList;

/**
 * 旧
 */
public class MessageCenterActivityold extends BaseActivity implements OnItemClickListener {

    private ArrayList<Messagebean> dataList = new ArrayList<>();
    private ListView mList;
    private MessageAdapter adapter;
    private TextView noResult;
    private Button btnBack;
    private TextView tvTitle;
    private Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        initData();
        findViewById();
        initView();
    }

    Handler mHandler;

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
        tvTitle.setText("消息中心");
        btnClear = ((Button) findViewById(R.id.b_title_operation));
        btnClear.setTextColor(ContextCompat.getColor(MessageCenterActivityold.this,R.color.gray_text_aaa));
        btnClear.setText("清空");
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
        //清空消息
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clearData();
                noResult.setVisibility(View.VISIBLE);
                CommonTools.showShortToast(MessageCenterActivityold.this,"已清空全部消息");
            }
        });
    }

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
                convertView = LayoutInflater.from(MessageCenterActivityold.this).inflate(R.layout.message_list_item, null, false);
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
