package io.github.linxiaobaixcg;

import io.github.linxiaobaixcg.annonation.WhaleRpcService;
import io.github.linxiaobaixcg.service.HelloTestService;

//服务端实现
@WhaleRpcService(HelloTestService.class)
public class HelloServiceImpl implements HelloTestService {
    @Override
    public String hello(String name) {
        return "hello, " + name;
    }
}