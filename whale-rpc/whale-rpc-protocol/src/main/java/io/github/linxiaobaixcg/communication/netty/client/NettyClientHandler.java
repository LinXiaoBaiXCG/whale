package io.github.linxiaobaixcg.communication.netty.client;


import io.github.linxiaobaixcg.enums.HeartBeatType;
import io.github.linxiaobaixcg.enums.MessageType;
import io.github.linxiaobaixcg.model.RpcRequest;
import io.github.linxiaobaixcg.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    public static final Map<String, CompletableFuture<RpcResponse>> RESPONSE_FUTURES_FUTURE_MAP = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse msg) throws Exception {
        log.debug("收到返回结果:{}", msg);
        if (MessageType.DATA.equals(msg.getMessageType())){
            CompletableFuture<RpcResponse> responseCompletableFuture = RESPONSE_FUTURES_FUTURE_MAP.remove(msg.getRequestId());
            if (null != responseCompletableFuture) {
                // 完成异步执行，并返回future的结果
                responseCompletableFuture.complete(msg);
            } else {
                throw new IllegalStateException();
            }
        }else if (MessageType.HEARTBEAT.equals(msg.getMessageType())){
            log.info("心跳检测成功");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Unexpected exception from upstream.", cause);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state().equals(IdleState.READER_IDLE)) {
                System.out.println("长期没收到服务器推送数据");
                //可以选择重新连接
            } else if (idleStateEvent.state().equals(IdleState.WRITER_IDLE)) {
                System.out.println("长期未向服务器发送数据");
                log.info(HeartBeatType.PING.name());
                //发送心跳包
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setMessageType(MessageType.HEARTBEAT);
                ctx.writeAndFlush(rpcRequest);
            } else if (idleStateEvent.state().equals(IdleState.ALL_IDLE)) {
                System.out.println("ALL");
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
