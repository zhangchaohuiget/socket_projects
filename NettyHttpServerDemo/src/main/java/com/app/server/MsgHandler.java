package com.app.server;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 消息异步处理
 */
@Slf4j
@Service
public class MsgHandler {

    @Async
    public void aMsgHandler(ChannelHandlerContext ctx) throws Exception {
        log.info("模拟/a 路由的处理");
        Thread.sleep(1000);
        HttpServerHandler.sendHttpResponse(ctx, "{\"result\":\"1\",\"errorCode\":\"\",\"errorMsg\":\"\"}");
    }
}
