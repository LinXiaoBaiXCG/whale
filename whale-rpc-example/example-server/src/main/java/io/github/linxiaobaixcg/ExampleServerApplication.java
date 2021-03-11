package io.github.linxiaobaixcg;

import io.github.linxiaobaixcg.communication.netty.server.NettyServer;
import io.github.linxiaobaixcg.service.HelloTestService;
import io.github.linxiaobaixcg.service.RegisterService;
import io.github.linxiaobaixcg.service.impl.ZkRegisterCenterImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class ExampleServerApplication {

	public static void main(String[] args) throws Exception {
		HelloTestService helloTestService = new HelloServiceImpl();
		RegisterService registerCenter = new ZkRegisterCenterImpl("127.0.0.1:2181");
		NettyServer nettyServer = new NettyServer(registerCenter, "127.0.0.1", 9090);
		nettyServer.bindService(Arrays.asList(helloTestService));
		nettyServer.start();
	}

}
