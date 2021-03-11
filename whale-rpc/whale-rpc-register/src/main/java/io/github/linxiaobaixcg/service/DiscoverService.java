package io.github.linxiaobaixcg.service;

import java.util.List;

public interface DiscoverService {

    /**
     * 根据服务名获取远程地址列表
     * @param serviceName
     * @return
     */
    List<String> discover(String serviceName);
}
