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
     * @param
     * @title byte数组转16进制字符串
     * @discribtion
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
     * @param
     * @title 16进制字符串转byte数组
     * @discribtion
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
     * @param
     * @title 合并多个byte数组
     * @discribtion
     * @author zch
     * @Date 2020年5月21日 下午4:41:36
     * @vision V1.0
     */
    public static byte[] mergeAllBytes(byte[]... values) {
        int length_byte = 0;
        for (int i = 0; i < values.length; i++) {
            length_byte += values[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

    /**
     * @param
     * @title 异或校验
     * @discribtion
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

    public static byte[] getXorToarr(byte[] arr) {
        byte x = getXor(arr);
        String hex8 = Integer.toBinaryString((x & 0xFF) + 0x100).substring(1);// 杞垚2杩涘埗瀛楃涓�
        String hight4 = hex8.substring(0, 4);
        int heighint = Integer.valueOf(hight4, 2) + 48;
        String low4 = hex8.substring(4);
        int lowint = Integer.valueOf(low4, 2) + 48;

        return new byte[]{(byte) lowint, (byte) heighint};
    }

    /**
     * @param
     * @title 16进制字符串转整数
     * @discribtion
     * @author zch
     * @Date 2020年5月21日 下午4:54:15
     * @vision V1.0
     */
    public static int hexStr2Int(String hexStr) {
        return Integer.parseInt(hexStr, 16);
    }

    /**
     * @param
     * @title 整数转16进制字符串
     * @discribtion
     * @author zch
     * @Date 2020年5月21日 下午4:54:54
     * @vision V1.0
     */
    public static String int2HexStr(int num) {
        return Integer.toHexString(num);
    }

    /**
     * @param
     * @title 16进制字符串转2进制字符串
     * @discribtion
     * @author zch
     * @Date 2020年5月21日 下午5:21:30
     * @vision V1.0
     */
    public static String hexStr2BinStr(String hexStr) {
        if (hexStr == null || hexStr.length() % 2 != 0) {
            return null;
        }
        String bString = "", tmp;
        for (int i = 0; i < hexStr.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexStr.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    /**
     * @param
     * @title 2进制字符串转16进制字符串
     * @discribtion
     * @author zch
     * @Date 2020年5月21日 下午5:21:49
     * @vision V1.0
     */
    public static String binStr2HexStr(String binStr) {
        if (binStr == null || binStr.equals("") || binStr.length() % 8 != 0) {
            return null;
        }
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
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
     * @param
     * @title 字节转位
     * @discribtion
     * @author zch
     * @Date 2020年5月21日 下午5:23:29
     * @vision V1.0
     */
    public static String byte2Bit(byte by) {
        // @formatter:off
        StringBuffer sb = new StringBuffer();
        sb.append((by >> 7) & 0x1).append((by >> 6) & 0x1).append((by >> 5) & 0x1).append((by >> 4) & 0x1)
                .append((by >> 3) & 0x1).append((by >> 2) & 0x1).append((by >> 1) & 0x1).append((by >> 0) & 0x1);
        // @formatter:on
        return sb.toString();
    }

}
