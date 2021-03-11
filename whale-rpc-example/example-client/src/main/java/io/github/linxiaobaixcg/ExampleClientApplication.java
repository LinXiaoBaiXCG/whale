package io.github.linxiaobaixcg;

import io.github.linxiaobaixcg.communication.netty.client.ProxyFactory;
import io.github.linxiaobaixcg.service.DiscoverService;
import io.github.linxiaobaixcg.service.HelloTestService;
import io.github.linxiaobaixcg.service.impl.RandomLoadBalance;
import io.github.linxiaobaixcg.service.impl.ZkDiscoverImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class ExampleClientApplication {

	public static void main(String[] args) throws Exception {
		DiscoverService discoverService = new ZkDiscoverImpl("127.0.0.1:2181");
		ProxyFactory proxyFactory = new ProxyFactory(discoverService);
		HelloTestService helloTestService = proxyFactory.clientProxy(HelloTestService.class, new RandomLoadBalance());
		log.info("响应结果“: {}", helloTestService.hello("lcq"));
	}

}
