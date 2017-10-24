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
 * Server注册心跳
 */
@Component
@Handle(MessageType.HEARTBEAT_SERVER_REQ)
public class ServerRegIdleHandler extends IPTableMessageInboundHandlerAdapter {
//    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Logger logger = Idle.getLogger(this.getClass());

    @Autowired
    ServerTable serverTable;

    @Autowired
    ClientTable clientTable;

    @Override
    protected IPTable getTable() {
        return serverTable;
    }

    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
        logger.info(serverTable.getIdByChannel(ctx.channel())+" -> "+serverTable);
        //响应client心跳消息
        ctx.writeAndFlush(MESSAGES.HEARTBEAT_SERVER_RESP(clientTable.toString()));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //TODO 服务端断线 不出发该方法 而是 该类ServerRegIdleHandler 的 channelInactive方法
        IPTable.Vo remove = getTable().remove(ctx.channel());
        if (remove!=null){
            logger.info("Server:"+remove.getId()+"离线");
        }
    }
}
