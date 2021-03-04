package io.github.linxiaobaixcg.serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.github.linxiaobaixcg.model.RpcRequest;
import io.github.linxiaobaixcg.model.RpcResponse;
import io.github.linxiaobaixcg.serialize.Serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * @author lcq
 * @description: kyro序列化实现
 * @date 2021/3/4 14:16
 */
public class KryoImpl implements Serialize {

    /**
     * 因为Kryo不是线程安全的。所以，使用ThreadLocal来存储Kryo对象
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        return kryo;
    });

    public <T> byte[] serialize(T o) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        try {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, o);
            kryoThreadLocal.remove();
            return output.toBytes();
        } finally {
            output.close();
            outputStream.close();
        }
    }

    public <T> T deserialization(byte[] bytes, Class<T> clazz) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(inputStream);
        try {
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return clazz.cast(o);
        } finally {
            input.close();
            inputStream.close();
        }
    }
}
