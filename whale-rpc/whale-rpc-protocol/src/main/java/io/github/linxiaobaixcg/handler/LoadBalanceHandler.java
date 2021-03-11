package io.github.linxiaobaixcg.handler;

import io.github.linxiaobaixcg.enums.LoadBalanceStrategy;
import io.github.linxiaobaixcg.service.LoadBalance;
import io.github.linxiaobaixcg.service.impl.RandomLoadBalance;
import io.github.linxiaobaixcg.service.impl.RoundRobinLoadBalance;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author lcq
 * @description: 负载均衡处理器
 * @date 2021/3/11 18:44
 */
public class LoadBalanceHandler {

    public static final Map<LoadBalanceStrategy, LoadBalance> loadBalanceEnumMap = new EnumMap<>(LoadBalanceStrategy.class);

    static {
        loadBalanceEnumMap.put(LoadBalanceStrategy.DEFAULT, new RoundRobinLoadBalance());
        loadBalanceEnumMap.put(LoadBalanceStrategy.ROUND_ROBIN, new RoundRobinLoadBalance());
        loadBalanceEnumMap.put(LoadBalanceStrategy.Random, new RandomLoadBalance());
    }
}
