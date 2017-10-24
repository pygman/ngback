package com.pygman.ngback.server.handler;

import com.pygman.ngback.constants.MessageType;
import com.pygman.ngback.core.IPTable;
import com.pygman.ngback.handler.Handle;
import com.pygman.ngback.handler.IPTableMessageInboundHandlerAdapter;
import com.pygman.idle.Idle;
import com.pygman.ngback.server.core.World;
import com.pygman.ngback.struct.MESSAGES;
import com.pygman.ngback.struct.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 心跳响应
 */
@Component
@Handle(MessageType.HEARTBEAT_REQ)
public class IdleHandler extends IPTableMessageInboundHandlerAdapter {
    //    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Logger logger = Idle.getLogger(this.getClass());

    @Autowired
    World world;

    @Override
    protected IPTable getTable() {
        return world;
    }

    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
        Channel channel = ctx.channel();
        String idByChannel = world.getIdByChannel(channel);
        if (idByChannel==null){
            channel.close();
        }
        logger.info("...work heartbeat..." + idByChannel + " : " + channel);
        //响应client心跳消息
        ctx.writeAndFlush(MESSAGES.HEARTBEAT_RESP());
    }

}
