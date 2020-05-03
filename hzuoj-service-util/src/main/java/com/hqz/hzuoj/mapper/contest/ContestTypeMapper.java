package com.hqz.hzuoj.mapper.contest;

import com.hqz.hzuoj.bean.contest.ContestType;
import tk.mybatis.mapper.common.Mapper;

import javax.persistence.Table;
import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/27 19:02
 * @Description: TODO
 */
@Table(name = "hzuoj_contests_types")
public interface ContestTypeMapper extends Mapper<ContestType> {
    List<ContestType> getContestTypes();
}
