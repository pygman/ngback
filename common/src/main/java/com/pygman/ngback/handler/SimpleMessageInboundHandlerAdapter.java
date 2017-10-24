package com.pygman.ngback.handler;

import com.pygman.ngback.constants.MessageType;
import com.pygman.ngback.struct.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler公共代码
 * Created by pygman on 16-11-7.
 */
@ChannelHandler.Sharable
public abstract class SimpleMessageInboundHandlerAdapter extends ChannelInboundHandlerAdapter{
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    protected MessageType messageType;


    {
        Handle handle = this.getClass().getAnnotation(Handle.class);
        this.messageType = handle==null?null:handle.value();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (messageType==null)ctx.fireChannelRead(msg);
        //业务消息,此处是客户端对消息的响应
        Message message = (Message) msg;
        //客户端接收消息成功,删除保存的message
        if (message.getType() == messageType.value()) {
            channelRead1(ctx, message);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    protected abstract void channelRead1(ChannelHandlerContext ctx, Message message);

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //移除client,关闭连接
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //移除client,关闭连接
        cause.printStackTrace();
        ctx.close();
    }

}
