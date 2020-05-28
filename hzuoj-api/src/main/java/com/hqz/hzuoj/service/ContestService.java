package com.hqz.hzuoj.service;

import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.base.ResultEntity;
import com.hqz.hzuoj.bean.contest.*;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/21 18:41
 * @Description: TODO
 */
public interface ContestService {
    Contest saveContest(Contest contest);

    Contest getContest(Integer contestId);

    Contest getContestStatus(Integer contestId);

    ContestProblem saveContestProblem(ContestProblem contestProblem);

    ContestProblem getContestProblem(ContestProblem contestProblem);

    List<ContestProblem> getContestProblems(Integer contestId, Integer userId);

    ContestProblem getContestProblem(Integer contestId, Integer problemId);

    String delContestProblem(Integer contestId, Integer problemId);

    PageInfo<Contest> getAllContest(Integer page, ContestQuery contestQuery);

    PageInfo<Contest> getAllContest(Integer page);

    ContestApply getContestApplyUser(Integer contestId, Integer userId);

    ContestApply ApplyContest(ContestApply contestApply, String contestCode);

    List<ContestType> getContestTypes();

    public ResultEntity contestAfreshSubmit(Integer contestId);

    String contestDelete(Integer contestId);

    String contestUpdateRankFinal(Integer contestId);

    ContestProblem addContestProblem(Integer contestId, ContestProblem contestProblem);
}
