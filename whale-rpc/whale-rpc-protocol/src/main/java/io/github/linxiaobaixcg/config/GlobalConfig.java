package io.github.linxiaobaixcg.config;

import io.github.linxiaobaixcg.enums.LoadBalanceStrategy;

/**
 * @author lcq
 * @description: 全局配置
 * @date 2021/3/11 18:04
 */
public class GlobalConfig {

    /**
     * 全局轮询策略配置
     */
    public static LoadBalanceStrategy globalLoadBalanceStrategy = LoadBalanceStrategy.ROUND_ROBIN;
}
