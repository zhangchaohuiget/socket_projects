package com.app.utils;

/**
 * @author zch
 * @title 16进制相关工具类
 * @discribtion 本着依赖最少原则，封装常用工具类
 * @Date 2020年5月21日 下午4:31:17
 * @vision V1.0
 */
public class HexUtils {
    /**
     * byte数组转16进制字符串
     *
     * @author zch
     * @Date 2020年5月21日 下午4:36:49
     * @vision V1.0
     */
    public static String byteArr2HexStr(byte[] byteArr) {
        if (byteArr == null) {
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArr.length * 2];
        for (int j = 0; j < byteArr.length; j++) {
            int v = byteArr[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 16进制字符串转byte数组
     *
     * @author zch
     * @Date 2020年5月21日 下午4:39:00
     * @vision V1.0
     */
    public static byte[] hexStr2ByteArr(String hexStr) {
        if (hexStr == null) {
            return null;
        }
        if (hexStr.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[hexStr.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            String subStr = hexStr.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte) Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }

    /**
     * 合并多个byte数组
     *
     * @author zch
     * @Date 2020年5月21日 下午4:41:36
     * @vision V1.0
     */
    public static byte[] mergeAllBytes(byte[]... values) {
        int lengthByte = 0;
        for (byte[] value : values) {
            lengthByte += value.length;
        }
        byte[] allByte = new byte[lengthByte];
        int countLength = 0;
        for (byte[] b : values) {
            System.arraycopy(b, 0, allByte, countLength, b.length);
            countLength += b.length;
        }
        return allByte;
    }

    /**
     * 异或校验
     *
     * @author zch
     * @Date 2020年5月21日 下午4:47:40
     * @vision V1.0
     */
    public static byte getXor(byte[] datas) {
        byte temp = datas[0];
        for (int i = 1; i < datas.length; i++) {
            temp ^= datas[i];
        }
        return temp;
    }

    /**
     * 16进制字符串转整数
     *
     * @author zch
     * @Date 2020年5月21日 下午4:54:15
     * @vision V1.0
     */
    public static int hexStr2Int(String hexStr) {
        return Integer.parseInt(hexStr, 16);
    }

    /**
     * 整数转16进制字符串
     *
     * @author zch
     * @Date 2020年5月21日 下午4:54:54
     * @vision V1.0
     */
    public static String int2HexStr(int num) {
        return Integer.toHexString(num);
    }

    /**
     * 16进制字符串转2进制字符串
     *
     * @author zch
     * @Date 2020年5月21日 下午5:21:30
     * @vision V1.0
     */
    public static String hexStr2BinStr(String hexStr) {
        if (hexStr == null || hexStr.length() % 2 != 0) {
            return null;
        }
        StringBuilder bString = new StringBuilder();
        String tmp;
        for (int i = 0; i < hexStr.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexStr.substring(i, i + 1), 16));
            bString.append(tmp.substring(tmp.length() - 4));
        }
        return bString.toString();
    }

    /**
     * 2进制字符串转16进制字符串
     *
     * @author zch
     * @Date 2020年5月21日 下午5:21:49
     * @vision V1.0
     */
    public static String binStr2HexStr(String binStr) {
        if (binStr == null || "".equals(binStr) || binStr.length() % 8 != 0) {
            return null;
        }
        StringBuilder tmp = new StringBuilder();
        int iTmp;
        for (int i = 0; i < binStr.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(binStr.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    /**
     * 字节转位
     *
     * @author zch
     * @Date 2020年5月21日 下午5:23:29
     * @vision V1.0
     */
    public static String byte2Bit(byte by) {
        return String.valueOf((by >> 7) & 0x1) +
                ((by >> 6) & 0x1) +
                ((by >> 5) & 0x1) +
                ((by >> 4) & 0x1) +
                ((by >> 3) & 0x1) +
                ((by >> 2) & 0x1) +
                ((by >> 1) & 0x1) +
                ((by) & 0x1);
    }

}
