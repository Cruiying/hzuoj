package com.hqz.hzuoj.util;

import com.hqz.hzuoj.bean.user.Admin;
import com.hqz.hzuoj.bean.user.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Author: HQZ
 * @CreateTime: 2020/5/3 21:30
 * @Description: TODO
 */
@Component
public class SessionUtils {

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    public static User getUser(HttpServletRequest request) {
        return getUser(request.getSession());
    }

    /**
     * 获取当前登录用户userId
     * @param request
     * @return
     */
    public static Integer getUserId(HttpServletRequest request) {
        User user = getUser(request);
        return user.getUserId();
    }

    /**
     * 获取当前登录用户
     * @param session
     * @return
     */
    public static User getUser(HttpSession session) {
        Object str = session.getAttribute("userId");
        if (str == null) {
            throw new RuntimeException("用户未登陆");
        }
        Integer userId = Integer.parseInt(str.toString());
        User user = new User();
        user.setUserId(userId);
        return user;
    }

    /**
     * 获取当前登录userId
     * @param session
     * @return
     */
    public static Integer getUserId(HttpSession session) {
        User user = getUser(session);
        return user.getUserId();
    }

    /**
     * 获取管理员信息
     * @param request
     * @return
     */
    public static Admin getAdmin(HttpServletRequest request) {
        return getAdmin(request.getSession());
    }

    /**
     * 获取管理员id
     * @param request
     * @return
     */
    public static Integer getAdminId(HttpServletRequest request) {
        return getAdmin(request).getAdminId();
    }

    /**
     * 获取管理员信息
     * @param session
     * @return
     */
    public static Admin getAdmin(HttpSession session) {
        Object str = session.getAttribute("adminId");
        if (str == null) {
            throw new RuntimeException("管理员未登录");
        }
        Integer adminId = Integer.parseInt(str.toString());
        Admin admin = new Admin();
        admin.setAdminId(adminId);
        return admin;
    }

    /**
     * 获取管理员Id
     * @param session
     * @return
     */
    public static Integer getAdminId(HttpSession session) {
        return getAdmin(session).getAdminId();
    }
}
