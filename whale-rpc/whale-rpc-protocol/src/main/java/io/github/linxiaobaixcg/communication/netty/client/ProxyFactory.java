package io.github.linxiaobaixcg.communication.netty.client;

import io.github.linxiaobaixcg.enums.LoadBalanceStrategy;
import io.github.linxiaobaixcg.service.DiscoverService;
import io.github.linxiaobaixcg.service.LoadBalance;

import java.lang.reflect.Proxy;

public class ProxyFactory {

    public static <T> T getClientProxy(Class<T> clazz, String version, LoadBalanceStrategy loadBalanceStrategy){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz},new ClientProxy(version, loadBalanceStrategy));
    }

}