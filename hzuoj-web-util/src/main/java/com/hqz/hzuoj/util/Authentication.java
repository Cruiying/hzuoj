package com.hqz.hzuoj.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/29 21:48
 * @Description: TODO
 */
@Component
public class Authentication {
    public String verify(String token, String currentIp) {
        if (StringUtils.isBlank(token)) {
            token = "0000";
        }
        if (StringUtils.isBlank(currentIp)) {
            currentIp = "127.0.0.1";
        }
        //通jwt校验token真假
        Map<String, String> map = new HashMap<>();
        //对token进行解码
        Map<String, Object> decode = JwtUtil.decode(token, "hzuoj", currentIp);
        if (decode != null) {
            //如果解码成功
            map.put("status", "success");
            if (decode.get("adminId") != null) {
                map.put("adminId", (String) decode.get("adminId"));
            }
            if (decode.get("adminName") != null) {
                map.put("adminName", (String) decode.get("adminName"));
            }
            if (decode.get("userId") != null) {
                map.put("userId", (String)decode.get("userId"));
            }
            if (decode.get("username") != null) {
                map.put("username", (String) decode.get("username"));
            }
            if (decode.get("userImg") != null) {
                map.put("userImg", (String) decode.get("userImg"));
            }
        } else {
            //如果解码失败
            map.put("status", "fail");
        }
        return JSON.toJSONString(map);
    }
    public Map<String,String> authenticationToken(String token, String currentIp) {
        Map<String, String> successMap = new HashMap<>();
        //进行token认证（认证成功返回：用户信息和success，错误返回fail）
        String successJson = verify(token,currentIp);
        successMap = JSON.parseObject(successJson, Map.class);
        return successMap;
    }
}
