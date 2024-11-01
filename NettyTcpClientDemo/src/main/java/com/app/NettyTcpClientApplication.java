package com.app;

import com.app.netty.NettyStarter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class NettyTcpClientApplication implements CommandLineRunner {

    @Value("${tcp-server.enabled:false}")
    private boolean enabled;
    @Resource
    private NettyStarter nettyStarter;

    public static void main(String[] args) {
        SpringApplication.run(NettyTcpClientApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (enabled) {
            nettyStarter.clientStart();
        }
    }
}
