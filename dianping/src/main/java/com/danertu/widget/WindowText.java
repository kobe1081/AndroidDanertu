package com.danertu.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.danertu.dianping.R;

public class WindowText extends TextView {

    private Paint paint = null;
    private Drawable bg = null;

    public WindowText(Context context) {
        this(context, null);
    }

    public WindowText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WindowText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        bg = getResources().getDrawable(R.drawable.tips_window);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
    }

    Canvas canvas;

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        final String text = getText().toString();
        final float textSize = getTextSize();
        paint.setTextSize(textSize);
        final float tWidth = paint.measureText(text);
        final int space = dp2px(15);
        final int xWidth = getWidth() / 4;

        canvas.save();
        int w = (int) (tWidth + space);
        int h = bg.getIntrinsicHeight();
        int left = (int) (xWidth * 1.5 - w / 2);
        int top = getHeight() - h;
        int right = left + w;
        int bottom = getHeight();
        Rect rect = new Rect();
        rect.set(left, top, right, bottom);
        bg.setBounds(rect);
        bg.draw(canvas);
        canvas.restore();

        drawText(text, rect.centerX(), rect.centerY() - dp2px(8), Side.CENTER, Alignment.ALIGN_CENTER);

    }

    public int dp2px(int dp) {
        return CommonTools.dip2px(getContext(), dp);
    }

    public enum Side {
        TOP,
        LEFT,
        RIGHT,
        RIGHT_CV,
        RIGHT_BOTTOM,
        BOTTOM,
        BOTTOM_CH,
        CENTER_HORIZONTAL,
        CENTER_VERTICAL,
        CENTER
    }

    public int drawText(String text, float dx, float dy, Side side, Alignment align) {
        align = align == null ? Alignment.ALIGN_NORMAL : align;
        canvas.save();
        TextPaint paint = new TextPaint(this.paint);
        float spacingmult = 1;
        float spacingadd = 0;
        boolean includepad = true;
        final int textWidth = (int) paint.measureText(text);
        final float textSize = paint.getTextSize();
        if (side == Side.RIGHT) {
            dx -= textWidth;
        } else if (side == Side.RIGHT_CV) {
            dx -= textWidth;
            dy -= textSize / 2;
        } else if (side == Side.BOTTOM) {
            dy -= textSize;
        } else if (side == Side.CENTER_HORIZONTAL) {
            dx -= textWidth / 2;
        } else if (side == Side.CENTER_VERTICAL) {
            dy -= textSize / 2;
        } else if (side == Side.CENTER) {
            dx -= textWidth / 2;
            dy -= textSize / 2;
        } else if (side == Side.BOTTOM_CH) {
            dx -= textWidth / 2;
            dy -= textSize;
        } else if (side == Side.RIGHT_BOTTOM) {
            dx -= textWidth;
            dy -= textSize;
        }

        StaticLayout layout = new StaticLayout(text, paint, textWidth, align, spacingmult, spacingadd, includepad);
        canvas.translate(dx, dy);
        layout.draw(canvas);
        canvas.restore();
        return textWidth;
    }
}
