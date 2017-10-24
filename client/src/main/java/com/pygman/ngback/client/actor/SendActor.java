package com.pygman.ngback.client.actor;

import com.pygman.ngback.client.exec.SendExecutor;
import com.pygman.ngback.client.mail.SendMailBox;
import com.pygman.ngback.client.mail.SendStateMap;
import com.pygman.ngback.constants.MessageType;
import com.pygman.ngback.struct.Message;
import com.pygman.ngback.util.TheTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by pygman on 12/12/2016.
 */
@Component
public class SendActor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SendMailBox sendMailBox;

    @Autowired
    SendStateMap sendStateMap;

    @Autowired
    SendExecutor sendExecutor;

    /**
     * 无关队列具体实现
     * 只管调用
     */
    @Async
    public void mailEventLoop() {

        while (true) {
            Map.Entry<Long, byte[]> firstEntry = sendMailBox.poll();
            byte[] firstEntryValue = firstEntry.getValue();

            sendExecutor.execute(firstEntryValue, () -> {
                //TODO 成功不做处理
                //TODO 可扩展 如果需要返回结果
                logger.info("Success传输成功" + firstEntryValue);
            }, () -> {
                //TODO 失败不做处理
                //TODO 失败立刻重发 暂定
                //DONE 如果大于一分钟前的消息不进行即时重发
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ignored) {
                }
                long lastIntegralPoint = TheTime.lastIntegralPoint(1);
                Long firstEntryKey = firstEntry.getKey();
                if (firstEntryKey > lastIntegralPoint) {
                    sendMailBox.put(firstEntryKey, firstEntryValue);
                }
            });
        }

    }

    public boolean newSendTask(Message message) {
        //TODO 不需返回来源客户端的情况返回false ? 什么时候 报错?
        long id = message.getId();
        byte[] messageBytes = message.serialize();
        sendMailBox.put(id, messageBytes);
        sendStateMap.put(id, messageBytes);
        logger.info("存入" + message + "成功");
        return true;
    }

    public void complete(Message message) {
        sendStateMap.remove(message.getId());
        logger.info("信息送达" + message);
    }

    /**
     * 路由失败 未发现目标客户机
     *
     * @param message
     */
    public void routeError(Message message) {
        //DONE 如果大于一分钟前的消息不进行即时重发
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignored) {
        }
        long lastIntegralPoint = TheTime.lastIntegralPoint(1);
        Long id = message.getId();
        if (id > lastIntegralPoint) {
            message.setType(MessageType.SERVICE_REQ.value());
            message.setStatus((byte) 0);
            sendMailBox.put(id, message.serialize());
        }
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void resend() {
        //DONE 重发 已投递出去 一直未有回复的信息
//        Pair<Long, Long> longLongPair = TheTime.lastIntegralPointBetween(1, 2);
//        sendStateMap.sub
        long lastIntegralPoint = TheTime.lastIntegralPoint(1);
        ConcurrentNavigableMap<Long, byte[]> headMap = sendStateMap.headMap(lastIntegralPoint);
        headMap.forEach((id, messageBytes) -> sendMailBox.put(id, messageBytes));
    }

    /**
     * TODO 清理state一天前所有消息
     */
    @Scheduled(cron = "0 0 0 0/1 * *")
    public void clear() {
        long lastIntegralPoint = TheTime.lastIntegralPoint(24 * 60);
        ConcurrentNavigableMap<Long, byte[]> headMap = sendStateMap.headMap(lastIntegralPoint);
        headMap.forEach((id, messageBytes) -> {
            sendMailBox.remove(id);
            sendStateMap.remove(id);
            logger.error("移除囤积消息:" + Message.deserialize(messageBytes));
        });
    }

}
