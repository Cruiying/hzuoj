package com.hqz.hzuoj.service.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.contest.*;
import com.hqz.hzuoj.bean.problem.Example;
import com.hqz.hzuoj.bean.problem.ProblemSubmitInfo;
import com.hqz.hzuoj.bean.problem.Tag;
import com.hqz.hzuoj.mapper.contest.ContestApplyMapper;
import com.hqz.hzuoj.mapper.contest.ContestMapper;
import com.hqz.hzuoj.mapper.contest.ContestTypeMapper;
import com.hqz.hzuoj.mapper.problem.ExampleMapper;
import com.hqz.hzuoj.mapper.problem.ProblemMapper;
import com.hqz.hzuoj.service.ContestService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/21 18:41
 * @Description: TODO
 */
@Service
public class ContestServiceImpl implements ContestService {

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

    /**
     * 根据比赛contestId获取一个比赛
     *
     * @param contestId
     * @return
     */
    public synchronized Contest getContest(Integer contestId) {
        return getContestStatus(contestId);
    }

    /**
     * 放回比赛状态 0比赛未开始，1比赛进行中，2比赛已经结束
     *
     * @param contestId
     * @return
     */
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
        if (contest == null) return  null;
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
            List<Example> examples = exampleMapper.selectProblem(problemId);
            if (null != contestProblem.getProblem()) {
                contestProblem.getProblem().setExamples(examples);
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
        if (contestQuery == null) contestQuery = new ContestQuery();
        if (page == null) page = 1;
        if (contestQuery.getContestTypeId() != null && contestQuery.getContestTypeId() == -1)
            contestQuery.setContestTypeId(null);
        //先更新全部比赛的状态,再查询
        contestMapper.updateAllContestStatus(new Date());
        PageHelper.startPage(page, 20, true);
        List<Contest> contests = contestMapper.getAllContest(contestQuery);
        return new PageInfo<>(contests, 20);
    }

    /**
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

    @Override
    public ContestApply getContestApplyUser(Integer contestId, Integer userId) {
        if (userId == null || contestId == null) return null;
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
        if (contestId == null || userId == null) return null;
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

    @Override
    public List<ContestType> getContestTypes() {
        return contestTypeMapper.getContestTypes();
    }


}
