package com.hqz.hzuoj.mapper.solution;

import com.hqz.hzuoj.bean.solution.SolutionStatus;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/20 11:06
 * @Description: TODO
 */
public interface SolutionStatusMapper extends Mapper<SolutionStatus> {

    SolutionStatus getSolutionStatusByStatusName(String statusName);
}
