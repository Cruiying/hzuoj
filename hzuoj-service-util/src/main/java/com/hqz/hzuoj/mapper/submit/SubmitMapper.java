package com.hqz.hzuoj.mapper.submit;

import com.hqz.hzuoj.bean.submit.JudgeResult;
import com.hqz.hzuoj.bean.submit.Submit;
import com.hqz.hzuoj.bean.submit.SubmitQuery;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/30 14:54
 * @Description: TODO
 */
public interface SubmitMapper extends Mapper<Submit> {

    void saveSubmit(Submit submit);

    Submit getSubmit(Integer submitId);

    List<Submit> getSubmits(SubmitQuery submitQuery);

    List<Submit> selectAll();

    JudgeResult getJudgeResult(Integer judgeResultId);

    List<Submit> getCheckSubmits(Date date);

    void restartSubmit(Submit submit);
}
