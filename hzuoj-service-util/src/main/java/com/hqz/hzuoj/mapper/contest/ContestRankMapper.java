package com.hqz.hzuoj.mapper.contest;

import com.hqz.hzuoj.bean.contest.ContestRank;
import com.hqz.hzuoj.bean.contest.ContestRankQuery;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/31 10:24
 * @Description: TODO
 */
public interface ContestRankMapper extends Mapper<ContestRank> {

    List<ContestRank> getContestRanks(Integer contestId, ContestRankQuery contestRankQuery);

    void saveContestRank(ContestRank contestRank);

    void deleteContestRank(Long contestRankId);

    void deleteContestRanks(Integer contestId);

    ContestRank getContestRank(Integer contestRankId);

    List<ContestRank> getContestRanksAndContestRankInfos(Integer contestId);
}
