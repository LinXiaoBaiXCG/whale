package io.github.linxiaobaixcg.service;

import java.util.List;

/**
 * 负载均衡服务
 */
public interface LoadBalance {

    /**
     * 根据服务列表获取一个服务地址
     * @param serviceAddresses
     * @return
     */
    String selectServiceAddress(List<String> serviceAddresses);
}
