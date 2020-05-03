package com.hqz.hzuoj.admin.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.annotations.AdminLoginCheck;
import com.hqz.hzuoj.bean.solution.Solution;
import com.hqz.hzuoj.bean.solution.SolutionStatus;
import com.hqz.hzuoj.service.SolutionService;
import com.hqz.hzuoj.util.MarkdownUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: HQZ
 * @CreateTime: 2020/2/20 18:34
 * @Description: TODO
 */
@Controller
@RequestMapping("/admin")
@AdminLoginCheck
public class SolutionController {

    @Reference
    private SolutionService solutionService;

    /**
     * 题解列表页面
     *
     * @return
     */
    @RequestMapping("/solutions")
    public String solutions() {
        return "solutions";
    }

    /**
     * 获取题解列表
     *
     * @param page
     * @return
     */
    @RequestMapping("/solutions/list")
    @ResponseBody
    public PageInfo<Solution> getSolution(Integer page) {
        PageInfo<Solution> pageInfo = solutionService.getSolutions(page);
        List<Solution> list = pageInfo.getList();
        return pageInfo;
    }

    /**
     * 题解页面
     *
     * @param solutionId
     * @param modelMap
     * @return
     */
    @RequestMapping("/solution")
    public String solution(Integer solutionId, ModelMap modelMap) {
        Solution solution = solutionService.getSolution(solutionId);
        List<SolutionStatus> solutionStatus = solutionService.getSolutionStatus();
        solution.setSolutionContent(MarkdownUtils.markdownToHtml(solution.getSolutionContent()));
        modelMap.put("solution", solution);
        modelMap.put("solutionStatus",solutionStatus);
        return "solution";
    }

    /**
     * 删除题解
     * @param solutionId
     * @return
     */
    @RequestMapping("/delete/solution/{solutionId}")
    @ResponseBody
    private String deleteSolution(@PathVariable Integer solutionId) {
        boolean flag = solutionService.deleteSolution(solutionId);
        Map<String, Boolean> map = new HashMap<>();
        map.put("flag", flag);
        return JSON.toJSONString(map);
    }

    @RequestMapping("/solution/status")
    @ResponseBody
    private Solution solutionStatus(@RequestBody Solution solution) {
        System.out.println(solution);
        return solutionService.updateSolutionStatus(solution);
    }

}
