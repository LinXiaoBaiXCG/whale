package io.github.linxiaobaixcg.communication.netty.processor;

import io.github.linxiaobaixcg.annotation.WhaleRPC;
import io.github.linxiaobaixcg.communication.netty.client.ProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author lcq
 * @description: 注解处理器
 * @date 2021/3/11 15:57
 */
@Slf4j
@Component
public class AnnotationProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        Class<?> clazz = o.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            WhaleRPC annotation = declaredField.getAnnotation(WhaleRPC.class);
            if (null != annotation) {
                Object clientProxy = ProxyFactory.getClientProxy(declaredField.getType(), annotation.version(), annotation.loadBalance());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(o, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return o;
    }
}
