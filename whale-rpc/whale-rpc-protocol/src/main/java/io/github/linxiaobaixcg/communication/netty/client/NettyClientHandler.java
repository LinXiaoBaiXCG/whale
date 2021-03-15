package io.github.linxiaobaixcg.communication.netty.client;


import io.github.linxiaobaixcg.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    public static final Map<String, CompletableFuture<RpcResponse>> RESPONSE_FUTURES_FUTURE_MAP = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse msg) throws Exception {
        log.debug("收到response:{}", msg);
        CompletableFuture<RpcResponse> responseCompletableFuture = RESPONSE_FUTURES_FUTURE_MAP.remove(msg.getRequestId());
        if (null != responseCompletableFuture) {
            responseCompletableFuture.complete(msg);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Unexpected exception from upstream.", cause);
        super.exceptionCaught(ctx, cause);
    }
}
