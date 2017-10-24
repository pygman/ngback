package com.pygman.ngback.server.handler;

import com.pygman.ngback.constants.MessageType;
import com.pygman.ngback.core.IPTable;
import com.pygman.ngback.handler.Handle;
import com.pygman.ngback.handler.IPTableMessageInboundHandlerAdapter;
import com.pygman.ngback.server.core.World;
import com.pygman.ngback.struct.MESSAGES;
import com.pygman.ngback.struct.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 响应登录
 * Created by kevin  on 16/8/26.
 */
@Component
@Handle(MessageType.LOGIN_REQ)
public class LoginHandler extends IPTableMessageInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    World world;

    @Value("${ng.server.reg}")
    private String serverReg;

    @Override
    protected IPTable getTable() {
        return world;
    }

    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
        Object body = message.getBody();
        String wareHouseId = (String) body;
        //登录消息,添加client到world
        Channel channelByWhId = world.getChannelById(wareHouseId);
        Message resp;
        if (channelByWhId != null) {
            channelByWhId.close();
//            resp = MESSAGES.LOGIN_RESP_REPEAT();
        }
        world.put(wareHouseId, ctx.channel());
        //响应登录成功到client
        resp = MESSAGES.LOGIN_RESP(serverReg);
        logger.info(world.toString());
        ctx.writeAndFlush(resp);
    }

}
