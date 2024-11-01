package com.app.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * netty客户端启停类
 */
@Slf4j
@Component
public class NettyStarter {

    @Value("${tcp-server.ip}")
    private String host;
    @Value("${tcp-server.port}")
    private int port;

    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup workGroup = new NioEventLoopGroup(8);

    public void clientStart() {
        NettyStarter nettyStarter = this;
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(workGroup);
        bootstrap.option(NioChannelOption.SO_REUSEADDR, true)
                .option(NioChannelOption.TCP_NODELAY, true)
                .option(NioChannelOption.SO_KEEPALIVE, true);
        // 1. ChannelInitializer
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                socketChannel.pipeline().addLast(new IdleStateHandler(30, 30, 30));
                // netty自带行解码器
                socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                socketChannel.pipeline().addLast(new StringDecoder());
                socketChannel.pipeline().addLast(new StringEncoder());
                // 2. ChannelHandler
                socketChannel.pipeline().addLast(new TcpClientChannelHandler(nettyStarter));
            }
        });
        bootstrap.remoteAddress(new InetSocketAddress(host, port));
        doConnect();
    }

    public void doConnect() {
        bootstrap.connect().addListener((ChannelFuture f) -> {
            if (!f.isSuccess()) {
                log.info("连接服务端失败，5s后重连...");
                final EventLoop loop = f.channel().eventLoop();
                loop.schedule(this::doConnect, 5L, TimeUnit.SECONDS);
            } else {
                log.info("建立连接，连接服务端成功！");
            }
        });
    }

}