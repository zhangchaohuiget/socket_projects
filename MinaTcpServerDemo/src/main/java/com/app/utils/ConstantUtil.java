package com.app.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantUtil {
    public static final String ZORO_STRING = "0";

    public static final String ONE_STRING = "1";

    public static final int ZERO = 0;

    public static final int ONE = 1;

    @Value("${TCPServer.enabled}")
    public boolean socketEnabled;

    @Value("${TCPServer.port}")
    public int socketPort;

}
