package com.pygman.ngback.server.handler;

import com.pygman.ngback.constants.MessageType;
import com.pygman.ngback.core.IPTable;
import com.pygman.ngback.handler.Handle;
import com.pygman.ngback.handler.IPTableMessageInboundHandlerAdapter;
import com.pygman.ngback.server.core.World;
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
@Handle(MessageType.SERVER_REGISTER_RESP)
public class ServerRegister extends IPTableMessageInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${ng.server.reg}")
    String serverReg;

    @Autowired
    World world;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("server:"+serverReg+"注册");
        ctx.writeAndFlush(MESSAGES.SERVER_REGISTER_REQ(serverReg));
    }

    @Override
    protected IPTable getTable() {
        return world;
    }

    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
        logger.info("server:"+serverReg+"注册成功");
    }

}
