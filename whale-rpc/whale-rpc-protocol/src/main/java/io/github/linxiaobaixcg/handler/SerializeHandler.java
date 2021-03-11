package io.github.linxiaobaixcg.handler;

import io.github.linxiaobaixcg.enums.LoadBalanceStrategy;
import io.github.linxiaobaixcg.enums.SerializeType;
import io.github.linxiaobaixcg.serialize.Serialize;
import io.github.linxiaobaixcg.serialize.impl.HessianImpl;
import io.github.linxiaobaixcg.serialize.impl.KryoImpl;
import io.github.linxiaobaixcg.serialize.impl.MsgPackImpl;
import io.github.linxiaobaixcg.serialize.impl.ProtobufImpl;
import io.github.linxiaobaixcg.service.LoadBalance;
import io.github.linxiaobaixcg.service.impl.RandomLoadBalance;
import io.github.linxiaobaixcg.service.impl.RoundRobinLoadBalance;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author lcq
 * @description: 负载均衡处理器
 * @date 2021/3/11 18:44
 */
public class SerializeHandler {

    public static final Map<SerializeType, Serialize> serializeMap = new EnumMap<>(SerializeType.class);

    /**
     * 注册已有的实现类
     */
    static{
        serializeMap.put(SerializeType.Default,new ProtobufImpl());
        serializeMap.put(SerializeType.HessianSerializer,new HessianImpl());
        serializeMap.put(SerializeType.ProtoStuffSerializer,new ProtobufImpl());
        serializeMap.put(SerializeType.KryoSerializer,new KryoImpl());
        serializeMap.put(SerializeType.MsgPackSerializer,new MsgPackImpl());
    }

    /**
     * 通用序列化方法
     * @param obj
     * @param serializeType
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> byte[] serialize(T obj,SerializeType serializeType) throws IOException {
        Serialize serializer = serializeMap.get(serializeType);
        return serializer.serialize(obj);

    }

    /**
     * 通用反序列化方法
     * @param data
     * @param clazz
     * @param serializeType
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T deserialize(byte[] data, Class<T> clazz,SerializeType serializeType) throws IOException {
        Serialize serializer = serializeMap.get(serializeType);
        return serializer.deserialization(data,clazz);
    }
}
