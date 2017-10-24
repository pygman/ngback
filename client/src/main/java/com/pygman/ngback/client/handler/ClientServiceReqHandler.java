package com.pygman.ngback.client.handler;

import com.pygman.ngback.client.actor.ProxyActor;
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
 * 业务确认
 * Created by pygman on 16/11/04.
 */
@Component
@Handle(MessageType.SERVICE_REQ)
public class ClientServiceReqHandler extends SimpleMessageInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProxyActor proxyActor;

    @Value("${ng.client.name}")
    String clientName;

    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
        /*
        TODO 放入队列 返回收到
        //service message
        long messageId = message.getId();
        Object body = message.getBody();
        logger.info("客户端" + clientName + "收到消息,转发请求:" + body);
        //callback方法处理业务
        String resp = httpProxy.onMessageRead(body);
        //响应客户端success
        Message messageResp = MESSAGES.SERVICE_RESP(resp, messageId);
        logger.info("客户端" + clientName + "收到消息,转发请求:" + body + ",获取结果成功,回写:" + messageResp);
        ctx.writeAndFlush(messageResp);
        */
        logger.info("收到消息:"+message);
        boolean ok = proxyActor.newProxyTask(message);
        if (ok){
            logger.info("给:"+message.getFrom()+"返回确认");
            ctx.writeAndFlush(MESSAGES.SERVICE_RESP(message));
        }else {
            //TODO 无视来源客户端
            logger.info("无视来源客户端");
        }
    }

}