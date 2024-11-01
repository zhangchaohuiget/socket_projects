package com.app.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * Netty启动器
 */
@Slf4j
@Component
public class NettyServer {

    /**
     * 创建bootstrap
     */
    ServerBootstrap serverBootstrap = new ServerBootstrap();
    /**
     * BOSS
     */
    EventLoopGroup boss = new NioEventLoopGroup();
    /**
     * Worker
     */
    EventLoopGroup work = new NioEventLoopGroup(8);
    /**
     * 通道适配器
     */
    @Resource
    private HttpServerChannelInitializer httpServerChannelInitializer;

    @PreDestroy
    public void close() {
        // 优雅退出
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }


    /**
     * 开启及服务线程
     */
    public void start() {
        serverBootstrap.group(boss, work).channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_REUSEADDR, true).childOption(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, 1024 * 1024)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture chHttp = null;
        try {
            // http服务
            serverBootstrap.childHandler(httpServerChannelInitializer);
            chHttp = serverBootstrap.bind(8089).sync();
            log.info("Open HttpServer http://localhost:8089");
            chHttp.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

}
