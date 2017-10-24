package com.pygman.ngback.codec;

import com.pygman.ngback.struct.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 消息解码器
 * Created by kevin  on 16/8/26.
 */
public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int length = in.readInt();
        //TODO 加入Message实体 db序列化添加
        Message message = new Message(in.readLong());

        message.setType(in.readByte());
        int fromLength = in.readInt();
        message.setFrom(readString(in, fromLength));
        int toLength = in.readInt();
        message.setTo(readString(in, toLength));
        int bodyLength = in.readInt();
        message.setBody(readString(in, bodyLength));
        message.setStatus(in.readByte());

        out.add(message);
        //TODO in.release();
    }

    private String readString(ByteBuf byteBuf, int length) throws UnsupportedEncodingException {
        if (length == 0) {
            return null;
        } else {
            byte[] fromBytes = new byte[length];
            byteBuf.readBytes(fromBytes);
            return new String(fromBytes, "utf-8");
        }
    }

}
