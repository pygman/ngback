package com.pygman.ngback.client.exec;

import com.pygman.ngback.client.boot.ClientMain;
import com.pygman.ngback.constants.ResponseStatus;
import com.pygman.ngback.util.TheHttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 代理请求http
 * Created by pygman on 2016/9/18.
 */
@Component
public class HttpProxy implements ClientMain.OnMessageReadListener {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${ng.client.host}")
    private String clientHost;

    @Autowired
    TheHttpClient theHttpClient;

    @Override
    public String onMessageRead(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (object instanceof String){
            try {
                Map value = objectMapper.readValue((String) object, Map.class);
                String m = (String)value.get("m");
                //TODO 失败 重发
                try {
                    return "GET".equalsIgnoreCase(m)? theHttpClient.HttpGet(clientHost + value.get("u"), (Map) value.get("p"))
                            : theHttpClient.HttpPost(clientHost + value.get("u"), (Map) value.get("p"));
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseStatus.SERVICE_NOT_FOUND.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
