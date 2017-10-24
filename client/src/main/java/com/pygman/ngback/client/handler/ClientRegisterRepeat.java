package com.pygman.ngback.client.handler;

import com.pygman.ngback.client.core.ServerTableManager;
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
 * 注册
 */
@Component
@Handle(MessageType.CLIENT_REGISTER_RESP_REPEAT)
public class ClientRegisterRepeat extends SimpleMessageInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${ng.client.name}")
    String clientName;

    @Autowired
    ServerTableManager serverTableManager;

    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {

        byte status = message.getStatus();
        if (status == 0){
            logger.error("client注册失败,已存在的客户名");
            //TODO 获取IPTable
            String body = message.getBody();
            logger.error("已在线客户机:"+body);
        }else {
            logger.error("client被强制下线");
            //TODO 获取IPTable
            String body = message.getBody();
            logger.error("已在线客户机:"+body);
        }
            System.exit(1);


    }

}
