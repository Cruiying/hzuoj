package com.hqz.hzuoj.service;

import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.*;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/30 14:27
 * @Description: TODO
 */
public interface SubmitService {

    Submit getSubmit(Integer submitId);

    Submit saveSubmit(Submit submit);

    TestCode getTestCode(Long testId);

    PageInfo<Submit> getSubmits(Integer page, SubmitQuery submitQuery);

    TestCode saveTestCode(TestCode testCode);

    ContestSubmit saveContestSubmit(ContestSubmit contestSubmit);

    ContestSubmit getContestSubmit(ContestSubmit contestSubmit);

    PageInfo<ContestSubmit> getContestSubmits(Integer page, Integer contestId, SubmitQuery submitQuery);

    PageInfo<ContestRank> getContestRanks(Integer page, Contest contest);

    List<ContestRank> getContestRanks(Integer contestId);

    String calculateContestRating(Contest contest);
}
