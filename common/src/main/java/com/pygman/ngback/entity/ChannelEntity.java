package com.pygman.ngback.entity;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by kevin  on 16/8/28.
 */
@Deprecated
@Component
public class ChannelEntity {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Channel channel;

    private volatile long lastActiveTime;


    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }


    private ChannelEntity(){}

}
