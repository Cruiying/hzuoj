package com.hqz.hzuoj.service;

import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.contest.Contest;
import com.hqz.hzuoj.bean.contest.ContestRank;
import com.hqz.hzuoj.bean.contest.ContestRankQuery;
import com.hqz.hzuoj.bean.contest.ContestSubmit;
import com.hqz.hzuoj.bean.submit.Submit;
import com.hqz.hzuoj.bean.submit.SubmitQuery;
import com.hqz.hzuoj.bean.submit.TestCode;

import java.io.IOException;
import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/30 14:27
 * @Description: TODO
 */
public interface SubmitService {

    Submit getSubmit(Integer submitId);

    Submit saveSubmit(Submit submit);

    String restartSubmit(Integer submitId);

    String restartSubmits();

    PageInfo<Submit> getSubmits(Integer page, SubmitQuery submitQuery);

    PageInfo<Submit> getSubmits(Integer page);

    TestCode saveTestCode(TestCode testCode);

    ContestSubmit saveContestSubmit(ContestSubmit contestSubmit);

    ContestSubmit getContestSubmit(ContestSubmit contestSubmit);

    PageInfo<ContestSubmit> getContestSubmits(Integer page, Integer contestId, SubmitQuery submitQuery);

    PageInfo<ContestRank> getContestRanks(Integer page, Contest contest, ContestRankQuery contestRankQuery);

    List<ContestRank> getContestRanks(Integer contestId);

    String calculateContestRating(Integer contestId);

    String getContestExcel(Integer contestId) throws IOException;
}
