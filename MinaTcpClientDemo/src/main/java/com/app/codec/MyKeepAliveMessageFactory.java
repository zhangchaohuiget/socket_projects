package com.app.codec;

import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

/**
 * 心跳协议包
 */
@Slf4j
public class MyKeepAliveMessageFactory implements KeepAliveMessageFactory {

    @Override
    public boolean isRequest(IoSession session, Object message) {
        return false;
    }

    @Override
    public boolean isResponse(IoSession session, Object message) {
        return false;
    }

    @Override
    public Object getRequest(IoSession session) {
        log.info("心跳发送成功");
        return "1";
    }

    @Override
    public Object getResponse(IoSession session, Object request) {
        return null;
    }
}
