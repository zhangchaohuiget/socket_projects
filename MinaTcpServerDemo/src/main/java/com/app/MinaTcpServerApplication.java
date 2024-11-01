package com.app;

import com.app.codec.TcpServerIoHandler;
import com.app.service.BusinessService;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootApplication
public class MinaTcpServerApplication implements CommandLineRunner {

    @Value("${tcp-server.enabled}")
    public boolean enabled;

    @Value("${tcp-server.port}")
    public int serverPort;

    @Resource
    public BusinessService businessService;

    public static void main(String[] args) {
        SpringApplication.run(MinaTcpServerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (enabled) {
            try {
                IoAcceptor acceptor = new NioSocketAcceptor();
                // 文本行解码器
                TextLineCodecFactory factory = new TextLineCodecFactory(StandardCharsets.UTF_8);
                factory.setDecoderMaxLineLength(Integer.MAX_VALUE);
                acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(factory));
                acceptor.getSessionConfig().setReadBufferSize(2048);
                acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 20);
                acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
                acceptor.setHandler(new TcpServerIoHandler(businessService));
                acceptor.bind(new InetSocketAddress("127.0.0.1", serverPort));
                log.info("=== TCP Server start on port：{}", serverPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
