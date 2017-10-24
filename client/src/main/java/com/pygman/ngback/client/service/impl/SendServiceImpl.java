package com.pygman.ngback.client.service.impl;

import com.pygman.ngback.client.core.ServerTableManager;
import com.pygman.ngback.client.service.SendService;
import com.pygman.ngback.struct.Message;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 发送服务
 * Created by pygman on 12/6/2016.
 */
@Service
public class SendServiceImpl implements SendService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ServerTableManager serverTableManager;

    @Override
    public Message send(Message message) {
        Channel channel = serverTableManager.randomChannel();
        if (channel == null) return null;

        channel.writeAndFlush(message);
        return message;

    }
}
