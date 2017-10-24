package com.pygman.ngback.client.exec;

import com.pygman.ngback.client.boot.ClientBoot;
import com.pygman.ngback.client.core.ServerTableManager;
import com.pygman.ngback.client.service.SendService;
import com.pygman.ngback.core.TaskExecutor;
import com.pygman.ngback.struct.Message;
import com.pygman.ngback.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created by pygman on 12/12/2016.
 */
@Component
public class SendExecutor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TaskExecutor taskExecutor;

    @Autowired
    SendService sendService;

    @Autowired
    JSON json;

    @Lazy
    @Autowired
    ClientBoot clientBoot;

    @Autowired
    ServerTableManager serverTableManager;

    /**
     * TODO 判断
     *
     * @param messageBytes
     * @param successCallBack
     * @param faultCallBack
     */
    @Async
    public void execute(byte[] messageBytes, Runnable successCallBack, Runnable faultCallBack) {
        Message deserializedMessage = Message.deserialize(messageBytes);
        Message send = sendService.send(deserializedMessage);
        if (send != null) {
            //DONE 成功
            taskExecutor.execute(successCallBack);
        } else {
            //DONE 不在线
            logger.info("传输失败" + deserializedMessage);
            taskExecutor.execute(faultCallBack);
            String tableCache = serverTableManager.watchTableCache();
//            if (tableCache != null && !tableCache.equals("")) {
                clientBoot.restart();
//            }
        }
    }
}
