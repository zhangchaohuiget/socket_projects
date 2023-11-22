package com.app.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * http 协议解码器
 *
 * @author zhangch
 * @date 2023/11/20 22:48
 */
@Component
public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Resource
    private HttpServerHandler httpServerHandler;

    public HttpServerChannelInitializer() {

    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // HTTP 服务的解码器
        socketChannel.pipeline().addLast(new HttpServerCodec());
        // HTTP 消息的合并处理
        socketChannel.pipeline().addLast(new HttpObjectAggregator(65536));
        // HTTP 消息的合并处理
        socketChannel.pipeline().addLast(new ChunkedWriteHandler());
        socketChannel.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
        socketChannel.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));
        socketChannel.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
        socketChannel.pipeline().addLast(httpServerHandler);
    }
}
