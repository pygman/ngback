package com.pygman.ngback.monitor.jwt;

import org.apache.commons.lang.RandomStringUtils;

import java.util.Map;

/**
 * 进行SHA256校验
 * Created by jhl on 16/3/29.
 */
public class SHA256Sign {


    /**
     * 进请求参数进行签名
     *
     * @param requestParames 请求参数
     * @param secretKey      sha256密钥
     * @return
     */
    public static Map<String, String> sign(Map<String, String> requestParames, String secretKey) {
        requestParames.put("ctime", System.currentTimeMillis() + "");
        requestParames.put("nonce", RandomStringUtils.randomAlphanumeric(20));
        return SHA256Verify.genSha256SignMap(requestParames, secretKey);
    }

}
