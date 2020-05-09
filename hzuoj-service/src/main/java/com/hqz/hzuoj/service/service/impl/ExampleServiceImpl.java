package com.hqz.hzuoj.service.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hqz.hzuoj.bean.problem.Example;
import com.hqz.hzuoj.mapper.problem.ExampleMapper;
import com.hqz.hzuoj.service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 题目样例服务
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/17 21:42
 * @Description: TODO
 */
@Service
public class ExampleServiceImpl implements ExampleService {

    @Autowired
    private ExampleMapper exampleMapper;

    /**
     * 保存测试样例
     *
     * @param examples
     * @param problemId
     */
    @Override
    public void saveExamples(List<Example> examples, Integer problemId) {
        if (examples == null || examples.size() == 0) {
            return;
        }
        //先删除原测试样例
        exampleMapper.deleteExamples(problemId);
        //插入现在的测试数据
        exampleMapper.saveExamples(examples, problemId);
    }
}
