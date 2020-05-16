package com.hqz.hzuoj.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: HQZ
 * @CreateTime: 2020/5/9 13:38
 * @Description: TODO
 */
@Controller
public class ViewController {

    /**
     * 管理员登陆界面
     * @return
     */
    @RequestMapping("")
    public String admin(String ReturnUrl, ModelMap modelMap) {
        modelMap.put("ReturnUrl", ReturnUrl);
        return "login";
    }
}
