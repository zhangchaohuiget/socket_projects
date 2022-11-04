package com.app.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class NettyServer {

    @Value("${tcpServer.port}")
    private int serverPort;
    @Autowired
    private NettyServerHandler nettyServerHandler;

    private static boolean isStart = false;

    EventLoopGroup bossGroup = new NioEventLoopGroup(5);
    EventLoopGroup workerGroup = new NioEventLoopGroup(20);

    public boolean serverStart() {
        try {
            if (isStart) {
                return true;
            }

            bossGroup = new NioEventLoopGroup(5);
            workerGroup = new NioEventLoopGroup(20);

            ServerBootstrap bootstrap = new ServerBootstrap();
            // 绑定线程池
            bootstrap.group(bossGroup, workerGroup);
            //服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝
            bootstrap.option(ChannelOption.SO_BACKLOG, 2048)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    //保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.channel(NioServerSocketChannel.class);
            ChannelInitializer<SocketChannel> channelChannelInitializer = new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new IdleStateHandler(40, 0, 0, TimeUnit.MINUTES));
                    pipeline.addLast(new HeatBeatHandler());
                    pipeline.addLast(new LineBasedFrameDecoder(1024));
                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new StringEncoder());
                    pipeline.addLast(nettyServerHandler);
                }
            };
            bootstrap.childHandler(channelChannelInitializer);
            ChannelFuture channelFuture = bootstrap.bind(serverPort).sync();
            log.info("Netty Tcp Server start success on port：{}", serverPort);
//            channelFuture.channel().closeFuture().sync(); // 此处关闭在另一个独立方法，无需阻塞
            isStart = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized boolean serverStop() {
        try {
            if (!isStart) {
                return true;
            }
            Future<?> future = this.workerGroup.shutdownGracefully().await();
            if (!future.isSuccess()) {
                log.error("<---- netty workerGroup cannot be stopped", future.cause());
                return false;
            }
            future = this.bossGroup.shutdownGracefully().await();
            if (!future.isSuccess()) {
                log.error("<---- netty bossGroup cannot be stopped", future.cause());
                return false;
            }
            log.info("关闭Netty Tcp 服务端成功");
            isStart = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
