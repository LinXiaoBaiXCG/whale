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
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
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

    private Bootstrap bootstrap;

    private EventLoopGroup group = new NioEventLoopGroup();

    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

    public NettyClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws InterruptedException {
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
                        // 心跳控制
                        pipeline.addLast(new IdleStateHandler(0,10,0,TimeUnit.SECONDS));
                        pipeline.addLast(new NettyClientHandler());
                    }
                });
        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channel = channelFuture.channel();
        } finally {
            scheduledExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        try {
                            log.info("重连服务端");
                            connect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public RpcResponse send(RpcRequest request) throws InterruptedException {
        channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("client send message: [{}]", request);
            } else {
                future.channel().close();
                log.error("Send failed:", future.cause());
            }
        });
        //当通道关闭了，就继续往下走
        channel.closeFuture().sync();
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
        return channel.attr(key).get();
    }

}
