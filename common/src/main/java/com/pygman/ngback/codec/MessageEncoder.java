package com.pygman.ngback.codec;

import com.pygman.ngback.struct.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.UnsupportedEncodingException;


/**
 * 消息编码器
 * Created by kevin  on 16/8/26.
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        if (msg == null) {
            throw new Exception("message need encode is null");
        }

        out.writeInt(0);
        out.writeLong(msg.getId());


        out.writeByte(msg.getType());

        String from = msg.getFrom();
        int fromLength = writeString(out, from);

        String to = msg.getTo();
        int toLength = writeString(out, to);

        String body = msg.getBody();
        int bodyLength = writeString(out, body);

        out.writeByte(msg.getStatus());
        int length = 8 + 4 + 1 + 4 + 4 + 1 + fromLength + toLength + bodyLength;
        out.setInt(0, length);
    }

    private int writeString(ByteBuf out, String str) throws UnsupportedEncodingException {
        if (str == null) {
            out.writeInt(0);
            return 0;
        } else {
            byte[] strBytes = str.getBytes("utf-8");
            int length = strBytes.length;
            out.writeInt(length);
            out.writeBytes(strBytes);
            return length;
        }
    }
}
