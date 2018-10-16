package com.danertu.dianping;

import android.content.Context;
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
import android.widget.TextView;

import com.config.Constants;
import com.danertu.entity.Messagebean;
import com.danertu.tools.DateTimeUtils;
import com.danertu.widget.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.danertu.entity.Messagebean.NOTICE_TYPE_ORDER;
import static com.danertu.entity.Messagebean.NOTICE_TYPE_SYSTEM;

/**
 * 消息中心清空逻辑
 * 1.删除本地通知表(tb_notice)中当前账号的记录
 * 2.记录当前列表中最新的时间，下次加载页面时取出此时间，在这时间之前的数据都不予显示
 */
public class SystemMessageActivity extends BaseActivity implements OnItemClickListener, View.OnClickListener, XListView.IXListViewListener {

    private ArrayList<Messagebean> dataList = new ArrayList<>();
    private XListView mList;
    private MessageAdapter adapter;
    private TextView tvNoResult;
    private TextView tvTitle;
    private TextView tvBack;
    private Button btnClear;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_message);
        context = this;
        setSystemBarWhite();
        findViewById();
        initView();
        initData();
    }

    private void initData() {
        dataList.addAll(getLocalNotice());
        adapter = new MessageAdapter();
        mList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (dataList.size() <= 0) {
            mList.setVisibility(View.GONE);
            tvNoResult.setVisibility(View.VISIBLE);
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
        btnClear = ((Button) findViewById(R.id.b_title_operation));
        tvNoResult = (TextView) findViewById(R.id.noresult_text);
        mList = (XListView) findViewById(R.id.msgList);
        tvBack = $(R.id.tv_back);
        tvBack.setOnClickListener(this);
        btnClear.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        mList.setPullRefreshEnable(false);
        mList.setPullLoadEnable(false);
        mList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_title_operation:
                adapter.clearData();
                //删除本地通知
                db.deleteAllNotice(context, getUid());
                jsShowMsg("已清空");
                mList.setVisibility(View.GONE);
                tvNoResult.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_back:
                finish();
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
                convertView = LayoutInflater.from(SystemMessageActivity.this).inflate(R.layout.message_list_item, null, false);
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
            String modiflyTime1 = o1.getModiflyTime().replace("/", "-");
            String modiflyTime2 = o2.getModiflyTime().replace("/", "-");

            //小于0，参数date1就是在date2之后,大于0，参数date1就是在date2之前
            boolean result = DateTimeUtils.compareDate2(modiflyTime1, modiflyTime2);
            if (result) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
