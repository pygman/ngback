package com.pygman.ngback.client.handler;

import com.pygman.ngback.client.core.ServerTableManager;
import com.pygman.ngback.constants.MessageType;
import com.pygman.ngback.handler.Handle;
import com.pygman.ngback.handler.SimpleMessageInboundHandlerAdapter;
import com.pygman.idle.Idle;
import com.pygman.ngback.struct.MESSAGES;
import com.pygman.ngback.struct.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 注册心跳
 */
@Component
@Handle(MessageType.HEARTBEAT_CLIENT_RESP)
public class ClientRegIdle extends SimpleMessageInboundHandlerAdapter {
    //    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Logger logger = Idle.getLogger(this.getClass());

    @Autowired
    ServerTableManager serverTableManager;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
//                logger.info("...all idle! send heart beat...");
                Message message = MESSAGES.HEARTBEAT_CLIENT_REQ();
                ctx.channel().writeAndFlush(message);
            }
        }
    }

    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
        Object body = message.getBody();
        if (body != null && ((String) body).length() > 0) {
            serverTableManager.serverTableUpdate((String) body);
        }
//        logger.info("...get heartbeat back...");
    }

}
