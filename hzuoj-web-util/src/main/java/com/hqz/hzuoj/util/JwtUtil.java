package com.hqz.hzuoj.util;

import io.jsonwebtoken.*;

import java.util.Map;

/**
 * jwt工具类
 */
public class JwtUtil {
    /**
     * jwt加密 返回一个token信息
     *
     * @param key   服务器公共key（服务器的密钥）---->    服务器
     * @param param 用户的基本信息
     * @param salt  盐值（用户注册时，系统用来和用户密码进行组合而生成的随机数值）------> 浏览器（ip，time）
     * @return
     */
    public static String encode(String key, Map<String, Object> param, String salt) {
        if (salt != null) {
            key += salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256, key);

        jwtBuilder = jwtBuilder.setClaims(param);

        String token = jwtBuilder.compact();
        return token;

    }

    /**
     * jwt解密 将token解密
     *
     * @param token 解码token
     * @param key  服务器公共key（服务器的密钥）---->    服务器
     * @param salt 盐值（用户注册时，系统用来和用户密码进行组合而生成的随机数值）------> 浏览器（ip，time）
     * @return 解密成功返回解密信息，否则返回空
     */
    public static Map<String, Object> decode(String token, String key, String salt) {
        Claims claims = null;
        if (salt != null) {
            key += salt;
        }
        try {
            claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            return null;
        }
        return claims;
    }

}
