package com.hqz.hzuoj.service;

import com.hqz.hzuoj.bean.submit.JudgeResult;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/10 10:45
 * @Description: TODO
 */
public interface JudgeResultService {

    JudgeResult getJudgeResult(Integer judgeResultId);

    JudgeResult selectJudgeName(String judgeName);

    List<JudgeResult> getJudgeResults();

}
