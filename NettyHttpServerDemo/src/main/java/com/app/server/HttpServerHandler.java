package com.app.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * http 请求处理器
 */
@Slf4j
@Sharable
@Component
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private MsgHandler msgHandler;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof FullHttpRequest) {
                handleHttpRequest(ctx, (FullHttpRequest) msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
            msg = null;
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        String info = req.content().toString(CharsetUtil.UTF_8);
        String uri = req.uri();
        if (uri.startsWith("/a")) {
            msgHandler.aMsgHandler(ctx);
        } else if (uri.startsWith("/b")) {
            log.info("模拟/b 路由的处理");
            Thread.sleep(1000);
            sendHttpResponse(ctx, "{\"result\":\"1\",\"errorCode\":\"\",\"errorMsg\":\"\"}");
        } else {
            // 无效的请求地址
            log.info("无效的请求地址");
            sendHttpResponse(ctx, "无效的请求地址");
        }
    }

    /**
     * 发送响应消息
     */
    public static void sendHttpResponse(ChannelHandlerContext ctx, String rel) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(rel.getBytes(CharsetUtil.UTF_8)));
        response.headers().set(CONTENT_TYPE, "application/json; charset=utf-8");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        if (response.status().code() != OK.code()) {
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(response, response.content().readableBytes());
        }
        ChannelFuture f = ctx.writeAndFlush(response);
        if (!HttpUtil.isKeepAlive(response) || response.status().code() != OK.code()) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.warn("http服务客户端建立链接-----------");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.error("http服务客户端发生异常-----------" + cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 从channel group中移除这个channel
        ctx.pipeline().remove(ctx.handler());
    }

}
