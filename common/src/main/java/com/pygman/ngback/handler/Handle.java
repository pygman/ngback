package com.pygman.ngback.handler;

import com.pygman.ngback.constants.MessageType;

import java.lang.annotation.*;

/**
 * 可接受的消息类型
 * Created by pygman on 16-11-7.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Handle {
    MessageType value();
}
