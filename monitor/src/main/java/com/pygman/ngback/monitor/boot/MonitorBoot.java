package com.pygman.ngback.monitor.boot;

import com.pygman.ngback.struct.Pair;
import com.pygman.ngback.util.IPParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 服务调控端启动器
 * Created by pygman on 2016/9/13.
 */
@Component
public class MonitorBoot {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${ng.monitor.reg}")
    private String monitorReg;
    private String monitorAddr;
    private Integer monitorPort;


    @Autowired
    MonitorMain monitorMain;

    @Autowired
    IPParser ipParser;

    @PostConstruct
    public void init() {

        try {
            Pair<String, Integer> monitorPair = ipParser.parse(monitorReg);
            monitorPort = monitorPair.getSecond();
            monitorAddr = monitorPair.getFirst();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        logger.info("Monitor启动开始");
        monitorMain.start(monitorPort);
        logger.info("Monitor启动成功");
    }

    @PreDestroy
    public void exit() {
        logger.info("Monitor关闭");
        monitorMain.shutdown();
    }
}
