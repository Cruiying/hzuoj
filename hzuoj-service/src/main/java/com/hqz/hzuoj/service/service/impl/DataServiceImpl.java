package com.hqz.hzuoj.service.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.hqz.hzuoj.bean.problem.Data;
import com.hqz.hzuoj.mapper.problem.DataMapper;
import com.hqz.hzuoj.mapper.problem.ProblemMapper;
import com.hqz.hzuoj.service.DataService;
import com.hqz.hzuoj.util.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/17 19:52
 * @Description: TODO
 */
@Service
public class DataServiceImpl implements DataService {

    @Autowired
    private DataMapper dataMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ProblemMapper problemMapper;

    /**
     * 保存测试数据
     *
     * @param uploadPath 测试数据所在的路径
     * @return 成功返回 success， 失败返回：error
     */
    @Override
    public void saveData(Integer problemId, List<Data> dataList, String uploadPath) {
        if (null == dataList || dataList.size() <= 0) {
            return;
        }

        List<Data> problemDatas = dataMapper.getProblemDatas(problemId);
        System.out.println(problemDatas);
        for (Data problemData : problemDatas) {
            dataMapper.deleteByPrimaryKey(problemData);
        }
        dataMapper.deleteData(problemId);
        for (Data data : dataList) {
            dataMapper.insert(data);
        }
        problemMapper.updateProblemData(problemId, uploadPath);
        String key = "data:" + problemId + ":info";
        int time = (int) (Math.random() * 60 * 60 * 24 * 30 + 10);
        redisUtil.set(key, JSON.toJSONString(dataList), time);
    }

    /**
     * 返回问题所有的测试点信息
     *
     * @param problemId
     * @return
     */
    @Override
    public List<Data> getProblemDatas(Integer problemId) {
        List<Data> list = null;
        try {
            list = dataMapper.getProblemDatas(problemId);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 修改测试运行时间和运行内存
     *
     * @param datas
     */
    @Override
    public void updateProblemData(List<Data> datas) {
        if (datas != null) {
            for (Data data : datas) {
                dataMapper.updataTimeAndMemory(data);
            }
            if (datas.size()>0) {
                Integer problemId = datas.get(0).getProblem().getProblemId();
                String key = "data:" + problemId + ":info";
                int time = (int) (Math.random() * 60 * 60 + 10);
                redisUtil.set(key, JSON.toJSONString(datas), time);
            }
        }
    }
}
