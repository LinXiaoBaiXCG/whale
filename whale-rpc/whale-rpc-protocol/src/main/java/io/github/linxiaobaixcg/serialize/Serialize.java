package io.github.linxiaobaixcg.serialize;

import java.io.IOException;

public interface Serialize {

    /**
     * 序列化
     * @param o 对象
     * @param <T>
     * @return 字节数组
     */
    <T> byte[] serialize(T o) throws IOException;

    /**
     * 反序列化
     * @param bytes 字节数组
     * @param clazz 目标类
     * @param <T>
     * @return 反序列化的对象
     */
    <T> T deserialization(byte[] bytes, Class<T> clazz) throws IOException;
}
