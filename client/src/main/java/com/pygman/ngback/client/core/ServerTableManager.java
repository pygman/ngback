package com.pygman.ngback.client.core;

import com.pygman.ngback.client.boot.ClientBoot;
import com.pygman.ngback.struct.Pair;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 服务端列表管理
 * Created by pygman on 16-11-1.
 */
@Component
public class ServerTableManager {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Lazy
    @Autowired
    ClientBoot clientBoot;

    private Random random = new Random();
    private String tableCache = "";
    private ConcurrentHashMap<String, EventLoopGroup> groupTable = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Channel> serverChannelTable = new ConcurrentHashMap<>();

    public Channel putServerChannelTable(String name, Channel channel) {
        return serverChannelTable.put(name, channel);
    }

    public String watchTableCache(){
        return tableCache;
    }

    public void removeServerChannelTable(String name) {
        Channel channel = serverChannelTable.get(name);
        if (channel!=null)channel.close();
        serverChannelTable.remove(name);
        EventLoopGroup eventLoopGroup = groupTable.get(name);
        if (eventLoopGroup!=null)eventLoopGroup.shutdownGracefully();
        groupTable.remove(name);
        tableCache = "";
    }

    public Channel randomChannel() {
        Object[] array = serverChannelTable.values().toArray();
        if (array.length < 1) return null;
        Object o = array[random.nextInt(array.length)];
        return (Channel) o;
    }

    public Channel[] randomChannels(int count) {
        Object[] array = serverChannelTable.keySet().toArray();
        Channel[] result = new Channel[count];
        for (int i = 0; i < result.length; i++) {
            result[i] = (Channel) array[random.nextInt(array.length)];
        }
        return result;
    }

    public String serverTableUpdate(String newTable) {
        if (tableCache.equals(newTable)) return tableCache;
        logger.info("update:old【" + groupTable.keySet() + "】");
        logger.info("update:new【" + newTable + "】");
        if (newTable != null && newTable.length() > 1) {
            String newTableIn = newTable.substring(1, newTable.length() - 1);

            Collection<String> newServers = new LinkedList<>(), nS = newServers;
            Collection<String> deletedServers = new LinkedList<>(), dS = deletedServers;

            Collection<String> A = groupTable.keySet();
            Collection<String> B = Arrays.asList(newTableIn.split(", "));

            Collection<String> max = A;
            Collection<String> min = B;
            if (A.size() < B.size()) {
                max = B;
                min = A;
                nS = deletedServers;
                dS = newServers;
            }
            Map<String, Boolean> map = new HashMap<>(max.size());
            for (String s : max) {
                map.put(s, true);
            }
            for (String s : min) {
                if (map.get(s) == null) {
                    if (s.length() > 0) {
                        nS.add(s);
                    }
                } else {
                    map.put(s, false);
                }
            }
            for (Map.Entry<String, Boolean> stringBooleanEntry : map.entrySet()) {
                String key;
                if (stringBooleanEntry.getValue() && (key = stringBooleanEntry.getKey()).length() > 0) {
                    dS.add(key);
                }
            }

            for (String deletedServer : deletedServers) {
                logger.info("断开连接 " + deletedServer);
                clientBoot.shutdown(groupTable.get(deletedServer));
                groupTable.remove(deletedServer);
                removeServerChannelTable(deletedServer);
            }
            for (String newServer : newServers) {
                logger.info("新增连接 " + newServer);
                Pair<EventLoopGroup, Channel> nettyPair = clientBoot.login(newServer);
                EventLoopGroup eventLoopGroup = nettyPair.getFirst();
                Channel channel = nettyPair.getSecond();
                if (eventLoopGroup != null) {
                    groupTable.put(newServer, eventLoopGroup);
                }
                if (channel != null) {
                    //TODO 走login
//                    serverChannelTable.put(newServer, channel);
                }
            }
        }
        return tableCache = newTable;
    }

    /**
     * 登录
     *
     * @param table serverTable
     * @return serverTable
     */
    public String login(String table) {
        logger.info("Login:【" + table + "】");
        if (table != null && table.length() > 2) {
            table = table.substring(1, table.length() - 1);
            String[] serverIPs = table.split(", ");
            for (String serverIP : serverIPs) {
                Pair<EventLoopGroup, Channel> nettyPair = clientBoot.login(serverIP);
                EventLoopGroup eventLoopGroup = nettyPair.getFirst();
                Channel channel = nettyPair.getSecond();
                if (eventLoopGroup != null) {
                    groupTable.put(serverIP, eventLoopGroup);
                }
                if (channel != null) {
                    //TODO 走login
//                    serverChannelTable.put(serverIP, channel);
                }
            }
        }
        return tableCache = table;
    }

    public void shutdown(String serverIPStr) {
        EventLoopGroup group = groupTable.get(serverIPStr);
        if (group == null) return;
        logger.info("将关闭连接" + serverIPStr);
        try {
            group.shutdownGracefully();
        } catch (Exception e) {
            e.printStackTrace();
        }
        groupTable.remove(serverIPStr);
        removeServerChannelTable(serverIPStr);
    }

    public void shutdown() {
        for (Map.Entry<String, EventLoopGroup> stringEventLoopGroupEntry : groupTable.entrySet()) {
            EventLoopGroup group = stringEventLoopGroupEntry.getValue();
            if (group != null) {
                logger.info("将关闭连接" + stringEventLoopGroupEntry.getKey());
                group.shutdownGracefully();
            }
        }
        groupTable.clear();
        serverChannelTable.clear();
        tableCache = "";
    }

    public void restartWork(String serverIPStr) {
        //TODO 检测是否在线 发起重连

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException ignored) {
        }
        Pair<EventLoopGroup, Channel> nettyPair = clientBoot.login(serverIPStr);
        EventLoopGroup eventLoopGroup = nettyPair.getFirst();
        Channel channel = nettyPair.getSecond();
        if (eventLoopGroup != null) {
            groupTable.put(serverIPStr, eventLoopGroup);
        }
        if (channel != null) {
            //TODO 走login
//            serverChannelTable.put(serverIPStr, channel);
        }
    }
}
