package com.hqz.hzuoj.mapper.contest;

import com.hqz.hzuoj.bean.contest.ContestProblem;
import com.hqz.hzuoj.bean.contest.ContestSubmit;
import com.hqz.hzuoj.bean.submit.SubmitQuery;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/14 12:39
 * @Description: TODO
 */
public interface ContestSubmitMapper extends Mapper<ContestSubmit> {
    void saveContestSubmit(ContestSubmit contestSubmit);

    ContestSubmit getContestSubmit(ContestSubmit contestSubmit);

    List<ContestSubmit> getContestSubmits(Integer contestId, SubmitQuery submitQuery);

    List<ContestSubmit> getRankContestSubmits(Integer contestId);

    List<ContestProblem> getContestProblemAll(Integer contestId);
}
