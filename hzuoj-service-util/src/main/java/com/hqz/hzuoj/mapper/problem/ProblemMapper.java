package com.hqz.hzuoj.mapper.problem;

import com.hqz.hzuoj.bean.problem.*;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/17 14:00
 * @Description: TODO
 */
@Component
public interface ProblemMapper extends Mapper<Problem> {
    /**
     * 保存问题
     *
     * @param problem
     * @return
     */
    Integer saveProblem(Problem problem);

    @Override
    Problem selectOne(Problem problem);

    void updateProblemData(Integer problemId, String path);

    List<Problem> getAllProblem(Integer problemPublic, ProblemQuery problemQuery);

    Problem getProblem(Integer problemId);

    void updateProblemPublic(Problem problem);

    void deleteProblem(Integer problemId);

    Integer getAllAcceptedTotal(Integer problemId);

    Integer getAllTotal(Integer problemId);

    Integer getMyAcceptedTotal(Integer userId, Integer problemId);

    Integer getMyAllTotal(Integer userId, Integer problemId);

    void deleteProblemTags(Integer problemId);

    void saveProblemTag(Integer tagId, Integer problemId);

    List<Tag> getProblemTags(Integer problemId);

    void updateProblemLevel(Integer problemId, ProblemLevel problemLevel);

}
