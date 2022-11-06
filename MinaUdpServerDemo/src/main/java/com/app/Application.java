package com.app;

import com.app.codec.DockHandler;
import com.app.service.SockerHandlerService;
import com.app.utils.ConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
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
                NioDatagramAcceptor acceptor = new NioDatagramAcceptor();
                acceptor.setHandler(new DockHandler(sockerHandlerService));
                acceptor.getFilterChain().addLast("logger", new LoggingFilter());
                acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
                acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
                // 设置接收最大字节默认2048
                acceptor.getSessionConfig().setReadBufferSize(4096);
                acceptor.getSessionConfig().setMaxReadBufferSize(65536);
                // 设置输入缓冲区的大小
                acceptor.getSessionConfig().setReceiveBufferSize(1024);
                // 设置输出缓冲区的大小
                acceptor.getSessionConfig().setSendBufferSize(1024);
                // 设置每一个非主监听连接的端口可以重用
                acceptor.getSessionConfig().setReuseAddress(true);
                acceptor.bind(new InetSocketAddress(constantUtil.socketPort));
                log.info("=== UDP Server start on port：{}", constantUtil.socketPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
