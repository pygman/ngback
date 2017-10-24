package com.pygman.ngback.monitor.handler;

import com.pygman.ngback.constants.MessageType;
import com.pygman.ngback.core.IPTable;
import com.pygman.ngback.handler.Handle;
import com.pygman.ngback.handler.IPTableMessageInboundHandlerAdapter;
import com.pygman.idle.Idle;
import com.pygman.ngback.monitor.core.ClientTable;
import com.pygman.ngback.monitor.core.ServerTable;
import com.pygman.ngback.struct.MESSAGES;
import com.pygman.ngback.struct.Message;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Client注册心跳
 */
@Component
@Handle(MessageType.HEARTBEAT_CLIENT_REQ)
public class ClientRegIdleHandler extends IPTableMessageInboundHandlerAdapter {
    //    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Logger logger = Idle.getLogger(this.getClass());

    @Autowired
    ClientTable clientTable;

    @Autowired
    ServerTable serverTable;

    @Override
    protected IPTable getTable() {
        return clientTable;
    }

    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
        logger.info(clientTable.getIdByChannel(ctx.channel()) + " -> " + clientTable);
        //响应client心跳消息
        ctx.writeAndFlush(MESSAGES.HEARTBEAT_CLIENT_RESP(serverTable.toString()));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        IPTable.Vo remove = getTable().remove(ctx.channel());
        if (remove != null) {
            logger.info("Client:" + remove.getId() + "离线");
        }
    }
}
