package com.pygman.ngback.core;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by pygman on 12/12/2016.
 */
@Component
public class TaskExecutor {

    ThreadPoolTaskExecutor poolTaskExecutor;

    @PostConstruct
    public void init() {
        poolTaskExecutor = new ThreadPoolTaskExecutor();
        //线程池所使用的缓冲队列
        poolTaskExecutor.setQueueCapacity(200);
        //线程池维护线程的最少数量
        poolTaskExecutor.setCorePoolSize(5);
        //线程池维护线程的最大数量
        poolTaskExecutor.setMaxPoolSize(1000);
        //线程池维护线程所允许的空闲时间
        poolTaskExecutor.setKeepAliveSeconds(30000);
        poolTaskExecutor.initialize();
    }

    public void execute(Runnable runnable){
        poolTaskExecutor.execute(runnable);
    }

}
