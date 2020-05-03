package com.hqz.hzuoj.mapper.submit;

import com.hqz.hzuoj.bean.problem.TestPoint;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/8 19:14
 * @Description: TODO
 */
@Component
public interface TestPointMapper extends Mapper<TestPoint> {

    void saveTestPoint(TestPoint testPoint);

    List<TestPoint> getTestPoint(Integer submitId);

    void deleteSubmitTestPoint(Integer submitId);
}
