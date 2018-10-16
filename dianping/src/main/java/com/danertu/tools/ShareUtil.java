package com.danertu.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.system.email.Email;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.dianping.HtmlActivityNew;
import com.danertu.dianping.R;
import com.danertu.download.FileUtil;
import com.danertu.widget.CommonTools;

public class ShareUtil {
    private Context context;
    private String title;
    private String imgPath;
    private String targetPath;
    private String description;
    private LoadingDialog lDialog;
    private String uid;

    public ShareUtil(Context context) {
        this.context = context;
        lDialog = new LoadingDialog(context);
        uid = DBManager.getInstance().GetLoginUid(context);
    }

    private void onlyShow(OnekeyShare oks, String platformList) {
        if (TextUtils.isEmpty(platformList)) {
            return;
        }
        String[] platforms = platformList.split("&");
        Platform[] ps = ShareSDK.getPlatformList();
        for (Platform item : ps) {
            String psName = item.getName();
            boolean isHide = true;
            for (String itemName : platforms) {
                if (itemName.equalsIgnoreCase(psName)) {
                    isHide = false;
                    break;
                }
            }
            if (isHide) {
                oks.addHiddenPlatform(psName);
            }
        }
    }

    public void share(String type, String shopid, String title, String imgPath, String targetPath, String description, String onlyShow) {
        Logger.e("share", type + "/" + shopid + "/" + title + "/" + imgPath + "/" + targetPath + "/" + description + "/" + onlyShow);
        if (TextUtils.isEmpty(title)) {
            title = "单耳兔商城";
        }
        if (TextUtils.isEmpty(imgPath)) {
            imgPath = Constants.APP_URL.imgServer + "/pptImage/icon.png";
        }

        if (type.equals("android")) {
            if (TextUtils.isEmpty(description))
                description = context.getString(R.string.share_text);
            if (TextUtils.isEmpty(targetPath))
                targetPath = Constants.APP_URL.SHOP_SHARE_URL + shopid;
        } else if (type.equals("ios")) {
            if (TextUtils.isEmpty(description))
                description = context.getString(R.string.share_text);
            if (TextUtils.isEmpty(targetPath))
                targetPath = Constants.APP_URL.SHOP_SHARE_URL + shopid;
        }
        this.description = description;
        this.imgPath = imgPath;
        this.targetPath = targetPath;
        this.title = title;
        new GetImgToShare(onlyShow).execute(this.imgPath);
    }



    public void share(String type, String shopid, String title, String imgPath, String targetPath, String description) {
        share(type, shopid, title, imgPath, targetPath, description, null);
    }

    private File shareImg = null;

    public class GetImgToShare extends AsyncTask<String, Integer, String> {
        private String onlyShow;

        public GetImgToShare(String onlyShow) {
            super();
            this.onlyShow = onlyShow;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lDialog.show();
        }

        @Override
        protected String doInBackground(String... param) {
            String fImgPath = null;
            String imgUrl = param[0];
            String imgName = FileUtil.getFileName(imgUrl);
            String dir = FileUtil.setMkdir(context);
            dir += File.separator + "shareImg";
            Logger.e("file", "imgUrl=" + imgUrl + ",imgName=" + imgName + ",dir=" + dir);
            shareImg = new File(dir);

            if (!shareImg.exists()) {
                shareImg.mkdir();
            }
            shareImg = new File(dir, imgName);
            if (shareImg.exists()) {
                //删除
                shareImg.delete();
                shareImg = new File(dir, imgName);
            }

            InputStream in = null;
            FileOutputStream out = null;
            HttpURLConnection conn = null;
            try {
                URL url = new URL(imgUrl);
                conn = (HttpURLConnection) url.openConnection();
                int rCode = conn.getResponseCode();
                if (rCode == 200) {
                    in = conn.getInputStream();
                    out = new FileOutputStream(shareImg);
                    int len = 0;
                    byte buffer[] = new byte[1024];
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    out.close();
                    in.close();
                } else {
                    imgPath = Constants.APP_URL.imgServer + "/pptImage/icon.png";
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
                try {
                    if (in != null)
                        in.close();
                    if (out != null)
                        out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            fImgPath = shareImg.getAbsolutePath();
            return fImgPath;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            showShare(context, null, true, onlyShow);
            lDialog.dismiss();
        }

    }

    /**
     * 演示调用ShareSDK执行分享
     *
     * @param context
     * @param platformToShareStr 指定直接分享平台名称（一旦设置了平台名称，则九宫格将不会显示）
     * @param showContentEdit    是否显示编辑页
     */

//    public void showShare(final Context context, String platformToShareStr, boolean showContentEdit, String onlyShow) {
//        OnekeyShare oks = new OnekeyShare();
//        onlyShow(oks, onlyShow);
//        oks.setSilent(!showContentEdit);
//        if (platformToShareStr != null) {
//            oks.setPlatform(platformToShareStr);
//        }
//        //ShareSDK快捷分享提供两个界面第一个是九宫格 CLASSIC  第二个是SKYBLUE
//        oks.setTheme(OnekeyShareTheme.CLASSIC);
//        // 令编辑页面显示为Dialog模式
//        oks.setDialogMode(true);
//        // 在自动授权时可以禁用SSO方式
//        oks.disableSSOWhenAuthorize();
//        //oks.setAddress("12345678901"); //分享短信的号码和邮件的地址
//        oks.setText(description);
//        oks.setTitle(title);
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(targetPath);
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        if (shareImg.isFile()) {
//            oks.setImagePath(shareImg.getAbsolutePath());
//        } else {
//            oks.setImageUrl(imgPath);
//        }
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(targetPath);
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        // oks.setComment("我是测试评论文本");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(context.getString(R.string.app_name));
//        //oks.setFilePath("/sdcard/test-pic.jpg");  //filePath是待分享应用程序的本地路劲，仅在微信（易信）好友和Dropbox中使用，否则可以不提供
////		oks.setComment("分享"); //我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
//        oks.setSiteUrl("http://mob.com");//QZone分享参数
////		oks.setVenueName("ShareSDK");
////		oks.setVenueDescription("This is a beautiful place!");
//        // 将快捷分享的操作结果将通过OneKeyShareCallback回调
//        //oks.setCallback(new OneKeyShareCallback());
//        oks.setCallback(new PlatformActionListener() {
//            @Override
//            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                CommonTools.showShortToast(context, "分享成功");
//            }
//
//            @Override
//            public void onError(Platform platform, int i, Throwable throwable) {
//                CommonTools.showShortToast(context, "分享失败");
//            }
//
//            @Override
//            public void onCancel(Platform platform, int i) {
//                CommonTools.showShortToast(context, "您已取消分享");
//            }
//        });
//        // 去自定义不同平台的字段内容
//        //oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());
//        // 在九宫格设置自定义的图标
////		 Bitmap enableLogo = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
////		 Bitmap disableLogo = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
////		 String label = "ShareSDK";
////		 OnClickListener listener = new OnClickListener() {
////		 	public void onClick(View v) {
////
////		 	}
////		 };
////		 oks.setCustomerLogo(enableLogo, disableLogo, label, listener);
//
//        // 为EditPage设置一个背景的View
//        //oks.setEditPageBackground(getPage());
//        // 隐藏九宫格中的新浪微博
//        // oks.addHiddenPlatform(SinaWeibo.NAME);
//
//        // String[] AVATARS = {
//        // 		"http://99touxiang.com/public/upload/nvsheng/125/27-011820_433.jpg",
//        // 		"http://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339485237265.jpg",
//        // 		"http://diy.qqjay.com/u/files/2012/0523/f466c38e1c6c99ee2d6cd7746207a97a.jpg",
//        // 		"http://diy.qqjay.com/u2/2013/0422/fadc08459b1ef5fc1ea6b5b8d22e44b4.jpg",
//        // 		"http://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339510584349.jpg",
//        // 		"http://diy.qqjay.com/u2/2013/0401/4355c29b30d295b26da6f242a65bcaad.jpg" };
//        // oks.setImageArray(AVATARS);              //腾讯微博和twitter用此方法分享多张图片，其他平台不可以
//
//        // 启动分享
//        oks.show(context);
//    }
    public void showShare(final Context context, final String platformToShareStr, boolean showContentEdit, String onlyShow) {
        OnekeyShare oks = new OnekeyShare();
        onlyShow(oks, onlyShow);
        oks.setSilent(!showContentEdit);
        if (platformToShareStr != null) {
            oks.setPlatform(platformToShareStr);
        }
        //ShareSDK快捷分享提供两个界面第一个是九宫格 CLASSIC  第二个是SKYBLUE
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        // 令编辑页面显示为Dialog模式
        oks.setDialogMode(true);
        // 在自动授权时可以禁用SSO方式
        oks.disableSSOWhenAuthorize();
        oks.setText(description);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        if (shareImg.isFile()) {
            oks.setImagePath(shareImg.getAbsolutePath());
        } else {
            oks.setImageUrl(imgPath);
        }
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                //分享至微信
                if (Wechat.NAME.equals(platform.getName())) {
                    // url仅在微信（包括好友和朋友圈）中使用
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setTitleUrl(targetPath);
                    paramsToShare.setUrl(targetPath);
                }
                //微信朋友圈
                if (WechatMoments.NAME.equals(platform.getName())) {
                    // url仅在微信（包括好友和朋友圈）中使用
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setTitleUrl(targetPath);
                    paramsToShare.setUrl(targetPath);
                    Logger.e("share", "paramsToShare.toString()=" + paramsToShare.toString());

                }
                if (WechatFavorite.NAME.equals(platform.getName())) {
                    // url仅在微信（包括好友和朋友圈）中使用
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    paramsToShare.setTitle(title);
                    paramsToShare.setTitleUrl(targetPath);
                    paramsToShare.setUrl(targetPath);
                }
                //分享至QQ
                if (QQ.NAME.equals(platform.getName())) {
                    paramsToShare.setTitle(title);
                    paramsToShare.setTitleUrl(targetPath);
                    paramsToShare.setUrl(targetPath);
                    // site是分享此内容的网站名称，仅在QQ空间使用
                    paramsToShare.setSite(context.getString(R.string.app_name));
                    paramsToShare.setSiteUrl("http://www.danertu.com");//QZone分享参数
                }
                //分享至QQ空间
                if (QZone.NAME.equals(platform.getName())) {
                    paramsToShare.setTitle(title);
                    paramsToShare.setTitleUrl(targetPath);
                    // site是分享此内容的网站名称，仅在QQ空间使用
                    paramsToShare.setSite(context.getString(R.string.app_name));
                    paramsToShare.setSiteUrl("http://www.danertu.com");//QZone分享参数
                    platform.isClientValid();
                }
                //分享至新浪微博
                if (SinaWeibo.NAME.equals(platform.getName())) {

                }
                //分享至腾讯微博
                if (TencentWeibo.NAME.equals(platform.getName())) {

                }
                //分享至短信
                if (ShortMessage.NAME.equals(platform.getName())) {
                    paramsToShare.setTitle(title);
                }
                //分享至Facebook
                if (Facebook.NAME.equals(platform.getName())) {

                }
                //分享至Twitter
                if (Twitter.NAME.equals(platform.getName())) {

                }
                //分享至Email
                if (Email.NAME.equals(platform.getName())) {
                    paramsToShare.setTitle(title);
                }

            }
        });
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                CommonTools.showShortToast(context, "分享成功");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                final String msg = "platform=" + platform + "------i=" + i + "----throwable=" + throwable;
                Log.e("Share Fail", msg);

                CommonTools.showShortToast(context, "分享失败");

                //将失败信息反馈给服务器
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 耗时操作
                        try {
                            String result = AppManager.getInstance().sendErrInfo("分享失败，message=：\n" + msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                CommonTools.showShortToast(context, "您已取消分享");
            }
        });
        // 启动分享
        oks.show(context);
    }

    /**
     * 分享图片
     *
     * @param title
     * @param text
     * @param imgPath
     */
    public void shareImg(String title, String text, String imgPath,final String platformList, final PlatformActionListener listener) {
        OnekeyShare oks = new OnekeyShare();
        onlyShow(oks, platformList);
        if (TextUtils.isEmpty(title)) {
            title = "单耳兔商城";
        }
        oks.setTitle(title);
        oks.setImagePath(imgPath);
        //ShareSDK快捷分享提供两个界面第一个是九宫格 CLASSIC  第二个是SKYBLUE
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        // 令编辑页面显示为Dialog模式
        oks.setDialogMode(true);
        // 在自动授权时可以禁用SSO方式
        oks.disableSSOWhenAuthorize();
        if (!TextUtils.isEmpty(text)) {
            oks.setText(text);
        }

        if (TextUtils.isEmpty(imgPath)) {
            CommonTools.showShortToast(context, "图片地址不能为空");
            return;
        }

//        final String finalTitle = title;
//        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
//            @Override
//            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
//                //分享至微信
//                if (Wechat.NAME.equals(platform.getName())) {
//                    // url仅在微信（包括好友和朋友圈）中使用
//                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
//                    paramsToShare.setTitle(finalTitle);
//                    paramsToShare.setTitleUrl(targetPath);
//                    paramsToShare.setUrl(targetPath);
//                }
//                //微信朋友圈
//                if (WechatMoments.NAME.equals(platform.getName())) {
//                    // url仅在微信（包括好友和朋友圈）中使用
//                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
//                    paramsToShare.setTitle(finalTitle);
//                    paramsToShare.setTitleUrl(targetPath);
//                    paramsToShare.setUrl(targetPath);
//                    Logger.e("share", "paramsToShare.toString()=" + paramsToShare.toString());
//
//                }
//                if (WechatFavorite.NAME.equals(platform.getName())) {
//                    // url仅在微信（包括好友和朋友圈）中使用
//                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
//                    paramsToShare.setTitle(finalTitle);
//                    paramsToShare.setTitleUrl(targetPath);
//                    paramsToShare.setUrl(targetPath);
//                }
//                //分享至QQ
//                if (QQ.NAME.equals(platform.getName())) {
//                    paramsToShare.setTitle(finalTitle);
//                    paramsToShare.setTitleUrl(targetPath);
//                    paramsToShare.setUrl(targetPath);
//                    // site是分享此内容的网站名称，仅在QQ空间使用
//                    paramsToShare.setSite(context.getString(R.string.app_name));
//                    paramsToShare.setSiteUrl("http://www.danertu.com");//QZone分享参数
//                }
//                //分享至QQ空间
//                if (QZone.NAME.equals(platform.getName())) {
//                    paramsToShare.setTitle(finalTitle);
//                    paramsToShare.setTitleUrl(targetPath);
//                    // site是分享此内容的网站名称，仅在QQ空间使用
//                    paramsToShare.setSite(context.getString(R.string.app_name));
//                    paramsToShare.setSiteUrl("http://www.danertu.com");//QZone分享参数
//                    platform.isClientValid();
//                }
//                //分享至新浪微博
//                if (SinaWeibo.NAME.equals(platform.getName())) {
//
//                }
//                //分享至腾讯微博
//                if (TencentWeibo.NAME.equals(platform.getName())) {
//
//                }
//                //分享至短信
//                if (ShortMessage.NAME.equals(platform.getName())) {
//                    paramsToShare.setTitle(finalTitle);
//                }
//                //分享至Facebook
//                if (Facebook.NAME.equals(platform.getName())) {
//
//                }
//                //分享至Twitter
//                if (Twitter.NAME.equals(platform.getName())) {
//
//                }
//                //分享至Email
//                if (Email.NAME.equals(platform.getName())) {
//                    paramsToShare.setTitle(finalTitle);
//                }
//
//            }
//        });
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                listener.onComplete(platform, i, hashMap);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                listener.onError(platform, i, throwable);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                listener.onCancel(platform, i);
            }
        });
        oks.show(context);
    }

    public void shareCallBack(String error, String name) {
//		if(webView != null){//js回调
//			webView.loadUrl(Constants.IFACE+"shareCallBack('"+error+"','"+name+"')");
//		}
        Logger.e("error", "error=" + error + "/" + name);
        if (shareImg.exists())
            shareImg.delete();
        if (TextUtils.isEmpty(error)) {
            new AsyncTask<String, Integer, Boolean>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    lDialog.show();
                }

                @Override
                protected Boolean doInBackground(String... param) {
                    String result = AppManager.getInstance().postRedPacket(uid, "", 3);
                    Logger.e("shareSDK", "ShareUtil  shareCallBack result=" + result);
                    String isSuccess = "false";
                    try {
                        JSONObject obj = new JSONObject(result);
                        isSuccess = obj.getString("result");
                        return Boolean.parseBoolean(isSuccess);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean isSuccess) {
                    super.onPostExecute(isSuccess);
                    lDialog.dismiss();
                    if (isSuccess) {
                        Intent i = new Intent(context, HtmlActivityNew.class);
                        i.putExtra("url", Constants.appWebPageUrl + "Activity/share_success_app.html?platform=android");
                        context.startActivity(i);
                    }
                }
            }.execute();
        }
    }
}
