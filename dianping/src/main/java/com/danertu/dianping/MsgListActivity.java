package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/*  
 * 留言列表
 * liujun
 * 
 */
public class MsgListActivity extends BaseActivity implements OnClickListener {

    private ListView msgListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_list);
        initTitle("消息列表");
        findViewById();
        initView();
    }

    private void initTitle(String string) {
        Button b_title = (Button) findViewById(R.id.b_order_title_back);
        b_title.setText(string);
        b_title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    protected void findViewById() {
        msgListView = (ListView) findViewById(R.id.msgList);
        // contentviTextView=(TextView)findViewById(R.id.content);
        // uptimeTextView=(TextView)findViewById(R.id.uptime);
    }

    protected void initView() {
        try {
            bindListData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        msgListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                // setTitle("GUID:"+section);
                HashMap item = (HashMap) arg0.getItemAtPosition(arg2);
                String guid = String.valueOf(item.get("guid").toString());
                String uid = String.valueOf(item.get("uid").toString());
                Intent toDetail = new Intent();
                toDetail.setClassName(getApplicationContext(), "com.danertu.dianping.MessageDetailActivity");
                Bundle bundle = new Bundle();
                bundle.putString("guid", guid);
                bundle.putString("uid", uid);
                toDetail.putExtras(bundle);
                startActivity(toDetail);
            }
        });

    }

    public void bindListData() {
        List<Map<String, Object>> list = new ArrayList<>();

        Cursor cursor = db.GetMessage(MsgListActivity.this, db.GetLoginUid(MsgListActivity.this));
        while (cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<>();
            String cString = null;
            if (cursor.getString(0).length() > 20) {
                cString = cursor.getString(0).substring(0, 20) + ".........";
            } else {
                cString = cursor.getString(0);
            }
            map.put("content", cString);
            map.put("uid", cursor.getString(1));
            map.put("upTime", cursor.getString(4));
            map.put("guid", cursor.getString(5));
            list.add(map);
        }
        cursor.close();
        SimpleAdapter adapter = new SimpleAdapter(MsgListActivity.this, list, R.layout.message_item, new String[]{"content", "upTime"}, new int[]{R.id.content, R.id.uptime});
        try {
            msgListView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }


}
