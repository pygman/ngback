package com.pygman.ngback.util;

import com.pygman.ngback.struct.Pair;
import org.springframework.stereotype.Component;

/**
 * 解析ip
 * Created by pygman on 16-10-27.
 */
@Component
public class IPParser {

    public Pair<String, Integer> parse(String reg) throws Exception{
        if (reg == null) throw new Exception("reg未设置");
        int lastSlash = reg.lastIndexOf("//");
        if (lastSlash > -1) {
            reg = reg.substring(lastSlash + 2);
        }
        int lastColon = reg.lastIndexOf(":");
        String addr;
        Integer port = 80;
        if (lastColon > -1) {
            addr = reg.substring(0, lastColon);
            if (lastColon + 1 < reg.length()) {
                try {
                    port = Integer.parseInt(reg.substring(lastColon + 1));
                    if (port < 1 || port > 65535) throw new Exception("端口设置异常");
                } catch (NumberFormatException e) {
                    throw new Exception("端口设置异常");
                }
            }
        } else {
            addr = reg;
        }

        return new Pair<>(addr, port);
    }
}
