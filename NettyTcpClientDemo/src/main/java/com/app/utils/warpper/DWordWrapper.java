package com.app.utils.warpper;

import io.github.hylexus.oaks.utils.IntBitOps;
import io.github.hylexus.oaks.utils.Numbers;

/**
 * 无符号四字节整型(双字，32 位)
 **/
public class DWordWrapper extends ValueWrapper {
    private int readBitIndex;

    public DWordWrapper(byte[] bytes) {
        super(bytes);
        readBitIndex = 0;
    }

    public int toInt() {
        return IntBitOps.intFromBytes(bytes);
    }

    public static DWordWrapper valueOf(Integer integer) {
        return new DWordWrapper(IntBitOps.intTo4Bytes(integer));
    }

    public boolean getBooleanForBit(int index) {
        return Numbers.getBitAt(bytes[0], index) == 1;
    }

    public boolean readBooleanForBit() {
        boolean bool = getBooleanForBit(readBitIndex);
        readBitIndex++;
        return bool;

    }
}
