package com.hqz.hzuoj.service.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.hqz.hzuoj.bean.language.Language;
import com.hqz.hzuoj.bean.submit.JudgeResult;
import com.hqz.hzuoj.mapper.result.JudgeResultMapper;
import com.hqz.hzuoj.service.JudgeResultService;
import com.hqz.hzuoj.util.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/10 10:46
 * @Description: TODO
 */
@Service
public class JudgeResultServiceImpl implements JudgeResultService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private JudgeResultMapper judgeResultMapper;

    /**
     * 根据id返回测评结果
     *
     * @param judgeResultId
     * @return
     */
    @Override
    public JudgeResult getJudgeResult(Integer judgeResultId) {
        JudgeResult judgeResult;
        String key = "judgeResult:" + judgeResultId + ":info";
        String json = null;
        try {
            // 查询redis缓存中是否存在
            json = (String) redisUtil.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotBlank(json)) {
            judgeResult = JSON.parseObject(json, JudgeResult.class);
        } else {
            judgeResult = judgeResultMapper.selectByPrimaryKey(judgeResultId);
            int time = (int) (Math.random() * 60 * 60 * 24 + 10);
            //将数据写入redis中，避免缓存击穿
            redisUtil.set(key, JSON.toJSONString(judgeResult), time);
        }
        return judgeResult;
    }

    /**
     * 根据测评名称返回测评结果
     *
     * @param judgeName
     * @return
     */
    @Override
    public JudgeResult selectJudgeName(String judgeName) {
        JudgeResult judgeResult;
        String key = "judgeResult:" + judgeName + ":judgeName:info";
        String json = null;
        try {
            // 查询redis缓存中是否存在
            json = (String) redisUtil.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotBlank(json)) {
            judgeResult = JSON.parseObject(json, JudgeResult.class);
        } else {
            judgeResult = judgeResultMapper.selectJudgeName(judgeName);
            //将数据写入redis中，避免缓存击穿
            try {
                int time = (int) (Math.random() * 60 * 60 * 24 + 10);
                redisUtil.set(key, JSON.toJSONString(judgeResult), time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return judgeResult;
    }

    /**
     * 获取全部测评结果
     *
     * @return
     */
    @Override
    public List<JudgeResult> getJudgeResults() {
        List<JudgeResult> judgeResults;
        String key = "judgeResults:all:info";
        String json = null;
        try {
            // 查询redis缓存中是否存在
            json = (String) redisUtil.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotBlank(json)) {
            judgeResults = JSON.parseArray(json, JudgeResult.class);
        } else {
            judgeResults = judgeResultMapper.selectAll();
            try {
                int time = (int) (Math.random() * 60 * 60 * 24 + 10);
                //将数据写入redis中，避免缓存击穿
                redisUtil.set(key, JSON.toJSONString(judgeResults), time);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return judgeResults;
    }
}
