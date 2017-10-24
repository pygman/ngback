package com.pygman.ngback.server.handler;

import com.pygman.ngback.constants.MessageType;
import com.pygman.ngback.core.IPTable;
import com.pygman.ngback.handler.Handle;
import com.pygman.ngback.handler.IPTableMessageInboundHandlerAdapter;
import com.pygman.ngback.server.core.World;
import com.pygman.ngback.struct.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 路由
 * Created by pygman on 12/6/2016.
 */
@Component
@Handle(MessageType.SERVICE_REQ)
public class RouteHandler extends IPTableMessageInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    World world;

    @Override
    protected IPTable getTable() {
        return world;
    }

    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
        long messageId = message.getId();
        String to = message.getTo();
        Channel toChannel = world.getChannelById(to);
        if (toChannel != null) {
            logger.info("message " + messageId + " : " + message.getFrom() + " ==> " + to);
            toChannel.writeAndFlush(message);
        } else {
            logger.error("客户机: " + to + "不在线，messageId:" + messageId);
            //通知来源客户 目标客户不在线
            Message respMessage = new Message(messageId);
            respMessage.setType(MessageType.SERVICE_RESP.value());
            respMessage.setFrom(message.getFrom());
            respMessage.setTo(message.getTo());
            respMessage.setBody(message.getBody());
            respMessage.setStatus((byte) 2);
            ctx.channel().writeAndFlush(respMessage);
        }

    }

}