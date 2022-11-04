package com.app.utils.warpper;

import io.github.hylexus.oaks.utils.BcdOps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 8421 码，n 字节
 **/
public class BCDWrapper extends ValueWrapper {
    public BCDWrapper(byte[] bytes) {
        super(bytes);
    }

    public static BCDWrapper valueOf(String bcdString) {
        return new BCDWrapper(BcdOps.string2Bcd(bcdString));
    }

    @Override
    public String toString() {
        return BcdOps.bcd2String(bytes);
    }

    public Date toDate() {
        try {
            return new SimpleDateFormat("yyMMddHHmmss").parse(toString());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
