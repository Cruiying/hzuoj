package com.hqz.hzuoj.admin.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.annotations.AdminLoginCheck;
import com.hqz.hzuoj.bean.problem.Problem;
import com.hqz.hzuoj.bean.submit.Submit;
import com.hqz.hzuoj.service.SubmitService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: HQZ
 * @CreateTime: 2020/2/26 21:14
 * @Description: TODO
 */
@Controller
@RequestMapping("/admin")
@AdminLoginCheck
public class SubmitController {

    @Reference
    private SubmitService submitService;

    /**
     * 测评记录列表页面
     * @return
     */
    @RequestMapping("/submits")
    public String submits() {
        return "submits";
    }

    /**
     * 测评记录列表
     *
     * @param page
     * @return
     */
    @RequestMapping("/submits/list")
    @ResponseBody
    public Map<String, Object> list(Integer page) {
        Map<String, Object> map = new HashMap<>();
        PageInfo<Submit> pageInfo = submitService.getSubmits(page);
        map.put("pageInfo", pageInfo);
        return map;
    }

    /**
     * 重新测评提交
     * @param submitId
     * @return
     */
    @RequestMapping("/restart/submit/{submitId}")
    @ResponseBody
    public Map<String, Object> restartSubmit(@PathVariable Integer submitId) {
        Map<String,Object> map = new HashMap<>();
        map.put("msg", submitService.restartSubmit(submitId));
        System.out.println(map);
        return map;
    }

    /**
     * 重新测评提交
     * @return
     */
    @RequestMapping("/restart/submits")
    @ResponseBody
    public Map<String, Object> restartSubmits() {
        Map<String,Object> map = new HashMap<>();
        map.put("msg", submitService.restartSubmits());
        return map;
    }

}
