package com.app;

import com.app.codec.DockHandler;
import com.app.service.SockerHandlerService;
import com.app.utils.ConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    public ConstantUtil constantUtil;

    @Autowired
    public SockerHandlerService sockerHandlerService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (constantUtil.socketEnabled) {
            try {
                IoAcceptor acceptor = new NioSocketAcceptor();
                TextLineCodecFactory factory = new TextLineCodecFactory(StandardCharsets.UTF_8);
                factory.setDecoderMaxLineLength(Integer.MAX_VALUE);
                acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(factory));
                acceptor.getSessionConfig().setReadBufferSize(2048);
                acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 20);
                acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
                acceptor.setHandler(new DockHandler(sockerHandlerService));
                acceptor.bind(new InetSocketAddress("127.0.0.1", constantUtil.socketPort));
                log.info("=== TCP Server start on port：{}", constantUtil.socketPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
