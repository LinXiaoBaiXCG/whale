package io.github.linxiaobaixcg.communication.netty.server;

import io.github.linxiaobaixcg.enums.HeartBeatEnum;
import io.github.linxiaobaixcg.enums.MessageType;
import io.github.linxiaobaixcg.model.RpcRequest;
import io.github.linxiaobaixcg.model.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * @author lcq
 * @description: 服务端处理器
 * @date 2021/3/8 11:34
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    /**
     * 服务映射
     */
    private Map<String, Object> handlerMap;

    public NettyServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest msg) throws Exception {
        log.debug("收到request:{}", msg);
        Object result;
        if (MessageType.DATA.equals(msg.getMessageType())){
            result = this.invoke(msg);
        }else if (MessageType.HEARTBEAT.equals(msg.getMessageType())){
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setMessageType(MessageType.HEARTBEAT);
            result = rpcResponse;
            log.info("收到心跳包");
            log.info(HeartBeatEnum.PONG.name());
        } else {
            throw new IllegalStateException("请求异常");
        }
        ChannelFuture future = channelHandlerContext.writeAndFlush(result);
        future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Unexpected exception from downstream.", cause);
        ctx.close();
    }

    /**
     * 服务调用返回处理结果
     *
     * @param request 服务请求
     *
     * @return 处理结果
     */
    private Object invoke(RpcRequest request)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //获得服务名称
        String serviceName = request.getClassName();
        //获得版本号
        String version = request.getVersion();
        //获得方法名
        String methodName = request.getMethodName();
        //获得参数数组
        Object[] params = request.getParameters();
        //获得参数类型数据
        Class<?>[] argTypes = Arrays.stream(params).map(Object::getClass).toArray(Class<?>[]::new);
        if (version != null && !"".equals(version)) {
            serviceName = serviceName + "-" + version;
        }
        Object service = handlerMap.get(serviceName);

        Method method = service.getClass().getMethod(methodName, argTypes);
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(request.getRequestId());
        rpcResponse.setMsg("success");
        rpcResponse.setResult(method.invoke(service, params));
        rpcResponse.setMessageType(MessageType.DATA);
        return rpcResponse;
    }
}
