package com.hqz.hzuoj.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.annotations.UserLoginCheck;
import com.hqz.hzuoj.base.ResultEntity;
import com.hqz.hzuoj.bean.contest.*;
import com.hqz.hzuoj.bean.language.Language;
import com.hqz.hzuoj.bean.problem.Problem;
import com.hqz.hzuoj.bean.submit.Submit;
import com.hqz.hzuoj.bean.submit.SubmitQuery;
import com.hqz.hzuoj.bean.submit.TestCode;
import com.hqz.hzuoj.bean.user.RankingQuery;
import com.hqz.hzuoj.bean.user.User;
import com.hqz.hzuoj.service.ContestService;
import com.hqz.hzuoj.service.LanguageService;
import com.hqz.hzuoj.service.SubmitService;
import com.hqz.hzuoj.service.UserService;
import com.hqz.hzuoj.user.mq.MessageSender;
import com.hqz.hzuoj.user.mq.SubmitResultMessageListener;
import com.hqz.hzuoj.util.MarkdownUtils;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.*;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/10 18:33
 * @Description: TODO
 */
@Controller
@CrossOrigin
@Slf4j
public class ContestController {

    @Reference
    private ContestService contestService;

    @Reference
    private LanguageService languageService;
    @Autowired
    private SubmitResultMessageListener submitResultMessageListener;

    @Reference
    private SubmitService submitService;
    @Reference
    private UserService userService;

    @Autowired
    private MessageSender sender;
    @Value("${testResult}")
    private String testResult;

    @Value("${submitResult}")
    private String submitResult;

    @Value("${submitQueue}")
    private String submitQueue;

    @Value("${testQueue}")
    private String testQueue;


    /**
     * 比赛页面
     *
     * @return
     */
    @RequestMapping("/contests/{contestId}")
    public String contest(@PathVariable Integer contestId, HttpServletRequest request, ModelMap modelMap, SubmitQuery submitQuery, ContestRankQuery contestRankQuery) throws NotFoundException, ParseException {
        modelMap.put("now", new Date());
        if (submitQuery == null) {
            submitQuery = new SubmitQuery();
        }
        if (contestRankQuery == null) {
            contestRankQuery = new ContestRankQuery();
        }
        modelMap.put("submitQuery", submitQuery);
        modelMap.put("contestRankQuery", contestRankQuery);
        Contest contest = contestService.getContest(contestId);
        if (contest == null) {
            return "404";
        }
        contest.setContestProblems(null);

        String str = (String) request.getSession().getAttribute("userId");
        if (str == null) {
            return "redirect:/user/login?ReturnUrl=/contests/" + contestId;
        }
        Integer userId = Integer.parseInt(str);
        List<ContestProblem> contestProblem = contestService.getContestProblems(contestId, userId);

        ContestApply contestApply = contestService.getContestApplyUser(contestId, userId);
        modelMap.put("contestApply", contestApply);
        if (contest.getContestStatus() == 0) {
            contest.setContestProblems(null);
            modelMap.put("contest", contest);
        } else if (contest.getContestStatus() == 1) {
            if (contestApply == null) {

            } else {
                contest.setContestProblems(contestProblem);
            }
            modelMap.put("contest", contest);
        } else {
            contest.setContestProblems(contestProblem);
            modelMap.put("contest", contest);
        }
        return "contest";
    }

    /**
     * 比赛列表页面
     *
     * @return
     */
    @RequestMapping("/contests")
    public String contests(Integer page, ModelMap modelMap, HttpServletRequest request, ContestQuery contestQuery) {
        if (contestQuery == null) {
            contestQuery = new ContestQuery();
        }
        modelMap.put("contestQuery", contestQuery);
        String str = (String) request.getSession().getAttribute("userId");
        if (str == null) {
            contestQuery.setUserId(null);
        } else {
            Integer userId = Integer.parseInt(str);
            contestQuery.setUserId(userId);
        }
        PageInfo<Contest> pageInfo = contestService.getAllContest(page, contestQuery);
        List<ContestType> contestTypes = contestService.getContestTypes();
        List<ContestApply> contestApplys = new ArrayList<>();
        if (str != null) {
            List<Contest> contests = pageInfo.getList();
            Integer userId = Integer.parseInt(str);
            if (contests != null) {
                for (Contest contest : contests) {
                    contestApplys.add(contestService.getContestApplyUser(contest.getContestId(), userId));
                }
            }
        }
        modelMap.put("page", JSON.toJSONString(pageInfo));
        modelMap.put("pageInfo", pageInfo);
        modelMap.put("contestApplys", contestApplys);
        //modelMap.put("rankList", userService.getRanks(1, new RankingQuery()));
        modelMap.put("contestQuery", contestQuery);
        modelMap.put("contestTypes", contestTypes);
        return "contests";
    }

    @RequestMapping("/user/rank/list")
    @ResponseBody
    public ResultEntity getRankList() {
        try {
            return ResultEntity.success("获取成功", userService.getRanks(1, new RankingQuery()));
        } catch (Exception e) {
            log.error("Error", e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 获取比赛列表
     *
     * @param contestQuery
     * @return
     */
    @RequestMapping("/contests/info")
    @ResponseBody
    public PageInfo<Integer> getContests(ContestQuery contestQuery) {
        return null;
    }

    /**
     * 获取比赛类型
     *
     * @return
     */
    @RequestMapping("/contest/types")
    @ResponseBody
    private List<ContestType> getContestTypes() {
        return null;
    }

    /**
     * 报名比赛
     *
     * @param contestId
     * @param contestCode
     * @return
     */
    @RequestMapping("/apply/contest/{contestId}")
    @ResponseBody
    @UserLoginCheck
    public String ApplyContest(@PathVariable Integer contestId, String contestCode, HttpServletRequest request) {
        String str = (String) request.getSession().getAttribute("userId");
        if (str == null) {
            return "fail";
        }
        Integer userId = Integer.parseInt(str);
        ContestApply contestApply = new ContestApply();
        contestApply.setApplyTime(new Date());
        contestApply.setContestId(contestId);
        contestApply.setUserId(userId);

        ContestApply saveContestApply = contestService.ApplyContest(contestApply, contestCode);
        if (saveContestApply == null) {
            return "fail";
        }
        return "success";
    }

    /**
     * 获取比赛题目
     *
     * @param contestId
     * @param problemId
     * @param modelMap
     * @return
     */

    @RequestMapping("/contest/{contestId}/{problemId}")
    @UserLoginCheck
    public String ContestProblem(@PathVariable Integer contestId, @PathVariable Integer problemId, ModelMap modelMap) {
        Contest contest = contestService.getContest(contestId);
        if (null != contest && contest.getContestStatus() != 0) {
            ContestProblem contestProblem = contestService.getContestProblem(contestId, problemId);
            if (contestProblem == null) {
                return "404";
            }
            List<Language> languages = languageService.getLanguages();
            if (languages != null) {
                for (int i = 0; i < languages.size(); i++) {
                    Language language = languages.get(i);
                    language.setLanguageCompileCmd("");
                    language.setLanguageRuntimeCmd("");
                }
                modelMap.put("languages", languages);
            }
            Problem problem = contestProblem.getProblem();
            if (problem == null) {
                return "404";
            }
            contestProblem.setProblem(getProblem(contestProblem.getProblem()));
            modelMap.put("contestId", contestId);
            modelMap.put("problemId", problemId);
            modelMap.put("problem", contestProblem.getProblem());
        }
        if (null == contest) {
            return "404";
        }
        modelMap.put("testCode", new TestCode());
        return "problem";
    }

    private Problem getProblem(Problem problem) {
        String problemContent = problem.getProblemContent();
        String problemBackground = problem.getProblemBackground();
        String problemExplain = problem.getProblemExplain();
        problem.setProblemContent(MarkdownUtils.markdownToHtml(problemContent));
        problem.setProblemBackground(MarkdownUtils.markdownToHtml(problemBackground));
        problem.setProblemExplain(MarkdownUtils.markdownToHtml(problemExplain));
        return problem;
    }

    /**
     * 比赛题目提交
     *
     * @param contestId
     * @param submit
     * @param request
     * @param response
     * @return
     * @throws NotFoundException
     */
    @RequestMapping("/contest/submit/{contestId}")
    @ResponseBody
    @UserLoginCheck
    public String submitContest(@PathVariable Integer contestId, @RequestBody Submit submit, HttpServletRequest request, HttpServletResponse response) throws NotFoundException {

        if (submit == null || contestId == null || submit.getProblem() == null || submit.getProblem().getProblemId() == null) {
            return null;
        }
        Contest contest = contestService.getContest(contestId);
        if (contest == null) {
            return null;
        }
        ContestProblem contestProblem = contestService.getContestProblem(contestId, submit.getProblem().getProblemId());
        if (contestProblem == null) {
            return null;
        }
        User user = new User();
        String userId = (String) request.getSession().getAttribute("userId");
        user.setUserId(Integer.parseInt(userId));
        submit.setUser(user);
        submit.setSubmitPublic(0); //比赛提交
        submit.setSubmitTime(new Date());

        submit.setSubmitCodeLength(submit.getSubmitCode().length());
        ContestSubmit contestSubmit = new ContestSubmit();
        contestSubmit.setContest(contest);
        contestSubmit.setSubmit(submit);
        ContestSubmit saveContestSubmit = submitService.saveContestSubmit(contestSubmit);
        if (saveContestSubmit == null) {
            throw new RuntimeException("");
        }
        Map<String, Object> saveMap = new HashMap<>();
        saveMap.put("submit", saveContestSubmit.getSubmit());
        Map<String, Object> map = new HashMap<>();
        map.put("submitId", saveContestSubmit.getSubmit().getSubmitId());
        sender.sendQueue(submitQueue, map);
        return saveContestSubmit.getSubmit().getSubmitId().toString();
    }

    /**
     * 获取比赛提交历史提交信息（数据库中查询）
     *
     * @param submitId
     * @return
     */
    @RequestMapping("/contest/submit/{contestId}/{submitId}")
    @UserLoginCheck
    public String getSubmit(@PathVariable Integer contestId, @PathVariable Integer submitId, ModelMap modelMap, HttpServletRequest request) {
        Contest contest = contestService.getContest(contestId);
        if (contest == null) {
            throw new RuntimeException("该提交不存在");
        }
        ContestSubmit contestSubmit = new ContestSubmit();
        Submit submit = new Submit();
        submit.setSubmitId(submitId);
        contestSubmit.setSubmit(submit);
        contestSubmit.setContest(contest);
        ContestSubmit newContestSubmit = submitService.getContestSubmit(contestSubmit);
        if (newContestSubmit == null) {
            throw new RuntimeException("该提交不存在");
        }
        String str = (String) request.getSession().getAttribute("userId");
        if (contest.getContestStatus() == 2) {
            modelMap.put("submitId", newContestSubmit.getSubmit().getSubmitId());
            modelMap.put("contestId", contestId);
            modelMap.put("submit", newContestSubmit.getSubmit());
            return "submission";
        }
        if (str == null) {
            throw new RuntimeException("该提交不存在");
        }
        int userId = Integer.parseInt(str);
        int s = newContestSubmit.getSubmit().getUser().getUserId();
        if (contest.getContestStatus() == 1 && userId == s) {
            modelMap.put("submitId", newContestSubmit.getSubmit().getSubmitId());
            modelMap.put("contestId", contestId);
            modelMap.put("submit", newContestSubmit.getSubmit());
            return "submission";
        } else {
            modelMap.put("submitId", newContestSubmit.getSubmit().getSubmitId());
            modelMap.put("contestId", contestId);
            Submit submit1 = newContestSubmit.getSubmit();
            submit1.setSubmitScore(0);
            submit1.setSubmitRuntimeTime(0);
            submit1.setTestPoints(null);
            submit1.setSubmitCode(null);
            submit1.getJudgeResult().setJudgeName("已提交");
            modelMap.put("submit", submit1);
        }
        return "submission";
    }

    /**
     * 获取最新比赛提交提交信息（从消息队列中获取最新消息）
     *
     * @param submitId
     * @return
     */

    @RequestMapping("/contest/{contestId}/{submitId}/latest")
    @ResponseBody
    @UserLoginCheck
    public SseEmitter submit(@PathVariable Integer contestId, @PathVariable Integer submitId) throws RuntimeException {
        final SseEmitter emitter = new SseEmitter();
        Contest contest = contestService.getContest(contestId);
        if (contest == null) {
            throw new RuntimeException("该提交不存在");
        }
        if (contest.getContestStatus() == 1 && "IO".equals(contest.getContestType().getContestTypeName())) {
            throw new RuntimeException("该提交不存在");
        }
        ContestSubmit contestSubmit = new ContestSubmit();
        Submit submit = new Submit();
        submit.setSubmitId(submitId);
        contestSubmit.setContest(contest);
        contestSubmit.setSubmit(submit);
        ContestSubmit newContestSubmit = submitService.getContestSubmit(contestSubmit);
        if (newContestSubmit == null) {
            throw new RuntimeException("该提交不存在");
        }
        if ("PD".equals(newContestSubmit.getSubmit().getJudgeResult().getJudgeName()) || "queue".equals(newContestSubmit.getSubmit().getJudgeResult().getJudgeName()) || "Running".equals(newContestSubmit.getSubmit().getJudgeResult().getJudgeName())) {
            try {
                submitResultMessageListener.addSseEmitters(submitId, emitter);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
            return emitter;
        } else {
            throw new RuntimeException("该提交已经运行完成");
        }
    }

    /**
     * 获取比赛提交列表
     *
     * @param page
     * @param contestId
     * @return
     */
    @RequestMapping("/contest/submits/{contestId}")
    @ResponseBody
    public PageInfo<ContestSubmit> getContestSubmits(Integer page, @PathVariable Integer contestId, @RequestBody SubmitQuery submitQuery) {
        if (page == null || page <= 0) {
            page = 1;
        }
        Contest contest = contestService.getContest(contestId);
        if (contest == null) {
            return null;
        }

        PageInfo<ContestSubmit> contestSubmits = submitService.getContestSubmits(page, contestId, submitQuery);
        return contestSubmits;
    }

    /**
     * 获取比赛排名
     *
     * @param request
     * @param contestId
     * @param page
     * @param contestRankQuery
     * @return
     */
    @RequestMapping("/contest/rank/{contestId}")
    @ResponseBody
    public PageInfo<ContestRank> getContestRank(HttpServletRequest request, @PathVariable Integer contestId, Integer page, @RequestBody ContestRankQuery contestRankQuery) {
        Contest contest = contestService.getContest(contestId);
        if (contest == null) {
            return null;
        }
        if ("OI".equals(contest.getContestType().getContestTypeName()) && contest.getContestStatus() == 1) {
            return null;
        }
        PageInfo<ContestRank> pageInfo = submitService.getContestRanks(page, contest, contestRankQuery);
        String str = (String) request.getSession().getAttribute("userId");
        if (pageInfo != null && str != null && pageInfo.getPageNum() == 1 && pageInfo.getList().size() != 1) {
            Integer userId = Integer.parseInt(str);
            ContestRankQuery contestRankQuery1 = new ContestRankQuery();
            contestRankQuery1.setUserId(userId);
            PageInfo<ContestRank> contestRanks = submitService.getContestRanks(1, contest, contestRankQuery1);
            if (contestRanks != null && contestRanks.getList() != null && contestRanks.getList().size() == 1) {
                pageInfo.getList().add(0, contestRanks.getList().get(0));
            }

        }
        return pageInfo;
    }

}
