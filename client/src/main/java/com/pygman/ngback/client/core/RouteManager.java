package com.pygman.ngback.client.core;

import com.pygman.ngback.core.IPTable;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Observable;

/**
 * Server路由管理
 * Created by pygman on 12/6/2016.
 */
@Component
public class RouteManager extends IPTable<RouteManager.Route> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Route实体
     */
    class Route extends IPTable.Vo {
        Route(String id, Channel channel) {
            this.id = id;
            this.channel = channel;
        }

        @Override
        public void update(Observable o, Object arg) {
            logger.info("×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××");
        }
    }

    public void put(String id, Channel channel) {
        Route route = new Route(id, channel);
        logger.info("world: before put == " + this.toString() + ";channelMap == " + channelMap.values().toString());
        put(id, channel, route);
        logger.info("world: after put == " + this.toString() + ";channelMap == " + channelMap.values().toString());
    }

}