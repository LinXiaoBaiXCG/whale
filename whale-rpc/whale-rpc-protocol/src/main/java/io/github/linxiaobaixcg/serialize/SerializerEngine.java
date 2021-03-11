package io.github.linxiaobaixcg.serialize;



import io.github.linxiaobaixcg.enums.SerializeType;
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


}
