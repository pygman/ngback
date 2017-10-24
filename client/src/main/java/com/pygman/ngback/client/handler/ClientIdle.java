package com.pygman.ngback.client.handler;

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
import org.springframework.stereotype.Component;

/**
 * 心跳
 */
@Component
@Handle(MessageType.HEARTBEAT_RESP)
public class ClientIdle extends SimpleMessageInboundHandlerAdapter {
    //    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Logger logger = Idle.getLogger(this.getClass());

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
//                logger.info("...client idle! send heart beat...");
                Message message = MESSAGES.HEARTBEAT_REQ();
                ctx.channel().writeAndFlush(message);
            }
        }
    }


    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
//        logger.info("...get heartbeat back from server..." + ctx.channel());
    }

}
