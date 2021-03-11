package io.github.linxiaobaixcg.service.impl;

import io.github.linxiaobaixcg.service.RegisterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author lcq
 * @description: zookeeper注册中心实现
 * @date 2021/3/9 14:30
 */
@Slf4j
public class ZkRegisterCenterImpl implements RegisterService {

    /** 注册根节点 **/
    public static final String ZK_REGISTER_PATH = "/test13";

    private String connectionAddress;

    private CuratorFramework curatorFramework;

    public ZkRegisterCenterImpl(String connectionAddress) {
        this.connectionAddress = connectionAddress;
        //初始化curator
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(connectionAddress)
                .sessionTimeoutMs(15000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        curatorFramework.start();
    }

    public void register(String serviceName, String serviceAddress) throws Exception {
        //需要注册的服务根节点
        String servicePath = ZK_REGISTER_PATH + "/" + serviceName;
        //注册服务，创建临时节点
        String serviceAddr = servicePath + "/" + serviceAddress;
        curatorFramework.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(serviceAddr, "".getBytes());
        log.debug("节点创建成功，节点为:{}", servicePath);
    }
}
