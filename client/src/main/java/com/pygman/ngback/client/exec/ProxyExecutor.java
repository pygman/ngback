package com.pygman.ngback.client.exec;

import com.pygman.ngback.core.TaskExecutor;
import com.pygman.ngback.struct.Message;
import com.pygman.ngback.util.JSON;
import com.pygman.ngback.util.TheHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by pygman on 12/12/2016.
 */
@Component
public class ProxyExecutor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${ng.client.host}")
    String host;

    @Autowired
    TaskExecutor taskExecutor;

    @Autowired
    TheHttpClient theHttpClient;

    @Autowired
    JSON json;

    /**
     * TODO 判断 失败成功 各种情况 404
     *
     * @param messageBytes
     * @param successCallBack
     * @param faultCallBack
     */
    @Async
    public void execute(byte[] messageBytes, Runnable successCallBack, Runnable faultCallBack) {
        Message message = Message.deserialize(messageBytes);
        String body = message.getBody();

        Map map = json.toMap(body);
        String method = (String) map.get("m");
        String url = (String) map.get("u");
        Map params = (Map) map.get("p");

        if ("POST".equals(method)) {
            try {
                String s = theHttpClient.HttpPost(host + url, params);
                if (s != null) {
                    //成功
                    taskExecutor.execute(successCallBack);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                String s = theHttpClient.HttpGet(host + url, params);
                if (s != null) {
                    //成功
                    taskExecutor.execute(successCallBack);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        taskExecutor.execute(faultCallBack);
    }
}
