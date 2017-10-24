package com.pygman.ngback.monitor.core;

import com.pygman.ngback.monitor.jwt.SHA256Sign;
import com.pygman.ngback.util.TheHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aydxx on 2017/1/3.
 * 短信通知类
 */
@Component
public class SMSNotification {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${ng.monitor.phone}")
    private String phone;

    //生成签名所需密钥
    private static final String KEY = "******";
    //测试地址
//    private static final String URL = "http://**";
    //正式地址
    private static final String URL="http://******";

    /**
     * 发送短消息
     *
     * @param sendType 1:业务期间下发包含验证码、订单通知、物流通知等通知类短信，请使用行业账号进行短信下发
     *                 2:行业短信：通知类的消息,比如验证码，退货消息，订单消息等
     *                 营销短信：活动类的消息,比如超市打折，举办活动等
     * @param content  短信内容，最多350个汉字，必填,内容中不要出现【】[]这两种方括号，该字符为签名专用
     * @return {"status":"0","message":"提交成功"}  status非0为失败
     */
    public String sendMessage(String sendType, String content) {

        if("000000".equals(phone)){
            return "";
        }

        logger.info("---------------------进入短信提醒---------------------------");
        TheHttpClient theHttpClient = new TheHttpClient();

        /**
         * 根据发送短信息参数进行加密签名
         */
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phones", phone);
        paramMap.put("sendType", sendType);
        paramMap.put("content", content);

        /**
         * 进行签名
         * 获取ctime,nonce,sign
         */
        paramMap = SHA256Sign.sign(paramMap, KEY);
        String ctime = paramMap.get("ctime");
        String nonce = paramMap.get("nonce");
        String sign = paramMap.get("sign");

        /**
         * 构建发送短信的请求参数
         */
        Map<String, Object> sendParams = new HashMap();
        sendParams.put("phones", phone);
        sendParams.put("sendType", sendType);
        sendParams.put("content", content);
        sendParams.put("ctime", ctime);
        sendParams.put("nonce", nonce);
        sendParams.put("sign", sign);

        return theHttpClient.HttpPost(URL, sendParams);
    }

}
