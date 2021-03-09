package io.github.linxiaobaixcg.service;

import java.net.InetSocketAddress;

public interface RegisterService {

    void register(String serviceName, String serviceAddress) throws Exception;
}
