package io.github.linxiaobaixcg.annonation;

import io.github.linxiaobaixcg.enums.LoadBalanceEnum;

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
    LoadBalanceEnum loadBalance();
}
