package com.hqz.hzuoj.bean;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 题解状态实体类
 * @Author: HQZ
 * @CreateTime: 2019/12/11 20:38
 * @Description: TODO
 */
public class SolutionStatus implements Serializable {
    //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer solutionStatusId;
    //状态名称
    @Column
    private String solutionStatusName;

    /**
     * 获取题解主键
     * @return
     */
    public Integer getSolutionStatusId() {
        return solutionStatusId;
    }

    /**
     * 设置题解主键
     * @param solutionStatusId
     */
    public void setSolutionStatusId(Integer solutionStatusId) {
        this.solutionStatusId = solutionStatusId;
    }

    /**
     * 获取题解名称
     * @return
     */
    public String getSolutionStatusName() {
        return solutionStatusName;
    }

    /**
     * 设置题解名称
     * @param solutionStatusName
     */
    public void setSolutionStatusName(String solutionStatusName) {
        this.solutionStatusName = solutionStatusName;
    }

    @Override
    public String toString() {
        return "SolutionStatus{" +
                "solutionStatusId=" + solutionStatusId +
                ", solutionStatusName='" + solutionStatusName + '\'' +
                '}';
    }
}
