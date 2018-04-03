package com.danertu.tools;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

// 验证码生成工具类
public class VerityCode {

    private static final String[] strContent = new String[]{
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "l", "m",
            "n", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    private static VerityCode bmpCode;

    public static VerityCode getInstance() {
        if (bmpCode == null)
            bmpCode = new VerityCode();
        return bmpCode;
    }

    //default settings
    private static final int DEFAULT_CODE_LENGTH = 4;
    private static final int DEFAULT_FONT_SIZE = 25;
    private static final int DEFAULT_LINE_NUMBER = 2;
    private static final int BASE_PADDING_LEFT = 5, RANGE_PADDING_LEFT = 15, BASE_PADDING_TOP = 15, RANGE_PADDING_TOP = 20;
    private static final int DEFAULT_WIDTH = 60, DEFAULT_HEIGHT = 40;

    //settings decided by the layout xml
    //canvas width and height
    private int width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT;

    //random word space and pading_top
    private int base_padding_left = BASE_PADDING_LEFT, range_padding_left = RANGE_PADDING_LEFT,
            base_padding_top = BASE_PADDING_TOP, range_padding_top = RANGE_PADDING_TOP;

    //number of chars, lines; font size
    private int codeLength = DEFAULT_CODE_LENGTH, line_number = DEFAULT_LINE_NUMBER, font_size = DEFAULT_FONT_SIZE;

    //variables
    private String[] code;
    private String[] strRes;
    private int padding_left, padding_top;
    private Random random = new Random();

    public Bitmap createBitmap() {
        return createBitmap(0, 0, 0, 0);
    }

    public Bitmap createBitmap(int width, int height) {
        return createBitmap(width, height, 0, 0);
    }

    /**
     * 绘制验证码图片并返回
     *
     * @param width
     * @param height
     * @param codeLength
     * @param font_size
     * @return
     */
    public Bitmap createBitmap(int width, int height, int codeLength, int font_size) {

        if (width == 0) {
            width = DEFAULT_WIDTH;
        }
        if (height == 0) {
            height = DEFAULT_HEIGHT;
        }
        if (codeLength == 0) {
            codeLength = DEFAULT_CODE_LENGTH;
        }
        if (font_size == 0) {
            font_size = DEFAULT_FONT_SIZE;
        }

        code = createCode();

        int isRes = isStrContent(strContent);
        if (isRes == 0) {
            return null;
        }

        //创建图片和画布
        Bitmap sourceBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(sourceBitmap);
        canvas.drawColor(Color.YELLOW);
        Paint numPaint = new Paint();
        numPaint.setTextSize(font_size);
        numPaint.setFakeBoldText(true);
        numPaint.setColor(Color.BLACK);

        //设置每个字

        for (int i = 0; i < codeLength; i++) {
            randomTextStyle(numPaint);
            canvas.drawText(code[i], width / 4 * i, height * 3 / 4, numPaint);
//				Matrix mMatrix = new Matrix();
//				mMatrix.setRotate((float)Math.random()*25);
//				canvas.setMatrix(mMatrix);
        }


        //设置绘制干扰的画笔
        Paint interferencePaint = new Paint();
        interferencePaint.setAntiAlias(true);
        interferencePaint.setStrokeWidth(4);
        interferencePaint.setColor(Color.BLACK);
        interferencePaint.setStyle(Paint.Style.FILL);    //设置paint的style

        //绘制直线
        int[] line;
        for (int i = 0; i < 2; i++) {
            line = getLine(height, width);
            canvas.drawLine(line[0], line[1], line[2], line[3], interferencePaint);
        }
        // 绘制小圆点
        int[] point;
        for (int i = 0; i < 100; i++) {
            point = getPoint(height, width);
            canvas.drawCircle(point[0], point[1], 1, interferencePaint);
        }

        canvas.save();
        return sourceBitmap;
    }


    private int isStrContent(String[] strContent) {
        if (strContent == null || strContent.length <= 0) {
            return 0;
        } else {
            return 1;
        }
    }

    //获得画干扰直线的位置
    public static int[] getLine(int height, int width) {
        int[] tempCheckNum = {0, 0, 0, 0, 0};
        for (int i = 0; i < 4; i += 2) {
            tempCheckNum[i] = (int) (Math.random() * width);
            tempCheckNum[i + 1] = (int) (Math.random() * height);
        }
        return tempCheckNum;
    }

    //获得干扰点的位置
    public static int[] getPoint(int height, int width) {
        int[] tempCheckNum = {0, 0, 0, 0, 0};
        tempCheckNum[0] = (int) (Math.random() * width);
        tempCheckNum[1] = (int) (Math.random() * height);
        return tempCheckNum;
    }

    public String[] getCode() {
        return code;
    }

    /**
     * 从指定数组中随机取出4个字符(数组)作为验证码
     *
     * @param
     * @return String
     */
    private String[] createCode() {
        String[] str = new String[codeLength];
//			// 随机串的个数
        int count = strContent.length;
//			// 生成4个随机数
        Random random = new Random();
        for (int i = 0; i < codeLength; i++) {
            str[i] = strContent[random.nextInt(count)].trim();
        }
        return str;
    }

    private void drawLine(Canvas canvas, Paint paint) {
        int color = randomColor();
        int startX = random.nextInt(width);
        int startY = random.nextInt(height);
        int stopX = random.nextInt(width);
        int stopY = random.nextInt(height);
        paint.setStrokeWidth(1);
        paint.setColor(color);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    private int randomColor() {
        return randomColor(1);
    }

    private int randomColor(int rate) {
        int red = random.nextInt(256) / rate;
        int green = random.nextInt(256) / rate;
        int blue = random.nextInt(256) / rate;
        return Color.rgb(red, green, blue);
    }

    private void randomTextStyle(Paint paint) {
        int color = randomColor();
        paint.setColor(color);
        paint.setFakeBoldText(random.nextBoolean());  //true为粗体，false为非粗体
        float skewX = random.nextInt(11) / 10;
        skewX = random.nextBoolean() ? skewX : -skewX;
        paint.setTextSkewX(skewX); //float类型参数，负数表示右斜，整数左斜
//	      paint.setUnderlineText(true); //true为下划线，false为非下划线  
//	      paint.setStrikeThruText(true); //true为删除线，false为非删除线  
    }

    private void randomPadding() {
        padding_left += base_padding_left + random.nextInt(range_padding_left);
        padding_top = base_padding_top + random.nextInt(range_padding_top);
    }


    // 验证输入验证码是否正确
    public boolean checkNum(String userCheck) {
        if (userCheck.length() != codeLength) {
            System.out.println("te.checkNum()return falsess");
            return false;
        }
        String checkString = "";
        for (int i = 0; i < codeLength; i++) {
            checkString += code[i];
        }
        return userCheck.equalsIgnoreCase(checkString);
    }

    public void invalidate() {
        code = createCode();
    }
}
