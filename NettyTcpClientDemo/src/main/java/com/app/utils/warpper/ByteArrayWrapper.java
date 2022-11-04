package com.app.utils.warpper;

import java.io.UnsupportedEncodingException;

public class ByteArrayWrapper extends ValueWrapper {
    public ByteArrayWrapper(byte[] bytes) {
        super(bytes);
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
