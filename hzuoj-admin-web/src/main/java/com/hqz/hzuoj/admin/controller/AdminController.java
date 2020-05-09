package com.hqz.hzuoj.admin.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.hqz.hzuoj.annotations.AdminLoginCheck;
import com.hqz.hzuoj.bean.user.Admin;
import com.hqz.hzuoj.service.AdminService;
import com.hqz.hzuoj.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/12 11:53
 * @Description: TODO
 */
@Controller
@RequestMapping("/admin")
public class AdminController {


    @Reference
    private AdminService adminService;

    /**
     * 管理员登陆界面
     * @return
     */
    @RequestMapping("")
    public String admin(String ReturnUrl, ModelMap modelMap) {
        modelMap.put("ReturnUrl", ReturnUrl);
        return "login";
    }

    @RequestMapping("/login")
    @ResponseBody
    public String login(@RequestBody Admin a, HttpServletRequest request) {
        String token = "111";
        // 调用用户服务验证用户名和密码
        Admin admin = adminService.loginAdmin(a);
        //用户登陆成功，才进行token日志
        if (admin != null && admin.getAdminStatus()== 1) {
            // 登录成功
            // 用jwt制作token
            Integer adminId = admin.getAdminId();
            String adminName = admin.getAdminName();
            Map<String, Object> adminMap = new HashMap<>();
            adminMap.put("adminId", adminId.toString());
            adminMap.put("adminName", adminName);
            String ip = request.getRemoteAddr();// 从request中获取ip
            if (StringUtils.isBlank(ip)) {
                ip = "127.0.0.1";
            }
            // 按照设计的算法对参数进行加密后，生成token
            token = JwtUtil.encode("hzuoj", adminMap, ip);
            // 将token存入redis一份
//            userService.addUserToken(token, memberId);
        } else {
            // 登录失败
            token = "fail";
//            System.out.println("登陆失败");
        }
        return token;
    }


    /**
     * 首页
     *
     * @return
     */
    @AdminLoginCheck
    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    /**
     * 欢迎界面
     *
     * @return
     */
    @RequestMapping("/welcome")
    public String welcome(HttpServletRequest request, ModelMap modelMap) {
        modelMap.put("adminName", request.getAttribute("adminName"));
        return "welcome";
    }

    /**
     * 管理员列表界面
     * @return
     */
    @AdminLoginCheck
    @RequestMapping("/list")
    public String list() {
        return "admin-list";
    }

    /**
     * 返回管理员列表
     * @param page
     * @return
     */
    @RequestMapping("/admins/list")
    @ResponseBody
    @AdminLoginCheck
    public Map<String, Object> getAllAdmin(Integer page) {
        if (page == null) {
            page = 1;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("pageInfo", adminService.getAll(page));
        return map;
    }

    /**
     * 修改管理员状态
     * @param adminId 待修改的管理员
     * @param adminStatus 待修改的状态
     * @param request
     * @return
     */
    @AdminLoginCheck
    @RequestMapping("/update/{adminId}/status")
    @ResponseBody
    public Map<String, Object> updateAdminStatus(@PathVariable Integer adminId, Integer adminStatus, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String loginId = (String) request.getAttribute("adminId");
        Integer loginAdmin = Integer.parseInt(loginId);
        if (loginAdmin == 1 && adminId != 1) {
            map.put("msg", "修成功");
            map.put("flag", true);
        } else if (loginAdmin == 1 && adminId == 1) {
            map.put("msg", "修改失败，您是超级管理员，不可以修改自己");
            map.put("flag", false);
        } else {
            map.put("msg", "修改失败，您没有权限！！！需要超级管理员才可以修改");
            map.put("flag", false);
        }
        return map;
    }
}
