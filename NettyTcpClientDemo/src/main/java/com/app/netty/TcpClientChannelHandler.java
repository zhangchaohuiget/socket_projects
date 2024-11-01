package com.app.netty;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@ChannelHandler.Sharable
@Component
public class TcpClientChannelHandler extends ChannelInboundHandlerAdapter {

    private final NettyStarter nettyStarter;

    public TcpClientChannelHandler(NettyStarter nettyStarter) {
        this.nettyStarter = nettyStarter;
    }

    public static final String HEART_BEAT = "heart beat!";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("接收到消息:{}", msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("与服务端断开连接，即将重连...");
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(() -> nettyStarter.doConnect(), 5L, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }

    /**
     * 空闲心跳机制
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("发送心跳");
                ctx.writeAndFlush(HEART_BEAT);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
//        定时心跳机制
//        this.channel = ctx.channel();
//        ping(ctx.channel());
    }

    private void ping(Channel channel) {
        ScheduledFuture<?> future = channel.eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                if (channel.isActive()) {
                    channel.writeAndFlush(HEART_BEAT);
                } else {
                    channel.closeFuture();
                    throw new RuntimeException();
                }
            }
        }, 10L, TimeUnit.SECONDS);
        future.addListener((GenericFutureListener) future1 -> {
            if (future1.isSuccess()) {
                ping(channel);
            }
        });
    }
}
