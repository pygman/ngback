package com.pygman.ngback.client.controller;

import com.pygman.ngback.client.actor.SendActor;
import com.pygman.ngback.client.service.SendService;
import com.pygman.ngback.struct.MESSAGES;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

/**
 * 发送
 * Created by pygman on 12/6/2016.
 */
@RestController
public class SendController {


    @Autowired
    SendService sendService;

    @Autowired
    SendActor sendActor;

    @Value("${ng.client.name}")
    String myName;

    @RequestMapping("/{clientName}/**")
    public String client(HttpServletRequest request, @PathVariable String clientName) {


        String path = request.getServletPath();
        String url = path.substring(clientName.length() + 1);
        Map<String, String[]> parameterMap = request.getParameterMap();

        String method = request.getMethod();

        ObjectMapper objectMapper = new ObjectMapper();
        String p;
        try {
            p = objectMapper.writeValueAsString(parameterMap);
        } catch (IOException e) {
            p = "{}";
            e.printStackTrace();
        }

        String body = "{\"u\":\"" + url + "\",\"m\":\"" + method + "\",\"p\":" + p + "}";
        if (clientName.contains("-")) {
            String[] clientNames = clientName.split("-");
            for (String name : clientNames) {
//                sendService.send(MESSAGES.SERVICE_REQ(myName, name, body));
                sendActor.newSendTask(MESSAGES.SERVICE_REQ(myName, name, body));
            }

        } else {
//            sendService.send(MESSAGES.SERVICE_REQ(myName, clientName, body));
            sendActor.newSendTask(MESSAGES.SERVICE_REQ(myName, clientName, body));
        }
        //TODO 日志输出加上请求路径和来源ip
        return "ok";
    }

    @RequestMapping("/jqk.php")
    public String client2(HttpServletRequest request) {

        return "client2";
    }
}
