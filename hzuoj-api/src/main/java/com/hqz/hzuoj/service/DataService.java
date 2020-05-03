package com.hqz.hzuoj.service;

import com.hqz.hzuoj.bean.problem.Data;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/17 19:52
 * @Description: TODO
 */
public interface DataService {
    void saveData(Integer problemId, List<Data> data, String uploadPath);

    List<Data> getProblemDatas(Integer problemId);

    void updateProblemData(List<Data> datas);
}
