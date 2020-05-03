package com.hqz.hzuoj.mapper.result;

import com.hqz.hzuoj.bean.submit.JudgeResult;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/31 14:27
 * @Description: TODO
 */
public interface JudgeResultMapper extends Mapper<JudgeResult> {

    JudgeResult selectJudgeName(String judgeName);
}
