package io.github.linxiaobaixcg.communication.netty.server;

import com.alibaba.fastjson.JSON;
import io.github.linxiaobaixcg.model.ResponseCode;
import io.github.linxiaobaixcg.model.RpcRequest;
import io.github.linxiaobaixcg.model.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 服务映射
     */
    private Map<String, Object> handlerMap;

    public NettyServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        log.debug("收到request:{}", s);
        Object result = this.invoke(JSON.parseObject(s, RpcRequest.class));
        ChannelFuture future = channelHandlerContext.writeAndFlush(JSON.toJSONString(result));
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
        if (null == service) {
            return RpcResponse.fail(ResponseCode.ERROR404, "未找到服务");
        }
        Method method = service.getClass().getMethod(methodName, argTypes);
        if (null == method) {
            return RpcResponse.fail(ResponseCode.ERROR404, "未找到服务方法");
        }
        return RpcResponse.success(method.invoke(service, params));
    }
}
