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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.data.Id;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @author lcq
 * @description: netty客户端
 * @date 2021/3/4 16:25
 */
@Slf4j
public class NettyClient {

    private ChannelProvider channelProvider;

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
                        pipeline.addLast(new IdleStateHandler(0, 10, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new NettyClientHandler());
                    }
                });
        bootstrap.connect(host, port).sync();
        this.channelProvider = new ChannelProvider();
    }

    public CompletableFuture<RpcResponse> send(RpcRequest request) throws InterruptedException {
        CompletableFuture<RpcResponse> responseCompletableFuture = new CompletableFuture<>();
        Channel channel = getChannel(new InetSocketAddress(host, port));
        if (channel.isActive()) {
            NettyClientHandler.RESPONSE_FUTURES_FUTURE_MAP.put(request.getRequestId(), responseCompletableFuture);
            channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("发送消息成功: [{}]", request);
                } else {
                    future.channel().close();
                    log.error("发送失败:", future.cause());
                }
            });
        }
        return responseCompletableFuture;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    public void close() {
        group.shutdownGracefully();
    }
}
