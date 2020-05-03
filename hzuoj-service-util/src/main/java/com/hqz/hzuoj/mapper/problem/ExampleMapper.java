package com.hqz.hzuoj.mapper.problem;

import com.hqz.hzuoj.bean.problem.Example;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/17 21:43
 * @Description: TODO
 */

@Component
public interface ExampleMapper extends Mapper<Example> {

    void saveExamples(List<Example> examples, Integer problemId);

    void deleteExamples(Integer problemId);

    List<Example> selectProblem(Integer problemId);

}
