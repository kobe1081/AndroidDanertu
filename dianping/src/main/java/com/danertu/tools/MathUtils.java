package com.danertu.tools;

import java.math.BigDecimal;

public class MathUtils {
    private static final int DEF_DIV_SCALE = 10;

    /**
     * 高精度double类型乘法
     *
     * @param ds 必须两个或以上
     * @return 多个参数相乘的值
     */
    public static BigDecimal multiply(double... ds) {
        if (ds == null || ds.length < 2) {
            return null;
        }
        BigDecimal result = new BigDecimal(Double.toString(ds[0]));
        for (int i = 1; i < ds.length; i++) {
            double d = ds[i];
            String s = Double.toString(d);
            result = result.multiply(new BigDecimal(s));
        }
        return result;
    }

    /**
     * * 两个Double数相加 *
     *
     * @param v1 *
     * @param v2 *
     * @return Double
     */
    public static Double add(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return Double.valueOf(b1.add(b2).doubleValue());
    }

    /**
     * * 两个Double数相减 *
     *
     * @param v1 *
     * @param v2 *
     * @return Double
     */
    public static Double sub(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return Double.valueOf(b1.subtract(b2).doubleValue());
    }

    /**
     * * 两个Double数相除 *
     *
     * @param v1 *
     * @param v2 *
     * @return Double
     */
    public static Double divide(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return Double.valueOf(b1.divide(b2, DEF_DIV_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue());
    }

    /**
     * * 两个Double数相除，并保留scale位小数 *
     *
     * @param v1    *
     * @param v2    *
     * @param scale *
     * @return Double
     */
    public static Double divide(Double v1, Double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return Double.valueOf(b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue());
    }
}
