package com.pygman.ngback.client.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by pygman on 12/12/2016.
 */
@Component
public class SendStateMap extends DBManager.DBMap<Long, byte[]> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DBManager dbManager;

    @PostConstruct
    public void init() {
        setMap(dbManager.makeMap("resend"));
    }

}

