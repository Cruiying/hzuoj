package com.hqz.hzuoj.service;

import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.Solution;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/20 10:11
 * @Description: TODO
 */
public interface SolutionService {

    Solution saveSolution(Solution solution);

    Solution getSolution(Integer solutionId);

    PageInfo<Solution> getSolutions(Integer page, Integer problemId);
}
