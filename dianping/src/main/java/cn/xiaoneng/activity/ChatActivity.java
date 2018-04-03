//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.xiaoneng.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danertu.dianping.R;

import cn.xiaoneng.adapter.ChatMsgAdapter;
import cn.xiaoneng.chatcore.GlobalParam;
import cn.xiaoneng.chatmsg.BaseMessage;
import cn.xiaoneng.chatmsg.ChatSystemMsg;
import cn.xiaoneng.chatsession.ChatScene;
import cn.xiaoneng.coreapi.ChatBaseUser;
import cn.xiaoneng.coreapi.ItemParamsBody;
import cn.xiaoneng.coreapi.SystemMessageBody;
import cn.xiaoneng.coreapi.TextMessageBody;
import cn.xiaoneng.coreapi.XNChatSDK;
import cn.xiaoneng.emotion.XNEmotion;
import cn.xiaoneng.image.ImageShow;
import cn.xiaoneng.uicore.ChatSessionData;
import cn.xiaoneng.uicore.OnToChatListener;
import cn.xiaoneng.uicore.XNSDKUICore;
import cn.xiaoneng.uicore.XNSDKUIListener;
import cn.xiaoneng.uiutils.XNUIUtils;
import cn.xiaoneng.uiview.FaceRelativeLayout;
import cn.xiaoneng.uiview.XNGeneralDialog;
import cn.xiaoneng.uiview.XNListView;
import cn.xiaoneng.uiview.XNGeneralDialog.OnCustomDialogListener;
import cn.xiaoneng.uiview.XNListView.OnRefreshListener;
import cn.xiaoneng.utils.TransferActionData;
import cn.xiaoneng.utils.XNCoreUtils;
import cn.xiaoneng.utils.XNLOG;
import cn.xiaoneng.utils.XNSPHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 2017年11月15日
 * 为解决客服页面经常性报空指针问题
 * 将xiaonengChatUI/libs/xiaonengchatui.jar包中此类拿出来
 */
public class ChatActivity extends Activity implements OnRefreshListener, OnToChatListener {
    private RelativeLayout sdk_chat_back;
    private TextView tv_setting_or_kefu_name;
    private ImageView finish_consult;
    private FaceRelativeLayout faceRelativeLayout;
    private FrameLayout fl_showtips;
    private TextView tv_showtips;
    private FrameLayout fl_showtips3;
    private Button bt_release;
    private FrameLayout fl_showtips2;
    private TextView tv_showtips2;
    private Button bt_refuse;
    private Button bt_accept;
    public RelativeLayout fl_Showgoods;
    private WebView wv_Goods;
    private ImageView iv_Goods;
    private TextView tv_Goodsname;
    private TextView tv_Goodsprice;
    private XNListView mListView;
    private ChatMsgAdapter mAdapter;
    private int lastvisible = 0;
    private String lasttipstr;
    private String tipstr;
    private String cancel;
    private String confirm;
    private String leave_confirm;
    private Animation alpha;
    private String _chatSessionId = null;
    private int _model;
    private ChatSessionData _chatData = null;
    private ProgressBar pb_requestkf;
    private int showMsgNumCount = 1;
    private long howmanytimecut_start = 0L;
    private boolean flag = true;
    private int _userType = 0;
    private String goodsPrice;
    private OnCustomDialogListener customDialogListener = new OnCustomDialogListener() {
        public void back(String name) {
            ChatActivity.this._chatData.toDestoryChatSession = false;
        }

        public void confirm(String name) {
            if(ChatActivity.this._chatData.toDestoryChatSession) {
                XNChatSDK.getInstance().stopChatBySession(ChatActivity.this._chatSessionId);
            }

            XNLOG.i(new String[]{"关闭聊窗ChatActivity", "6"});
            ChatActivity.this.closeChatWindow();
        }
    };

    public ChatActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(1);
        this.setContentView(R.layout.xn_activity_chatpage);
        this.howmanytimecut_start = System.currentTimeMillis();
        Intent intent = this.getIntent();
        this._chatSessionId = intent.getStringExtra("chatSessionId");
        this._model = intent.getIntExtra("model", 0);
        XNLOG.i(new String[]{"建立聊窗", "onCreate,_chatSessionId=" + this._chatSessionId});
        XNUIUtils.checkNeedReInit(this.getApplicationContext(), true, this._chatSessionId, this._model);
        XNSDKUICore.getInstance().setChatActivity(this);
        ChatSessionData chatData = XNSDKUICore.getInstance().getChatSessionData(this._chatSessionId);
        this.switch2ChatData(chatData);
        if(this._chatData != null) {
            this.goodsPrice = chatData._chatParams.itemparams.goods_price;
            ++this._chatData._comeToChatWindowNum;
            XNLOG.i(new String[]{"以上是历史消息，_comeToChatWindowNum=" + this._chatData._comeToChatWindowNum + ",_chatSessionId=" + this._chatSessionId});
            this.initView();
            if(this._model == 0) {
                this.showProductAtLocal(this._chatData);
                this.fl_showtips3.setVisibility(View.GONE);
            }

            if(this._model == 1) {
                this.onTransferAction(this._chatData);
                this.showProductInfoByWidgets(this._chatData);
                this.refreshBlacklist();
            }

            long now = System.currentTimeMillis();
            XNLOG.i(new String[]{"花费时间", "cuttime=" + (now - this.howmanytimecut_start)});
            XNChatSDK.getInstance().setChatWindowStatus(this._chatSessionId, 1);
        }

    }

    private void refreshBlacklist() {
        if(this._chatData != null) {
            if(this._chatData.blacklistAction == 1) {
                this.fl_showtips3.setVisibility(View.VISIBLE);
            } else {
                this.fl_showtips3.setVisibility(View.GONE);
            }

            this.bt_release.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if(ChatActivity.this._chatData.blacklistAction == 1) {
                        OnCustomDialogListener blacknamecustomDialogListener2 = new OnCustomDialogListener() {
                            public void back(String name) {
                            }

                            public void confirm(String name) {
                                if(XNSDKUIListener.getInstance()._XNSDKListenerAPP != null) {
                                    XNSDKUIListener.getInstance()._XNSDKListenerAPP.onRefuseVisitor(ChatActivity.this._chatData._settingid, 0);
                                }

                                ChatActivity.this.fl_showtips3.setVisibility(View.GONE);
                                ChatActivity.this._chatData.blacklistAction = 0;
                            }
                        };
                        XNGeneralDialog.getInstance(ChatActivity.this, 2131427736, ChatActivity.this.getResources().getString(2131231294), ChatActivity.this.getResources().getString(2131231230), ChatActivity.this.getResources().getString(2131231217), blacknamecustomDialogListener2).show();
                    }

                }
            });
        }

    }

    private void sendConsultStartPage(ChatSessionData chatData) {
        if(chatData != null && chatData._chatParams != null && chatData._chatParams.itemparams != null) {
            SystemMessageBody consultStartPageMsgBody = new SystemMessageBody();
            consultStartPageMsgBody.msgsubtype = 58;
            consultStartPageMsgBody.parentpagetitle = chatData._chatParams.startPageTitle;
            consultStartPageMsgBody.parentpageurl = chatData._chatParams.startPageUrl;
            XNChatSDK.getInstance().sendSystemMessage(chatData._chatsessionid, consultStartPageMsgBody);
        }

    }

    public void initView() {
        this.confirm = this.getResources().getString(R.string.xn_btn_yes);
        this.cancel = this.getResources().getString(R.string.xn_btn_no);
        this.leave_confirm = this.getResources().getString(R.string.xn_leavesetting_confirm);
        this.faceRelativeLayout = (FaceRelativeLayout)this.findViewById(R.id.faceRelativeLayout);
        this.faceRelativeLayout.init();
        this.fl_showtips = (FrameLayout)this.findViewById(R.id.leave_sf);
        this.tv_showtips = (TextView)this.findViewById(R.id.fk_s);
        this.tv_setting_or_kefu_name = (TextView)this.findViewById(R.id.tv_chat_username);
        this.finish_consult = (ImageView)this.findViewById(R.id.over_chat);
        this.fl_showtips2 = (FrameLayout)this.findViewById(R.id.fl_tip2);
        this.tv_showtips2 = (TextView)this.findViewById(R.id.tv_tips2);
        this.bt_refuse = (Button)this.findViewById(R.id.bt_refuse);
        this.bt_accept = (Button)this.findViewById(R.id.bt_accept);
        this.fl_showtips3 = (FrameLayout)this.findViewById(R.id.fl_tip_black);
        this.bt_release = (Button)this.findViewById(R.id.bt_release);
        this.fl_Showgoods = (RelativeLayout)this.findViewById(R.id.fl_showgoods);
        this.wv_Goods = (WebView)this.findViewById(R.id.wv_goods);
        this.iv_Goods = (ImageView)this.findViewById(R.id.iv_goods);
        this.tv_Goodsname = (TextView)this.findViewById(R.id.tv_goodsname);
        this.tv_Goodsprice = (TextView)this.findViewById(R.id.tv_goodsprice);
        this.pb_requestkf = (ProgressBar)this.findViewById(R.id.pb_requestkf);
        this.mListView = (XNListView)this.findViewById(R.id.chatListView);
        this.sdk_chat_back = (RelativeLayout)this.findViewById(R.id.rl_finish);
        this.alpha = AnimationUtils.loadAnimation(this, R.anim.blacklist_anim);
        XNEmotion.getInstance().initEmotionFunction(this);
        TextView plant = new TextView(this);
        plant.setBackgroundColor(0);
        plant.setHeight(50);
        plant.setWidth(-1);
        this.mListView.addFooterView(plant);
        this.faceRelativeLayout.setModel(this._model);
        this.mAdapter = new ChatMsgAdapter(this, this._chatData, this._model, this);
        this.mListView.setAdapter(this.mAdapter);
        this.mListView.setSelection(this.mListView.getCount() - 1);
        this.mListView.setonRefreshListener(this);
        XNSDKUIListener.getInstance().setOnToChatListener(this);
        String title = this._chatData.getTitleName(this._model, this._chatData._settingname);
        if(title != null && title.trim().length() != 0) {
            this.tv_setting_or_kefu_name.setText(this._chatData.getTitleName(this._model, this._chatData._settingname));
        }

        this.recoverUIfromChatData();
        this.mListView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case 1:
                        if(v.getId() == 2131624997) {
                            ChatActivity.this.faceRelativeLayout.mInputMethodManager.hideSoftInputFromWindow(ChatActivity.this.faceRelativeLayout.mEditTextContent.getWindowToken(), 0);
                            ChatActivity.this.faceRelativeLayout.hideFaceView();
                            ChatActivity.this.faceRelativeLayout.faceBtn.setBackgroundResource(2130837847);
                        }
                    default:
                        return false;
                }
            }
        });
        this.finish_consult.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if((ChatActivity.this.fl_showtips2 == null || ChatActivity.this.fl_showtips2.getVisibility() != View.VISIBLE) && !ChatActivity.this.notifyCloseChatSession() && !ChatActivity.this.notifyQueueCancel() && !ChatActivity.this.forceEvalute()) {
                    XNChatSDK.getInstance().stopChatBySession(ChatActivity.this._chatSessionId);
                    XNLOG.i(new String[]{"关闭聊窗ChatActivity", "7"});
                    ChatActivity.this.closeChatWindow();
                }

            }
        });
        this.sdk_chat_back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(ChatActivity.this.fl_showtips2 == null || ChatActivity.this.fl_showtips2.getVisibility() != View.VISIBLE) {
                    ChatActivity.this._chatData.toDestoryChatSession = false;
                    if(!ChatActivity.this.notifyQueueCancel()) {
                        XNLOG.i(new String[]{"关闭聊窗ChatActivity", "8"});
                        ChatActivity.this.closeChatWindow();
                    }
                }

            }
        });
        this.tv_setting_or_kefu_name.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(ChatActivity.this.fl_showtips.getVisibility() == View.VISIBLE) {
                    ChatActivity.this.fl_showtips.setVisibility(View.GONE);
                }

            }
        });
        this.tv_setting_or_kefu_name.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                if(ChatActivity.this._chatData == null) {
                    return true;
                } else {
                    String edition = ChatActivity.this.getResources().getString(2131231261) + ChatActivity.this._chatData._version;
                    ChatActivity.this.tipstr = ChatActivity.this.tv_showtips.getText().toString();
                    if(ChatActivity.this.fl_showtips.getVisibility() == View.GONE) {
                        ChatActivity.this.tv_showtips.setText(edition);
                        ChatActivity.this.fl_showtips.setVisibility(View.VISIBLE);
                        ChatActivity.this.fl_showtips.bringToFront();
                        ChatActivity.this.lastvisible = View.GONE;
                        return true;
                    } else if(ChatActivity.this.lastvisible == View.GONE) {
                        ChatActivity.this.fl_showtips.setVisibility(View.GONE);
                        return true;
                    } else {
                        ChatActivity.this.lastvisible = 0;
                        if(!ChatActivity.this.tipstr.equals(edition)) {
                            ChatActivity.this.lasttipstr = ChatActivity.this.tv_showtips.getText().toString();
                            ChatActivity.this.tv_showtips.setText(edition);
                            return true;
                        } else {
                            ChatActivity.this.tv_showtips.setText(ChatActivity.this.lasttipstr);
                            return true;
                        }
                    }
                }
            }
        });
        this.bt_refuse.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SystemMessageBody systemMessageBody = new SystemMessageBody();
                systemMessageBody.msgsubtype = 56;
                systemMessageBody.invitation_type = ChatActivity.this._chatData.transferActionData.type;
                systemMessageBody.invitation_action = 2;
                systemMessageBody.invitation_srcUid = ChatActivity.this._chatData.transferActionData.srcId;
                XNChatSDK.getInstance().sendSystemMessage(ChatActivity.this._chatData._chatsessionid, systemMessageBody);
                if(XNSDKUIListener.getInstance()._XNSDKListenerAPP != null) {
                    XNSDKUIListener.getInstance()._XNSDKListenerAPP.onInvitationResponse(ChatActivity.this._chatData._settingid, ChatActivity.this._chatData._settingname, 2);
                }

                if(XNSDKUICore.getInstance() != null) {
                    XNSDKUICore.getInstance()._transferActionData = null;
                }

                ChatActivity.this._chatData.transferActionData = null;
                ChatActivity.this.fl_showtips2.setVisibility(View.GONE);
                XNChatSDK.getInstance().stopChatBySession(ChatActivity.this._chatSessionId);
                XNLOG.i(new String[]{"关闭聊窗ChatActivity", "9"});
                ChatActivity.this.closeChatWindow();
            }
        });
        this.bt_accept.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SystemMessageBody systemMessageBody = new SystemMessageBody();
                systemMessageBody.msgsubtype = 56;
                systemMessageBody.invitation_type = ChatActivity.this._chatData.transferActionData.type;
                systemMessageBody.invitation_action = 1;
                systemMessageBody.invitation_srcUid = ChatActivity.this._chatData.transferActionData.srcId;
                XNChatSDK.getInstance().sendSystemMessage(ChatActivity.this._chatData._chatsessionid, systemMessageBody);
                if(XNSDKUIListener.getInstance()._XNSDKListenerAPP != null) {
                    XNSDKUIListener.getInstance()._XNSDKListenerAPP.onInvitationResponse(ChatActivity.this._chatData._settingid, ChatActivity.this._chatData._settingname, 1);
                }

                if(XNSDKUICore.getInstance() != null) {
                    XNSDKUICore.getInstance()._transferActionData = null;
                }

                ChatActivity.this._chatData.transferActionData = null;
                ChatActivity.this.fl_showtips2.setVisibility(View.GONE);
            }
        });
    }

    private void switch2ChatData(ChatSessionData chatdata) {
        if(chatdata != null) {
            if(this._chatData != chatdata) {
                this._chatData = chatdata;
            }

            if(this._chatData != null) {
                this.refreshMsgStatus();
            }
        }

    }

    private boolean forceEvalute() {
        if(this._chatData == null) {
            return false;
        } else if(this._chatData._enableevaluation == 1 && !this._chatData._isNetInvalid) {
            this._chatData.toDestoryChatSession = true;
            Intent intent = new Intent(this, ValuationActivity.class);
            this.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

    private boolean notifyCloseChatSession() {
        if(this._model != 1) {
            return false;
        } else if(this._chatData == null) {
            return false;
        } else {
            XNGeneralDialog.getInstance(this, R.style.XNDialog, this.getResources().getString(R.string.xn_cancel_queue), this.confirm, this.cancel, this.customDialogListener).show();
            return true;
        }
    }

    private boolean notifyQueueCancel() {
        if(this._model != 0) {
            return false;
        } else if(this._chatData == null) {
            return false;
        } else if(this._chatData._isQueuing) {
            this._chatData.toDestoryChatSession = true;
            XNGeneralDialog.getInstance(this, R.style.XNDialog, this.getResources().getString(R.string.xn_cancel_queue), this.confirm, this.cancel, this.customDialogListener).show();
            return true;
        } else {
            return false;
        }
    }

    private void closeChatWindow() {
        if(this.mAdapter != null) {
            this.mAdapter.stopVoice();
        }

        if(this._chatData != null) {
            this._chatData._chatWindowOpen = false;
        }

        this.backupUIfromChatActivity();
        XNChatSDK.getInstance().sendStatisticalData(this._chatSessionId, 23);
        XNLOG.i(new String[]{"关闭聊窗ChatActivity", "2"});
        if(this.faceRelativeLayout != null && this.faceRelativeLayout.mEditTextContent != null) {
            this.faceRelativeLayout.mInputMethodManager.hideSoftInputFromWindow(this.faceRelativeLayout.mEditTextContent.getWindowToken(), 2);
        }

        this.finish();
    }

    public void recoverUIfromChatData() {
        this.updateSettingname();
        this.updateTipsShow();
        this.updateListView();
    }

    public void backupUIfromChatActivity() {
        this.saveSettingname();
        this.saveTipsShow();
        if(this.faceRelativeLayout != null) {
            this.faceRelativeLayout.saveButtonStatus();
        }

    }

    public void updateSettingname() {
        if(this.tv_setting_or_kefu_name != null && this._chatData != null && this._chatData.ui_settingname != null && this._chatData.ui_settingname.trim().length() != 0) {
            XNLOG.i(new String[]{"updateSettingname,001=" + this._chatData.ui_settingname});
            this.tv_setting_or_kefu_name.setText(this._chatData.getTitleName(this._model, this._chatData.ui_settingname));
        }

    }

    public void saveSettingname() {
        if(this.tv_setting_or_kefu_name != null && this._chatData != null) {
            String ui_settingname = this.tv_setting_or_kefu_name.getText().toString();
            if(ui_settingname != null && ui_settingname.trim().length() != 0) {
                this._chatData.ui_settingname = ui_settingname;
            }
        }

    }

    public void updateTipsShow() {
        if(this.fl_showtips != null && this.tv_showtips != null && this._chatData != null) {
            if(!this._chatData.ui_tipshow) {
                this.fl_showtips.setVisibility(View.GONE);
            } else {
                this.tv_showtips.setText(this._chatData._tipStringSave);
                this.fl_showtips.setVisibility(View.VISIBLE);
                this.fl_showtips.bringToFront();
            }
        }

    }

    public void saveTipsShow() {
        if(this.fl_showtips != null && this._chatData != null) {
            if(this.fl_showtips.getVisibility() == View.GONE) {
                this._chatData.ui_tipshow = false;
            } else {
                this._chatData._tipStringSave = this.tv_showtips.getText().toString();
                this._chatData.ui_tipshow = true;
            }
        }

    }

    public void updateListView() {
        if(this.mAdapter != null) {
            this.mAdapter.notifyDataSetChanged();
        }

    }

    private void showERPAtNet(ChatSessionData chatData) {
        try {
            if(chatData == null || chatData._chatParams == null) {
                return;
            }

            if(chatData._chatParams.erpParam == null || chatData._chatParams.erpParam.trim().length() == 0) {
                return;
            }

            SystemMessageBody e = new SystemMessageBody();
            e.msgsubtype = 57;
            e.erp = chatData._chatParams.erpParam;
            XNChatSDK.getInstance().sendSystemMessage(chatData._chatsessionid, e);
        } catch (Exception var3) {
            XNLOG.e(new String[]{"Exception showERPAtNet ", var3.toString()});
        }

    }

    private void showProductAtNet(ChatSessionData chatData) {
        try {
            if(chatData != null && chatData._chatParams != null && chatData._chatParams.itemparams != null && chatData._chatParams.itemparams.clientgoodsinfo_type != 0) {
                ItemParamsBody e = chatData._chatParams.itemparams;
                SystemMessageBody productMessageBody = new SystemMessageBody();
                productMessageBody.msgsubtype = 55;
                productMessageBody.goodShowType = e.clientgoodsinfo_type;
                productMessageBody.goodsid = e.goods_id;
                productMessageBody.goodsshowurl = e.goods_showurl;
                productMessageBody.itemparam = e.itemparam;
                if((e.clientgoodsinfo_type != 1 || productMessageBody.goodsid != null && productMessageBody.goodsid.trim().length() != 0) && (e.clientgoodsinfo_type != 2 || productMessageBody.goodsshowurl != null && productMessageBody.goodsshowurl.trim().length() != 0)) {
                    XNChatSDK.getInstance().sendSystemMessage(chatData._chatsessionid, productMessageBody);
                }
            }
        } catch (Exception var4) {
            XNLOG.e(new String[]{"Exception showProductAtNet ", var4.toString()});
        }

    }

    private void setShowGoodsClick(final String goodsurl, final ItemParamsBody itemParamsBody) {
        try {
            if(this.fl_Showgoods == null) {
                return;
            }

            this.fl_Showgoods.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ChatActivity.this.showUrlScanner(v.getContext(), goodsurl, itemParamsBody);
                }
            });
        } catch (Exception var4) {
            XNLOG.e(new String[]{"Exception setShowGoodsClick ", var4.toString()});
        }

    }

    public void showUrlScanner(Context context, String goodsurl, ItemParamsBody itemParamsBody) {
        try {
            if(goodsurl == null) {
                return;
            }

            if(this._model == 0) {
                if(itemParamsBody == null) {
                    return;
                }

                if(itemParamsBody.clicktoshow_type == 1) {
                    if(XNSDKUIListener.getInstance()._XNSDKListener != null) {
                        XNSDKUIListener.getInstance()._XNSDKListener.onClickShowGoods(itemParamsBody.appgoodsinfo_type, itemParamsBody.clientgoodsinfo_type, itemParamsBody.goods_id, itemParamsBody.goods_name, itemParamsBody.goods_price, itemParamsBody.goods_image, itemParamsBody.goods_url, itemParamsBody.goods_showurl);
                    }
                } else if(itemParamsBody.clicktoshow_type == 0) {
                    this.openUrlScannner(context, goodsurl);
                }
            } else if(this._model == 1) {
                this.openUrlScannner(context, goodsurl);
            }
        } catch (Exception var5) {
            XNLOG.e(new String[]{"Exception showUrlScanner ", var5.toString()});
        }

    }

    public void openUrlScannner(Context context, String goodsurl) {
        try {
            if(goodsurl == null) {
                goodsurl = "";
            }

            Intent e = new Intent(context, XNExplorerActivity.class);
            e.putExtra("urlintextmsg", goodsurl);
            context.startActivity(e);
        } catch (Exception var4) {
            XNLOG.e(new String[]{"Exception openUrlScannner ", var4.toString()});
        }

    }

    private boolean showProductInfoByWebView(String showUrl) {
        try {
            if(showUrl != null && showUrl.trim().length() != 0) {
                if(this._model == 1) {
                    this.setShowGoodsClick(showUrl, (ItemParamsBody)null);
                }

                this.fl_Showgoods.setVisibility(View.VISIBLE);
                this.wv_Goods.setVisibility(View.VISIBLE);
                this.iv_Goods.setVisibility(View.GONE);
                this.tv_Goodsname.setVisibility(View.GONE);
                this.tv_Goodsprice.setVisibility(View.GONE);
                this.wv_Goods.getSettings().setJavaScriptEnabled(true);
                this.wv_Goods.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                this.wv_Goods.setWebViewClient(new WebViewClient() {
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
                this.wv_Goods.loadUrl(showUrl);
                return true;
            } else {
                this.fl_Showgoods.setVisibility(View.GONE);
                return false;
            }
        } catch (Exception var3) {
            XNLOG.e(new String[]{"Exception showProductInfoByWebView ", var3.toString()});
            return false;
        }
    }

    private void showProductInfoByWidgets(ChatSessionData chatData) {
        try {
            if(chatData == null) {
                return;
            }

            if(this._model == 0) {
                if(chatData._chatParams == null) {
                    return;
                }

                if(chatData._chatParams.itemparams == null) {
                    return;
                }
            }

            if(chatData._itemParamsBody == null) {
                return;
            }

            if(TextUtils.isEmpty(chatData._itemParamsBody.goods_name) && TextUtils.isEmpty(chatData._itemParamsBody.goods_price) && TextUtils.isEmpty(chatData._itemParamsBody.goods_image)) {
                this.fl_Showgoods.setVisibility(View.GONE);
                return;
            }

            this.setShowGoodsClick(chatData._itemParamsBody.goods_url, chatData._itemParamsBody);
            this.fl_Showgoods.setVisibility(View.VISIBLE);
            this.iv_Goods.setVisibility(View.VISIBLE);
            this.tv_Goodsname.setVisibility(View.VISIBLE);
            this.tv_Goodsprice.setVisibility(View.VISIBLE);
            this.wv_Goods.setVisibility(View.GONE);
            this.tv_Goodsname.setText(chatData._itemParamsBody.goods_name);
            this.tv_Goodsprice.setText(this.goodsPrice);
            ImageShow.getInstance(this).DisplayImage(4, (String)null, chatData._itemParamsBody.goods_image, this.iv_Goods, (WebView)null, R.drawable.pic_icon, R.drawable.pic_icon, (Handler)null);
        } catch (Exception var3) {
            XNLOG.e(new String[]{"Exception showProductInfoByWidgets ", var3.toString()});
        }

    }

    private void showProductAtLocal(ChatSessionData chatData) {
        try {
            if(chatData == null || chatData._chatParams == null || chatData._chatParams.itemparams == null || chatData._chatParams.itemparams.appgoodsinfo_type == 0) {
                this.fl_Showgoods.setVisibility(View.GONE);
                return;
            }

            ItemParamsBody e = chatData._chatParams.itemparams;
            String showUrl = null;
            if(e.appgoodsinfo_type == 2) {
                showUrl = e.goods_showurl;
                XNLOG.e(new String[]{"showProductAtLocal", "url,showUrl=" + showUrl});
                this.showProductInfoByWebView(showUrl);
                return;
            }

            if(e.appgoodsinfo_type == 1) {
                showUrl = SystemMessageBody.createProductURLByID(chatData._settingid, e.goods_id, e.itemparam);
                XNLOG.e(new String[]{"showProductAtLocal", "id,showUrl=" + showUrl});
                XNChatSDK.getInstance().getGoodsInfo(this._chatSessionId, showUrl, chatData._goodsIdIsChanged, true);
                return;
            }

            if(e.appgoodsinfo_type == 3) {
                this.showProductInfoByWidgets(chatData);
            }
        } catch (Exception var4) {
            XNLOG.e(new String[]{"Exception showProductAtLocal ", var4.toString()});
        }

    }

    private void refreshMsgStatus() {
        try {
            if(this._chatData == null) {
                return;
            }

            this._chatData._chatWindowOpen = true;
            this._chatData._homeKeyDown = false;
            this._chatData._unReadMsgNum = 0;
            XNSPHelper e = new XNSPHelper(XNSDKUICore.getInstance().context, "unreadsp");
            String settingunreadinfo = e.getValue("settingunreadinfo");

            try {
                JSONObject e1 = null;
                if(settingunreadinfo != null) {
                    e1 = new JSONObject(settingunreadinfo);
                } else {
                    e1 = new JSONObject();
                }

                e1.put(this._chatData._settingid, this._chatData._unReadMsgNum);
                e.putValue("settingunreadinfo", e1.toString());
            } catch (JSONException var4) {
                var4.printStackTrace();
            }

            if(XNSDKUIListener.getInstance()._XNSDKListener != null) {
                XNSDKUIListener.getInstance()._XNSDKListener.onUnReadMsg(this._chatData._settingid, (String)null, (String)null, this._chatData._unReadMsgNum);
            }
        } catch (Exception var5) {
            XNLOG.e(new String[]{"Exception refreshMsgStatus ", var5.toString()});
        }

    }

    private void onTransferAction(ChatSessionData chatData) {
        try {
            if(chatData == null || chatData.transferActionData == null || chatData.transferActionData.action == 0) {
                this.fl_showtips2.setVisibility(View.GONE);
                return;
            }

            if(chatData.transferActionData.type != 1 && chatData.transferActionData.type != 0) {
                this.fl_showtips2.setVisibility(View.GONE);
                return;
            }

            String e = null;
            if(chatData.transferActionData.type == 0) {
                if(chatData.transferActionData.srcName != null && chatData.transferActionData.srcName.trim().length() != 0 && chatData.transferActionData.transferUserName != null && chatData.transferActionData.transferUserName.trim().length() != 0) {
                    e = this.getResources().getString(R.string.xn_transferui_tip2) + chatData.transferActionData.srcName + this.getResources().getString(R.string.xn_transferui_tip3) + chatData.transferActionData.transferUserName + this.getResources().getString(R.string.xn_transferui_tip4);
                } else {
                    e = this.getResources().getString(R.string.xn_transferui_tip1);
                }
            } else if(chatData.transferActionData.type == 1) {
                if(chatData.transferActionData.srcName != null && chatData.transferActionData.srcName.trim().length() != 0 && chatData.transferActionData.transferUserName != null && chatData.transferActionData.transferUserName.trim().length() != 0) {
                    e = this.getResources().getString(R.string.xn_transferui_tip2) + chatData.transferActionData.srcName + this.getResources().getString(R.string.xn_transferui_tip6) + chatData.transferActionData.transferUserName + this.getResources().getString(R.string.xn_transferui_tip7);
                } else {
                    e = this.getResources().getString(R.string.xn_transferui_tip5);
                }
            }

            this.tv_showtips2.setText(e);
            this.fl_showtips2.setVisibility(View.VISIBLE);
            this.fl_showtips2.bringToFront();
        } catch (Exception var3) {
            XNLOG.e(new String[]{"Exception refreshMsgStatus ", var3.toString()});
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == 4 && this.faceRelativeLayout.hideFaceView()) {
            return true;
        } else if(keyCode == 4) {
            if(this.fl_showtips2.getVisibility() == View.VISIBLE) {
                return true;
            } else if(this.notifyQueueCancel()) {
                return true;
            } else {
                XNLOG.i(new String[]{"关闭聊窗ChatActivity", "3"});
                this.closeChatWindow();
                return true;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    protected void onResume() {
        super.onResume();
        XNLOG.i(new String[]{"建立聊窗", "onResume="});
        this.refreshMsgStatus();
        if(XNUIUtils.checkNeedReInit(this.getApplicationContext(), true, this._chatSessionId, this._model)) {
            XNSDKUICore.getInstance().setChatActivity(this);
            ChatSessionData chatData = XNSDKUICore.getInstance().getChatSessionData(this._chatSessionId);
            this.switch2ChatData(chatData);
        }

    }

    protected void onRestart() {
        super.onRestart();
        XNLOG.i(new String[]{"建立聊窗", "onRestart="});
    }

    protected void onPause() {
        super.onPause();
        XNChatSDK.getInstance().setChatWindowStatus(this._chatSessionId, 2);
        XNLOG.i(new String[]{"建立聊窗", "onPause="});
    }

    protected void onStart() {
        super.onStart();
        XNLOG.i(new String[]{"建立聊窗", "onStart="});
    }

    protected void onStop() {
        super.onStop();
        XNLOG.i(new String[]{"建立聊窗", "onStop="});
        if(this._chatData != null) {
            this._chatData._homeKeyDown = true;
        }

    }

    protected void onDestroy() {
        XNChatSDK.getInstance().setChatWindowStatus(this._chatSessionId, 3);
        XNGeneralDialog.destoryInstance();
        ImageShow.destoryInstance();
        XNSDKUIListener.getInstance().setOnToChatListener((OnToChatListener)null);
        XNSDKUICore.getInstance().setChatActivity((ChatActivity)null);
        if(XNSDKUICore.getInstance().getCurrentChatSessionData() != null) {
            XNSDKUICore.getInstance().getCurrentChatSessionData().chatactivity = null;
        }

        XNLOG.i(new String[]{"建立聊窗", "onDestroy="});
        super.onDestroy();
    }

    public void onInitResult(String version, int result) {
        if(this._chatData != null) {
            this._chatData._version = version;
            this._chatData._initresult = result;
        }

    }

    public void onStartChatResult(String chatSessionId, int result) {
    }

    public void onLeaveMsgResult(int leaveMsgStatus) {
        try {
            if(leaveMsgStatus == 0) {
                if(LeaveMsgActivity.createLoadingDialog != null) {
                    LeaveMsgActivity.createLoadingDialog.dismiss();
                }

                if(LeaveMsgActivity.leaveMsgActivity != null) {
                    LeaveMsgActivity.leaveMsgActivity.finish();
                }

                Toast.makeText(this.getApplicationContext(), this.getResources().getString(R.string.xn_tt_leavemsg_failed), Toast.LENGTH_LONG).show();
            }

            if(leaveMsgStatus == 1) {
                if(LeaveMsgActivity.createLoadingDialog != null) {
                    LeaveMsgActivity.createLoadingDialog.dismiss();
                }

                if(LeaveMsgActivity.leaveMsgActivity != null) {
                    LeaveMsgActivity.leaveMsgActivity.finish();
                }

                Toast.makeText(this.getApplicationContext(), this.getResources().getString(R.string.xn_tt_leavemsgtip_success), Toast.LENGTH_LONG).show();
            }
        } catch (Exception var3) {
            XNLOG.i(new String[]{"Exception", "onStartChatResult " + var3.toString()});
        }

    }

    public void onConnectResult(final String chatSessionId, final int result, final int status, final int queuingmnum) {
        try {
            if(this.faceRelativeLayout == null) {
                return;
            }

            if(this._chatData != null) {
                this._chatData._XNSDKAuthority = result;
            }

            this.runOnUiThread(new Runnable() {
                public void run() {
                    XNLOG.i(new String[]{"状态提示", "status=" + status});
                    if(chatSessionId != null && chatSessionId.trim().length() != 0 && chatSessionId.equals(ChatActivity.this._chatSessionId)) {
                        if(result == 0) {
                            String bmsg = ChatActivity.this.tv_setting_or_kefu_name.getText().toString();
                            if(!bmsg.endsWith(ChatActivity.this.getResources().getString(R.string.xn_chatActivity_tryout))) {
                                ChatActivity.this.tv_setting_or_kefu_name.setText(bmsg + ChatActivity.this.getResources().getString(R.string.xn_sdk_tryuse));
                            }
                        }

                        XNLOG.i(new String[]{"jiaojiao__userType,_chatSessionId=" + XNChatSDK.getInstance().findChatSessionByChatSessionid(ChatActivity.this._chatSessionId)});
                        if(XNChatSDK.getInstance().findChatSessionByChatSessionid(ChatActivity.this._chatSessionId) != null) {
                            ChatActivity.this._userType = XNChatSDK.getInstance().findChatSessionByChatSessionid(ChatActivity.this._chatSessionId)._usertype;
                            XNLOG.i(new String[]{"jiaojiao__userType=" + ChatActivity.this._userType});
                            if(ChatActivity.this._userType != 1) {
                                XNLOG.i(new String[]{"jiaojiao__userType=人工客服,切换布局"});
                                ChatActivity.this.faceRelativeLayout.btnVoice.setVisibility(View.VISIBLE);
                                ChatActivity.this.faceRelativeLayout.rl_robot.setVisibility(View.GONE);
                            } else {
                                XNLOG.i(new String[]{"jiaojiao__userType=机器人客服"});
                                ChatActivity.this.faceRelativeLayout.btnVoice.setVisibility(View.GONE);
                                if(ChatActivity.this.faceRelativeLayout.btnRecord.getVisibility() == View.VISIBLE) {
                                    ChatActivity.this.faceRelativeLayout.btnRecord.setVisibility(View.INVISIBLE);
                                    ChatActivity.this.faceRelativeLayout.mEditTextContent.setVisibility(View.VISIBLE);
                                    ChatActivity.this.faceRelativeLayout.btnVoice.setBackgroundResource(R.drawable.btnvoice1);
                                }

                                ChatActivity.this.faceRelativeLayout.rl_robot.setVisibility(View.VISIBLE);
                                ChatActivity.this.faceRelativeLayout.rl_robot.setClickable(true);
                                ChatActivity.this.faceRelativeLayout.rl_robot.setOnClickListener(new OnClickListener() {
                                    public void onClick(View v) {
                                        TextMessageBody textMessageBody;
                                        if(GlobalParam.getInstance().firstClickRobot) {
                                            textMessageBody = new TextMessageBody();
                                            textMessageBody.textmsg = v.getContext().getResources().getString(R.string.xn_swifttorobot);
                                            textMessageBody.fontsize = 20;
                                            textMessageBody.color = "0x000000";
                                            textMessageBody.italic = false;
                                            textMessageBody.bold = false;
                                            textMessageBody.underline = false;
                                            textMessageBody.isrobert = true;
                                            XNLOG.i(new String[]{"机器人点击发送11"});
                                            GlobalParam.getInstance().firstClickRobot = false;
                                            XNChatSDK.getInstance().sendTextMessage(XNSDKUICore.getInstance().getCurrentChatSessionid(), textMessageBody);
                                        } else if(GlobalParam.getInstance().robotCanClick) {
                                            textMessageBody = new TextMessageBody();
                                            textMessageBody.textmsg = v.getContext().getResources().getString(R.string.xn_swifttorobot);
                                            textMessageBody.fontsize = 20;
                                            textMessageBody.color = "0x000000";
                                            textMessageBody.italic = false;
                                            textMessageBody.bold = false;
                                            textMessageBody.underline = false;
                                            textMessageBody.isrobert = true;
                                            XNLOG.i(new String[]{"机器人点击发送22"});
                                            GlobalParam.getInstance().robotCanClick = false;
                                            XNChatSDK.getInstance().sendTextMessage(XNSDKUICore.getInstance().getCurrentChatSessionid(), textMessageBody);
                                        }

                                    }
                                });
                            }
                        }

                        Iterator var3;
                        BaseMessage bmsg1;
                        BaseMessage message;
                        if(status == 6) {
                            ChatActivity.this.pb_requestkf.setVisibility(View.GONE);
                            if(ChatActivity.this._chatData != null) {
                                ChatActivity.this._chatData._isNetInvalid = true;
                            }

                            bmsg1 = null;
                            var3 = ChatActivity.this._chatData.ui_wholemsglist.iterator();

                            label115: {
                                do {
                                    if(!var3.hasNext()) {
                                        break label115;
                                    }

                                    message = (BaseMessage)var3.next();
                                } while(message.msgsubtype != 663 && message.msgsubtype != 660 && message.msgsubtype != 661);

                                bmsg1 = message;
                            }

                            if(bmsg1 != null) {
                                ChatActivity.this._chatData.ui_wholemsglist.remove(bmsg1);
                                ChatActivity.this.mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if(status != 1) {
                                ChatActivity.this._chatData._isNetInvalid = false;
                                ChatActivity.this.fl_showtips.setVisibility(View.GONE);
                            } else {
                                ChatActivity.this._chatData._isNetInvalid = true;
                                ChatActivity.this.fl_showtips.setVisibility(View.VISIBLE);
                                ChatActivity.this.tv_showtips.setVisibility(View.VISIBLE);
                                ChatActivity.this.pb_requestkf.setVisibility(View.GONE);
                                ChatActivity.this.fl_showtips.bringToFront();
                                ChatActivity.this.tv_showtips.setText(ChatActivity.this.getResources().getString(R.string.xn_toast_nointernet));
                                ChatActivity.this.tv_showtips.setClickable(false);
                                bmsg1 = null;
                                var3 = ChatActivity.this._chatData.ui_wholemsglist.iterator();

                                while(var3.hasNext()) {
                                    message = (BaseMessage)var3.next();
                                    if(message.msgsubtype == 663 || message.msgsubtype == 660 || message.msgsubtype == 661) {
                                        bmsg1 = message;
                                        break;
                                    }
                                }

                                if(bmsg1 != null) {
                                    ChatActivity.this._chatData.ui_wholemsglist.remove(bmsg1);
                                    ChatActivity.this.mAdapter.notifyDataSetChanged();
                                }
                            }

                            if(status != 2) {
                                ChatActivity.this._chatData._isRequestingKf = false;
                            } else {
                                ChatActivity.this._chatData._isRequestingKf = true;
                                XNLOG.i(new String[]{"系统消息提示", " 正在请求客服"});
                                ChatActivity.this.pb_requestkf.setVisibility(View.VISIBLE);
                                bmsg1 = null;
                                var3 = ChatActivity.this._chatData.ui_wholemsglist.iterator();

                                while(var3.hasNext()) {
                                    message = (BaseMessage)var3.next();
                                    if(message.msgsubtype == 663 || message.msgsubtype == 660 || message.msgsubtype == 661) {
                                        bmsg1 = message;
                                        break;
                                    }
                                }

                                if(bmsg1 != null) {
                                    ChatActivity.this._chatData.ui_wholemsglist.remove(bmsg1);
                                    ChatActivity.this.mAdapter.notifyDataSetChanged();
                                }
                            }

                            SystemMessageBody message1;
                            if(status != 4) {
                                ChatActivity.this._chatData._isQueuing = false;
                            } else {
                                XNLOG.i(new String[]{"系统消息提示", " 客服排队"});
                                ChatActivity.this._chatData._isQueuing = true;
                                ChatActivity.this._chatData.queuingmnum = queuingmnum;
                                ChatActivity.this._chatData._ui_offline = false;
                                ChatActivity.this.pb_requestkf.setVisibility(View.GONE);
                                bmsg1 = null;
                                var3 = ChatActivity.this._chatData.ui_wholemsglist.iterator();

                                while(var3.hasNext()) {
                                    message = (BaseMessage)var3.next();
                                    if(message.msgsubtype == 661 || message.msgsubtype == 660) {
                                        bmsg1 = message;
                                        break;
                                    }
                                }

                                if(bmsg1 != null) {
                                    ChatActivity.this._chatData.ui_wholemsglist.remove(bmsg1);
                                    ChatActivity.this.mAdapter.notifyDataSetChanged();
                                }

                                message1 = new SystemMessageBody();
                                message1.msgsubtype = 660;
                                message1.isonlyone = true;
                                message1.isnottosend = true;
                                XNChatSDK.getInstance().sendSystemMessage(ChatActivity.this._chatData._chatsessionid, message1);
                            }

                            if(status != 3) {
                                ChatActivity.this._chatData._ui_offline = false;
                            } else {
                                XNLOG.i(new String[]{"系统消息提示", " 客服不在线"});
                                bmsg1 = null;
                                var3 = ChatActivity.this._chatData.ui_wholemsglist.iterator();

                                while(var3.hasNext()) {
                                    message = (BaseMessage)var3.next();
                                    if(message.msgsubtype == 660 || message.msgsubtype == 661) {
                                        bmsg1 = message;
                                        break;
                                    }
                                }

                                if(bmsg1 != null) {
                                    ChatActivity.this._chatData.ui_wholemsglist.remove(bmsg1);
                                    ChatActivity.this.mAdapter.notifyDataSetChanged();
                                }

                                ChatActivity.this._chatData._ui_offline = true;
                                ChatActivity.this.pb_requestkf.setVisibility(View.GONE);
                                message1 = new SystemMessageBody();
                                message1.msgsubtype = 661;
                                message1.isonlyone = true;
                                message1.isnottosend = true;
                                XNChatSDK.getInstance().sendSystemMessage(ChatActivity.this._chatData._chatsessionid, message1);
                                if(ChatActivity.this._chatData.isopen != 0 && ChatActivity.this.flag) {
                                    ChatActivity.this.flag = false;
                                    if(ChatActivity.this._chatData.isannounce == 0) {
                                        XNGeneralDialog.getInstance(ChatActivity.this, R.style.XNDialog, ChatActivity.this._chatData.leavewords, ChatActivity.this.leave_confirm, ChatActivity.this.cancel, (OnCustomDialogListener)null).show();
                                    }
                                }
                            }

                            if(status == 0) {
                                XNLOG.i(new String[]{"系统消息提示", " 客服在线"});
                                XNLOG.i(new String[]{"客服状态2 status=" + status});
                                bmsg1 = null;
                                var3 = ChatActivity.this._chatData.ui_wholemsglist.iterator();

                                label128: {
                                    do {
                                        if(!var3.hasNext()) {
                                            break label128;
                                        }

                                        message = (BaseMessage)var3.next();
                                    } while(message.msgsubtype != 663 && message.msgsubtype != 660 && message.msgsubtype != 661);

                                    bmsg1 = message;
                                }

                                if(bmsg1 != null) {
                                    ChatActivity.this._chatData.ui_wholemsglist.remove(bmsg1);
                                    ChatActivity.this.mAdapter.notifyDataSetChanged();
                                }

                                ChatActivity.this.pb_requestkf.setVisibility(View.GONE);
                                ChatActivity.this.fl_showtips.setVisibility(View.GONE);
                            }
                        }
                    }

                }
            });
        } catch (Exception var6) {
            XNLOG.i(new String[]{"Exception", "onConnectResult " + var6.toString()});
        }

    }

    public void onUserJoinChat(String chatSessionId, String uid, final ChatBaseUser user, final boolean forceRefresh) {
        try {
            XNLOG.e(new String[]{"参与会话  onUserJoinChat uid:" + uid + ",forceRefresh:" + forceRefresh});
            if(this._chatData == null || this._chatData._users == null) {
                return;
            }

            this._chatData._users.put(uid, user);
            if(this._model == 0 && !forceRefresh) {
                this.sendConsultStartPage(this._chatData);
                this.showERPAtNet(this._chatData);
                this.showProductAtNet(this._chatData);
            }

            this.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        if(ChatActivity.this._model == 0) {
                            if(XNCoreUtils.isVisitID(user.uid)) {
                                return;
                            }

                            boolean e = false;
                            if(forceRefresh) {
                                e = true;
                            } else {
                                Iterator userx = ChatActivity.this._chatData.ui_wholemsglist.iterator();

                                while(userx.hasNext()) {
                                    BaseMessage kefunum = (BaseMessage)userx.next();
                                    if(kefunum != null && kefunum.isAutoReSend) {
                                        kefunum.isAutoReSend = false;
                                        XNChatSDK.getInstance().reSendMessage(ChatActivity.this._chatData._chatsessionid, kefunum);
                                        break;
                                    }
                                }
                            }

                            int var6 = 0;
                            Iterator var4 = ChatActivity.this._chatData._users.values().iterator();

                            while(var4.hasNext()) {
                                ChatBaseUser var7 = (ChatBaseUser)var4.next();
                                if(XNCoreUtils.isKFID(var7.uid)) {
                                    ++var6;
                                }
                            }

                            if(var6 == 1) {
                                e = true;
                            }

                            if(!e) {
                                return;
                            }

                            ChatActivity.this._chatData.ui_settingname = user.uname;
                            XNLOG.i(new String[]{"updateSettingname,002=" + ChatActivity.this._chatData.ui_settingname});
                            ChatActivity.this.tv_setting_or_kefu_name.setText(ChatActivity.this._chatData.getTitleName(ChatActivity.this._model, ChatActivity.this._chatData.ui_settingname));
                        } else if(ChatActivity.this._model == 1) {
                            if(XNCoreUtils.isKFID(user.uid)) {
                                return;
                            }

                            if(user.status != 1) {
                                return;
                            }

                            ChatActivity.this._chatData._visitor_status = 1;
                            ChatActivity.this._chatData.ui_settingname = user.uname;
                            XNLOG.i(new String[]{"updateSettingname,008=" + ChatActivity.this._chatData.ui_settingname});
                            ChatActivity.this.tv_setting_or_kefu_name.setText(ChatActivity.this._chatData.getTitleName(ChatActivity.this._model, ChatActivity.this._chatData.ui_settingname));
                        }
                    } catch (Exception var5) {
                        XNLOG.e(new String[]{"Exception onUserJoinChat:", var5.toString()});
                    }

                }
            });
        } catch (Exception var6) {
            XNLOG.i(new String[]{"Exception", "onUserJoinChat " + var6.toString()});
        }

    }

    public void onUserLeaveChat(String chatSessionId, String uid, final ChatBaseUser user) {
        try {
            XNLOG.e(new String[]{"参与会话  onUserLeaveChat:", uid});
            if(this._chatData == null || this._chatData._users == null) {
                return;
            }

            this._chatData._users.remove(uid);
            this.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        boolean e;
                        ChatBaseUser chatUser;
                        Iterator user1;
                        if(ChatActivity.this._model == 0) {
                            if(XNCoreUtils.isVisitID(user.uid)) {
                                return;
                            }

                            e = false;
                            chatUser = null;
                            user1 = ChatActivity.this._chatData._users.values().iterator();

                            while(user1.hasNext()) {
                                ChatBaseUser userx = (ChatBaseUser)user1.next();
                                if(XNCoreUtils.isKFID(userx.uid)) {
                                    chatUser = userx;
                                    break;
                                }
                            }

                            if(chatUser != null) {
                                e = true;
                            }

                            if(!e) {
                                return;
                            }

                            XNLOG.i(new String[]{"updateSettingname,0033,ui_settingname=" + ChatActivity.this._chatData.ui_settingname + ",user.uname=" + user.uname});
                            if(ChatActivity.this._chatData.ui_settingname.equals(user.uname)) {
                                ChatActivity.this._chatData.ui_settingname = chatUser.uname;
                                XNLOG.i(new String[]{"updateSettingname,003=" + ChatActivity.this._chatData.ui_settingname});
                                ChatActivity.this.tv_setting_or_kefu_name.setText(ChatActivity.this._chatData.getTitleName(ChatActivity.this._model, ChatActivity.this._chatData.ui_settingname));
                            }
                        } else if(ChatActivity.this._model == 1) {
                            if(XNCoreUtils.isKFID(user.uid)) {
                                return;
                            }

                            e = false;
                            user1 = ChatActivity.this._chatData._users.values().iterator();

                            while(user1.hasNext()) {
                                chatUser = (ChatBaseUser)user1.next();
                                if(XNCoreUtils.isVisitID(chatUser.uid)) {
                                    e = true;
                                }
                            }

                            if(!e) {
                                ChatActivity.this._chatData._visitor_status = 0;
                                ChatActivity.this._chatData.ui_settingname = user.uname;
                                XNLOG.i(new String[]{"updateSettingname,004=" + ChatActivity.this._chatData.ui_settingname});
                                ChatActivity.this.tv_setting_or_kefu_name.setText(ChatActivity.this._chatData.getTitleName(ChatActivity.this._model, ChatActivity.this._chatData.ui_settingname));
                            }
                        }
                    } catch (Exception var5) {
                        XNLOG.e(new String[]{"Exception onUserLeaveChat:", var5.toString()});
                    }

                }
            });
        } catch (Exception var5) {
            XNLOG.i(new String[]{"Exception", "onUserLeaveChat " + var5.toString()});
        }

    }

    public void onUserInfoChanged(String chatSessionId, String uid, ChatBaseUser user) {
    }

    public void onChatSceneChanged(String chatSessionId, ChatScene scene) {
        try {
            if(this._chatData == null || scene == null) {
                return;
            }

            if(this._model == 1) {
                return;
            }

            this._chatData._evaluateFlagNum = scene.evaluable;
            this._chatData._enableevaluation = scene.enableevaluation;
            if(scene.score == 0) {
                this._chatData._ealuated = false;
            } else {
                this._chatData._ealuated = true;
            }

            this.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        if(ChatActivity.this.faceRelativeLayout != null) {
                            ChatActivity.this.faceRelativeLayout.refreshFunctions(ChatActivity.this.getResources().getString(R.string.xn_valuation), ChatActivity.this._chatData._ealuated);
                        }

                        if(XNChatSDK.getInstance().findChatSessionByChatSessionid(ChatActivity.this._chatSessionId) != null) {
                            ChatActivity.this._userType = XNChatSDK.getInstance().findChatSessionByChatSessionid(ChatActivity.this._chatSessionId)._usertype;
                            XNLOG.i(new String[]{"jiaojiao__userType=" + ChatActivity.this._userType});
                            if(ChatActivity.this._userType != 1) {
                                XNLOG.i(new String[]{"jiaojiao__userType=人工客服,切换布局"});
                                ChatActivity.this.faceRelativeLayout.btnVoice.setVisibility(View.VISIBLE);
                                ChatActivity.this.faceRelativeLayout.rl_robot.setVisibility(View.GONE);
                            } else {
                                XNLOG.i(new String[]{"jiaojiao__userType=机器人客服"});
                                ChatActivity.this.faceRelativeLayout.btnVoice.setVisibility(View.GONE);
                                if(ChatActivity.this.faceRelativeLayout.btnRecord.getVisibility() == View.VISIBLE) {
                                    ChatActivity.this.faceRelativeLayout.btnRecord.setVisibility(View.INVISIBLE);
                                    ChatActivity.this.faceRelativeLayout.mEditTextContent.setVisibility(View.VISIBLE);
                                    ChatActivity.this.faceRelativeLayout.btnVoice.setBackgroundResource(R.drawable.btnvoice1);
                                }

                                ChatActivity.this.faceRelativeLayout.rl_robot.setVisibility(View.VISIBLE);
                                ChatActivity.this.faceRelativeLayout.rl_robot.setClickable(true);
                                ChatActivity.this.faceRelativeLayout.rl_robot.setOnClickListener(new OnClickListener() {
                                    public void onClick(View v) {
                                        TextMessageBody textMessageBody;
                                        if(GlobalParam.getInstance().firstClickRobot) {
                                            textMessageBody = new TextMessageBody();
                                            textMessageBody.textmsg = v.getContext().getResources().getString(R.string.xn_swifttorobot);
                                            textMessageBody.fontsize = 20;
                                            textMessageBody.color = "0x000000";
                                            textMessageBody.italic = false;
                                            textMessageBody.bold = false;
                                            textMessageBody.underline = false;
                                            textMessageBody.isrobert = true;
                                            GlobalParam.getInstance().firstClickRobot = false;
                                            XNChatSDK.getInstance().sendTextMessage(XNSDKUICore.getInstance().getCurrentChatSessionid(), textMessageBody);
                                        } else if(GlobalParam.getInstance().robotCanClick) {
                                            textMessageBody = new TextMessageBody();
                                            textMessageBody.textmsg = v.getContext().getResources().getString(R.string.xn_swifttorobot);
                                            textMessageBody.fontsize = 20;
                                            textMessageBody.color = "0x000000";
                                            textMessageBody.italic = false;
                                            textMessageBody.bold = false;
                                            textMessageBody.underline = false;
                                            textMessageBody.isrobert = true;
                                            XNLOG.i(new String[]{"机器人点击发送44"});
                                            GlobalParam.getInstance().robotCanClick = false;
                                            XNChatSDK.getInstance().sendTextMessage(XNSDKUICore.getInstance().getCurrentChatSessionid(), textMessageBody);
                                        }

                                    }
                                });
                            }
                        }
                    } catch (Exception var2) {
                        XNLOG.e(new String[]{"Exception onChatSceneChanged2:", var2.toString()});
                    }

                }
            });
        } catch (Exception var4) {
            XNLOG.i(new String[]{"Exception", "onChatSceneChanged " + var4.toString()});
        }

    }

    public void onInvitedEvaluate(String chatSessionId, String uid) {
        try {
            if(this._model == 1) {
                return;
            }

            Intent e = new Intent(this, ValuationActivity.class);
            this.startActivity(e);
        } catch (Exception var4) {
            XNLOG.i(new String[]{"Exception", "onInvitedEvaluate " + var4.toString()});
        }

    }

    public void onUserInputing(String chatSessionId, String uid) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                ChatActivity.this.tv_setting_or_kefu_name.setText(ChatActivity.this.getResources().getString(R.string.xn_kefu_inputing));
                ChatActivity.this.tv_setting_or_kefu_name.postDelayed(new Runnable() {
                    public void run() {
                        ChatActivity.this.tv_setting_or_kefu_name.setText(ChatActivity.this._chatData.getTitleName(ChatActivity.this._model, ChatActivity.this._chatData.ui_settingname));
                    }
                }, 3000L);
            }
        });
    }

    public void onChatShowMessage(String chatSessionId, final List<BaseMessage> chatMessageList, final BaseMessage chatMessage, final int msgstatus) {
        try {
            if(this._chatData == null) {
                return;
            }

            this.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        boolean e;
                        BaseMessage msg;
                        Iterator isInpuEditTextFocused;
                        if(chatMessageList != null && chatMessageList.size() != 0) {
                            if(ChatActivity.this._chatData.ui_wholemsglist.size() == 0) {
                                ChatActivity.this._chatData.ui_wholemsglist.addAll(chatMessageList);
                            } else {
                                e = false;
                                isInpuEditTextFocused = ChatActivity.this._chatData.ui_wholemsglist.iterator();

                                while(isInpuEditTextFocused.hasNext()) {
                                    msg = (BaseMessage)isInpuEditTextFocused.next();
                                    if(msg.msgid.equals(((BaseMessage)chatMessageList.get(0)).msgid)) {
                                        e = true;
                                        break;
                                    }
                                }

                                if(!e) {
                                    ArrayList var7 = new ArrayList();
                                    var7.addAll(ChatActivity.this._chatData.ui_wholemsglist);
                                    ChatActivity.this._chatData.ui_wholemsglist.clear();
                                    ChatActivity.this._chatData.ui_wholemsglist.addAll(chatMessageList);
                                    ChatActivity.this._chatData.ui_wholemsglist.addAll(var7);
                                }
                            }

                            if(ChatActivity.this._chatData._comeToChatWindowNum == 1) {
                                SystemMessageBody var6 = new SystemMessageBody();
                                var6.msgsubtype = 522;
                                var6.isnottosend = true;
                                XNChatSDK.getInstance().sendSystemMessage(ChatActivity.this._chatData._chatsessionid, var6);
                            }

                            ChatActivity.this.showChatContent(ChatActivity.this.showMsgNumCount, Boolean.valueOf(true));
                        }

                        if(chatMessage == null && msgstatus == 0) {
                            if(ChatActivity.this._chatData._comeToChatWindowNum == 1) {
                                ChatActivity.this._chatData._LocalOrHistoryMsgReady = true;
                            }

                            if(ChatActivity.this._chatData._goodsIdIsChanged) {
                                ChatActivity.this.sendConsultStartPage(ChatActivity.this._chatData);
                                ChatActivity.this.showERPAtNet(ChatActivity.this._chatData);
                                ChatActivity.this.showProductAtNet(ChatActivity.this._chatData);
                            }
                        }

                        if(chatMessage == null) {
                            return;
                        }

                        if(chatMessage.msgtype == 0) {
                            return;
                        }

                        if(chatMessage.msgtype == 5) {
                            if(chatMessage.msgsubtype == 0) {
                                return;
                            }

                            if(chatMessage.msgsubtype == 57) {
                                return;
                            }

                            if(chatMessage.msgsubtype == 55) {
                                return;
                            }

                            if(chatMessage.msgsubtype == 58) {
                                return;
                            }

                            if(chatMessage.msgsubtype == 513) {
                                return;
                            }

                            if(chatMessage.msgsubtype == 53 && ChatActivity.this._chatData.toDestoryChatSession) {
                                ChatActivity.this._chatData.toDestoryChatSession = false;
                                XNChatSDK.getInstance().stopChatBySession(ChatActivity.this._chatSessionId);
                                XNLOG.i(new String[]{"关闭聊窗ChatActivity", "4"});
                                ChatActivity.this.closeChatWindow();
                                Toast.makeText(ChatActivity.this.getApplicationContext(), ChatActivity.this.getResources().getString(R.string.xn_valuationtip_hasposted), Toast.LENGTH_SHORT).show();
                            }

                            ChatSystemMsg var8 = (ChatSystemMsg)chatMessage;
                            if(chatMessage.msgsubtype == 59) {
                                return;
                            }

                            if(chatMessage.msgsubtype == 56 && var8.invitation_action == 2) {
                                XNChatSDK.getInstance().stopChatBySession(ChatActivity.this._chatSessionId);
                                XNLOG.i(new String[]{"关闭聊窗ChatActivity", "5"});
                                ChatActivity.this.closeChatWindow();
                                Toast.makeText(ChatActivity.this.getApplicationContext(), ChatActivity.this.getResources().getString(R.string.xn_switch_succeed), Toast.LENGTH_SHORT).show();
                            }
                        }

                        if(chatMessage != null) {
                            e = false;
                            isInpuEditTextFocused = ChatActivity.this._chatData.ui_wholemsglist.iterator();

                            while(isInpuEditTextFocused.hasNext()) {
                                msg = (BaseMessage)isInpuEditTextFocused.next();
                                if(msg.msgid.equals(chatMessage.msgid)) {
                                    msg.sendstatus = chatMessage.sendstatus;
                                    e = true;
                                }
                            }

                            msg = null;
                            Iterator var4 = ChatActivity.this._chatData.ui_wholemsglist.iterator();

                            while(var4.hasNext()) {
                                BaseMessage var9 = (BaseMessage)var4.next();
                                if(chatMessage.msgtype == 5 && var9.msgsubtype == chatMessage.msgsubtype && var9.isonlyone) {
                                    msg = var9;
                                    e = false;
                                    break;
                                }
                            }

                            if(msg != null) {
                                ChatActivity.this._chatData.ui_wholemsglist.remove(msg);
                            }

                            if(e) {
                                if(chatMessage.isReSend && chatMessage.sendstatus == 2) {
                                    ChatActivity.this._chatData.ui_wholemsglist.remove(chatMessage);
                                    ChatActivity.this._chatData.ui_wholemsglist.add(chatMessage);
                                }

                                ChatActivity.this.mAdapter.notifyDataSetChanged();
                                if(chatMessage.msgtype == 2) {
                                    XNLOG.i(new String[]{"onPostFileACK", "chatMessage.sendstatus2:" + chatMessage.sendstatus});
                                    ChatActivity.this.mListView.requestFocusFromTouch();
                                }

                                if(chatMessage.msgsubtype != 660 && chatMessage.sendstatus == 2) {
                                    ChatActivity.this.mListView.setSelection(ChatActivity.this.mListView.getCount() - 1);
                                }
                            } else {
                                int var10;
                                if(chatMessage.msgsubtype == 522) {
                                    for(var10 = 0; var10 < ChatActivity.this._chatData.ui_wholemsglist.size(); ++var10) {
                                        if(!((BaseMessage)ChatActivity.this._chatData.ui_wholemsglist.get(var10)).isHistoryMsg) {
                                            if(var10 != 0) {
                                                ChatActivity.this._chatData.ui_wholemsglist.add(var10, chatMessage);
                                            }
                                            break;
                                        }

                                        if(var10 == ChatActivity.this._chatData.ui_wholemsglist.size() - 1) {
                                            if(((BaseMessage)ChatActivity.this._chatData.ui_wholemsglist.get(var10)).isHistoryMsg) {
                                                ChatActivity.this._chatData.ui_wholemsglist.add(chatMessage);
                                            }
                                            break;
                                        }
                                    }
                                } else if(chatMessage.isHistoryMsg) {
                                    if(ChatActivity.this._chatData.ui_wholemsglist.size() != 0) {
                                        for(var10 = ChatActivity.this._chatData.ui_wholemsglist.size() - 1; var10 >= 0; --var10) {
                                            if(chatMessage.msgtime < ((BaseMessage)ChatActivity.this._chatData.ui_wholemsglist.get(var10)).msgtime && ((BaseMessage)ChatActivity.this._chatData.ui_wholemsglist.get(var10)).msgtype != 5) {
                                                ChatActivity.this._chatData.ui_wholemsglist.add(var10, chatMessage);
                                                break;
                                            }

                                            if(var10 == ChatActivity.this._chatData.ui_wholemsglist.size() - 1) {
                                                ChatActivity.this._chatData.ui_wholemsglist.add(chatMessage);
                                                break;
                                            }
                                        }
                                    } else {
                                        ChatActivity.this._chatData.ui_wholemsglist.add(chatMessage);
                                    }
                                } else {
                                    ChatActivity.this._chatData.ui_wholemsglist.add(chatMessage);
                                }

                                ChatActivity.this.mAdapter.notifyDataSetChanged();
                                boolean var11 = false;
                                if(ChatActivity.this.faceRelativeLayout != null && ChatActivity.this.faceRelativeLayout.mEditTextContent != null) {
                                    var11 = ChatActivity.this.faceRelativeLayout.mEditTextContent.isFocused();
                                }

                                if(chatMessage.msgtype == 2 || chatMessage.msgsubtype == 522) {
                                    ChatActivity.this.mListView.requestFocusFromTouch();
                                }

                                if(chatMessage.msgsubtype != 660) {
                                    ChatActivity.this.mListView.setSelection(ChatActivity.this.mListView.getCount() - 1);
                                }

                                if(var11) {
                                    ChatActivity.this.faceRelativeLayout.mEditTextContent.requestFocus();
                                }
                            }

                            if(ChatActivity.this._model != 0) {
                                return;
                            }

                            if(chatMessage.isSelfMsg) {
                                return;
                            }

                            if(chatMessage.isHistoryMsg) {
                                return;
                            }

                            if(!XNCoreUtils.isKFID(chatMessage.uid)) {
                                return;
                            }

                            if(ChatActivity.this._chatData.scenemode == 0 && chatMessage.uid.contains("robot")) {
                                return;
                            }

                            ChatActivity.this._chatData._visitor_status = 1;
                            XNLOG.e(new String[]{"刷新台头", chatMessage.textmsg});
                            ChatActivity.this._chatData.ui_settingname = chatMessage.uname;
                            XNLOG.i(new String[]{"updateSettingname,005=" + ChatActivity.this._chatData.ui_settingname});
                            ChatActivity.this.tv_setting_or_kefu_name.setText(ChatActivity.this._chatData.getTitleName(ChatActivity.this._model, ChatActivity.this._chatData.ui_settingname));
                        }
                    } catch (Exception var7x) {
                        XNLOG.e(new String[]{"Exception onChatShowMessage:", var7x.toString()});
                    }

                }
            });
        } catch (Exception var6) {
            XNLOG.i(new String[]{"Exception", "onChatShowMessage " + var6.toString()});
        }

    }

    public void onNotifyUnreadMessage(String settingid, String settingname, String uid, ChatBaseUser user, int messagecount) {
    }

    public void onError(int code) {
    }

    public void onGetedGoodsInfo(String goodsinfojson) {
        try {
            if(this._chatData == null) {
                return;
            }

            if(goodsinfojson.contains("error") || TextUtils.isEmpty(goodsinfojson)) {
                return;
            }

            this.runOnUiThread(new Runnable() {
                public void run() {
                    ChatActivity.this.showProductInfoByWidgets(ChatActivity.this._chatData);
                }
            });
        } catch (Exception var3) {
            XNLOG.i(new String[]{"Exception", "onGetedGoodsInfo " + var3.toString()});
        }

    }

    public void onRefresh() {
        try {
            if(this._chatData == null || this._chatData.ui_wholemsglist == null || this.mAdapter == null || this.mListView == null) {
                this.mListView.onRefreshComplete();
                return;
            }

            if(this.mAdapter.getShowMsgNum() == this._chatData.ui_wholemsglist.size()) {
                this.mListView.setHasMoreStatus(false, false);
            } else {
                this.mListView.setHasMoreStatus(false, true);
            }

            this.mListView.postDelayed(new Runnable() {
                public void run() {
                    ChatActivity.this._chatData.lastDisplayNum = ChatActivity.this.mListView.getCount();
                    ChatActivity.this.showMsgNumCount = ChatActivity.this.showMsgNumCount + 1;
                    ChatActivity.this.showChatContent(ChatActivity.this.showMsgNumCount, Boolean.valueOf(false));
                    ChatActivity.this.mListView.onRefreshComplete();
                }
            }, 500L);
        } catch (Exception var2) {
            XNLOG.i(new String[]{"Exception onRefresh " + var2.toString()});
        }

    }

    private void showChatContent(int selectmsgnum, Boolean firstRefresh) {
        try {
            if(this._chatData == null || this.mAdapter == null || this.mListView == null) {
                return;
            }

            this.mAdapter.setShowMsgNum(selectmsgnum * 20);
            this.mAdapter.notifyDataSetChanged();
            if(firstRefresh.booleanValue()) {
                this.mListView.setSelection(this.mListView.getCount() - 1);
            } else {
                int e = this.mListView.getCount() - this._chatData.lastDisplayNum;
                if(e > 0) {
                    this.mListView.setSelection(e + 1);
                }
            }
        } catch (Exception var4) {
            XNLOG.i(new String[]{"出错了", "Addchatinfo()=" + var4.toString()});
        }

    }

    public void onNotifyTransfer(TransferActionData transferActionData) {
        try {
            if(this._model != 1) {
                return;
            }

            if(transferActionData == null) {
                return;
            }

            if(this._chatData == null || this._chatData._settingid == null || this._chatData._settingid.trim().length() == 0) {
                return;
            }

            if(!this._chatData._settingid.equals(transferActionData.transferUserId)) {
                return;
            }

            if(transferActionData.action == 0) {
                XNLOG.e(new String[]{"transfertest", "getChatSessionAndChatData 5"});
                this._chatData.transferActionData = null;
                this.fl_showtips2.setVisibility(View.GONE);
            }
        } catch (Exception var3) {
            XNLOG.i(new String[]{"Exception", "onNotifyTransfer " + var3.toString()});
        }

    }

    public void onNotifyFinishChatWindow() {
        XNLOG.i(new String[]{"关闭聊窗ChatActivity", "1"});
        this.finish();
    }

    public void onNotifyRefeshFunctions(String functionname) {
    }

    public void onNotifyReported(boolean changed, int actionStatus) {
        if(this._model == 1) {
            this.fl_showtips3.setVisibility(View.VISIBLE);
            this.fl_showtips3.bringToFront();
            if(!changed && actionStatus == 1) {
                this.fl_showtips3.startAnimation(this.alpha);
            }
        }

    }

    public void onNotityStopVoice() {
        if(this.mAdapter != null) {
            this.mAdapter.stopVoice();
        }

    }

    public void onSetMsgInEditText(String content) {
        if(this.faceRelativeLayout != null) {
            this.faceRelativeLayout.mEditTextContent.setText(content);
        }

    }
}
