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
 * 反向路由
 * Created by pygman on 12/6/2016.
 */
@Component
@Handle(MessageType.SERVICE_RESP)
public class ReRouteHandler extends IPTableMessageInboundHandlerAdapter {
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
        String from = message.getFrom();
        Channel toChannel = world.getChannelById(from);
        if (toChannel != null){
            logger.info("message "+messageId+" : "+from+" <== "+message.getTo());
            toChannel.writeAndFlush(message);
        }else {
//            logger.error("$$$$严重错误,已过期的消息无法成功获取:" + messageId + "=>" + bodyStr);
            logger.error("客户机 "+from+":不在线，messageId:" + messageId);
        }

    }

}