package com.pygman.ngback.core;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IPTable
 * Created by pygman on 16-10-28.
 */
public abstract class IPTable<T extends IPTable.Vo> extends Observable {

    public abstract class Vo implements Observer {
        protected String id;
        protected Channel channel;

        public String getId () {
            return id;
        }

        public Channel getChannel () {
            return channel;
        }
        @Override
        public int hashCode() {
            return channel==null?id.hashCode():channel.hashCode();
        }
    }

    private Random random = new Random();
    protected final Map<String, T> idMap = new ConcurrentHashMap<>();
    protected final Map<Channel, T> channelMap = new ConcurrentHashMap<>();

    protected void put(String id, Channel channel, T t) {
        T oT = idMap.get(id);
        idMap.put(id, t);
        if (oT!=null){
            Channel oTChannel = oT.getChannel();
            oTChannel.close();
            channelMap.remove(oTChannel);
        }
        channelMap.put(channel, t);
    }

    public String getIdByChannel(Channel channel) {
        T t = channelMap.get(channel);
        return t == null ? null : t.getId();
    }

    public Channel getChannelById(String id) {
        T t = idMap.get(id);
        return t == null ? null : t.getChannel();
    }

    public T remove(String id) {
        T t = idMap.get(id);
        if (t == null) return null;
        Channel channel = t.getChannel();
        idMap.remove(id);
        channelMap.remove(channel);
        this.setChanged();
        return t;
    }

    public T remove(Channel channel) {
        T t = channelMap.get(channel);
        if (t == null) return null;
        String id = t.getId();
        channelMap.remove(channel);
        idMap.remove(id);
        this.setChanged();
        return t;
    }

    public Channel randomChannel(){
        Object[] array = channelMap.keySet().toArray();
        Object o = array[random.nextInt(array.length)];
        return (Channel) o;
    }

    public Channel[] randomChannels(int count){
        Object[] array = channelMap.keySet().toArray();
        Channel[] result = new Channel[count];
        for (int i = 0; i < result.length; i++) {
            result[i] = (Channel) array[random.nextInt(array.length)];
        }
        return result;
    }

    @Override
    public String toString() {
        return idMap.keySet().toString();
    }


}
