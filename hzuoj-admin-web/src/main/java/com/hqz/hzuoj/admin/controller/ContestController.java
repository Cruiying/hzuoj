package com.hqz.hzuoj.admin.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.annotations.AdminLoginCheck;
import com.hqz.hzuoj.base.ResultEntity;
import com.hqz.hzuoj.bean.contest.Contest;
import com.hqz.hzuoj.bean.contest.ContestProblem;
import com.hqz.hzuoj.bean.contest.ContestQuery;
import com.hqz.hzuoj.bean.contest.ContestType;
import com.hqz.hzuoj.bean.problem.Problem;
import com.hqz.hzuoj.service.ContestService;
import com.hqz.hzuoj.service.ProblemService;
import com.hqz.hzuoj.service.SubmitService;
import jdk.nashorn.internal.runtime.ECMAException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/21 13:28
 * @Description: TODO
 */
@Controller
@AdminLoginCheck
@RequestMapping("/admin")
@Slf4j
public class ContestController {

    @Reference
    private ContestService contestService;


    @Reference
    private ProblemService problemService;

    @Reference
    private SubmitService submitService;

    /**
     * 比赛添加页面
     *
     * @return
     */
    @RequestMapping("/contest")
    public String addContest(HttpServletRequest request, ModelMap modelMap, Integer contestId) {
        String adminId = (String) request.getAttribute("adminId");
        modelMap.put("adminId", adminId);
        Contest contest = new Contest();
        if (contestId != null) {
            contest = contestService.getContest(contestId);
        }
        List<ContestType> contestTypes = contestService.getContestTypes();
        modelMap.put("contestTypes", contestTypes);
        modelMap.put("contest", contest);
        modelMap.put("contestId", contestId);
        return "add-contest";
    }

    /**
     * 返回比赛的基本信息
     *
     * @param contestId
     * @return
     */
    @RequestMapping("/contest/{contestId}/info")
    @ResponseBody
    public ResultEntity getContestInfo(@PathVariable Integer contestId) {
        try {
            Contest contest = contestService.getContest(contestId);
            if (contest == null) {
                return ResultEntity.error("比赛不存在");
            }
            List<ContestProblem> contestProblems = contestService.getContestProblems(contestId, null);
            contest.setContestProblems(contestProblems);
            return ResultEntity.success("获取成功", contest);
        }catch (Exception e) {
            log.error("getContestInfo({}) error message: {}", contestId, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }


    /**
     * 全部比赛列表页面
     *
     * @return
     */
    @RequestMapping("/contests")
    public String list() {
        return "contests";
    }



    /**
     * 添加比赛题目
     *
     * @param contestId
     * @return
     */
    @RequestMapping("/contest/{contestId}/add/problem")
    @ResponseBody
    public ResultEntity addContestProblem(@PathVariable Integer contestId, @RequestBody ContestProblem contestProblem) {
        try {
            return ResultEntity.success("添加成功", contestService.addContestProblem(contestId, contestProblem));
        }catch (Exception e) {
            log.error("addContestProblem({},{}) error message: {}", contestId, contestProblem, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 比赛列表分页数据
     *
     * @param page
     * @return
     */
    @RequestMapping("/contests/{page}/list")
    @ResponseBody
    public ResultEntity contestList(@PathVariable Integer page, @RequestBody ContestQuery contestQuery) {
        try {
            return ResultEntity.success("获取成功", contestService.getAllContest(page, contestQuery));
        }catch (Exception e) {
            log.error("contestList({}, {}) error message: {}", page, contestQuery, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 添加或者修改比赛
     *
     * @param contest
     * @return
     */
    @RequestMapping("add/contest")
    @ResponseBody
    public ResultEntity addContest(@RequestBody Contest contest) {
        try {
            return ResultEntity.success("成功", contestService.saveContest(contest));
        }catch (Exception e){
            log.error("addContest({}) error message: {}", contest, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }
    /**
     * 删除比赛题目
     *
     * @param contestId
     * @param problemId
     * @return
     */
    @RequestMapping("/contest/{contestId}/del/problem")
    @ResponseBody
    public ResultEntity delContestProblem(@PathVariable Integer contestId, Integer problemId) {
        try {
            return ResultEntity.success("删除成功", contestService.delContestProblem(contestId, problemId));
        }catch (Exception e) {
            log.error("delContestProblem({}, {}) error message: {}", contestId, problemId, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 更新比赛排名
     * @param contestId
     * @return
     */
    @RequestMapping("/contest/update/rank/{contestId}")
    @ResponseBody
    public ResultEntity updateContestRank(@PathVariable Integer contestId) {
        try {
            return ResultEntity.success("更新成功",submitService.calculateContestRating(contestId));
        }catch (Exception e) {
            log.error("updateContestRank({}) error message: {}", contestId, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 比赛提交重新测评
     * @param contestId
     * @return
     */
    @RequestMapping("/contest/afresh/{contestId}")
    @ResponseBody
    public ResultEntity contestAfreshSubmit(@PathVariable Integer contestId) {
        try {
            return contestService.contestAfreshSubmit(contestId);
        }catch (Exception e) {
            return ResultEntity.error("提交失败");
        }
    }

    /**
     * 比赛删除
     * @param contestId
     * @return
     */
    @ResponseBody
    @RequestMapping("/contest/delete/{contestId}")
    public ResultEntity contestDelete(@PathVariable Integer contestId) {
        try {
            return ResultEntity.success("删除成功", contestService.contestDelete(contestId));
        } catch (Exception e) {
            log.error("error", e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 修改比赛榜单状态
     * @param contestId
     * @return
     */
    @RequestMapping(value = "/contest/update/rank/final/{contestId}")
    @ResponseBody
    public ResultEntity contestUpdateRankFinal(@PathVariable Integer contestId) {
        try {
            return ResultEntity.success("修改成功", contestService.contestUpdateRankFinal(contestId));
        }catch (Exception e) {
            log.error("contestUpdateRankFinal({}) Error message: {}", contestId, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }
}
