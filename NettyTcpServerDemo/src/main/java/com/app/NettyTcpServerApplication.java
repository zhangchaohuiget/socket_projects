package com.app;

import com.app.netty.NettyServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class NettyTcpServerApplication implements CommandLineRunner {

    @Value("${tcp-server.enabled:false}")
    private boolean enabled;

    @Resource
    private NettyServer nettyServer;

    public static void main(String[] args) {
        SpringApplication.run(NettyTcpServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (enabled) {
            nettyServer.serverStart();
        }
    }

}
