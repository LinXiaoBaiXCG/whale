package io.github.linxiaobaixcg.communication.netty.client;

import cn.hutool.core.util.IdUtil;
import io.github.linxiaobaixcg.model.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author lcq
 * @description: 客户端代理
 * @date 2021/3/5 11:40
 */
public class ClientProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(IdUtil.getSnowflake(1, 1).nextIdStr())
                .className(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .parameterTypes(method.getParameterTypes())
                .build();

        return null;
    }
}
