package com.pygman.ngback.client.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 代理信箱
 * Created by pygman on 12/8/2016.
 */
@Component
public class ProxyMailBox extends DBManager.DBMap<Long, byte[]> {

    @Autowired
    DBManager dbManager;

    @PostConstruct
    public void init() {
        setMap(dbManager.makeMap("proxy"));
    }

}
