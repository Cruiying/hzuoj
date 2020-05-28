package com.hqz.hzuoj.service;

import com.hqz.hzuoj.bean.problem.Data;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/17 19:52
 * @Description: TODO
 */
public interface DataService {
    String saveData(Integer problemId, List<Data> data, String uploadPath);

    List<Data> getProblemDatas(Integer problemId);

    String updateProblemData(List<Data> datas);
}
