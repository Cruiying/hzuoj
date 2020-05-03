package com.hqz.hzuoj.service;

import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.user.Rank;
import com.hqz.hzuoj.bean.user.RankingQuery;
import com.hqz.hzuoj.bean.user.User;

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

    User updateUser(User user);

    User updateUserImg(User user);

}
