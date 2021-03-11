package io.github.linxiaobaixcg.communication.netty.codec;

import io.github.linxiaobaixcg.serialize.Serialize;
import io.github.linxiaobaixcg.serialize.SerializeType;
import io.github.linxiaobaixcg.serialize.SerializerEngine;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author lcq
 * @description: 解码器
 * @date 2021/3/5 15:48
 */
@AllArgsConstructor
public class MessageDecoder extends ByteToMessageDecoder {

    private Class<?> clazz;

    //解码对象编码所使用序列化类型
    private SerializeType serializeType;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        //获取消息头所标识的消息体字节数组长度
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        //读取完整的消息体字节数组
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        //将字节数组反序列化为java对象(SerializerEngine参考序列化与反序列化章节)
        Object obj = SerializerEngine.deserialize(data, clazz, serializeType.getSerializeType());
        list.add(obj);
    }
}
