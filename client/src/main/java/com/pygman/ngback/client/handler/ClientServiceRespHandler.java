package com.pygman.ngback.client.handler;

import com.pygman.ngback.client.actor.SendActor;
import com.pygman.ngback.constants.MessageType;
import com.pygman.ngback.handler.Handle;
import com.pygman.ngback.handler.SimpleMessageInboundHandlerAdapter;
import com.pygman.ngback.struct.Message;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 业务请求
 * Created by pygman on 16/11/04.
 */
@Component
@Handle(MessageType.SERVICE_RESP)
public class ClientServiceRespHandler extends SimpleMessageInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SendActor sendActor;

    @Value("${ng.client.name}")
    String clientName;

    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
        if (message.getStatus() == 0) {
            logger.info("收到来自:" + message.getTo() + "的信息确认");
            sendActor.complete(message);
        } else {
            logger.error("路由失败 未发现目标客户机" + message.getTo());
            sendActor.routeError(message);
        }
    }

}