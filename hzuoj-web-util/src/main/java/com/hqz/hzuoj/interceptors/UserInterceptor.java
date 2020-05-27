package com.hqz.hzuoj.interceptors;

import com.alibaba.fastjson.JSON;
import com.hqz.hzuoj.annotations.UserLoginCheck;
import com.hqz.hzuoj.util.CookieUtil;
import com.hqz.hzuoj.util.HttpclientUtil;
import com.hqz.hzuoj.util.Authentication;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员拦截器，判断管理员是否需要登陆
 */
@Component
public class UserInterceptor implements HandlerInterceptor {
    @Autowired
    private Authentication authentication;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 拦截代码
        // 判断被拦截的请求的访问的方法的注解(是否时需要拦截的)
        HandlerMethod hm;
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        hm = (HandlerMethod) handler;
        //获取是否包含拦截注解
        UserLoginCheck userLoginCheck = hm.getMethod().getDeclaringClass().getAnnotation(UserLoginCheck.class);
        UserLoginCheck annotation = hm.getMethod().getAnnotation(UserLoginCheck.class);

        //获取被拦截的请求地址
        StringBuffer url = request.getRequestURL();
        String token = "";
        //获取被拦截的请求中cookie中的token
        String userOldToken = CookieUtil.getCookieValue(request, "userOldToken", true);
        //cookie中是否包含token
        if (StringUtils.isNotBlank(userOldToken)) {
            token = userOldToken;
        }
        //获取请求中的token
        String userToken = request.getParameter("userToken");
        //请求中是否包含token
        if (StringUtils.isNotBlank(userToken)) {
            token = userToken;
        }
        // 调用认证中心进行验证
        String success = "fail";
        Map<String, String> successMap = authentication.authenticationToken(token, request.getRemoteAddr());
        //获取token认证的返回状态
        success = successMap.get("status");
        // 是否拦截
        if (userLoginCheck == null && annotation == null) {
            //没有请求地址没有包含拦截标志，直接放行
            if ("success".equals(success) && StringUtils.isNotBlank(token)) {
                setCookie(request, response, successMap, token);
            }
            return true;
        }
        //是否认证成功
        if (!"success".equals(success) || StringUtils.isBlank(token)) {
            // token认证失败，重定向回用户登陆界面
            StringBuffer requestURL = request.getRequestURL();
            response.sendRedirect("/user/login?ReturnUrl=" + requestURL);
            return false;
        } else {
            // token认证成功（登陆正确），保存用户登陆信息
            // 需要将token携带的管理员信息写入
            setCookie(request, response, successMap, token);
        }
        return true;
    }

    private void setCookie(HttpServletRequest request, HttpServletResponse response, Map<String, String> successMap, String token) {
        request.getSession().setAttribute("userId", successMap.get("userId"));
        request.getSession().setAttribute("username", successMap.get("username"));
        request.getSession().setAttribute("userImg", successMap.get("userImg"));
        //验证通过，覆盖cookie中的token
        if (StringUtils.isNotBlank(token)) {
            CookieUtil.setCookie(request, response, "userOldToken", token, 60 * 60 * 24 * 30, true);
        }
    }

}
