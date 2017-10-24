package com.pygman.ngback.monitor.handler;

import com.pygman.ngback.constants.MessageType;
import com.pygman.ngback.core.IPTable;
import com.pygman.ngback.handler.Handle;
import com.pygman.ngback.handler.IPTableMessageInboundHandlerAdapter;
import com.pygman.ngback.monitor.core.ServerTable;
import com.pygman.ngback.struct.Message;
import com.pygman.ngback.struct.MESSAGES;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Server注册
 * Created by kevin  on 16/8/26.
 */
@Component
@Handle(MessageType.SERVER_REGISTER_REQ)
public class ServerRegisterHandler extends IPTableMessageInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ServerTable serverTable;

    @Override
    protected IPTable getTable() {
        return serverTable;
    }

    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
        String body = (String)message.getBody();
        Channel channelById = serverTable.getChannelById(body);
        Message resp;
        if (channelById!=null){
            resp = MESSAGES.SERVER_REGISTER_RESP_REPEAT(body);
        }else {
            serverTable.put(body, ctx.channel());
            logger.info("Server register ::"+body);
            resp = MESSAGES.SERVER_REGISTER_RESP(body);
        }
        ctx.writeAndFlush(resp);
    }

}
