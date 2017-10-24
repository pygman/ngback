package com.pygman.ngback.client.actor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * ActorDispatcher
 * Created by pygman on 12/8/2016.
 */
@Component
public class ActorDispatcher {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProxyActor proxyActor;

    @Autowired
    SendActor sendActor;

    @PostConstruct
    public void init() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        proxyActor.mailEventLoop();
        sendActor.mailEventLoop();
    }

}
