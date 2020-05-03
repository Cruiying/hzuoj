package com.hqz.hzuoj.admin.controller;

import com.hqz.hzuoj.annotations.AdminLoginCheck;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/21 12:43
 * @Description: TODO
 */
@Controller
@AdminLoginCheck
@RequestMapping("/admin")
public class CommonController {

    @RequestMapping("/unicode")
    public String unicode() {
        return "unicode";
    }
}
