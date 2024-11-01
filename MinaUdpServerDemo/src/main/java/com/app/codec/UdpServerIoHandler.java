package com.app.codec;

import com.app.service.BusinessService;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

@Slf4j
public class UdpServerIoHandler extends IoHandlerAdapter {
    private final BusinessService businessService;

    public UdpServerIoHandler(BusinessService businessService) {
        this.businessService = businessService;
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        super.messageReceived(session, message);
        String msg = (String) message;
        log.info("收到消息：" + msg);
        businessService.msgHandle(msg);
    }

    @Override
    public void sessionCreated(IoSession session)
            throws Exception {
        super.sessionCreated(session);
        log.info("创建连接");
    }

    @Override
    public void sessionOpened(IoSession session)
            throws Exception {
        super.sessionOpened(session);
        log.info("建立连接");
    }

    @Override
    public void sessionClosed(IoSession session)
            throws Exception {
        super.sessionClosed(session);
        log.info("连接关闭");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        super.sessionIdle(session, status);
        log.info("重新连接");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        super.exceptionCaught(session, cause);
        log.info("会话异常！");
        if (session != null) {
            session.closeNow();
        }
    }

    @Override
    public void messageSent(IoSession session, Object message)
            throws Exception {
        super.messageSent(session, message);
    }

    @Override
    public void inputClosed(IoSession session)
            throws Exception {
        super.inputClosed(session);
    }

}
