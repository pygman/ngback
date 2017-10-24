package com.pygman.ngback.monitor.jwt;

import org.apache.commons.codec.digest.DigestUtils;

import java.text.Collator;
import java.util.*;

/**
 * SHA256请求校验帮助类
 * Created by jhl on 16/2/29.
 */
public class SHA256Verify {

    private static final String startWithChinesePattern = "^[\u4e00-\u9fa5]";
    private static Comparator comparator = Collator.getInstance(Locale.CHINA);


    /**
     * 生成SHA256校验Sign 并返回含有sign的Map
     *
     * @param parames   校验请求参数的Map
     * @param secretKey sha256密钥
     * @return 校验结果: true 正确, false 错误
     */
    public static Map<String, String> genSha256SignMap(Map<String, String> parames, String secretKey) {
        parames.put("sign", shuffAndSha256(parames, secretKey));
        return parames;
    }

    /**
     * 混淆请求值并Base64.
     * 使用ASCII排序.
     *
     * @param requestMap 校验请求参数的Map
     * @return 校验结果: true 正确, false 错误
     */
    private static String shuffAndSha256(Map<String, String> requestMap, String secretKey) {
        String values = shuff(requestMap, secretKey);
        return new String(DigestUtils.sha256Hex(values.getBytes()));
    }

    /**
     * 根据参数值进行排序.
     *
     * @param requestMap
     * @param secretKey
     * @return
     */
    protected static String shuff(Map<String, String> requestMap, String secretKey) {

        String values = "";
        List<String> valueList = new ArrayList<String>();

        for (Map.Entry<String, String> entry : requestMap.entrySet()) {
            valueList.add(entry.getValue());
        }
        valueList.add(secretKey);
        Collections.sort(valueList);


        for (String s : valueList)
            values += s;

        return values;
    }

}
