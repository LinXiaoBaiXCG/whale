package io.github.linxiaobaixcg.communication.netty.client;


import ch.qos.logback.classic.sift.SiftAction;
import com.alibaba.fastjson.JSON;
import io.github.linxiaobaixcg.model.RpcRequest;
import io.github.linxiaobaixcg.model.RpcResponse;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.debug("收到response:{}", msg);
        RpcResponse response = JSON.parseObject(msg, RpcResponse.class);
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
        ctx.channel().attr(key).set(response);
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Unexpected exception from upstream.", cause);
        super.exceptionCaught(ctx, cause);
    }
}
