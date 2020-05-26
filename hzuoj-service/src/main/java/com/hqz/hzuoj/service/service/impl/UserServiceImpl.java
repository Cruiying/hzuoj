package com.hqz.hzuoj.service.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.submit.JudgeResult;
import com.hqz.hzuoj.bean.user.Rank;
import com.hqz.hzuoj.bean.user.RankingQuery;
import com.hqz.hzuoj.bean.user.User;
import com.hqz.hzuoj.mapper.result.JudgeResultMapper;
import com.hqz.hzuoj.mapper.submit.SubmitMapper;
import com.hqz.hzuoj.mapper.user.UserMapper;
import com.hqz.hzuoj.service.UserService;
import com.hqz.hzuoj.util.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/16 13:18
 * @Description: TODO
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SubmitMapper submitMapper;

    @Autowired
    private JudgeResultMapper judgeResultMapper;


    /**
     * 获取用户
     *
     * @param userId
     * @return
     */
    @Override
    public User getUser(Integer userId) {
        User user;
        String key = "user:" + userId + ":info";
        String json = null;
        try {
            json = (String) redisUtil.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotBlank(json)) {
            user = JSON.parseObject(json, User.class);
        } else {
            user = userMapper.getUser(userId);
            if (user == null) {
                return null;
            }
            JudgeResult judgeResult = judgeResultMapper.selectJudgeName("AC");
            Integer userRanking = userMapper.getUserRanking(userId);
            Integer userAcceptedRanking = userMapper.getUserAcceptedRanking(userId);
            user.setUserRanking(userRanking + 1);
            user.setUserAcceptedRanking(userAcceptedRanking + 1);
            user.setUserChallengeTotal(userMapper.getUserChallengeTotal(userId, judgeResult.getJudgeResultId(), 1));
            user.setUserSubmitTotal(userMapper.getUserSubmitTotal(userId, judgeResult.getJudgeResultId(), 1));
            user.setUserAcceptedTotal(userMapper.getUserAcceptedTotal(userId, judgeResult.getJudgeResultId(), 1));
            user.setUserSubmitAcceptedTotal(userMapper.getUserSubmitAcceptedTotal(userId, judgeResult.getJudgeResultId(), 1));
            userMapper.updateByPrimaryKey(user);
            int time = (int) (Math.random() * 60 * 60 * 24 + 60);
            redisUtil.set(key, JSON.toJSONString(user), time);
        }
        return user;
    }


    /**
     * 保存用户
     *
     * @param user
     * @return
     */
    @Override
    public User saveUser(User user) {
        try {
            userMapper.insertSelective(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 用户登陆判断
     *
     * @param user
     * @return
     */
    @Override
    public User loginUser(User user) {
        return userMapper.loginUser(user);
    }

    /**
     * 获取排行榜
     *
     * @param page
     * @return
     */

    @Override
    public PageInfo<Rank> getRanks(Integer page, RankingQuery rankingQuery) {
        if (rankingQuery == null) {
            rankingQuery = new RankingQuery();
        }
        int size = 20;
        PageHelper.startPage(page, size, true);
        List<Rank> ranks = userMapper.getRanks(rankingQuery);
        for (Rank rank : ranks) {
            rankingQuery.setUserId(rank.getUser().getUserId());
            rank.setRank(userMapper.getUserRank(rankingQuery) + 1);
        }
        return new PageInfo<>(ranks, size);
    }

    /**
     * 更新用户信息
     *
     * @param user
     * @return
     */
    @Override
    public User updateUser(User user) {
        User dbUser = userMapper.getUser(user.getUserId());
        dbUser.setSchool(user.getSchool());
        dbUser.setSignature(user.getSignature());
        userMapper.updateByPrimaryKey(dbUser);
        String key = "user:" + dbUser.getUserId() + ":info";
        redisUtil.del(key);
        return dbUser;
    }

    /**
     * 更新用户头像
     *
     * @param user
     * @return
     */
    @Override
    public User updateUserImg(User user) {
        User db = userMapper.selectByPrimaryKey(user);
        db.setUserImg(user.getUserImg());
        userMapper.updateByPrimaryKey(db);
        String key = "user:" + db.getUserId() + ":info";
        redisUtil.del(key);
        return db;
    }
}
