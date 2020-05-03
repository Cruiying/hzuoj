package com.hqz.hzuoj.judge.mapper;

import com.hqz.hzuoj.bean.problem.Data;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/17 19:53
 * @Description: TODO
 */
@Component
public interface DataMapper extends Mapper<Data> {

    void deleteData(Integer problemId);

    @Override
    int insert(Data data);

    void updataTimeAndMemory(Data data);

    List<Data> getProblemDatas(Integer problemId);

}
