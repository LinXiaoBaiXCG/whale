package io.github.linxiaobaixcg.communication.netty.client;

import io.github.linxiaobaixcg.service.DiscoverService;
import io.github.linxiaobaixcg.service.LoadBalance;

import java.lang.reflect.Proxy;

public class ProxyFactory {

    private DiscoverService discoverService;

    public ProxyFactory(DiscoverService discoverService) {
        this.discoverService = discoverService;
    }

    public <T> T clientProxy(Class<T> interfaceCls, String version, LoadBalance loadBalance) {
        return (T) Proxy.newProxyInstance(interfaceCls.getClassLoader(), new Class[]{interfaceCls},
                new ClientProxy(discoverService, version,loadBalance));
    }

    public <T> T clientProxy(Class<T> interfaceCls, LoadBalance loadBalance) {
        return this.clientProxy(interfaceCls, null, loadBalance);
    }
}