package io.github.linxiaobaixcg.communication.netty.client;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.linxiaobaixcg.model.ResponseCode;
import io.github.linxiaobaixcg.model.RpcRequest;
import io.github.linxiaobaixcg.model.RpcResponse;
import io.github.linxiaobaixcg.service.DiscoverService;
import io.github.linxiaobaixcg.service.LoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * @author lcq
 * @description: 客户端代理
 * @date 2021/3/5 11:40
 */
@Slf4j
public class ClientProxy<T> implements InvocationHandler {

    private DiscoverService discoverService;

    private String version;

    private LoadBalance loadBalance;

    public ClientProxy(DiscoverService discoverService, String version) {
        this.discoverService = discoverService;
        this.version = version;
    }

    public ClientProxy(DiscoverService discoverService, String version, LoadBalance loadBalance) {
        this.discoverService = discoverService;
        this.version = version;
        this.loadBalance = loadBalance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构造请求对象
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(IdUtil.getSnowflake(1, 1).nextIdStr())
                .className(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .parameterTypes(method.getParameterTypes())
                .version(version)
                .build();
        log.info("请求内容: {}", rpcRequest);
        String serviceName = method.getDeclaringClass().getName();
        if (null != version && !"".equals(version)) {
            serviceName += "-" + version;
        }
        // 根据服务名获取服务地址
        List<String> servicePaths = discoverService.discover(serviceName);
        if (CollectionUtils.isEmpty(servicePaths)) {
            log.error("并未找到服务地址,className:{}", serviceName);
            throw new RuntimeException("未找到服务地址");
        }
        // 根据指定的负载均衡获取一个服务地址
        String servicePath = loadBalance.selectServiceAddress(servicePaths);
        String host = servicePath.split(":")[0];
        int port = Integer.parseInt(servicePath.split(":")[1]);
        // 通过netty发起请求
        RpcResponse response = new NettyClient(host, port).send(rpcRequest);
        if (response == null) {
            throw new RuntimeException("调用服务失败,servicePath:" + servicePath);
        }
        return response.getResult();
    }
}
