package io.github.linxiaobaixcg.service;

public interface DiscoverService {

    /**
     * 根据服务名获取远程地址
     * @param serviceName
     * @return
     */
    String discover(String serviceName);
}
