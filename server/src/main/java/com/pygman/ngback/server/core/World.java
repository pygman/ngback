package com.pygman.ngback.server.core;

import com.pygman.ngback.core.IPTable;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Observable;

/**
 * 保存所有连接到server的client
 * Created by kevin  on 16/8/28.
 */
@Component
public class World extends IPTable<World.Client> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 业务Client实体
     */
    class Client extends IPTable.Vo {
        Client(String id, Channel channel) {
            this.id = id;
            this.channel = channel;
        }

        @Override
        public void update(Observable o, Object arg) {
            logger.info("×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××");
        }
    }

    public void put(String id, Channel channel) {
        Client client = new Client(id, channel);
        logger.info("world: before put == " + this.toString() + ";channelMap == " + channelMap.values().toString());
        put(id, channel, client);
        logger.info("world: after put == " + this.toString() + ";channelMap == " + channelMap.values().toString());
    }

}
