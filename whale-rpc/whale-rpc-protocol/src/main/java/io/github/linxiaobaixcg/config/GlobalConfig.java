package io.github.linxiaobaixcg.config;

import io.github.linxiaobaixcg.enums.LoadBalanceStrategy;
import io.github.linxiaobaixcg.enums.SerializeType;

/**
 * @author lcq
 * @description: 全局配置
 * @date 2021/3/11 18:04
 */
public class GlobalConfig {

    /**
     * 全局轮询策略配置（默认为轮询）
     */
    public static LoadBalanceStrategy globalLoadBalanceStrategy = LoadBalanceStrategy.DEFAULT;

    /**
     * 全局序列化配置（默认为protobuf）
     */
    public static SerializeType serializeType = SerializeType.Default;
}
