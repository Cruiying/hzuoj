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
    public String saveData(Integer problemId, List<Data> dataList, String uploadPath) {
        if (dataList == null || dataList.isEmpty()) {
            throw new RuntimeException("测试数据不能为空");
        }
        dataMapper.deleteData(problemId);
        for (Data data : dataList) {
            dataMapper.insert(data);
        }
        problemMapper.updateProblemData(problemId, uploadPath);
        return "success";
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
    public String updateProblemData(List<Data> datas) {
        if (null == datas) {
            throw new RuntimeException("参数为空");
        }
        datas.forEach(data -> {
            if (data.getDataMaxRuntimeMemory() <= 0) {
                throw new RuntimeException("运行内存必须大于0");
            }
            if (data.getDataMaxRuntimeTime() <= 0) {
                throw new RuntimeException("运行时间必须大于0");
            }
            dataMapper.updataTimeAndMemory(data);
        });
        return "success";
    }
}
