package com.hqz.hzuoj.service.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.problem.Problem;
import com.hqz.hzuoj.bean.solution.Solution;
import com.hqz.hzuoj.bean.solution.SolutionStatus;
import com.hqz.hzuoj.mapper.solution.SolutionMapper;
import com.hqz.hzuoj.mapper.solution.SolutionStatusMapper;
import com.hqz.hzuoj.service.SolutionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/20 10:13
 * @Description: TODO
 */
@Service
public class SolutionServiceImpl implements SolutionService {

    @Autowired
    private SolutionMapper solutionMapper;

    @Autowired
    private SolutionStatusMapper solutionStatusMapper;

    /**
     * 保存或者修改题解
     * @param solution
     * @return
     */
    @Override
    public Solution saveSolution(Solution solution) {
        if (solution.getSolutionId() != null) {
            solution.setSolutionModifyTime(new Date());
            solutionMapper.updateSolution(solution);
        } else {
            SolutionStatus solutionStatus = solutionStatusMapper.getSolutionStatusByStatusName("审核中");
            solution.setSolutionModifyTime(new Date());
            solution.setSolutionCreateTime(new Date());
            solution.setSolutionStatus(solutionStatus);
            solutionMapper.saveSolution(solution);
        }
        return solution;
    }

    /**
     * 获取题解
     * @param solutionId
     * @return
     */
    @Override
    public Solution getSolution(Integer solutionId) {
        return solutionMapper.getSolution(solutionId);
    }

    /**
     * 获取题解列表
     * @param page
     * @param problemId
     * @return
     */
    @Override
    public PageInfo<Solution> getSolutions(Integer page, Integer problemId) {
        Solution solution = new Solution();
        if (problemId != null) {
            Problem problem = new Problem();
            problem.setProblemId(problemId);
            solution.setProblem(problem);
            SolutionStatus solutionStatus = solutionStatusMapper.getSolutionStatusByStatusName("已通过");
            solution.setSolutionStatus(solutionStatus);
        }
        PageHelper.startPage(page, 5, true);
        List<Solution> solutions = solutionMapper.getSolutions(solution);
        return new PageInfo<>(solutions, 5);
    }

    /**
     * 获取题解列表
     * @param page
     * @return
     */
    @Override
    public PageInfo<Solution> getSolutions(Integer page) {
        if (page == null || page <= 0) {
            page = 1;
        }
        PageHelper.startPage(page, 20, true);
        List<Solution> solutions = solutionMapper.getSolutions(null);
        return new PageInfo<>(solutions, 20);
    }

    /**
     * 获取全部题解状态
     * @return
     */
    @Override
    public List<SolutionStatus> getSolutionStatus() {
        return solutionStatusMapper.selectAll();
    }

    /**
     * 删除题解
     * @param solutionId
     * @return
     */
    @Override
    public boolean deleteSolution(Integer solutionId) {
        Solution solution = new Solution();
        solution.setSolutionId(solutionId);
        try {
            solutionMapper.deleteByPrimaryKey(solution);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Solution updateSolutionStatus(Solution solution) {
        if (solution == null || solution.getSolutionStatus() == null || solution.getSolutionStatus().getSolutionStatusId() == null) {
            return null;
        }
        SolutionStatus solutionStatus = solutionStatusMapper.selectByPrimaryKey(solution.getSolutionStatus());
        if (solutionStatus == null) {
            return null;
        }
        solutionMapper.updateSolutionStatus(solution);
        return  getSolution(solution.getSolutionId());
    }
}
