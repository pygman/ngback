package com.pygman.ngback.host.service;

import org.springframework.stereotype.Service;

/**
 * IndexService
 * Created by pygman on 16-10-14.
 */
@Service
public class IndexService {

    public String index(String str){
        return str + str;
    }

}
