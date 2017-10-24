package com.pygman.ngback.server.core;

import com.pygman.ngback.struct.Message;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * 同步服务
 * Created by pygman on 2016/7/7.
 */
@Service
public class SyncServer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ConcurrentContainer concurrentContainer;

    @Async
    public Future<String> send(Channel channel, Message message) {

        long messageId = message!=null?message.getId():0;
        concurrentContainer.put(message);
        channel.writeAndFlush(message);
        logger.info("<<<发送消息 "+message+" >>>");

        String result = concurrentContainer.get(channel, message);
        logger.info("<<<获取"+messageId+"消息 "+result+" >>>");

        // TODO 这样写有问题
        return new AsyncResult<>(result);
    }
}
