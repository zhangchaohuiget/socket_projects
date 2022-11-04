package com.app.utils.warpper;

public class ByteWrapper extends ValueWrapper {
    public ByteWrapper(byte b) {
        super(new byte[]{b});
    }

    public static ByteWrapper valueOf(byte b) {
        return new ByteWrapper(b);
    }

    public static ByteWrapper valueOf(int i) {
        return new ByteWrapper((byte) i);
    }

    public int toInt() {
        return toByte();
    }

    public byte toByte() {
        return bytes[0];
    }
}
