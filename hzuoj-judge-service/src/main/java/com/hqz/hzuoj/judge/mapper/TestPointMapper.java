package com.hqz.hzuoj.judge.mapper;

import com.hqz.hzuoj.bean.problem.TestPoint;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/8 19:14
 * @Description: TODO
 */
@Component
public interface TestPointMapper extends Mapper<TestPoint> {
    void saveTestPoint(TestPoint testPoint);

    void deleteSubmitTestPoint(Integer submitId);
}
