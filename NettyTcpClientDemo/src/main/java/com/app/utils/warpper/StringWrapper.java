package com.app.utils.warpper;

import java.io.UnsupportedEncodingException;

/**
 * GBK 编码
 **/
public class StringWrapper extends ValueWrapper {
    public StringWrapper(byte[] bytes) {
        super(bytes);
    }

    public static StringWrapper valueOf(String str) {
        try {
            return new StringWrapper(str.getBytes(CHARSET_NAME));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        try {
            return new String(bytes, CHARSET_NAME).trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
