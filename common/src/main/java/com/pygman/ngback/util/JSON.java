package com.pygman.ngback.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * jackson
 * Created by pygman on 16-10-17.
 */
@Component
public class JSON {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String toJSON(Object map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public Map toMap(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    public Map toMap(Object object) {
        return toMap((String) object);
    }
}
