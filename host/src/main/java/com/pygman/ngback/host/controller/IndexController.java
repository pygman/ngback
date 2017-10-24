package com.pygman.ngback.host.controller;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/**")
    public Map index(HttpServletRequest request){
        String path = request.getServletPath();
        Map<String, String[]> parameterMap = request.getParameterMap();

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("path",path);
        resultMap.put("para",parameterMap);

        logger.info("info:"+resultMap);
        logger.error("error:"+resultMap);

        return resultMap;
    }

}
