package io.github.linxiaobaixcg.service.impl;

import io.github.linxiaobaixcg.service.DiscoverService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lcq
 * @description: Zookeeper服务发现
 * @date 2021/3/9 15:40
 */

@Slf4j
public class ZkDiscoverImpl implements DiscoverService {

    Map<String, List<String>> serviceAddressMap = new ConcurrentHashMap<>();

    private String connectionAddress;

    private CuratorFramework curatorFramework;

    public ZkDiscoverImpl(String connectionAddress) {
        this.connectionAddress = connectionAddress;
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(connectionAddress)
                .sessionTimeoutMs(15000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        curatorFramework.start();
    }

    public List<String> discover(String serviceName) {
        List<String> serviceAddresses;
        if (!serviceAddressMap.containsKey(serviceName)) {
            //
            String path = ZkRegisterCenterImpl.ZK_REGISTER_PATH + "/" + serviceName;
            try {
                serviceAddresses = curatorFramework.getChildren().forPath(path);
                serviceAddressMap.put(serviceName, serviceAddresses);
                registerWatcher(serviceName);
            } catch (Exception e) {
                if (e instanceof KeeperException.NoNodeException) {
                    log.error("未获得该节点,serviceName:{}", serviceName);
                    serviceAddresses = null;
                } else {
                    throw new RuntimeException("获取子节点异常：" + e);
                }
            }
        } else {
            serviceAddresses = serviceAddressMap.get(serviceName);
        }
        return serviceAddresses;
    }

    /**
     * 注册监听
     *
     * @param serviceName 服务名称
     */
    private void registerWatcher(String serviceName) {
        String path = ZkRegisterCenterImpl.ZK_REGISTER_PATH + "/" + serviceName;
        PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, path, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(path);
            serviceAddressMap.put(serviceName, serviceAddresses);
        };
        childrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            childrenCache.start();
        } catch (Exception e) {
            throw new RuntimeException("注册PatchChild Watcher 异常" + e);
        }
    }
}
