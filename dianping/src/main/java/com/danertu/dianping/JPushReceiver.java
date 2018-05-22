package com.danertu.dianping;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import cn.jpush.android.api.JPushInterface;

import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.entity.Messagebean;
import com.danertu.tools.DateTimeUtils;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;

import org.json.JSONException;
import org.json.JSONObject;

import static com.danertu.dianping.PersonalActivity.KEY_FROM_PUSH;
import static com.danertu.dianping.PersonalActivity.KEY_IS_ONLY_HOTEL;
import static com.danertu.dianping.PersonalActivity.KEY_TAB_INDEX;

/**
 * 极光推送的自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

//        String mess="";
//        for (String s : bundle.keySet()) {
//            mess+=s+":"+bundle.get(s)+",";
//        }
//        Logger.e("message",mess);
//        Logger.d(TAG, "[JPushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

//        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
//            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
//            Log.d(TAG, "[JPushReceiver] 接收Registration Id : " + regId) ;
//            //send the Registration Id to your server...
//
//        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//        	Log.d(TAG, "[JPushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//        	processCustomMessage(context, bundle);
//
//        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//            Log.d(TAG, "[JPushReceiver] 接收到推送下来的通知");
//            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//            Log.d(TAG, "[JPushReceiver] 接收到推送下来的通知的ID: " + notifactionId);
//
//        } else

        /**
         * {
         "val":{
         "Title":"a",
         "Content":"b",
         "ToRank":"0,1,2,-1",
         "Link":"11 http://xxxxx 3ds4g3ds51vs6f6wq3 null",
         "Method":"0/1/2/3/4"	//0:咨询中心的某个资讯 1：活动链接 2：商品详情 3：后台拿货
         }
         }
         */
        /**
         * 接受自定义消息，保存到sqlite中并使用通知栏通知用户
         */
        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

            String jPushString = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            Logger.e(TAG, "[JPushReceiver] 接收到推送下来的自定义消息: " + jPushString);

            //接收到自定义消息后保存到sqlite的JPushMessage表中
            try {
                JSONObject object = new JSONObject(jPushString);
                String title = object.getString("title");
                String message = object.getString("message");
                String pushTime = object.getString("pushTime");

                try {
                    String extraJson = bundle.getString(JPushInterface.EXTRA_EXTRA);
                    JSONObject jsonObject = new JSONObject(new JSONObject(extraJson).getString("val"));
                    JSONObject param = jsonObject.getJSONObject("param");
                    String option = jsonObject.getString("option");

                    /**接收到的是系统公告、订单通知，保存到本地数据库中*/
                    if ("4".equals(option) || "1".equals(option) || "2".equals(option) || "3".equals(option) || "5".equals(option) || "6".equals(option)) {
                        String image = param.get("image") == null ? "" : param.get("image").toString().replace("\\", "");
                        DBManager dbManager = DBManager.getInstance();
                        dbManager.insertNotice(context, dbManager.GetLoginUid(context), image, title, message, option, pushTime.replace("\\", ""));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //创建通知
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = new Notification.Builder(context)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setTicker("提示")
                        .setSmallIcon(R.drawable.icon)
                        .setAutoCancel(true)
                        //点击时跳转至消息中心
                        .setContentIntent(PendingIntent.getActivity(context, 0, new Intent().setClass(context, MessageCenterActivity.class), 0))
                        .build();
                manager.notify(1, notification);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Logger.e(TAG, "[JPushReceiver] 接收到推送下来的通知");
            Logger.e(TAG, "bundle数据==" + printBundle(bundle));
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            Logger.e(TAG, " title : " + title);
            String message = bundle.getString(JPushInterface.EXTRA_ALERT);
            Logger.e(TAG, "message : " + message);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Logger.e(TAG, "extras : " + extras);

            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Logger.e(TAG, "[JPushReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            try {
                String extraJson = bundle.getString(JPushInterface.EXTRA_EXTRA);
                JSONObject val = new JSONObject(new JSONObject(extraJson).getString("val"));
                JSONObject jsonObject = val.getJSONObject("param");
                String option = val.getString("option");

                /**接收到的通知，保存到本地数据库中*/
                if ("4".equals(option) || "1".equals(option) || "2".equals(option) || "3".equals(option) || "5".equals(option) || "6".equals(option)) {
                    Logger.e(TAG, "接收到系统公告/订单通知");
                    String modifyTime =  DateTimeUtils.getDateToyyyyMMddHHmmss();
                    String image = jsonObject.get("image") == null ? "" : jsonObject.get("image").toString().replace("\\", "");
                    DBManager dbManager = DBManager.getInstance();
                    dbManager.insertNotice(context, dbManager.GetLoginUid(context), image, title, message, option, modifyTime);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        /**
         * 点开通知的操作
         */

        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            String extraJson = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Logger.e(TAG, extraJson);
            try {
                JSONObject jsonObject = new JSONObject(new JSONObject(extraJson).getString("val"));
                JSONObject objectParam = null;
                String param = jsonObject.get("param").toString();
                if (!TextUtils.isEmpty(param)) {
                    objectParam = new JSONObject(param);
                }


                switch (jsonObject.getString("option")) {
                    case "1"://某个公告
                        openApp(context);
                        Intent intent1 = new Intent(context, MessageDetail.class);
                        intent1.putExtra("url", Constants.appWebPageUrl + objectParam.getString("url"));
                        intent1.putExtra(Messagebean.COL_ID, objectParam.getString("id"));
                        intent1.putExtra(Messagebean.COL_MESSAGETITLE, objectParam.getString("messageTitle"));
                        intent1.putExtra(Messagebean.COL_MODIFLYTIME, objectParam.getString("ModiflyTime"));
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        context.startActivity(intent1);
                        break;
                    case "2"://某个活动
                        openApp(context);
                        Intent intent2 = new Intent(context, HtmlActivityNew.class);
                        intent2.putExtra("url", objectParam.getString("url"));
                        //表示为通过推送打开的，需要拼接参数
                        intent2.putExtra("push", true);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        context.startActivity(intent2);
                        break;
                    case "3"://打开商品详情
                        openApp(context);
                        Intent intent3 = new Intent(context, ProductDetailsActivity2.class);
                        intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                        intent3.putExtra(ProductDetailsActivity2.KEY_CAN_BUY, objectParam.getString("canBuy"));
                        intent3.putExtra("guid", objectParam.getString("guid"));

                        context.startActivity(intent3);
                        break;
                    case "4"://打开订单页面（全部、未付款、已付款..）
                        //无法直接打开订单页面，所以先打开个人中心,由个人中心打开订单页面
                        //为与iOS一致，接收参数名修改
                        openApp(context);
                        Intent intent4 = new Intent(context, PersonalActivity.class);
                        intent4.putExtra(KEY_FROM_PUSH, true);
                        intent4.putExtra(KEY_TAB_INDEX, objectParam.getString("orderType"));
                        intent4.putExtra(KEY_IS_ONLY_HOTEL, objectParam.getBoolean("isShowHotel"));
                        intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        context.startActivity(intent4);
                        break;
                    case "5"://打开后台拿货页面
                        openApp(context);
                        Intent intent5 = new Intent(context, HtmlActivity.class);
                        intent5.putExtra("pageName", "seller_take.html");
                        intent5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        context.startActivity(intent5);
                        break;
                    case "6"://打开店铺订单页面
                        openApp(context);
                        Intent intent6 = new Intent(context, HtmlActivity.class);
                        intent6.putExtra("pageName", "deal_manage.html");
                        intent6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        context.startActivity(intent6);
                        break;
                    default://0：打开app，什么都不做
                        openApp(context);
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


//            Logger.d(TAG, "[JPushReceiver] 用户点击打开了通知");
//            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            String packageName = context.getPackageName();
//            if (!CommonTools.isAppIsInBackground(am, packageName))
//                return;
//
//            //打开自定义的Activity
//            Intent i = new Intent(context, SplashActivity.class);
////        	i.putExtras(bundle);
//            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            context.startActivity(i);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Logger.d(TAG, "[JPushReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Logger.w(TAG, "[JPushReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Logger.d(TAG, "[JPushReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    /**
     * 打开app
     *
     * @param context
     */
    private void openApp(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();
        if (CommonTools.isAppRunning(am, packageName)) {
            //app正在运行

            return;
        } else {
            Intent intent = new Intent(context, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
//        if (CommonTools.isAppIsInBackground(am, packageName)) {
//            Intent intent = new Intent(context, SplashActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            context.startActivity(intent);
//        }
    }

    // 打印所有的 intent extra 数据
    private String printBundle(Bundle bundle) {
        if (bundle == null)
            return null;
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
//	private void processCustomMessage(Context context, Bundle bundle) {
//		if (MainActivity.isForeground) {
//			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//			if (!ExampleUtil.isEmpty(extras)) {
//				try {
//					JSONObject extraJson = new JSONObject(extras);
//					if (null != extraJson && extraJson.length() > 0) {
//						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//					}
//				} catch (JSONException e) {
//
//				}
//
//			}
//			context.sendBroadcast(msgIntent);
//		}
//	}
}
