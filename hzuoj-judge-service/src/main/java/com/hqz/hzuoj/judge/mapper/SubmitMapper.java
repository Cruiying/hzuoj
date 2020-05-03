package com.hqz.hzuoj.judge.mapper;

import com.hqz.hzuoj.bean.submit.Submit;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/30 14:54
 * @Description: TODO
 */
@Component
public interface SubmitMapper extends Mapper<Submit> {

//    void saveSubmit(Submit submit);

    Submit getSubmit(Integer submitId);

//    JudgeResult getJudgeResult(Integer judgeResultId);

    void updateSubmit(Submit submit);

    Integer getUserProblemTotal(Integer userId, Integer problemId);

    void updateUserAcceptedTotal(Integer userId, Integer accepted);
}
