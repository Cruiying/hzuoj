package com.hqz.hzuoj.admin.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.hqz.hzuoj.annotations.AdminLoginCheck;
import com.hqz.hzuoj.base.ResultEntity;
import com.hqz.hzuoj.bean.user.Admin;
import com.hqz.hzuoj.service.AdminService;
import com.hqz.hzuoj.util.JwtUtil;
import com.hqz.hzuoj.util.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.ResultSetExtractor;
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
@Slf4j
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
    public ResultEntity login(@RequestBody Admin a, HttpServletRequest request) {
        try {
            Admin admin = adminService.loginAdmin(a);
            if (null == admin) {
                return ResultEntity.error("账号或者密码错误");
            }
            if (!"1".equals(admin.getAdminStatus().toString())) {
                return ResultEntity.error("该管理员员已经停用");
            }
            // 登录成功
            // 用jwt制作token
            Integer adminId = admin.getAdminId();
            String adminName = admin.getAdminName();
            Map<String, Object> adminMap = new HashMap<>();
            adminMap.put("adminId", adminId.toString());
            adminMap.put("adminName", adminName);
            adminMap.put("admin", admin);
            String ip = request.getRemoteAddr();// 从request中获取ip
            if (StringUtils.isBlank(ip)) {
                ip = "127.0.0.1";
            }
            // 按照设计的算法对参数进行加密后，生成token
            String token = JwtUtil.encode("hzuoj", adminMap, ip);
            return ResultEntity.success("登录成功", token);
        }catch (Exception e) {
            log.error("login({}) error message: {}", a, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
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
    @AdminLoginCheck
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
    public ResultEntity getAllAdmin(Integer page) {
        try {
            return ResultEntity.success(adminService.getAll(page));
        } catch (Exception e) {
            log.error("getAllAdmin({}) error message: {}", page, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
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
    public ResultEntity updateAdminStatus(@PathVariable Integer adminId, Integer adminStatus, HttpServletRequest request) {
        try {
            Admin admin = SessionUtils.getAdmin(request);
            System.err.println(admin);
            return ResultEntity.success("修改成功", adminService.updateAdminStatus(admin));
        } catch (Exception e) {
            log.error("updateAdminStatus({}, {}) error message: {}", adminId, adminStatus, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 添加新的管理员
     * @param admin
     * @return
     */
    @RequestMapping("/add/admin")
    @ResponseBody
    @AdminLoginCheck
    public ResultEntity addAdmin(@RequestBody Admin admin) {
        try {
            return ResultEntity.success("添加成功",adminService.addAdmin(admin));
        }catch (Exception e) {
            log.error("addAdmin({}) error message: {}", admin, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }
}
