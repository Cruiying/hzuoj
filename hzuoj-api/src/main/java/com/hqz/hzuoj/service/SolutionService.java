package com.hqz.hzuoj.service;

import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.solution.Solution;
import com.hqz.hzuoj.bean.solution.SolutionStatus;
import com.hqz.hzuoj.vo.UserSolutionVO;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/20 10:11
 * @Description: TODO
 */
public interface SolutionService {

    Solution saveSolution(Solution solution);

    Solution getSolution(Integer solutionId);

    PageInfo<Solution> getSolutions(Integer page, Integer problemId);

    PageInfo<Solution> getSolutions(Integer page);

    List<SolutionStatus> getSolutionStatus();

    boolean deleteSolution(Integer solutionId);

    Solution updateSolutionStatus(Solution solution);

    PageInfo<Solution> getUserSolutions(UserSolutionVO userSolutionVO);
}
