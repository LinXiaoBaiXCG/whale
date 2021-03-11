package io.github.linxiaobaixcg.communication.netty.client;

import io.github.linxiaobaixcg.communication.netty.codec.MessageDecoder;
import io.github.linxiaobaixcg.communication.netty.codec.MessageEncoder;
import io.github.linxiaobaixcg.config.GlobalConfig;
import io.github.linxiaobaixcg.model.RpcRequest;
import io.github.linxiaobaixcg.model.RpcResponse;
import io.github.linxiaobaixcg.enums.SerializeType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author lcq
 * @description: netty客户端
 * @date 2021/3/4 16:25
 */
@Slf4j
public class NettyClient {

    private Channel channel;

    private String host;

    private Integer port;

    private static Bootstrap bootstrap;

    private static EventLoopGroup group = new NioEventLoopGroup();

    public NettyClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    static  {
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                    //指定传输使用的Channel
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // 编码器
                            pipeline.addLast(new MessageEncoder(RpcRequest.class, GlobalConfig.serializeType));
                            // 解码器
                            pipeline.addLast(new MessageDecoder(RpcResponse.class, GlobalConfig.serializeType));
                            pipeline.addLast(new ClientHandler());
                        }
                    });
    }

    public RpcResponse send(RpcRequest request) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        Channel channel = channelFuture.channel();
        channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("client send message: [{}]", request);
            } else {
                future.channel().close();
                log.error("Send failed:", future.cause());
            }
        });
        //当通道关闭了，就继续往下走
        channelFuture.channel().closeFuture().sync();
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
        return channel.attr(key).get();
    }

    /**
     * 失败重连机制
     *
     * @param bootstrap
     * @param host
     * @param port
     * @param retry
     */
    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        ChannelFuture channelFuture = bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                log.info("连接服务端成功");
            } else if (retry == 0) {
                log.error("重试次数已用完，放弃连接");
            } else {
                //第几次重连：
                int order = (5 - retry) + 1;
                //本次重连的间隔
                int delay = 1 << order;
                log.error("{} : 连接失败，第 {} 重连....", new Date(), order);
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
            }
        });
    }

    public static class ClientHandler extends SimpleChannelInboundHandler {

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
            log.debug("收到response:{}", msg);
            RpcResponse response = (RpcResponse)msg;
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            channelHandlerContext.channel().attr(key).set(response);
            channelHandlerContext.channel().close();

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("Unexpected exception from upstream.", cause);
            super.exceptionCaught(ctx, cause);
        }

    }
}
