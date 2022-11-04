package com.app.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * @author zhangchaohui
 * @title zx-work常用方法封装
 * @discribtion
 * @Date 2019年11月12日 上午11:49:19
 * @vision V1.0
 */
public class CommonUtil {
    /**
     * @title 获取yyyy-MM-dd格式的当前日期
     * @discribtion
     * @author zhangchaohui
     * @Date 2019年11月12日 上午11:50:49
     * @vision V1.0
     */
    public static String getTodayDateString() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /**
     * @title 获取yyyy-MM-dd HH:mm:ss格式的当前时间
     * @discribtion
     * @author zhangchaohui
     * @Date 2019年11月12日 上午11:51:52
     * @vision V1.0
     */
    public static String getNowDateTimeString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * @title 时间戳转yyyy-MM-dd HH:mm:ss格式的时间
     * @discribtion
     * @author zhangchaohui
     * @Date 2019年12月30日 上午11:18:04
     * @vision V1.0
     */
    public static String convertLong2DataTime(Long longTime) {
        if (longTime != null) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(longTime));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * @title 判断字符串不为空
     * @discribtion
     * @author zhangchaohui
     * @Date 2019年11月12日 上午11:54:06
     * @vision V1.0
     */
    public static boolean isNotEmpty(String str) {
        return (str != null && !"".equals(str));
    }

    /**
     * @title 判断集合不为空
     * @discribtion
     * @author zhangchaohui
     * @Date 2019年11月12日 上午11:54:06
     * @vision V1.0
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return (collection != null && !collection.isEmpty());
    }

    /**
     * @title int和double相乘
     * @discribtion
     * @author zhangchaohui
     * @Date 2020年1月3日 下午1:22:16
     * @vision V1.0
     */
    public static double mul(int d1, double d2) {
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.multiply(b2).doubleValue();
    }
}
