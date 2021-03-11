package io.github.linxiaobaixcg.controller;

import io.github.linxiaobaixcg.annotation.WhaleRPC;
import io.github.linxiaobaixcg.service.HelloTestService;

/**
 * @author lcq
 * @description:
 * @date 2021/3/11 16:28
 */
public class HelloController {

    @WhaleRPC
    private HelloTestService helloTestService;

    public String test(){
        return helloTestService.hello("LCQ");
    }
}
