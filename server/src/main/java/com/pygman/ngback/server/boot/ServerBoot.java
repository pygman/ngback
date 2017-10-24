package com.pygman.ngback.server.boot;

import com.pygman.ngback.struct.Pair;
import com.pygman.ngback.util.IPParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * 服务端启动器
 * Created by pygman on 2016/9/13.
 */
@Component
public class ServerBoot {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${ng.server.reg}")
    private String serverReg;
    private String serverAddr;
    private Integer serverPort;

    @Value("${ng.monitor.reg}")
    private String monitorReg;
    private String monitorAddr;
    private Integer monitorPort;

    @Autowired
    ServerMain serverMain;

    @Autowired
    ServerRegisterMain serverRegisterMain;

    @Autowired
    IPParser ipParser;

    @PostConstruct
    public void init() {
        try {
            Pair<String, Integer> serverPair = ipParser.parse(serverReg);
            serverAddr = serverPair.getFirst();
            serverPort = serverPair.getSecond();

            Pair<String, Integer> monitorPair = ipParser.parse(monitorReg);
            monitorAddr = monitorPair.getFirst();
            monitorPort = monitorPair.getSecond();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        //之前注册
        logger.info("---init:serverRegisterMain:"+serverRegisterMain+"---");
        serverRegisterMain.start(monitorAddr, monitorPort);

        logger.info("Server启动开始");
        serverMain.start(serverPort);
        logger.info("Server启动成功");
    }

    @PreDestroy
    public void exit() {
        logger.info("Server关闭");
//        serverMain.shutdown();
        serverRegisterMain.shutdown();
    }

    /**
     * 重启注册
     */
    public void restartRegister() {
        try {
            TimeUnit.SECONDS.sleep(1);
            logger.info("---restartRegister:serverRegisterMain:"+serverRegisterMain+"---");
            serverRegisterMain.start(monitorAddr, monitorPort);
        } catch (InterruptedException ignored) {
        }
    }

    public void restartWork(){
        logger.info("Server重新启动开始");
        serverMain.shutdown();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
        }
        serverMain.start(serverPort);
        logger.info("Server重新启动成功");
    }
}
