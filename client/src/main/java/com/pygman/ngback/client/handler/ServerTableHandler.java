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
import org.springframework.stereotype.Component;

/**
 * 服务端列表更新
 */
@Component
@Handle(MessageType.SERVER_TABLE_UPDATE)
public class ServerTableHandler extends SimpleMessageInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ServerTableManager serverTableManager;

    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
        String body = message.getBody();
        //TODO 启动和更新工作线程
        logger.info("...get server_table_update...:" + body);
        if (body != null && body.length() > 0) {
            serverTableManager.serverTableUpdate(body);
        }
    }

}
