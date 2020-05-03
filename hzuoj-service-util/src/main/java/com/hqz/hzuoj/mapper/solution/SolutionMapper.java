package com.hqz.hzuoj.mapper.solution;

import com.hqz.hzuoj.bean.solution.Solution;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/20 10:14
 * @Description: TODO
 */
public interface SolutionMapper extends Mapper<Solution> {

    void saveSolution(Solution solution);

    Solution getSolution(Integer solutionId);

    void updateSolution(Solution solution);

    List<Solution> getSolutions(Solution solution);

    void updateSolutionStatus(Solution solution);
}
