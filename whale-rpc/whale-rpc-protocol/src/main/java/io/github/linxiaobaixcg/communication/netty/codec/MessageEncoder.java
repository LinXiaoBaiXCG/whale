package io.github.linxiaobaixcg.communication.netty.codec;

import io.github.linxiaobaixcg.enums.SerializeType;
import io.github.linxiaobaixcg.handler.SerializeHandler;
import io.github.linxiaobaixcg.serialize.SerializerEngine;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * @author lcq
 * @description: 编码器
 * @date 2021/3/4 16:51
 */
@AllArgsConstructor
public class MessageEncoder extends MessageToByteEncoder {

    private Class<?> clazz;

    /** 序列化类型 */
    private SerializeType serializeType;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object message, ByteBuf byteBuf) throws Exception {
        if (clazz != null && clazz.isInstance(message)) {
            byte[] bytes = SerializeHandler.serialize(message, serializeType);
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }
    }
}
