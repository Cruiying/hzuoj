package com.hqz.hzuoj.service;

import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.Rank;
import com.hqz.hzuoj.bean.RankingQuery;
import com.hqz.hzuoj.bean.User;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/16 13:16
 * @Description: TODO
 */
public interface UserService {

    User getUser(Integer userId);

    User saveUser(User user);

    User loginUser(User user);

    PageInfo<Rank> getRanks(Integer page, RankingQuery rankingQuery);

    User.UserContestInfo getUserContestInfo(Integer page, Integer userId);

    User.UserProblemInfo getUserProblemInfo(Integer page, Integer userId);

    User updateUser(User user);
}
