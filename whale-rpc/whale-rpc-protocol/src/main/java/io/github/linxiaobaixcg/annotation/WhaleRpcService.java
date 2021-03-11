package io.github.linxiaobaixcg.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 提供服务的注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WhaleRpcService {

    /**
     * 对外发布的服务接口地址
     */
    Class<?> value();

    /**
     * 版本
     */
    String version() default "";
}
