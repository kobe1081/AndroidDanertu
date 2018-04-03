package com.danertu.tools;

import java.util.ArrayList;

import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {
    private Drawable dRight;
    private Rect rBounds;

    private RecognizerDialog rd;

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context) {
        super(context);
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (right != null) {
            dRight = right;
        }
        super.setCompoundDrawables(left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP && dRight != null) {
            try {
                rBounds = dRight.getBounds();
            } catch (Exception e) {
                @SuppressWarnings("unused")
                String s = "123";
            }
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            //System.out.println("x:/y: "+x+"/"+y);
            //System.out.println("bounds: "+bounds.left+"/"+bounds.right+"/"+bounds.top+"/"+bounds.bottom);
            //check to make sure the touch event was within the bounds of the drawable
            if (x >= (this.getLeft() - rBounds.width()) && x <= (this.getRight() - this.getPaddingRight())
                    && y >= this.getPaddingTop() && y <= (this.getHeight() - this.getPaddingBottom())) {
                //System.out.println("touch");
                //   this.setText("");
                try {
                    rd = new RecognizerDialog(getContext(), "appid=50e1b967");
                } catch (Exception e) {
                    @SuppressWarnings("unused")
                    String s = "123";
                }
                rd.setEngine("sms", null, null);

                //设置采样频率，默认是16k，android手机一般只支持8k、16k.为了更好的识别，直接弄成16k即可。
                rd.setSampleRate(RATE.rate16k);

                final StringBuilder sb = new StringBuilder();
                //	Log.i(TAG, "识别准备开始.............");

                //设置识别后的回调结果
                rd.setListener(new RecognizerDialogListener() {
                    @Override
                    public void onResults(ArrayList<RecognizerResult> result, boolean isLast) {
                        for (RecognizerResult recognizerResult : result) {
                            sb.append(recognizerResult.text);
                            //				Log.i(TAG, "识别一条结果为::"+recognizerResult.text);
                        }
                    }

                    @Override
                    public void onEnd(SpeechError error) {
                        //       				Log.i(TAG, "识别完成.............");
                        setText(sb.toString().replace('。', ' ').trim());
                    }
                });

                this.setText(""); //先设置为空，等识别完成后设置内容
                rd.show();

                event.setAction(MotionEvent.ACTION_CANCEL);//use this to prevent the keyboard from coming up
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        dRight = null;
        rBounds = null;
        super.finalize();
    }


} 

