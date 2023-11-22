package com.app.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 消息异步处理
 *
 * @author ch
 * @date 2023/11/21 11:16
 */
@Slf4j
@Service
public class MsgHandler {

    @Async
    public void aMsgHandler(ChannelHandlerContext ctx) throws Exception {
        log.info("模拟/a 路由的处理");
        Thread.sleep(1000);
        sendHttpResponse(ctx, "{\"result\":\"1\",\"errorCode\":\"\",\"errorMsg\":\"\"}");
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, String rel) {
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

}
