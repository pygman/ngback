package com.pygman.ngback.monitor.core;

import com.pygman.ngback.core.IPTable;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Observable;

/**
 * ServerTable
 * Created by pygman on 16-10-28.
 */
@Component
public class ServerTable extends IPTable<ServerTable.Server> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Server实体
     */
    class Server extends IPTable.Vo {
        Server(String id, Channel channel) {
            this.id = id;
            this.channel = channel;
        }

        @Override
        public void update(Observable o, Object arg) {
            System.out.println("×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××××");
        }

    }

    public void put(String id, Channel channel) {
        Server server = new Server(id, channel);
        logger.info("serverTable: before put == "+this.toString() + ";channelMap == " + channelMap.values().toString());
        put(id, channel, server);
        logger.info("serverTable: after put == "+this.toString() + ";channelMap == " + channelMap.values().toString());
        // this.addObserver(clientTable.);
        this.setChanged();
        this.notifyObservers(idMap.keySet());
    }

    @Scheduled(cron = "30 0/1 * * * *")
    public void scheduledUpdate(){
        this.setChanged();
        this.notifyObservers(idMap.keySet());
    }

    @Override
    public Server remove(String id) {
        Server remove = super.remove(id);
        logger.info("serverTable: before remove id == "+this.toString() + ";channelMap == " + channelMap.values().toString());
        this.notifyObservers(idMap.keySet());
        logger.info("serverTable: after remove id == "+this.toString() + ";channelMap == " + channelMap.values().toString());
        return remove;
    }

    @Override
    public Server remove(Channel channel) {
        logger.info("serverTable: before remove channel == "+this.toString() + ";channelMap == " + channelMap.values().toString());
        Server remove = super.remove(channel);
        logger.info("serverTable: after remove channel == "+this.toString() + ";channelMap == " + channelMap.values().toString());
        this.notifyObservers(idMap.keySet());
        return remove;
    }
}
