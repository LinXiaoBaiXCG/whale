package io.github.linxiaobaixcg.communication.netty.client;

import cn.hutool.core.util.IdUtil;
import io.github.linxiaobaixcg.config.GlobalConfig;
import io.github.linxiaobaixcg.enums.LoadBalanceStrategy;
import io.github.linxiaobaixcg.model.RpcRequest;
import io.github.linxiaobaixcg.model.RpcResponse;
import io.github.linxiaobaixcg.service.DiscoverService;
import io.github.linxiaobaixcg.service.LoadBalance;
import io.github.linxiaobaixcg.handler.LoadBalanceHandler;
import io.github.linxiaobaixcg.service.impl.ZkDiscoverImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
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

    private LoadBalanceStrategy loadBalanceStrategy;

    public ClientProxy( String version, LoadBalanceStrategy loadBalanceStrategy) {
        this.version = version;
        this.loadBalanceStrategy = loadBalanceStrategy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构造请求对象
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId(IdUtil.getSnowflake(1, 1).nextIdStr());
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameters(args);
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setVersion(version);
        log.info("请求内容: {}", rpcRequest);
        String serviceName = method.getDeclaringClass().getName();
        if (null != version && !"".equals(version)) {
            serviceName += "-" + version;
        }
        // TODO 修改为全局配置或者自定义配置文件
        discoverService = new ZkDiscoverImpl("127.0.0.1:2181");
        // 根据服务名获取服务地址
        List<String> servicePaths = discoverService.discover(serviceName);
        if (CollectionUtils.isEmpty(servicePaths)) {
            log.error("并未找到服务地址,className:{}", serviceName);
            throw new RuntimeException("未找到服务地址");
        }
        // 根据指定的负载均衡获取一个服务地址,优先使用注解配置
        if (this.loadBalanceStrategy == LoadBalanceStrategy.DEFAULT){
            this.loadBalanceStrategy = GlobalConfig.globalLoadBalanceStrategy;
        }
        LoadBalance loadBalance = LoadBalanceHandler.loadBalanceEnumMap.get(this.loadBalanceStrategy);
        String servicePath = loadBalance.selectServiceAddress(servicePaths);
        String host = servicePath.split(":")[0];
        int port = Integer.parseInt(servicePath.split(":")[1]);
        // 通过netty发起请求
        NettyClient nettyClient = new NettyClient(host, port);
        nettyClient.connect();
        RpcResponse response = nettyClient.send(rpcRequest);
        if (response == null) {
            throw new RuntimeException("调用服务失败,servicePath:" + servicePath);
        }
        return response.getResult();
    }
}
