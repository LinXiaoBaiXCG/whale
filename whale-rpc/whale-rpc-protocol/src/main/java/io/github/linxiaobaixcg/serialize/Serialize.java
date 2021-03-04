package io.github.linxiaobaixcg.serialize;

import java.io.IOException;

public interface Serialize {

    /**
     * 序列化
     * @param o
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T o) throws IOException;

    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T deserialization(byte[] bytes, Class<T> clazz) throws IOException;
}
