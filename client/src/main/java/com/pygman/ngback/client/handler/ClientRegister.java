package com.pygman.ngback.client.handler;

import com.pygman.ngback.client.core.ServerTableManager;
import com.pygman.ngback.constants.MessageType;
import com.pygman.ngback.handler.Handle;
import com.pygman.ngback.handler.SimpleMessageInboundHandlerAdapter;
import com.pygman.ngback.struct.MESSAGES;
import com.pygman.ngback.struct.Message;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 注册
 */
@Component
@Handle(MessageType.CLIENT_REGISTER_RESP)
public class ClientRegister extends SimpleMessageInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${ng.client.name}")
    String clientName;

    @Value("${ng.client.force}")
    String force;

    public void setForce(String force) {
        this.force = force;
    }

    @Autowired
    ServerTableManager serverTableManager;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client:" + clientName + "注册");
        if ("true".equals(force)) {
            setForce("false");
            ctx.writeAndFlush(MESSAGES.CLIENT_REGISTER_FORCE_REQ(clientName));
        } else {
            ctx.writeAndFlush(MESSAGES.CLIENT_REGISTER_REQ(clientName));
        }
    }

    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
        logger.info("client:" + clientName + "注册成功");
        //TODO 获取IPTable
        Object body = message.getBody();
        logger.info("获取IPTable" + body);
        serverTableManager.login((String) body);
    }

}
