package com.app;

import com.app.server.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * 启动类
 */
@SpringBootApplication
public class NettyHttpApplication {

    @Autowired
    private NettyServer nettyServer;
    private static NettyServer nettyServer_t;

    @PostConstruct
    public void init() {
        setNettyServer(nettyServer);
    }

    private static void setNettyServer(NettyServer nettyServer) {
        nettyServer_t = nettyServer;
    }

    public static void main(String[] args) {
        SpringApplication.run(NettyHttpApplication.class, args);
        nettyServer_t.start();
    }

}
