package com.hqz.hzuoj.service;

import com.hqz.hzuoj.bean.Example;
import com.hqz.hzuoj.bean.Problem;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/17 21:42
 * @Description: TODO
 */
public interface ExampleService {
    void saveExamples(List<Example> examples, Integer problemId);
}
