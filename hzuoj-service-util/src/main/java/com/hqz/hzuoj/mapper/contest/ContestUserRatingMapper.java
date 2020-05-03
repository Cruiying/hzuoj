package com.hqz.hzuoj.mapper.contest;

import com.hqz.hzuoj.bean.contest.ContestUserRating;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/29 9:08
 * @Description: TODO
 */
public interface ContestUserRatingMapper extends Mapper<ContestUserRating> {

    List<ContestUserRating> getContestUserRatings(Integer contestId);

    void updateUserRating(Integer userId, Integer rank);

    void updateContestUserRatingStatus(Integer contestId, Integer status);

    void deleteContestUserRating(Integer Id);

    void saveContestUserRating(ContestUserRating contestUserRating);
}
