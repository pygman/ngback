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
 * 登录
 */
@Component
@Handle(MessageType.LOGIN_RESP)
public class ClientLogin extends SimpleMessageInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${ng.client.name}")
    String clientName;

    @Autowired
    ServerTableManager serverTableManager;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client:" + clientName + "登录" + ctx.channel());
        ctx.writeAndFlush(MESSAGES.LOGIN_REQ(clientName));
    }

    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
        //登录成功 TODO 考虑在ServerTableManager中加入成功失败信息独立管理
        String serverName = message.getBody();
        serverTableManager.putServerChannelTable(serverName, ctx.channel());
        logger.info(clientName + "登录" + ctx.channel() + "成功");
    }

}
