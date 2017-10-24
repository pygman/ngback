package com.pygman.ngback.server.core;

import com.pygman.ngback.constants.ResponseStatus;
import com.pygman.ngback.struct.Message;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * 当前在等待的订单的容器
 * Created by pygman on 2016/7/6.
 */
@Component
public class ConcurrentContainer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final long timeout = 30;

    private static final class MessageEntry {
        private Long id;
        private Message message;
        private SynchronousQueue<String> billQueue;

        MessageEntry(Long id, Message message) {
            this.id = id;
            this.message = message;
            this.billQueue = new SynchronousQueue<>();
        }

        Long getId() {
            return id;
        }

        public Message getMessage() {
            return message;
        }

        SynchronousQueue<String> getBillQueue() {
            return billQueue;
        }
    }

    /**
     * 主容器
     */
    private final ConcurrentHashMap<Long, MessageEntry> ContainerMap = new ConcurrentHashMap<>();

    /**
     * 放入主容器订单
     */
    public boolean put(Message message) {
        long messageId = message.getId();
        try {
            ContainerMap.put(messageId, new MessageEntry(messageId, message));
            return true;
        } catch (Exception e) {
            logger.error("<<<存入暂存容器失败>>>");
            return false;
        }
    }

    /**
     * 阻塞获取主容器中返回数据
     */
    public String get(Channel channel, Message message) {
        long messageId = message.getId();

        MessageEntry billEntry = ContainerMap.get(messageId);
        if (billEntry == null) {
            logger.error("$$$$严重错误,已过期的消息无法成功获取" + message);
            return null;
        }
        SynchronousQueue<String> billQueue = billEntry.getBillQueue();
        try {
            String result = billQueue.poll(timeout, TimeUnit.SECONDS);
            // TODO if 里添加其他未送达的可能情况
            if (result == null) {
                logger.error("<<<获取返回数据超时" + message);
                // TODO 失败重发
                int times = 0;
                while (result == null && times < 1) {
                    channel.writeAndFlush(message);
                    logger.error("<<< 信息未送达,第" + ++times + "次失败重发 " + message);
                    result = billQueue.poll(timeout, TimeUnit.SECONDS);
                }
                if (result == null) {
                    // 认为彻底失败 记录错误
                    logger.error("$$$$严重错误,消息错误未发送成功" + message);
                    result = ResponseStatus.MESSAGE_SEND_ERROR.toString();
                }
            }
            return result;
        } catch (InterruptedException e) {
            logger.error("<<<取出暂存容器消费异常>>>" + message);
            e.printStackTrace();
            return null;
        } finally {
            logger.debug("移除消息:"+messageId);
            ContainerMap.remove(messageId);
        }
    }

    /**
     * 获取响应成功
     * @param id 消息id
     * @param msg 消息体
     * @return 是否可以返回
     */
    public boolean success(Long id, String msg) {

        MessageEntry billEntry = ContainerMap.get(id);
        if (billEntry == null) return false;
        SynchronousQueue<String> billQueue = billEntry.getBillQueue();
        return billQueue.offer(msg);
    }
}
