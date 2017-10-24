package com.pygman.ngback.server.handler;

import com.pygman.idle.Idle;
import com.pygman.ngback.constants.MessageType;
import com.pygman.ngback.core.IPTable;
import com.pygman.ngback.handler.Handle;
import com.pygman.ngback.handler.IPTableMessageInboundHandlerAdapter;
import com.pygman.ngback.server.boot.ServerBoot;
import com.pygman.ngback.server.core.World;
import com.pygman.ngback.struct.MESSAGES;
import com.pygman.ngback.struct.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 注册心跳
 */
@Component
@Handle(MessageType.HEARTBEAT_SERVER_RESP)
public class ServerRegIdle extends IPTableMessageInboundHandlerAdapter {
    //    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Logger logger = Idle.getLogger(this.getClass());

    @Lazy
    @Autowired
    ServerBoot serverBoot;

    @Autowired
    World world;

    private int modCount = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                logger.info("...all idle! send heart beat...");
                Message message = MESSAGES.HEARTBEAT_SERVER_REQ();
                ctx.channel().writeAndFlush(message);
            }
        }
    }

    @Override
    protected IPTable getTable() {
        return world;
    }
    
    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
        String body = message.getBody();
        logger.info("...get heartbeat back ==>{\"client\":" + body + ",\"world\":" + world + ",\"modCount\":" + modCount + "}");
        String worldStr = world.toString().trim();
        body = body.trim().substring(1, body.length() - 1);
        worldStr = worldStr.substring(1, worldStr.length() - 1);
        String[] bodyArray = body.split(", ");
        String[] worldArray = worldStr.split(", ");
        Arrays.sort(bodyArray, String.CASE_INSENSITIVE_ORDER);
        Arrays.sort(worldArray, String.CASE_INSENSITIVE_ORDER);
        if (bodyArray.length > worldArray.length) {
            logger.info(">>> bodyArray.length 大于 worldArray.length <<<");
            if (++modCount > 16) {
                modCount = 0;
                serverBoot.restartWork();
                ctx.close();
            }
        } else if (bodyArray.length < worldArray.length) {
            logger.info(">>> bodyArray.length 小于 worldArray.length <<<");
            if (++modCount > 16) {
                modCount = 0;
                List<String> bodyList = Arrays.asList(bodyArray);
                for (int i = 0; i < worldArray.length; i++) {
                    String clientName = worldArray[i];
                    if (!bodyList.contains(clientName)) {
                        Channel channelById = world.getChannelById(clientName);
                        logger.debug("---clientName:{},in moniter none.in server visible.---", clientName);
                        logger.info("---channelById:{}---" + channelById);
                        if (channelById != null) {
                            channelById.writeAndFlush(MESSAGES.CLIENT_REGISTER_RESP_REPEAT(world.toString()));
                        }
                    }
                }
            }
        } else {
            logger.info(">>> bodyArray.length 等于 worldArray.length <<<");
            List<String> worldList = Arrays.asList(worldArray);
            boolean flag = false;
            for (int i = 0; i < bodyArray.length; i++) {
                String clientName = bodyArray[i];
                if (!worldList.contains(clientName)) {
                    flag = true;
                }
            }
            if (flag && ++modCount > 16) {
                modCount = 0;
                serverBoot.restartWork();
                ctx.close();
            }
        }
        
    }

}
