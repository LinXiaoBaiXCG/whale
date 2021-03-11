package io.github.linxiaobaixcg.serialize;



import io.github.linxiaobaixcg.serialize.impl.HessianImpl;
import io.github.linxiaobaixcg.serialize.impl.KryoImpl;
import io.github.linxiaobaixcg.serialize.impl.MsgPackImpl;
import io.github.linxiaobaixcg.serialize.impl.ProtobufImpl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializerEngine {

    private static final Map<SerializeType,Serialize> serializeMap = new ConcurrentHashMap<>();

    //注册已有的实现类
    static{
        serializeMap.put(SerializeType.HessianSerializer,new HessianImpl());
        serializeMap.put(SerializeType.ProtoStuffSerializer,new ProtobufImpl());
        serializeMap.put(SerializeType.KryoSerializer,new KryoImpl());
        serializeMap.put(SerializeType.MsgPackSerializer,new MsgPackImpl());
    }

    //通用的序列化方法
    public static <T> byte[] serialize(T obj,String serializerType) throws IOException {
        SerializeType serializeType = SerializeType.queryByType(serializerType);
        if(serializeType == null){
            serializeType = SerializeType.ProtoStuffSerializer;
        }
        Serialize serializer = serializeMap.get(serializeType);

        return serializer.serialize(obj);

    }

    //通用反序列化方法
    public static <T> T deserialize(byte[] data, Class<T> clazz,String serialzieType) throws IOException {
        SerializeType serializeType = SerializeType.queryByType(serialzieType);
        if(serializeType == null){
            serializeType = SerializeType.ProtoStuffSerializer;
        }
        Serialize serializer = serializeMap.get(serializeType);
        return serializer.deserialization(data,clazz);
    }
}
