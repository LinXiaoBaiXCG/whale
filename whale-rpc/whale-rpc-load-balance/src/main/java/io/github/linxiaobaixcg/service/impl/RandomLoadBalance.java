package io.github.linxiaobaixcg.service.impl;

import io.github.linxiaobaixcg.service.LoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @author lcq
 * @description: 随机负载均衡实现
 * @date 2021/3/10 14:45
 */
public class RandomLoadBalance implements LoadBalance {
    public String selectServiceAddress(List<String> serviceAddresses) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
