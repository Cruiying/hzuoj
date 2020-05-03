package com.hqz.hzuoj.mapper.contest;

import com.hqz.hzuoj.bean.contest.ContestRankInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/31 10:25
 * @Description: TODO
 */
public interface ContestRankInfoMapper extends Mapper<ContestRankInfo> {

    List<ContestRankInfo> getContestRankInfos(Long contestRankId);

    void saveContestRankInfo(ContestRankInfo contestRankInfo);

    void deleteContestRankInfos(Long contestRankId);

    void deleteContestRankInfo(Integer contestRankInfoId);

    ContestRankInfo getContestRankInfo(Integer contestRankInfoId);

}
