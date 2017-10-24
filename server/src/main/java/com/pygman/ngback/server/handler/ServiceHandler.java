package com.pygman.ngback.server.handler;

import com.pygman.ngback.constants.MessageType;
import com.pygman.ngback.core.IPTable;
import com.pygman.ngback.handler.Handle;
import com.pygman.ngback.handler.IPTableMessageInboundHandlerAdapter;
import com.pygman.ngback.server.core.ConcurrentContainer;
import com.pygman.ngback.server.core.World;
import com.pygman.ngback.struct.Message;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 业务响应
 * Created by kevin  on 16/8/26.
 */
@Component
@Handle(MessageType.SERVICE_RESP)
public class ServiceHandler extends IPTableMessageInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //DONE 自己实现类似SimpleChannelInboundHandler的功能 用于分发MessageType

    @Autowired
    ConcurrentContainer concurrentContainer;

    @Autowired
    World world;

    @Override
    protected IPTable getTable() {
        return world;
    }

    @Override
    protected void channelRead1(ChannelHandlerContext ctx, Message message) {
        Object body = message.getBody();
        String bodyStr = String.valueOf(body);
        long messageId = message.getId();
        if (!concurrentContainer.success(messageId, bodyStr)) {
            logger.error("$$$$严重错误,已过期的消息无法成功获取:" + messageId + "=>" + bodyStr);
        }
    }

}
