package com.hqz.hzuoj.mapper.submit;

import com.hqz.hzuoj.bean.submit.JudgeResult;
import com.hqz.hzuoj.bean.submit.Submit;
import com.hqz.hzuoj.bean.submit.SubmitQuery;
import io.lettuce.core.dynamic.annotation.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/30 14:54
 * @Description: TODO
 */
public interface SubmitMapper extends Mapper<Submit> {
    /**
     *
     * @param submit
     */
    void saveSubmit(Submit submit);

    /**
     *
     * @param submitId
     * @return
     */
    Submit getSubmit(Integer submitId);

    /**
     *
     * @param submitQuery
     * @return
     */
    List<Submit> getSubmits(SubmitQuery submitQuery);

    /**
     *
     * @return
     */
    List<Submit> selectAll();

    /**
     *
     * @param judgeResultId
     * @return
     */
    JudgeResult getJudgeResult(Integer judgeResultId);

    /**
     *
     * @param date
     * @return
     */
    List<Submit> getCheckSubmits(Date date);

    /**
     *
     * @param submit
     */
    void restartSubmit(Submit submit);

    /**
     *
     * @param submitQuery
     * @return
     */
    List<Integer> getSubmitIds(SubmitQuery submitQuery);

    /**
     *
     * @param submitIds
     * @return
     */
    List<Submit> getSubmitsBySubmitId(@Param("submitIds") List<Integer> submitIds);

    /**
     *
     * @return
     */
    Integer getSubmitCount(SubmitQuery submitQuery);


}
