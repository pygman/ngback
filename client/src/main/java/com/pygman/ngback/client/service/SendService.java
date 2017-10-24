package com.pygman.ngback.client.service;

import com.pygman.ngback.struct.Message;

/**
 * 发送
 * Created by pygman on 12/6/2016.
 */
public interface SendService {

    Message send(Message message);

}
