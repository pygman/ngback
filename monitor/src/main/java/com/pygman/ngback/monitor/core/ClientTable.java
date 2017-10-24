package com.pygman.ngback.monitor.core;

import com.pygman.ngback.core.IPTable;
import com.pygman.ngback.struct.MESSAGES;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Observable;

/**
 * ClientTable
 * Created by pygman on 16-10-28.
 */
@Component
public class ClientTable extends IPTable<ClientTable.Client> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ServerTable serverTable;

    @Autowired
    private SMSNotification smsNotification;
    /**
     * Client实体
     */
    class Client extends IPTable.Vo {
        Client(String id, Channel channel) {
            this.id = id;
            this.channel = channel;
        }

        @Override
        public void update(Observable o, Object arg) {
            channel.writeAndFlush(MESSAGES.SERVER_TABLE_UPDATE(String.valueOf(arg)));
        }

        @Override
        public String toString() {
            return id;
        }
    }

    public void put(String id, Channel channel) {
        Client client = new Client(id, channel);
        logger.info("clientTable: before put == "+this.toString() + ";channelMap == " + channelMap.values().toString());
        put(id, channel, client);
        logger.info("clientTable: after put == "+this.toString() + ";channelMap == " + channelMap.values().toString());
        serverTable.addObserver(client);
    }

    @Override
    public Client remove(String id) {
        logger.info("clientTable: before remove id == "+this.toString() + ";channelMap == " + channelMap.values().toString());
        Client remove = super.remove(id);
        if(!"".equals(id)){
            smsNotification.sendMessage("1",id+"-client端已下线,时间:"+LocalDateTime.now());
        }
        logger.info("clientTable: after remove id == "+this.toString() + ";channelMap == " + channelMap.values().toString());
        serverTable.deleteObserver(remove);
        return remove;
    }

    @Override
    public Client remove(Channel channel) {
        logger.info("clientTable: before remove channel == "+this.toString() + ";channelMap == " + channelMap.values().toString());
        Client remove = super.remove(channel);
        if(remove!=null){
            smsNotification.sendMessage("1",remove.getId()+"-client端已下线,时间:"+LocalDateTime.now());
        }
        logger.info("clientTable: after remove channel == "+this.toString() + ";channelMap == " + channelMap.values().toString());
        serverTable.deleteObserver(remove);
        return remove;
    }

    /**
     * 获取所有client
     * @return
     */
    public String getClients(){
        return channelMap.values().toString();
    }
}
