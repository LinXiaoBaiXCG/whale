package io.github.linxiaobaixcg.communication.netty.server;

import io.github.linxiaobaixcg.annonation.WhaleRpcService;
import io.github.linxiaobaixcg.communication.netty.client.NettyClient;
import io.github.linxiaobaixcg.communication.netty.codec.MessageDecoder;
import io.github.linxiaobaixcg.communication.netty.codec.MessageEncoder;
import io.github.linxiaobaixcg.model.RpcRequest;
import io.github.linxiaobaixcg.model.RpcResponse;
import io.github.linxiaobaixcg.serialize.SerializeType;
import io.github.linxiaobaixcg.serialize.impl.ProtobufImpl;
import io.github.linxiaobaixcg.service.RegisterService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import javax.imageio.spi.ServiceRegistry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lcq
 * @description: netty服务端
 * @date 2021/3/8 11:29
 */
@Slf4j
public class NettyServer {

    /**
     * 服务发布的ip地址
     * 这边自定义因为 InetAddress.getLocalHost().getHostAddress()可能获得是127.0.0.1
     */
    private String serviceIp;

    /**
     * 服务发布端口
     */
    private int servicePort;

    /**
     * 服务名称和服务对象的关系
     */
    private Map<String, Object> handlerMap = new HashMap<>();

    private RegisterService registerService;

    public NettyServer(RegisterService registerService, String ip, int servicePort) {
        this.registerService = registerService;
        this.serviceIp = ip;
        this.servicePort = servicePort;
    }

    /**
     * 绑定服务名以及服务对象
     *
     * @param services 服务列表
     */
    public void bindService(List<Object> services) {
        for (Object service : services) {
            WhaleRpcService anno = service.getClass().getAnnotation(WhaleRpcService.class);
            if (null == anno) {
                //注解为空的情况，version就是空，serviceName就是
                throw new RuntimeException("服务并没有注解，请检查。" + service.getClass().getName());
            }
            String serviceName = anno.value().getName();
            String version = anno.version();
            if (!"".equals(version)) {
                serviceName += "-" + version;
            }
            handlerMap.put(serviceName, service);
        }
    }

    public void start() throws Exception {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        serverBootstrap.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .handler(new LoggingHandler())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // 编码器
                        pipeline.addLast(new MessageEncoder(RpcResponse.class, NettyClient.serializeType));
                        // 解码器
                        pipeline.addLast(new MessageDecoder(RpcRequest.class, NettyClient.serializeType));
                        pipeline.addLast(new NettyServerHandler(handlerMap));
                    }
                });
        serverBootstrap.bind(serviceIp, servicePort).sync();
        log.info("成功启动服务,host:{},port:{}", serviceIp, servicePort);
        //服务注册
        handlerMap.keySet().forEach(serviceName -> {
            try {
                registerService.register(serviceName, serviceIp + ":" + servicePort);
            } catch (Exception e) {

                log.error("服务注册失败,e:{}", e.getMessage());
                throw new RuntimeException("服务注册失败");
            }
            log.info("成功注册服务，服务名称：{},服务地址：{}", serviceName, serviceIp + ":" + servicePort);
        });
    }

    /**
     * 如果端口绑定失败，端口数+1,重新绑定
     *
     * @param serverBootstrap
     * @param port
     */
    public void bind(final ServerBootstrap serverBootstrap, String serviceIp,int port) {
        serverBootstrap.bind(serviceIp,port).addListener(future -> {
            if (future.isSuccess()) {
                log.info("端口[ {} ] 绑定成功", port);
            } else {
                log.error("端口[ {} ] 绑定失败", port);
                bind(serverBootstrap, serviceIp,port + 1);
            }
        });
    }
}
