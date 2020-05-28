package com.hqz.hzuoj.service.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.base.ResultEntity;
import com.hqz.hzuoj.bean.contest.*;
import com.hqz.hzuoj.bean.problem.Data;
import com.hqz.hzuoj.bean.problem.Example;
import com.hqz.hzuoj.bean.problem.ProblemSubmitInfo;
import com.hqz.hzuoj.bean.problem.Tag;
import com.hqz.hzuoj.bean.submit.JudgeResult;
import com.hqz.hzuoj.bean.submit.Submit;
import com.hqz.hzuoj.mapper.contest.ContestApplyMapper;
import com.hqz.hzuoj.mapper.contest.ContestMapper;
import com.hqz.hzuoj.mapper.contest.ContestTypeMapper;
import com.hqz.hzuoj.mapper.problem.DataMapper;
import com.hqz.hzuoj.mapper.problem.ExampleMapper;
import com.hqz.hzuoj.mapper.problem.ProblemMapper;
import com.hqz.hzuoj.mapper.result.JudgeResultMapper;
import com.hqz.hzuoj.mapper.submit.SubmitMapper;
import com.hqz.hzuoj.mapper.submit.TestPointMapper;
import com.hqz.hzuoj.service.ContestService;
import com.hqz.hzuoj.service.SubmitService;
import com.hqz.hzuoj.service.mq.MessageSender;
import com.hqz.hzuoj.util.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/21 18:41
 * @Description: TODO
 */
@Transactional
@Service
@Slf4j
public class ContestServiceImpl implements ContestService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ContestMapper contestMapper;

    @Autowired
    private ContestApplyMapper contestApplyMapper;

    @Autowired
    private ExampleMapper exampleMapper;

    @Autowired
    private ContestTypeMapper contestTypeMapper;

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private DataMapper dataMapper;

    @Autowired
    private TestPointMapper testPointMapper;

    @Autowired
    private SubmitService submitService;

    @Autowired
    private JudgeResultMapper judgeResultMapper;

    @Autowired
    private SubmitMapper submitMapper;

    @Value("${submitQueue}")
    private String submitQueue;

    @Autowired
    private MessageSender sender;

    /**
     * 根据比赛contestId获取一个比赛
     *
     * @param contestId
     * @return
     */
    @Override
    public synchronized Contest getContest(Integer contestId) {
        return getContestStatus(contestId);
    }

    @Override
    public ResultEntity contestAfreshSubmit(Integer contestId) {
        List<Submit> contestSubmits = contestMapper.getContestSubmits(contestId);
        for (Submit contestSubmit : contestSubmits) {
            startSubmit(contestSubmit);
        }
        return ResultEntity.success("提交成功", "");
    }



    /**重新测评
     * @param submit
     */
    public void startSubmit(Submit submit) {
        if (submit == null) {
            return;
        }
        redisUtil.del("submit:" + submit.getSubmitId() + ":info");
        JudgeResult judgeResult = judgeResultMapper.selectJudgeName("queue");
        if (judgeResult == null) {
            return;
        }
        submit.setJudgeResult(judgeResult);
        submit.setSubmitScore(0);
        submit.setSubmitRuntimeTime(0);
        submit.setSubmitRuntimeMemory(0);
        testPointMapper.deleteSubmitTestPoint(submit.getSubmitId());
        submitMapper.restartSubmit(submit);
        Map<String, Object> map = new HashMap<>();
        map.put("submitId", submit.getSubmitId());
        sender.sendQueue(submitQueue, map);
    }

    /**
     * 放回比赛状态 0比赛未开始，1比赛进行中，2比赛已经结束
     *
     * @param contestId
     * @return
     */
    @Override
    public Contest getContestStatus(Integer contestId) {
        contestMapper.updateAllContestStatus(new Date());
        Contest contest = contestMapper.getContest(contestId);
        if (contest == null || contest.getContestStatus() == 2) {
            //比赛已经结束
            return contest;
        }
        Date now = new Date();//当前时间
        Date s = contest.getContestStart();
        Date e = contest.getContestEnd();
        if (now.getTime() >= e.getTime()) {
            //比赛已经结束
            //修改数据库中的比赛状态
            contest.setContestStatus(2);
            contestMapper.updateContestStatus(contest);
            return contest;
        }
        if (now.getTime() >= s.getTime() && now.getTime() < e.getTime()) {
            //比赛正在进行中
            if (contest.getContestStatus() == 1) {
                //数据库状态本身就是进行中，不需要修改数据库
                return contest;
            } else {
                //否则数据库中的比赛状态未0，需要修改数据库中的转态
                contest.setContestStatus(1);
                contestMapper.updateContestStatus(contest);
                return contest;
            }
        }
        //否则比赛还未开始
        return contest;
    }


    /**
     * 保存比赛
     *
     * @param contest
     * @return
     */
    @Override
    public Contest saveContest(Contest contest) {
        if (contest == null) {
            return null;
        }
        if (contest.getContestId() != null) {
            Contest c = getContestStatus(contest.getContestId());
            //比赛还未开始，可以修改除了比赛的邀请码和比赛的contestId和比赛的创建者任何属性
            contest.setAdmin(c.getAdmin());
            contest.setContestStatus(c.getContestStatus());
            contest.setContestId(c.getContestId());
            contest.setContestCode(c.getContestCode());
            contest.setContestTimeLength(contest.getContestEnd().getTime() - contest.getContestStart().getTime());
            contestMapper.updateContest(contest);
            return contest;

        } else {
            //保存比赛
            contestMapper.saveContest(contest);
            return contest;
        }
    }

    /**
     * 保存保存比赛题目
     *
     * @param contestProblem
     */
    @Override
    public ContestProblem saveContestProblem(ContestProblem contestProblem) {
        contestMapper.saveContestProblem(contestProblem);
        return contestProblem;
    }

    /**
     * 判断比赛题目列表中是否已经存在该是否存在
     *
     * @param contestProblem
     * @return
     */
    @Override
    public ContestProblem getContestProblem(ContestProblem contestProblem) {
        return contestMapper.getContestProblem(contestProblem);
    }

    /**
     * 返回题目中的所有比赛题目
     *
     * @param contestId
     * @return
     */
    @Override
    public List<ContestProblem> getContestProblems(Integer contestId, Integer userId) {
        Contest contest = contestMapper.getContest(contestId);
        List<ContestProblem> contestProblems = contestMapper.getContestProblemAll(contestId);
        if (contestProblems != null) {
            for (ContestProblem contestProblem : contestProblems) {
                List<Tag> tags = problemMapper.getProblemTags(contestProblem.getProblem().getProblemId());
                contestProblem.getProblem().setTags(tags);
                if (contest.getContestStatus() == 1) {
                    contestProblem.getProblem().setTags(null);
                }
                ProblemSubmitInfo contestProblemSubmitInfo = contestMapper.getContestProblemSubmitInfo(contestId, contestProblem.getProblem().getProblemId(), 0, userId);
                if (contest.getContestStatus() == 1 && "OI".equals(contest.getContestType().getContestTypeName())) {
                    contestProblemSubmitInfo.setMyAcceptedTotal(0);
                    contestProblemSubmitInfo.setAllAcceptedTotal(0);
                    contestProblemSubmitInfo.setShowSubmit(false);

                } else {
                    contestProblemSubmitInfo.setShowSubmit(true);
                }
                contestProblem.getProblem().setProblemSubmitInfo(contestProblemSubmitInfo);
            }
        }
        return contestProblems;
    }

    /**
     * 返回比赛题目
     *
     * @param contestId
     * @return
     */
    @Override
    public ContestProblem getContestProblem(Integer contestId, Integer problemId) {
        ContestProblem contestProblem = contestMapper.selectContestProblem(contestId, problemId);
        if (contestProblem != null) {
            if (null != contestProblem.getProblem()) {
                List<Example> examples = exampleMapper.selectProblem(problemId);
                List<Data> problemDatas = dataMapper.getProblemDatas(problemId);
                contestProblem.getProblem().setExamples(examples);
                contestProblem.getProblem().setDatas(problemDatas);
            }
        }
        return contestProblem;
    }

    /**
     * 删除比赛题目
     *
     * @param contestId
     * @param problemId
     * @return
     */
    @Override
    public String delContestProblem(Integer contestId, Integer problemId) {
        Contest contestStatus = getContestStatus(contestId);
        if (contestStatus.getContestStatus() != 0) {
            //如果比赛已经结束或者比赛正在进行，则不允许删除比赛题目
            return "fail";
        }
        contestMapper.delContestProblem(contestId, problemId);
        return "success";
    }

    /**
     * 按contestQuery中的查询条件进行进行查询所有比赛信息
     *
     * @param page
     * @param contestQuery
     * @return
     */
    @Override
    public PageInfo<Contest> getAllContest(Integer page, ContestQuery contestQuery) {
        if (contestQuery == null) {
            contestQuery = new ContestQuery();
        }
        if (page == null) {
            page = 1;
        }
        if (contestQuery.getContestTypeId() != null && contestQuery.getContestTypeId() == -1) {
            contestQuery.setContestTypeId(null);
        }
        //先更新全部比赛的状态,再查询
        contestMapper.updateAllContestStatus(new Date());
        PageHelper.startPage(page, 20, true);
        List<Contest> contests = contestMapper.getAllContest(contestQuery);
        return new PageInfo<>(contests, 20);
    }

    /** 获取所有比赛
     * @param page
     * @return
     */
    @Override
    public PageInfo<Contest> getAllContest(Integer page) {
        //先更新全部比赛的状态,再查询
        contestMapper.updateAllContestStatus(new Date());
        PageHelper.startPage(page, 20, true);
        ContestQuery contestQuery = new ContestQuery();
        List<Contest> contests = contestMapper.getAllContest(contestQuery);
        return new PageInfo<>(contests, 20);
    }

    /**
     * 获取用户比赛报名信息
     * @param contestId
     * @param userId
     * @return
     */
    @Override
    public ContestApply getContestApplyUser(Integer contestId, Integer userId) {
        if (userId == null || contestId == null) {
            return null;
        }
        try {
            return contestApplyMapper.selectContestApplyByUser(contestId, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 报名比赛
     *
     * @param contestApply
     * @param contestCode
     * @return
     */
    @Override
    public ContestApply ApplyContest(ContestApply contestApply, String contestCode) {
        contestMapper.updateAllContestStatus(new Date());
        Integer contestId = contestApply.getContestId();
        Integer userId = contestApply.getUserId();
        if (contestId == null || userId == null) {
            return null;
        }
        Contest contest = contestMapper.getContest(contestId);
        if (contest == null || contest.getContestStatus() == 2 || contest.getContestApplyStatus() != 1) {
            //比赛不存在，比赛结束，不是比赛报名时间
            return null;
        }
        Integer contestPublic = contest.getContestPublic();
        ContestApply contestApplyByUser = contestApplyMapper.selectContestApplyByUser(contestId, userId);
        if (contestApplyByUser != null) {
            //已经报名
            return null;
        }
        if (contestPublic == 1) {
            //公开比赛
            //不需要需要吗
            contest.setContestApplyCount(contest.getContestApplyCount() + 1);
            contestMapper.updateContestApplyCount(contest);
            contestApplyMapper.insertSelective(contestApply);
            return contestApply;
        } else if (contestPublic == 0) {
            if (contestCode != null && contestCode.equals(contest.getContestCode())) {
                //验证码正确
                contest.setContestApplyCount(contest.getContestApplyCount() + 1);
                contestMapper.updateContestApplyCount(contest);
                contestApplyMapper.insertSelective(contestApply);
                return contestApply;
            }

        }
        return null;
    }

    /**
     * 获取所有比赛类型
     * @return
     */
    @Override
    public List<ContestType> getContestTypes() {
        return contestTypeMapper.getContestTypes();
    }

    /**
     * 删除比赛
     * @param contestId
     * @return
     */
    @Override
    public String contestDelete(Integer contestId) {
        int count = contestMapper.deleteByPrimaryKey(contestId);
        return count == 1 ? "success" : "error";
    }

    @Override
    public String contestUpdateRankFinal(Integer contestId) {
        contestMapper.contestUpdateRankFinal(contestId);
        return "success";
    }

}
