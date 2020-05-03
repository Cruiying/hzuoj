package com.hqz.hzuoj.judge.mapper;

import com.hqz.hzuoj.bean.submit.JudgeResult;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/31 14:27
 * @Description: TODO
 */
@Component
public interface JudgeResultMapper extends Mapper<JudgeResult> {
    JudgeResult selectJudgeName(String judgeName);
}
