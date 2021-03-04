package io.github.linxiaobaixcg.serialize.impl;

import io.github.linxiaobaixcg.serialize.Serialize;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * @author lcq
 * @description: google protobuf实现
 * @date 2021/3/4 11:44
 */
public class ProtobufImpl implements Serialize {

    /**
     * 避免每次序列化时都重新创建缓冲区空间
     */
    private static final LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    public <T> byte[] serialize(T o) {
        Class<T> aClass = (Class<T>) o.getClass();
        Schema<T> schema = RuntimeSchema.getSchema(aClass);
        byte[] bytes;
        try {
            bytes = ProtostuffIOUtil.toByteArray(o, schema, BUFFER);
        } finally {
            BUFFER.clear();
        }
        return bytes;
    }

    public <T> T deserialization(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T o = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, o, schema);
        return o;
    }
}
