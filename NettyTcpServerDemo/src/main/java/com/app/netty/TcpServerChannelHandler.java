package com.app.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@ChannelHandler.Sharable
public class TcpServerChannelHandler extends ChannelInboundHandlerAdapter {

    private static final ConcurrentHashMap<String, ChannelHandlerContext> CHANNEL_MAP = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("收到消息：{}", msg);
    }

    /**
     * session管理
     **/
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //获取连接通道唯一标识
        String channelId = ctx.channel().id().asLongText();
        //如果map中不包含此连接，就保存连接
        if (!CHANNEL_MAP.containsKey(channelId)) {
            CHANNEL_MAP.put(channelId, ctx);
            log.info("新的连接加入了：[{}]", channelId);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String channelId = ctx.channel().id().asLongText();
        if (CHANNEL_MAP.containsKey(channelId)) {
            //删除连接
            CHANNEL_MAP.remove(channelId);
            log.info("连接已断开：[{}]", channelId);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            ctx.close();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (!(evt instanceof IdleStateEvent)) {
            super.userEventTriggered(ctx, evt);
            return;
        }
        if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
            ctx.disconnect();
        }
    }
}
