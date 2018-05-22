package com.danertu.dianping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.entity.Messagebean;
import com.danertu.tools.DateTimeUtils;
import com.danertu.tools.Logger;
import com.danertu.tools.NoticeManager;
import com.danertu.tools.SPTool;
import com.danertu.widget.CommonTools;
import com.danertu.widget.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import static com.danertu.entity.Messagebean.NOTICE_TYPE_ORDER;
import static com.danertu.entity.Messagebean.NOTICE_TYPE_SYSTEM;
import static com.danertu.tools.SPTool.SP_MESSAGE;
import static com.danertu.tools.SPTool.SP_MESSAGE_CLEAR;
import static com.danertu.tools.SPTool.SP_MESSAGE_CLEAR_TIME;

/**
 * 消息中心清空逻辑
 * 1.删除本地通知表(tb_notice)中当前账号的记录
 * 2.记录当前列表中最新的时间，下次加载页面时取出此时间，在这时间之前的数据都不予显示
 */
public class MessageCenterActivity extends HomeActivity implements OnItemClickListener, View.OnClickListener, XListView.IXListViewListener {

    private ArrayList<Messagebean> dataList = new ArrayList<>();
    private ArrayList<Messagebean> localNoticeList = new ArrayList<>();
    private XListView mList;
    private MessageAdapter adapter;
    private TextView tvNoResult;
    private TextView tvTitle;
    private Button btnClear;
    private boolean isCleared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        setSystemBarWhite();
        initData();
        localNoticeList.addAll(getLocalNotice());
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
                            if (isCleared) {
                                String clearTime = SPTool.getString(context, SP_MESSAGE, SP_MESSAGE_CLEAR_TIME);
                                List<Messagebean> list = new ArrayList<>();
                                ArrayList<Messagebean> msgLists = NoticeManager.getInstance().getMsgLists();
                                for (Messagebean messagebean : msgLists) {
                                    //比较两个日期,如果日期相等返回0；小于0，参数date1就是在date2之后,大于0，参数date1就是在date2之前
                                    if (DateTimeUtils.compareDate(clearTime, messagebean.getModiflyTime()) > 0) {
                                        list.add(messagebean);
                                    } else {
                                        break;
                                    }
                                }
                                dataList.addAll(list);
                            } else {
                                dataList.addAll(NoticeManager.getInstance().getMsgLists());
                            }
                            dataList.addAll(localNoticeList);
                            Logger.e(TAG, localNoticeList.toString());
                            sortData();
                            Logger.e(TAG, "排序后的数据:" + dataList.toString());
                            adapter.notifyDataSetChanged();
                            if (dataList.size() <= 0) {
                                mList.setVisibility(View.GONE);
                                tvNoResult.setVisibility(View.VISIBLE);
                            }
                        } else {
                            tvNoResult.setVisibility(View.VISIBLE);
                        }
                        break;

                    default:
                        break;
                }
            }

        };
        showLoadDialog();
        isCleared = SPTool.getBoolean(context, SPTool.SP_MESSAGE, SP_MESSAGE_CLEAR);
        //传入一个handler, 异步任务完成时通知主线程处理
        NoticeManager.getInstance().setHandler(mHandler);
//        String memberid = getIntent().getStringExtra("memberid");
        String memberid = getUid();
        if (TextUtils.isEmpty(memberid)) {
            NoticeManager.getInstance().undateMsg();
        } else {
            NoticeManager.getInstance().updateMsg(memberid);
        }

    }

    /**
     * 重新排序数据
     */
    private void sortData() {
        Collections.sort(dataList, new DateComparator());
    }

    /**
     * 获取本地数据
     *
     * @return
     */
    private List<Messagebean> getLocalNotice() {
        return db.getNotice(context, getUid());
    }

    @Override
    protected void findViewById() {
        tvTitle = ((TextView) findViewById(R.id.tv_title));
        tvTitle.setText("消息中心");
        btnClear = ((Button) findViewById(R.id.b_title_operation));
        tvNoResult = (TextView) findViewById(R.id.noresult_text);
        mList = (XListView) findViewById(R.id.msgList);
        btnClear.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        mList.setPullRefreshEnable(false);
        mList.setPullLoadEnable(false);
        adapter = new MessageAdapter();
        mList.setAdapter(adapter);
        mList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_title_operation:
                //清空，
                //记录清空过记录
                SPTool.updateBoolean(context, SPTool.SP_MESSAGE, SPTool.SP_MESSAGE_CLEAR, true);
                //记录列表的最新时间
                for (Messagebean messagebean : dataList) {
                    int messageType = messagebean.getMessageType();
                    if (messageType != NOTICE_TYPE_SYSTEM && messageType != NOTICE_TYPE_ORDER) {
                        SPTool.updateString(context, SP_MESSAGE, SP_MESSAGE_CLEAR_TIME, messagebean.getModiflyTime());
                        break;
                    }
                }
                adapter.clearData();
                //删除本地通知
                db.deleteAllNotice(context, getUid());
                jsShowMsg("已清空");
                mList.setVisibility(View.GONE);
                tvNoResult.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 刷新
     */
    @Override
    public void onRefresh() {

    }

    /**
     * 加载更多
     */
    @Override
    public void onLoadMore() {

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
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = ((ViewHolder) convertView.getTag());
            }
            Messagebean messagebean = dataList.get(position);
            viewHolder.messageTitle.setText(messagebean.getMessageTitle());
            viewHolder.messageSub.setText(messagebean.getSubtitle());

            //时间判断，如果是今天的，就显示hh:mm，昨天则显示昨天，否则显示日期
            String displayTime = "";
            String modiflyTime = messagebean.getModiflyTime();
            String[] strings = modiflyTime.split(" ");
            String dateStr = strings[0].replace("/", "-");
            if (DateTimeUtils.isToday(dateStr)) {
                //是今天
                displayTime = DateTimeUtils.formatDateStr(strings[1], "HH:mm");
            } else if (DateTimeUtils.isYesterday(dateStr, "yyyy-MM-dd")) {
                //是昨天
                displayTime = "昨天";
            } else {
                displayTime = dateStr;
            }
            viewHolder.messageTime.setText(displayTime);
            //缩略图显示
            switch (messagebean.getMessageType()) {
                case NOTICE_TYPE_SYSTEM:
                    viewHolder.ivMessageImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_system_news));
                    break;
                case NOTICE_TYPE_ORDER:
                    viewHolder.ivMessageImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_msg_order));
                    break;
                default:
                    String image = messagebean.getImage();
                    if (TextUtils.isEmpty(image)) {
                        viewHolder.ivMessageImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_system_news));
                    } else {
                        ImageLoader.getInstance().displayImage(image, viewHolder.ivMessageImage);
                    }
                    break;
            }
            return convertView;
        }

        class ViewHolder {
            TextView messageTitle;
            TextView messageTime;
            TextView messageSub;
            ImageView ivMessageImage;

            public ViewHolder(View view) {
                messageTitle = (TextView) view.findViewById(R.id.message_title);
                messageTime = (TextView) view.findViewById(R.id.message_time);
                messageSub = (TextView) view.findViewById(R.id.message_sub);
                ivMessageImage = (ImageView) view.findViewById(R.id.iv_message_image);
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
        Messagebean bean = dataList.get(position - 1);
        if (bean.getMessageType() == NOTICE_TYPE_ORDER) {
            //如果是订单，跳转至订单中心
            jsToOrderActivity(0);
        } else {
            Intent it = new Intent(this, MessageDetail.class);
            Bundle bundle = new Bundle();
//    bundle.putString("url", "http://192.168.1.129:778/AnnouncementDetail.htm");
            bundle.putString("url", Constants.appWebPageUrl + WEB_PAGE_NAME);
            bundle.putString(Messagebean.COL_ID, bean.getId());
            bundle.putString(Messagebean.COL_MESSAGETITLE, bean.getMessageTitle());
            bundle.putString(Messagebean.COL_MODIFLYTIME, bean.getModiflyTime());
            it.putExtras(bundle);
            startActivity(it);
        }

    }

    /**
     * 日期比较器
     */
    class DateComparator implements Comparator<Messagebean> {

        @Override
        public int compare(Messagebean o1, Messagebean o2) {
            String modiflyTime1 = o1.getModiflyTime().replace("/","-");
            String modiflyTime2 = o2.getModiflyTime().replace("/","-");

            //小于0，参数date1就是在date2之后,大于0，参数date1就是在date2之前
            boolean result = DateTimeUtils.compareDate2(modiflyTime1, modiflyTime2);
            if (result){
                return -1;
            }else {
                return 1;
            }
        }
    }
}
