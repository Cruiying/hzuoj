package com.hqz.hzuoj.admin.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.annotations.AdminLoginCheck;
import com.hqz.hzuoj.bean.contest.Contest;
import com.hqz.hzuoj.bean.contest.ContestProblem;
import com.hqz.hzuoj.bean.contest.ContestQuery;
import com.hqz.hzuoj.bean.contest.ContestType;
import com.hqz.hzuoj.bean.problem.Problem;
import com.hqz.hzuoj.service.ContestService;
import com.hqz.hzuoj.service.ProblemService;
import com.hqz.hzuoj.service.SubmitService;
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
     * @param modelMap
     * @return
     */
    @RequestMapping("/contest/{contestId}/info")
    @ResponseBody
    public Map<String, Object> getContestInfo(ModelMap modelMap, @PathVariable Integer contestId, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Contest contest = contestService.getContest(contestId);
        List<ContestProblem> contestProblems = contestService.getContestProblems(contestId, null);
        if (contest != null) {
            modelMap.put("adminId", contest.getAdmin().getAdminId());
            contest.setContestProblems(contestProblems);
        } else {
            modelMap.put("adminId", request.getAttribute("adminId"));
        }

        map.put("contest", contest);
        modelMap.put("contest", contest);
        return map;
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
     * 比赛列表分页数据
     *
     * @param page
     * @return
     */
    @RequestMapping("/contests/{page}/list")
    @ResponseBody
    public Map<String, Object> contestList(@PathVariable Integer page, @RequestBody ContestQuery contestQuery) {
        if (page == null) {
            page = 1;
        }
        Map<String, Object> map = new HashMap<>();
        PageInfo<Contest> pageInfo = contestService.getAllContest(page, contestQuery);
        map.put("pageInfo", pageInfo);
        return map;
    }

    /**
     * 添加或者修改比赛
     *
     * @param contest
     * @return
     */
    @RequestMapping("add/contest")
    @ResponseBody
    public Map<String, Object> addContest(@RequestBody Contest contest) {
        Map<String, Object> map = new HashMap<>();
        if (contest == null) {
            map.put("msg", "比赛创建失败");
            map.put("flag", false);
            return map;
        }
        if (StringUtils.isBlank(contest.getContestName())) {
            map.put("msg", "比赛创建失败！！！必须写入比赛名称");
            map.put("flag", false);
            return map;
        }
        Date s = contest.getContestStart();
        Date e = contest.getContestEnd();
        Date as = contest.getContestApplyStartTime();
        Date ae = contest.getContestApplyEndTime();
        if (s == null || e == null || as == null || ae == null) {
            map.put("msg", "比赛创建失败！！！必须写入日期");
            map.put("flag", false);
            return map;
        }
        long contestLength = e.getTime() - s.getTime();
        long applyLength = ae.getTime() - as.getTime();
        if (contestLength < 1080000) {
            //比赛的进行必须大于等于30分钟
            map.put("msg", "比赛创建失败！！！比赛时长必须大于30分钟");
            map.put("flag", false);
            return map;
        }
        if (applyLength < 1080000) {
            //比赛报名时长时间必须大于等于30分钟
            map.put("msg", "比赛创建失败！！！比赛报名时长必须大于30分钟");
            map.put("flag", false);
            return map;
        }
        if (contest.getContestId() == null) {
            //新创建的比赛，需要生成验证码
            String code = UUID.randomUUID().toString().substring(0, 5);
            contest.setContestCode(code);
            //比赛时长=比赛结束时间-比赛开始时间
            contest.setContestTimeLength((e.getTime() - s.getTime()));
            //比赛创建时间
            contest.setContestCreateTime(new Date());
            //比赛状态
            contest.setContestStatus(0);
            //比赛报名状态
            contest.setContestApplyStatus(0);
            Contest c = contestService.saveContest(contest);
            c = contestService.getContest(c.getContestId());
            map.put("msg", "比赛添加成功");
            map.put("contest", c);
            map.put("flag", true);
        } else {
            Contest c = contestService.saveContest(contest);
            c = contestService.getContest(c.getContestId());
            map.put("contest", c);
            map.put("msg", "比赛修改成功");
            map.put("flag", true);
        }
        return map;
    }


    /**
     * 添加比赛题目
     *
     * @param contestId
     * @return
     */
    @RequestMapping("/contest/{contestId}/add/problem")
    @ResponseBody
    public Map<String, Object> addContestProblem(@PathVariable Integer contestId, @RequestBody ContestProblem contestProblem) {
        Map<String, Object> map = new HashMap<>();
        if (contestProblem == null || contestProblem.getContest() == null
                || contestProblem.getProblem() == null || contestProblem.getContest().getContestId() == null
                || contestProblem.getProblem().getProblemId() == null || contestProblem.getContestProblemScore() == null
                || contestProblem.getContestProblemScore() <= 0) {
            map.put("msg", "题目添加失败");
            map.put("flag", false);
            return map;
        }
        //查询比赛比赛状态
        Problem problem = problemService.getProblem(contestProblem.getProblem().getProblemId());
        if (problem == null) {
            //判断题目是否成功
            map.put("msg", "添加失败！！！题目不存在");
            map.put("flag", false);
            return map;
        }
        ContestProblem contestProblem1 = contestService.getContestProblem(contestProblem);
        if (contestProblem1 != null) {
            map.put("msg", "添加失败！！！比赛题目已经存在");
            map.put("flag", false);
            return map;
        }
        int contestProblemScore = contestProblem.getContestProblemScore();
        if(contestProblemScore <= 0) {
            map.put("msg", "添加失败！！比赛题目分数必须大于0");
            map.put("flag", false);
            return map;
        }
        ContestProblem saveContestProblem = contestService.saveContestProblem(contestProblem);
        saveContestProblem.setProblem(problem);
        map.put("contestProblem", saveContestProblem);
        Contest contest = contestService.getContest(contestId);
        map.put("contest", contest);
        map.put("msg", "添加成功");
        map.put("flag", true);
        return map;
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
    public Map<String, Object> delContestProblem(@PathVariable Integer contestId, Integer problemId) {
        Map<String, Object> map = new HashMap<>();
        if (problemId == null) {
            map.put("msg", "删除失败");
            map.put("flag", true);
            return map;
        }
        String result = contestService.delContestProblem(contestId, problemId);
        if ("success".equals(result)) {
            map.put("msg", "删除成功");
            map.put("flag", true);
        } else {
            map.put("msg", "删除失败");
            map.put("flag", false);
        }
        return map;
    }

    @RequestMapping("/contest/update/rank/{contestId}")
    @ResponseBody
    public String updateContestRank(@PathVariable Integer contestId) {
        if (contestId == null) {
            return JSON.toJSONString("fail");
        }
        Contest contest = contestService.getContest(contestId);
        if (contest == null) {
            return JSON.toJSONString("fail");
        }
        if (contest.getContestIsRank() == null || contest.getContestIsRank() != 1) {
            return JSON.toJSONString("fail");
        }
        return JSON.toJSONString(submitService.calculateContestRating(contest));
    }

}
