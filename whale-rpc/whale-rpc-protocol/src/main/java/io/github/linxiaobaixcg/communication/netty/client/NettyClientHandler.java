package io.github.linxiaobaixcg.communication.netty.client;


import io.github.linxiaobaixcg.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse msg) throws Exception {
        log.debug("收到response:{}", msg);
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
        channelHandlerContext.channel().attr(key).set(msg);
        channelHandlerContext.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Unexpected exception from upstream.", cause);
        super.exceptionCaught(ctx, cause);
    }
}
