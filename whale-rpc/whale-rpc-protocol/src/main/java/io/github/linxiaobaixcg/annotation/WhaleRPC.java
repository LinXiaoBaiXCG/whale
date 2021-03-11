package io.github.linxiaobaixcg.annotation;

import io.github.linxiaobaixcg.enums.LoadBalanceStrategy;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface WhaleRPC {

    /**
     * 版本
     * @return
     */
    String version() default "";

    /**
     * 分组
     * @return
     */
    String group() default "";

    /**
     * 负载均衡
     * @return
     */
    LoadBalanceStrategy loadBalance() default LoadBalanceStrategy.DEFAULT;
}
