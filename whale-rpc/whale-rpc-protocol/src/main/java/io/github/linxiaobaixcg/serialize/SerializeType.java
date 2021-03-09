package io.github.linxiaobaixcg.serialize;

public enum SerializeType {

    HessianSerializer("Hessian"),
    ProtoStuffSerializer("ProtoStuff"),
    KryoSerializer("Kryo"),
    MsgPackSerializer("MsgPack");

    private String serializeType;

    private SerializeType(String serializeType) {
        this.serializeType = serializeType;
    }

    public String getSerializeType() {
        return serializeType;
    }

    public static SerializeType queryByType(String serializeType) {
        if ("".equals(serializeType) || null == serializeType) {
            return null;
        }

        for (SerializeType serialize : SerializeType.values()) {
            if (serializeType.equals(serialize.getSerializeType())) {
                return serialize;
            }
        }
        return null;
    }
}