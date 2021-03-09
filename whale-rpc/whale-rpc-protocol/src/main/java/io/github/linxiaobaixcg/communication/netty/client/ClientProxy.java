package io.github.linxiaobaixcg.communication.netty.client;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.linxiaobaixcg.model.ResponseCode;
import io.github.linxiaobaixcg.model.RpcRequest;
import io.github.linxiaobaixcg.model.RpcResponse;
import io.github.linxiaobaixcg.service.DiscoverService;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @author lcq
 * @description: 客户端代理
 * @date 2021/3/5 11:40
 */
@Slf4j
public class ClientProxy<T> implements InvocationHandler {

    private DiscoverService discoverService;

    private String version;

    public ClientProxy(DiscoverService discoverService, String version) {
        this.discoverService = discoverService;
        this.version = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(IdUtil.getSnowflake(1, 1).nextIdStr())
                .className(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .parameterTypes(method.getParameterTypes())
                .version(version)
                .build();
        log.info("请求内容: {}",rpcRequest);
        String serviceName = method.getDeclaringClass().getName();
        if (null != version && !"".equals(version)) {
            serviceName += "-" + version;
        }
        String servicePath = discoverService.discover(serviceName);
        if (null == servicePath) {
            log.error("并未找到服务地址,className:{}", serviceName);
            throw new RuntimeException("未找到服务地址");
        }
        String host = servicePath.split(":")[0];
        int port = Integer.parseInt(servicePath.split(":")[1]);
        RpcResponse response = new NettyClient(host, port).send(rpcRequest);
        if (response == null) {
            throw new RuntimeException("调用服务失败,servicePath:" + servicePath);
        }
        if (response.getCode() == null || !response.getCode().equals(ResponseCode.SUCCESS.getValue())) {
            log.error("调用服务失败,servicePath:{},RpcResponse:{}", servicePath,
                    JSONObject.toJSONString(JSON.toJSONString(response)));
            throw new RuntimeException(response.getMsg());
        } else {
            return response.getResult();
        }
    }
}
