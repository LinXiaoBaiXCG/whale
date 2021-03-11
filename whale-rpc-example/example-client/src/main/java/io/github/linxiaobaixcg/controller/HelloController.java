package io.github.linxiaobaixcg.controller;

import io.github.linxiaobaixcg.annotation.WhaleRPC;
import io.github.linxiaobaixcg.enums.LoadBalanceStrategy;
import io.github.linxiaobaixcg.service.HelloTestService;
import org.springframework.stereotype.Component;

/**
 * @author lcq
 * @description:
 * @date 2021/3/11 16:28
 */
@Component
public class HelloController {

    @WhaleRPC(loadBalance = LoadBalanceStrategy.Random)
    private HelloTestService helloTestService;

    public String test(){
        return helloTestService.hello("LCQ");
    }
}
