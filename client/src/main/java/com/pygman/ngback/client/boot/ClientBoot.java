package com.pygman.ngback.client.boot;

import com.pygman.ngback.client.core.ServerTableManager;
import com.pygman.ngback.client.handler.ClientRegister;
import com.pygman.ngback.struct.Pair;
import com.pygman.ngback.util.IPParser;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * 客户端启动器
 * Created by pygman on 16-10-14.
 */
@Component
public class ClientBoot {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${ng.monitor.reg}")
    String monitorReg;
    String monitorAddr;
    Integer monitorPort;

    @Value("${ng.client.name}")
    String clientName;

    @Autowired
    ClientMain clientMain;

    @Autowired
    ClientRegisterMain clientRegisterMain;

    @Autowired
    IPParser ipParser;

    @Autowired
    ClientRegister clientRegister;

    @Autowired
    ServerTableManager serverTableManager;

    @PostConstruct
    public void init() {
        if (clientName == null || clientName.equals("default")) {
            logger.error("无法使用默认客户端名称启动,请指定--name参数");
            System.exit(1);
        }
        if (clientName.contains("-")) {
            logger.error("客户端名称包含非法字符,请使用正确的--name参数");
            System.exit(1);
        }
        try {
            Pair<String, Integer> monitorPair = ipParser.parse(monitorReg);
            monitorAddr = monitorPair.getFirst();
            monitorPort = monitorPair.getSecond();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        clientRegisterMain.start(monitorAddr, monitorPort);
        logger.info("客户端:" + clientName + "上线");
    }

    /**
     * 登录服务端
     *
     * @param serverIPStr 服务器地址端口
     * @return EventLoopGroup
     */
    public Pair<EventLoopGroup, Channel> login(String serverIPStr) {
        try {
            Pair<EventLoopGroup, Channel> nettyPair = new Pair<>();
            EventLoopGroup group = new NioEventLoopGroup();
            nettyPair.setFirst(group);
            clientMain.start(serverIPStr, group, nettyPair);
            while (nettyPair.getSecond() == null) {
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
            return nettyPair;
        } catch (IllegalStateException ise) {
            ise.printStackTrace();
            logger.error("---failed to create a child event loop---",ise);
            System.exit(1);
            return null;
        }
    }

    /**
     *
     */
    public void restartRegister() {
        if (isRestarting) return;
        try {
            clientRegister.setForce("true");
            TimeUnit.SECONDS.sleep(1);
            clientRegisterMain.start(monitorAddr, monitorPort);
        } catch (InterruptedException ignored) {
        }
    }

    public void shutdown(EventLoopGroup group) {
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    @PreDestroy
    public void shutdown() {
        //TODO 全部依次关闭
        logger.info("断开与server之间的连接");
        serverTableManager.shutdown();
//        clientMain.shutdown();
    }

    private boolean isRestarting = false;

    @Async
    public void restart() {
        if (isRestarting) return;
        isRestarting = true;
        logger.info("断开与monitor之间的连接");
        clientRegisterMain.shutdown();
        logger.info("断开与server之间的连接");
        serverTableManager.shutdown();
        System.gc();
        try {
            clientRegister.setForce("true");
            TimeUnit.SECONDS.sleep(3);
            logger.info("重启注册");
            clientRegisterMain.start(monitorAddr, monitorPort);
            logger.info("客户端:" + clientName + "上线");

            TimeUnit.SECONDS.sleep(20);

        } catch (InterruptedException ignored) {
        }
        isRestarting = false;
    }

}
