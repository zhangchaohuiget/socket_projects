package com.app;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.app.codec.DockHandler;
import com.app.codec.MyKeepAliveMessageFactory;
import com.app.service.SockerHandlerService;
import com.app.utils.ConstantUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(value = 5)
public class StartService implements ApplicationRunner {
    @Autowired
    public ConstantUtil constantUtil;
    @Autowired
    public SockerHandlerService sockerHandlerService;

    public static Map<String, IoSession> sessions = new HashMap<>();

    @Override
    public void run(ApplicationArguments args) {
        if (constantUtil.socketEnabled) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        NioSocketConnector connector = new NioSocketConnector(); // 创建连接客户端
                        connector.setConnectTimeoutMillis(30000); // 设置连接超时
                        connector.getFilterChain().addFirst("reconnection", new IoFilterAdapter() {
                            @Override
                            public void sessionClosed(NextFilter nextFilter, IoSession ioSession)
                                    throws Exception {
                                for (; ; ) {
                                    try {
                                        Thread.sleep(3000);
                                        ConnectFuture future = connector.connect();
                                        future.awaitUninterruptibly();// 等待连接创建成功
                                        IoSession session = future.getSession();// 获取会话
                                        if (session.isConnected()) {
                                            log.info("断线重连成功");
                                            sessions.put("session", session);
                                            break;
                                        }
                                    } catch (Exception ex) {
                                        log.info("断线重连失败,3s再次连接");
                                    }
                                }
                            }
                        });
                        TextLineCodecFactory factory = new TextLineCodecFactory(StandardCharsets.UTF_8);
                        factory.setDecoderMaxLineLength(Integer.MAX_VALUE);
                        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(factory));
                        connector.getSessionConfig().setReceiveBufferSize(Integer.MAX_VALUE); // 设置接收缓冲区的大小
                        connector.getSessionConfig().setSendBufferSize(Integer.MAX_VALUE);// 设置输出缓冲区的大小
                        connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
                        connector.setDefaultRemoteAddress(new InetSocketAddress(constantUtil.socketIP, constantUtil.socketPort));// 设置默认访问地址
                        connector.getSessionConfig().setTcpNoDelay(true);
                        connector.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
                        connector.setHandler(new DockHandler(sockerHandlerService));

                        // 心跳设置
                        MyKeepAliveMessageFactory heartBeat = new MyKeepAliveMessageFactory();
                        KeepAliveFilter keepAliveFilter = new KeepAliveFilter(heartBeat, IdleStatus.READER_IDLE, KeepAliveRequestTimeoutHandler.NOOP);
                        keepAliveFilter.setForwardEvent(false);
                        keepAliveFilter.setRequestInterval(10);
                        keepAliveFilter.setRequestTimeout(1);
                        connector.getFilterChain().addLast("heart", keepAliveFilter);

                        // 启动时连接失败自动重连
                        for (; ; ) {
                            try {
                                ConnectFuture future = connector.connect();
                                future.awaitUninterruptibly(); // 等待连接创建成功
                                IoSession session = future.getSession(); // 获取会话
                                sessions.put("session", session);
                                log.info("连接服务端[成功]");
                                break;
                            } catch (RuntimeIoException e) {
                                log.error("连接服务端[失败]，5S后重新连接");
                                Thread.sleep(5000);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

}
