package com.pygman.ngback.monitor.controller;

import com.pygman.ngback.monitor.core.ClientTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by aydxx on 2017/1/6.
 */
@RestController
@RequestMapping(value = "/base/")
public class BaseController {


    @Autowired
    private ClientTable clientTable;


    /**
     * 获取所有在线的client
     * @return
     */
    @RequestMapping(value = "/getClients")
    public String getClients(){
        return clientTable.getClients();
    }
}
