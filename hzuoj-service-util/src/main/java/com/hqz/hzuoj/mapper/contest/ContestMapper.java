package com.hqz.hzuoj.mapper.contest;

import com.hqz.hzuoj.bean.contest.*;
import com.hqz.hzuoj.bean.problem.ProblemSubmitInfo;
import com.hqz.hzuoj.bean.submit.Submit;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/21 18:42
 * @Description: TODO
 */
public interface ContestMapper extends Mapper<Contest> {

    Integer saveContest(Contest contest);

    void updateContestStatus(Contest contest);

    void updateContest(Contest contest);

    Contest getContest(Integer contestId);

    void saveContestProblem(ContestProblem contestProblem);

    ContestProblem getContestProblem(ContestProblem contestProblem);

    List<ContestProblem> getContestProblemAll(Integer contestId);

    ContestProblem selectContestProblem(Integer contestId, Integer problemId);

    void delContestProblem(Integer contestId, Integer problemId);

    List<Contest> getAllContest(ContestQuery contestQuery);

    void updateAllContestStatus(Date date);

    void updateContestApplyCount(Contest contest);

    ProblemSubmitInfo getContestProblemSubmitInfo(Integer contestId, Integer problemId, Integer submitPublic, Integer userId);

    List<Submit> getContestSubmits(Integer contestId);

    /**
     * 获取比赛用户获得的积分积分
     * @param contestApply
     */
    ContestUserRating getContestUserRating(ContestApply contestApply);

    void updateContestRankIsFinish(Contest contest);

    void contestUpdateRankFinal(Integer contestId);
}
