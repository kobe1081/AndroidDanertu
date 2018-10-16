package com.danertu.dianping;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

import com.danertu.entity.MyOrderData;
import com.danertu.tools.AppManager;

public class AddCommentActivity extends BaseActivity implements OnClickListener {
    private Context context;
    private RatingBar rank;
    private EditText commentText;
    private Button addButton;
    String rankNum = "5";
    String productGuid = "";
    String loginID = "";
    String agentID = "";
    String isInserted = "false";
    String content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        initTitle("写评论");
        findViewById();
        initView();
    }

    public void initTitle(String title) {
        Button b_title = (Button) findViewById(R.id.b_order_title_back);
        b_title.setText(title);
        b_title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    @Override
    protected void findViewById() {
        addButton = (Button) findViewById(R.id.com_save);
        addButton.setOnClickListener(this);

        commentText = (EditText) findViewById(R.id.main_content);
        rank = (RatingBar) findViewById(R.id.rankBar);
        rank.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                rank.setRating(rating);
                rankNum = String.valueOf(rating);
                /*
                 * Toast.makeText(AddCommentActivity.this, "rating:" +
                 * String.valueOf(rating), Toast.LENGTH_LONG) .show();
                 */
            }
        });

    }

    @Override
    protected void initView() {
        productGuid = getIntent().getExtras().getString("proGuid");
        loginID = getIntent().getExtras().getString("loginID");
        agentID = getIntent().getExtras().getString("agentID");

    }

    Runnable addRunnable = new Runnable() {

        @Override
        public void run() {
            if (agentID == null || agentID.trim().length() == 0) {
                agentID = "danertu";
            }
            isInserted = AppManager.getInstance().addComment("0067", productGuid, loginID, content, agentID, rankNum);

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.com_save:
                content = commentText.getText().toString();
                if (content.trim().length() == 0) {
                    Toast.makeText(AddCommentActivity.this, "评论内容不能为空！", Toast.LENGTH_LONG).show();
                } else {
                    Thread addThread = new Thread(addRunnable);
                    addThread.start();
                    try {
                        addThread.join();
                        if (isInserted.equals("true")) {
                            Toast.makeText(AddCommentActivity.this, "提交评论成功！", Toast.LENGTH_LONG).show();
//						Intent intent = new Intent();
//						Bundle bundle = new Bundle();
//						bundle.putString("proGuid", productGuid);
//						bundle.putString("shopid", agentID);
//						intent.putExtras(bundle);
//						intent.setClassName(AddCommentActivity.this,
//								"com.danertu.dianping.ProductCommentActivity");
//						startActivity(intent);
                            MyOrderData.commentOrder(context);
                            finish();
                        } else {
                            judgeIsTokenException(isInserted, new TokenExceptionCallBack() {
                                @Override
                                public void tokenException(String code, final String info) {
                                    sendMessageNew(WHAT_TO_LOGIN, -1, info);
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            jsShowMsg(info);
//                                            quitAccount();
//                                            finish();
//                                            jsStartActivity("LoginActivity", "");
//                                        }
//                                    });
                                }

                                @Override
                                public void ok() {
                                    Toast.makeText(AddCommentActivity.this, "提交评论失败，请检查网络是否正常！", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;

            default:
                break;
        }
    }

}
