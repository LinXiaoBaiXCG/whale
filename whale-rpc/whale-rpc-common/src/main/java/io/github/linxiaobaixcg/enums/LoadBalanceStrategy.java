package io.github.linxiaobaixcg.enums;

/**
 * 负载均衡策略枚举
 */
public enum LoadBalanceStrategy {

    /**
     * 默认
     **/
    DEFAULT,

    /** 轮询 */
    ROUND_ROBIN,

    /** 随机 */
    Random;
}
