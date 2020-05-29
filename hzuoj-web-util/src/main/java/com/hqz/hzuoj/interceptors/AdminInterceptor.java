package com.hqz.hzuoj.interceptors;

import com.alibaba.fastjson.JSON;
import com.hqz.hzuoj.annotations.AdminLoginCheck;
import com.hqz.hzuoj.annotations.LoginWeb;
import com.hqz.hzuoj.annotations.UserLoginCheck;
import com.hqz.hzuoj.util.Authentication;
import com.hqz.hzuoj.util.CookieUtil;
import com.hqz.hzuoj.util.HttpclientUtil;
import com.hqz.hzuoj.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员拦截器，判断管理员是否需要登陆
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Autowired
    private Authentication authentication;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 拦截代码
        // 判断被拦截的请求的访问的方法的注解(是否时需要拦截的)
        HandlerMethod hm;
        if (! (handler instanceof  HandlerMethod)) {
            return true;
        }
        hm = (HandlerMethod) handler;
        //获取是否包含拦截注解
        AdminLoginCheck adminLoginCheck = hm.getMethod().getDeclaringClass().getAnnotation(AdminLoginCheck.class);
        AdminLoginCheck annotation = hm.getMethod().getAnnotation(AdminLoginCheck.class);
        //获取被拦截的请求地址
        StringBuffer url = request.getRequestURL();
        String token = "";
        //获取被拦截的请求中cookie中的token
        String adminOldToken = CookieUtil.getCookieValue(request, "adminOldToken", true);
        //cookie中是否包含token
        if (StringUtils.isNotBlank(adminOldToken)) {
            token = adminOldToken;
        }
        //获取请求中的token
        String adminToken = request.getParameter("adminToken");
        //请求中是否包含token
        if (StringUtils.isNotBlank(adminToken)) {
            token = adminToken;
        }
        // 调用认证中心进行验证
        String success = "fail";
        Map<String, String> successMap = authentication.authenticationToken(token, request.getRemoteAddr());
        //获取token认证的返回状态
        success = successMap.get("status");
        // 是否拦截
        if (adminLoginCheck == null && annotation == null) {
            //没有请求地址没有包含拦截标志，直接放行
            if ("success".equals(success) && StringUtils.isNotBlank(token)) {
                request.getSession().setAttribute("adminId", successMap.get("adminId"));
                request.getSession().setAttribute("adminName", successMap.get("adminName"));
                //验证通过，覆盖cookie中的token
                if (StringUtils.isNotBlank(token)) {
                    CookieUtil.setCookie(request, response, "adminOldToken", token, 60 * 60 * 30, true);
                }
            }
            return true;
        }
        //是否认证成功
        if (!"success".equals(success) || StringUtils.isBlank(token)) {
            // token认证失败，重定向回用户登陆界面
            StringBuffer requestURL = request.getRequestURL();
            response.sendRedirect("/admin?ReturnUrl=" + requestURL);
            return false;
        } else  {
            // token认证成功（登陆正确），保存用户登陆信息
            // 需要将token携带的管理员信息写入
            request.getSession().setAttribute("adminId", successMap.get("adminId"));
            request.getSession().setAttribute("adminName", successMap.get("adminName"));
            //验证通过，覆盖cookie中的token
            if (StringUtils.isNotBlank(token)) {
                CookieUtil.setCookie(request, response, "adminOldToken", token, 60 * 60 * 2, true);
            }
        }
        return true;
    }
}
