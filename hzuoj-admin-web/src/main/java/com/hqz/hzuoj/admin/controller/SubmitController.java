package com.hqz.hzuoj.admin.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.annotations.AdminLoginCheck;
import com.hqz.hzuoj.base.ResultEntity;
import com.hqz.hzuoj.bean.problem.Problem;
import com.hqz.hzuoj.bean.submit.Submit;
import com.hqz.hzuoj.service.SubmitService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
    public ResultEntity list(Integer page) {
        try {
            return ResultEntity.success("获取成功", submitService.getSubmits(page));
        }catch (Exception e) {
            log.error("list({}) error message: {}", page, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 重新测评提交
     * @param submitId
     * @return
     */
    @RequestMapping("/restart/submit/{submitId}")
    @ResponseBody
    public ResultEntity restartSubmit(@PathVariable Integer submitId) {
        try {
            return ResultEntity.success("提交成功", submitService.restartSubmit(submitId));
        }catch (Exception e) {
            log.error("restartSubmit({}) error message: {}", submitId, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 重新测评提交
     * @return
     */
    @RequestMapping("/restart/submits")
    @ResponseBody
    public ResultEntity restartSubmits() {
        try {
            return ResultEntity.success("提交成功", submitService.restartSubmits());
        }catch (Exception e) {
            log.error("restartSubmits() error message: {}", e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

}
