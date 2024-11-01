package com.app;

import com.app.codec.MyKeepAliveMessageFactory;
import com.app.codec.TcpClientIoHandler;
import com.app.service.BusinessService;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * mina tcp client
 */
@Slf4j
@Component
@Order(value = 1)
public class StartComponent implements ApplicationRunner {
    @Value("${tcp-server.enabled}")
    public boolean enabled;

    @Value("${tcp-server.ip}")
    public String serverIp;

    @Value("${tcp-server.port}")
    public int serverPort;
    @Resource
    public BusinessService businessService;

    public static Map<String, IoSession> sessions = new HashMap<>();

    @Override
    public void run(ApplicationArguments args) {
        if (enabled) {
            // 创建连接客户端
            NioSocketConnector connector = new NioSocketConnector();
            // 设置连接超时
            connector.setConnectTimeoutMillis(30000);
            connector.getFilterChain().addFirst("reconnection", new IoFilterAdapter() {
                @Override
                public void sessionClosed(NextFilter nextFilter, IoSession ioSession) {
                    log.info("连接断开");
                    for (; ; ) {
                        try {
                            TimeUnit.SECONDS.sleep(3);
                            ConnectFuture future = connector.connect();
                            // 等待连接创建成功
                            future.awaitUninterruptibly();
                            // 获取会话
                            IoSession session = future.getSession();
                            if (session.isConnected()) {
                                log.info("断线重连成功");
                                sessions.put("session", session);
                                break;
                            }
                        } catch (Exception e) {
                            log.info("断线重连失败，3s后将再次尝试");
                        }
                    }
                }
            });
            // 使用文本行解码器
            TextLineCodecFactory factory = new TextLineCodecFactory(StandardCharsets.UTF_8);
            factory.setDecoderMaxLineLength(Integer.MAX_VALUE);
            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(factory));
            // 设置接收缓冲区的大小
            // 设置输出缓冲区的大小
            connector.getSessionConfig().setReceiveBufferSize(Integer.MAX_VALUE);
            connector.getSessionConfig().setSendBufferSize(Integer.MAX_VALUE);
            connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
            // 设置默认访问地址
            connector.setDefaultRemoteAddress(new InetSocketAddress(serverIp, serverPort));
            connector.getSessionConfig().setTcpNoDelay(true);
            connector.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
            connector.setHandler(new TcpClientIoHandler(businessService));
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
                    // 等待连接创建成功
                    future.awaitUninterruptibly();
                    // 获取会话
                    IoSession session = future.getSession();
                    sessions.put("session", session);
                    log.info("连接服务端[成功]");
                    break;
                } catch (RuntimeIoException e) {
                    log.error("连接服务端[失败]，3S后重新连接");
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }

    }

}
