package com.hqz.hzuoj.bean;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/31 15:40
 * @Description: TODO
 */
@Table(name = "hzuoj_problems_level")
public class ProblemLevel implements Serializable {
    /**
     * 主键
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer problemLevelId;
    /**
     * 等级名称
     */
    @Column
    private String problemLevelName;

    public Integer getProblemLevelId() {
        return problemLevelId;
    }

    public void setProblemLevelId(Integer problemLevelId) {
        this.problemLevelId = problemLevelId;
    }

    public String getProblemLevelName() {
        return problemLevelName;
    }

    public void setProblemLevelName(String problemLevelName) {
        this.problemLevelName = problemLevelName;
    }

    @Override
    public String toString() {
        return "ProblemLevel{" +
                "problemLevelId=" + problemLevelId +
                ", problemLevelName='" + problemLevelName + '\'' +
                '}';
    }
}
