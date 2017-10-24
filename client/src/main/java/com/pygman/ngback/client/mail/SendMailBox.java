package com.pygman.ngback.client.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by pygman on 12/12/2016.
 */
@Component
public class SendMailBox extends DBManager.DBMap<Long, byte[]> {

    @Autowired
    DBManager dbManager;

    @PostConstruct
    public void init() {
        setMap(dbManager.makeMap("send"));
    }

}
