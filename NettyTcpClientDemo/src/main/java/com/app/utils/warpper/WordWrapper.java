package com.app.utils.warpper;

import io.github.hylexus.oaks.utils.IntBitOps;

/**
 * 无符号双字节整型(字，16 位)
 **/
public class WordWrapper extends ValueWrapper {
    public WordWrapper(byte[] bytes) {
        super(bytes);
    }

    public int toInt() {
        return IntBitOps.intFromBytes(bytes);
    }

    public static WordWrapper valueOf(Integer integer) {
        return new WordWrapper(IntBitOps.intTo2Bytes(integer));
    }
}
