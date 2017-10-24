package com.pygman.ngback.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * HttpClient
 */
@Component
public class TheHttpClient {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public String HttpGet(String url, Map<String, Object> params) {
        return HttpGet(url, params, "utf-8");
    }

    public String HttpPost(String url, Map<String, Object> params) {
        return HttpPost(url, params, "utf-8");
    }

    public String HttpGet(String url, Map<String, Object> params, String charSet) {
        StringBuilder loggerBuilder = new StringBuilder();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet;
        if (params != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("?");
            Set<Map.Entry<String, Object>> entries = params.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                if (entry.getValue() == null) continue;
                String key = entry.getKey();
                List values = (List) entry.getValue();
                for (Object value : values) {
                    stringBuilder.append(key).append("=").append(value).append("&");
                    loggerBuilder.append(key).append(":").append(value).append(",");
                }
            }
            String paramStr = stringBuilder.substring(0, stringBuilder.length() - 1);
            httpGet = new HttpGet(url + paramStr);
        } else {
            httpGet = new HttpGet(url);
        }
        try {
            HttpResponse response = httpClient.execute(httpGet);
            if(response != null){
                int statusCode = response.getStatusLine().getStatusCode();
                logger.info("---HttpGet:statusCode:{}---", String.valueOf(statusCode));
                if(statusCode > 500){
                    logger.error("---HttpGet,url:{},params:{},response code:{}，need retry---",
                            url,params.toString(),String.valueOf(statusCode));
                    return null;
                }
            }
            HttpEntity entity = response.getEntity();
            String html = EntityUtils.toString(entity, charSet);
            if (html != null && html.trim().equals("")){
                logger.error("---HttpPost request return is empty str---");
                return null;
            }
            logger.info("HTTP:" + url + " PARAMS:" + loggerBuilder.toString() + "\nRESPONSE:" + html);
            return html;
        } catch (IOException e) {
            logger.error("HTTP:" + url + " PARAMS:" + loggerBuilder.toString() + "\nERROR");
            logger.error(e.toString());
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public String HttpPost(String url, Map<String, Object> params, String charSet) {
        StringBuilder loggerBuilder = new StringBuilder();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost;

        httpPost = new HttpPost(url);
        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            if (params != null) {
                for (Map.Entry<String, Object> stringObjectEntry : params.entrySet()) {
                    String key = stringObjectEntry.getKey();
                    Object value = stringObjectEntry.getValue();
                    if (value instanceof List) {
                        List values = (List) stringObjectEntry.getValue();
                        for (Object value2 : values) {
                            nameValuePairs.add(new BasicNameValuePair(key, value2 instanceof String ? (String) value2 : String.valueOf(value2)));
                            loggerBuilder.append(key).append(":").append(values).append(",");
                        }
                    }else{
                        nameValuePairs.add(new BasicNameValuePair(key, value instanceof String ? (String) value : String.valueOf(value)));
                    }
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, charSet));
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                int statusCode = response.getStatusLine().getStatusCode();
                logger.info("---HttpPost:statusCode:{}---", String.valueOf(statusCode));
                if(statusCode > 500){
                    logger.error("---HttpPost,url:{},params:{},response code:{}，need retry---",
                            url,params.toString(),String.valueOf(statusCode));
                    return null;
                }
            }
            HttpEntity entity = response.getEntity();
            String html = EntityUtils.toString(entity);
            if (html != null && html.trim().equals("")){
                logger.error("---HttpPost request return is empty str---");
                return null;
            }
            logger.info("HTTP:" + url + " PARAMS:" + loggerBuilder.toString() + "\nRESPONSE:" + html);
            return html;
        } catch (IOException e) {
            logger.error("HTTP:" + url + " PARAMS:" + loggerBuilder.toString() + "\nERROR");
            logger.error(e.toString());
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
