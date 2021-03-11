package io.github.linxiaobaixcg.service.impl;

import io.github.linxiaobaixcg.service.LoadBalance;

import java.util.List;

/**
 * @author lcq
 * @description: 轮询负载均衡实现
 * @date 2021/3/11 17:37
 */
public class RoundRobinLoadBalance implements LoadBalance {

    private static Integer value = 0;

    @Override
    public String selectServiceAddress(List<String> serviceAddresses) {
        String serviceAddress;
        synchronized (value) {
            if (value > serviceAddresses.size()) {
                value = 0;
            }
            serviceAddress = serviceAddresses.get(value);
            value++;
        }
        return serviceAddress;
    }
}
