package com.danertu.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.config.Constants;
import com.danertu.entity.Messagebean;
import com.google.gson.Gson;


public class NoticeManager {

    //	private BaseHandler myHandler;
    private ArrayList<Messagebean> msgList;
    private String result;
    private Boolean hasNewMsg;
    private int mNewMsgCount;
    private Handler mHandler;
    public static final int GETMSG_COMPLETE = 7;
    private static final String TAG = "NoticeManager";

    // single instance for login
    private static NoticeManager mNoticeManager = null;

    public static final int TASK_GETMSG_ID = 7;

    public static NoticeManager getInstance() {
        if (mNoticeManager == null) {
            synchronized (NoticeManager.class) {
                if (mNoticeManager == null)
                    mNoticeManager = new NoticeManager();
            }
        }
        return mNoticeManager;
    }

    public NoticeManager() {
        this.msgList = new ArrayList<>();
        this.result = "";
        this.hasNewMsg = false;
        this.mHandler = null;
        this.mNewMsgCount = 0;
    }

    public Boolean hasNewMsg() {
        return this.hasNewMsg;
    }

    public void undateMsg() {
        // get all msg list
        updateMsg("");
    }

    /**
     * 更新消息列表
     *
     * @param memberId
     */
    public void updateMsg(String memberId) {
        Hashtable<String, String> msgParams = new Hashtable<>();
        msgParams.put("apiid", "0007");
        msgParams.put("memberId", memberId);
        this.doTaskAsync(GETMSG_COMPLETE, "", msgParams);
    }

    public String getMsg() {
        return result;
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public int getUnreadMsgCount() {
        return this.mNewMsgCount;
    }

    public void resetUnsetMsgCount() {
        this.mNewMsgCount = 0;
    }

    public ArrayList<Messagebean> getMsgLists() {
        return this.msgList;
    }


    public void sendMessage(int what, int taskId, String data) {
        Bundle b = new Bundle();
        b.putInt("task", taskId);
        b.putString("data", data);
        Message m = new Message();
        m.what = what;
        m.setData(b);
        mHandler.sendMessage(m);
    }

    public void sendMessage(int what) {
        if (mHandler != null) {
            Message m = new Message();
            m.what = what;
            mHandler.sendMessage(m);
        }
    }


    public void doTaskAsync() {
        onTaskComplete();
    }

    public void doTaskAsync(final int taskId, final String taskUrl) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AppClient client = new AppClient(taskUrl);
                    String httpResult = client.get();
                    onTaskComplete(taskId, httpResult);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void doTaskAsync(final int taskId, final String taskUrl, final Hashtable<String, String> taskArgs) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AppClient client = new AppClient(taskUrl);
                    String httpResult = client.post(taskArgs);

                    onTaskComplete(taskId, httpResult);
//                    if (httpResult != null) {
//                        onTaskComplete(taskId, httpResult);
//                    } else {
//                        onError(Constants.err.MESSAGE);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void onTaskComplete(int taskId, String result) {
        try {
//				BaseMessage MESSAGE = AppUtil.getMessage(result, "messageList");
//				msgList = (ArrayList<Messagebean>) MESSAGE.getResultList("messagebean");
            msgList = analysisJson(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.e("Message", "msgList size=" + msgList.size());
        Logger.e("Message", "msgList=" + msgList);
        hasNewMsg = msgList.size() >= 0;
        this.mNewMsgCount = msgList.size();
        sendMessage(GETMSG_COMPLETE);
    }

    /**
     * json解析
     *
     * @param jsonStr json
     * @return list
     * @throws JSONException
     */
    public ArrayList<Messagebean> analysisJson(String jsonStr) throws JSONException {
        ArrayList<Messagebean> results = new ArrayList<>();

        JSONObject obj = new JSONObject(jsonStr).getJSONObject("messageList");
        JSONArray arr = obj.getJSONArray("messagebean");

        Gson gson = new Gson();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject oj = arr.getJSONObject(i);

            Messagebean json = gson.fromJson(oj.toString(), Messagebean.class);

//            Messagebean item = new Messagebean();
//            item.setId(oj.getString(Messagebean.COL_ID));
//            item.setMessageTitle(oj.getString(Messagebean.COL_MESSAGETITLE));
//            item.setModiflyTime(oj.getString(Messagebean.COL_MODIFLYTIME));
//            item.setSubtitle(oj.get(Messagebean.COL_SUBTITLE)==null?"":oj.getString(Messagebean.COL_SUBTITLE));
//            item.setImage(oj.get(Messagebean.COL_IMAGE)==null?"":oj.getString(Messagebean.COL_IMAGE));
//            results.add(item);

            results.add(json);
        }
        return results;
    }

    public void onTaskComplete() {

    }

    public void onError(String error) {
        Log.w(TAG, error);
    }


}
