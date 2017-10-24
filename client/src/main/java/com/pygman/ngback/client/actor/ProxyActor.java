package com.pygman.ngback.client.actor;

import com.pygman.ngback.client.exec.ProxyExecutor;
import com.pygman.ngback.client.mail.ProxyMailBox;
import com.pygman.ngback.client.mail.ProxyStateMap;
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

/**
 * 代理发送行为
 * Created by pygman on 12/8/2016.
 */
@Component
public class ProxyActor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProxyMailBox proxyMailBox;

    @Autowired
    ProxyStateMap proxyStateMap;

    @Autowired
    ProxyExecutor proxyExecutor;

    /**
     * 无关队列具体实现
     * 只管调用
     */
    @Async
    public void mailEventLoop() {

        while (true) {
            Map.Entry<Long, byte[]> firstEntry = proxyMailBox.poll();
            Long firstEntryKey = firstEntry.getKey();
            byte[] firstEntryValue = firstEntry.getValue();
            proxyExecutor.execute(firstEntryValue, () -> {
                //TODO 修改state表状态
//                byte[] bytes = Arrays.copyOf(firstEntryValue, firstEntryValue.length);
//                firstEntryValue[firstEntryValue.length-1] = 1;
//                proxyStateMap.put(firstEntryKey, firstEntryValue);
                //TODO 可扩展 如果需要返回结果
                logger.info("Success代发成功" + firstEntryValue);
                int byteLength = firstEntryValue.length;
                byte[] completeBytes = new byte[byteLength];
                System.arraycopy(firstEntryValue, 0, completeBytes, 0, byteLength - 1);
                completeBytes[byteLength - 1] = (byte) 1;
                proxyStateMap.put(firstEntryKey, completeBytes);
            }, () -> {
                //TODO 插入proxy表新的一条
                //TODO 真实id插入 则 优先重发 暂定
                //TODO 新id插入 则队尾重发
                logger.info("Fault代发失败" + firstEntryValue);
                long lastIntegralPoint = TheTime.lastIntegralPoint(1);
                if (firstEntryKey > lastIntegralPoint) {
                    proxyMailBox.put(firstEntryKey, firstEntryValue);
                }
            });
        }

    }

    public boolean newProxyTask(Message message) {
        //TODO 不需返回来源客户端的情况返回false ? 什么时候 报错?
        long id = message.getId();
        if (!proxyStateMap.containsKey(id)) {
            byte[] messageBytes = message.serialize();
            proxyMailBox.put(id, messageBytes);
            proxyStateMap.put(id, messageBytes);
        } else {
            logger.info("已存在消息，无需处理");
        }
        return true;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void reproxy() {
        long lastIntegralPoint = TheTime.lastIntegralPoint(1);
        ConcurrentNavigableMap<Long, byte[]> headMap = proxyStateMap.headMap(lastIntegralPoint);
        headMap.forEach((id, messageBytes) -> {
            if (messageBytes[messageBytes.length - 1] == 0) {
                proxyMailBox.put(id, messageBytes);
            }
        });
    }


    @Scheduled(cron = "0 0 0 0/1 * *")
    public void clear() {
        //TODO 清理state一天前已完成消息
        long lastIntegralPoint = TheTime.lastIntegralPoint(24 * 60);
        ConcurrentNavigableMap<Long, byte[]> headMap = proxyStateMap.headMap(lastIntegralPoint);
        headMap.forEach((id, messageBytes) -> {
            if (messageBytes[messageBytes.length - 1] == 1) {
                proxyMailBox.remove(id);
                proxyStateMap.remove(id);
            } else {
                logger.error("年老消息:" + Message.deserialize(messageBytes));
                proxyStateMap.remove(id);
                proxyMailBox.remove(id);
//                proxyMailBox.put(id, messageBytes);
            }
        });
    }

}
