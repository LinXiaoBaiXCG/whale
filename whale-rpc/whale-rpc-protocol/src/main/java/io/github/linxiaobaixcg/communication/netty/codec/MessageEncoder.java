package io.github.linxiaobaixcg.communication.netty.codec;

import io.github.linxiaobaixcg.serialize.Serialize;
import io.github.linxiaobaixcg.serialize.SerializeType;
import io.github.linxiaobaixcg.serialize.SerializerEngine;
import io.github.linxiaobaixcg.serialize.impl.ProtobufImpl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * @author lcq
 * @description: 编码器
 * @date 2021/3/4 16:51
 */
public class MessageEncoder extends MessageToByteEncoder {

    //序列化类型
    private SerializeType serializeType;

    public MessageEncoder(SerializeType serializeType) {
        this.serializeType = serializeType;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object message, ByteBuf byteBuf) throws Exception {

            byte[] bytes = SerializerEngine.serialize(byteBuf, serializeType.getSerializeType());
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);

    }
}
