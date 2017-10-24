package com.pygman.idle;

import com.pygman.ngback.logger.SpecialLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 心跳日志
 * Created by pygman on 16-11-25.
 */
@Component
public class Idle extends SpecialLogger {
    private Logger logger;
    private String clazzName;

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public Idle() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    public Idle(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
        setClazzName(clazz.getSimpleName());
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    public static Logger getLogger(Class<?> clazz) {
        Idle idle = new Idle();
        idle.setClazzName(clazz.getSimpleName());
        return idle;
    }

    @Override
    public void info(String s) {
        super.info(":=> " + clazzName + " - " + s);
    }
}
