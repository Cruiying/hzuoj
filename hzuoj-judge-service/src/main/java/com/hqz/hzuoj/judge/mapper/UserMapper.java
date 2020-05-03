package com.hqz.hzuoj.judge.mapper;

import com.hqz.hzuoj.bean.user.Rank;
import com.hqz.hzuoj.bean.user.RankingQuery;
import com.hqz.hzuoj.bean.user.User;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/16 13:19
 * @Description: TODO
 */
@Component
public interface UserMapper extends Mapper<User> {

    User loginUser(User user);

    User getUser(Integer userId);

    Integer getUserAcceptedTotal(Integer userId, Integer judgeResultId, Integer submitPublic);

    Integer getUserSubmitTotal(Integer userId, Integer judgeResultId, Integer submitPublic);

    Integer getUserSubmitAcceptedTotal(Integer userId, Integer judgeResultId, Integer submitPublic);

    Integer getUserChallengeTotal(Integer userId, Integer judgeResultId, Integer submitPublic);

    List<Rank> getRanks(RankingQuery rankingQuery);

    Integer getUserRank(RankingQuery rankingQuery);

    Integer getUserRanking(Integer userId);

    Integer getUserAcceptedRanking(Integer userId);
}
