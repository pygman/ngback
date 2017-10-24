package com.pygman.ngback.monitor.handler;

import com.pygman.ngback.constants.MessageType;
import com.pygman.ngback.core.IPTable;
import com.pygman.ngback.handler.Handle;
import com.pygman.ngback.handler.IPTableMessageInboundHandlerAdapter;
import com.pygman.ngback.monitor.core.ClientTable;
import com.pygman.ngback.monitor.core.ServerTable;
import com.pygman.ngback.struct.MESSAGES;
import com.pygman.ngback.struct.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Client注册
 * Created by kevin  on 16/8/26.
 */
@Component
@Handle(MessageType.CLIENT_REGISTER_REQ)
public class ClientRegisterHandler extends IPTableMessageInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ServerTable serverTable;

    @Autowired
    ClientTable clientTable;

    @Override
    protected IPTable getTable() {
        return clientTable;
    }

    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
        String body = message.getBody();
        Channel channelById = clientTable.getChannelById(body);
        Message resp;
        byte status = message.getStatus();
        if (channelById != null && status != 0) {
            //DONE 重复客户端名称处理 挤下去指令发送
            logger.info("Client register repeat(force)::" + body);
            Message sp = MESSAGES.CLIENT_REGISTER_RESP_REPEAT(clientTable.toString());
            sp.setStatus(status);
            channelById.writeAndFlush(sp);
            clientTable.remove(channelById);
        }
        if (channelById != null && status == 0) {
            //DONE 重复客户端名称处理 不让启动
            logger.info("Client register repeat::" + body);
            resp = MESSAGES.CLIENT_REGISTER_RESP_REPEAT(clientTable.toString());
        } else {
            clientTable.put(body, ctx.channel());
            logger.info("Client register ::" + body);
            resp = MESSAGES.CLIENT_REGISTER_RESP(serverTable.toString());
            resp.setStatus((byte) 0);
        }
        ctx.writeAndFlush(resp);
    }
}
