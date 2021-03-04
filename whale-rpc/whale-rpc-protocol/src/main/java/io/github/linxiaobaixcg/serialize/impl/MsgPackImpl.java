package io.github.linxiaobaixcg.serialize.impl;

import io.github.linxiaobaixcg.serialize.Serialize;
import org.msgpack.MessagePack;

import java.io.IOException;

/**
 * @author lcq
 * @description: msgpack序列化实现
 * @date 2021/3/4 14:45
 */
public class MsgPackImpl implements Serialize {
    @Override
    public <T> byte[] serialize(T o) throws IOException {
        return new MessagePack().write(o);
    }

    @Override
    public <T> T deserialization(byte[] bytes, Class<T> clazz) throws IOException {
        return new MessagePack().read(bytes, clazz);
    }
}
