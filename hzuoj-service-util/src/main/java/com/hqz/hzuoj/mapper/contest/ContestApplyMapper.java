package com.hqz.hzuoj.mapper.contest;

import com.hqz.hzuoj.bean.contest.ContestApply;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/11 15:28
 * @Description: TODO
 */
public interface ContestApplyMapper extends Mapper<ContestApply> {

    List<ContestApply> selectAllByContest(Integer contestId);

    void saveContestApply(ContestApply contestApply);

    ContestApply selectContestApplyByUser(Integer contestId, Integer userId);
}
