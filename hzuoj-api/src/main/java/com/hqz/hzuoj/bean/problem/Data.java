package com.hqz.hzuoj.bean.problem;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 数据实体类
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/11 18:03
 * @Description: TODO
 */
@Table(name = "hzuoj_datas")
public class Data  implements Serializable {
    //数据主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer dataId;
    //关联的问题
    @Transient
    private Problem problem;
    //对于的输入数据
    @Column
    private String dataInput;
    //对于的输出数据
    @Column
    private String dataOutput;
    //最大运行时间
    @Column
    private Integer dataMaxRuntimeTime;
    //最大运行内存
    @Column
    private Integer dataMaxRuntimeMemory;

    /**
     * 获取主键
     *
     * @return
     */
    public Integer getDataId() {
        return dataId;
    }

    /**
     * 设置主键
     *
     * @param dataId
     */
    public void setDataId(Integer dataId) {
        this.dataId = dataId;
    }

    /**
     * 获取对应关联的问题
     *
     * @return
     */
    public Problem getProblem() {
        return problem;
    }

    /**
     * 设置对应关联的问题
     *
     * @param problem
     */
    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    /**
     * 获取输入数据路径
     *
     * @return
     */
    public String getDataInput() {
        return dataInput;
    }

    /**
     * 设置输入数据路径
     *
     * @param dataInput
     */
    public void setDataInput(String dataInput) {
        this.dataInput = dataInput;
    }

    /**
     * 获取输出数据路径
     *
     * @return
     */
    public String getDataOutput() {
        return dataOutput;
    }

    /**
     * 设置输出数据路径
     *
     * @param dataOutput
     */
    public void setDataOutput(String dataOutput) {
        this.dataOutput = dataOutput;
    }

    /**
     * 获取最大运行时间
     * @return
     */
    public Integer getDataMaxRuntimeTime() {
        return dataMaxRuntimeTime;
    }

    /**
     * 设置最大运行时间
     * @param dataMaxRuntimeTime
     */
    public void setDataMaxRuntimeTime(Integer dataMaxRuntimeTime) {
        this.dataMaxRuntimeTime = dataMaxRuntimeTime;
    }

    /**
     * 获取最大运行内存
     * @return
     */
    public Integer getDataMaxRuntimeMemory() {
        return dataMaxRuntimeMemory;
    }

    /**
     * 设置最大运行内存
     * @param dataMaxRuntimeMemory
     */
    public void setDataMaxRuntimeMemory(Integer dataMaxRuntimeMemory) {
        this.dataMaxRuntimeMemory = dataMaxRuntimeMemory;
    }

    @Override
    public String toString() {
        return "Data{" +
                "dataId=" + dataId +
                ", problem=" + problem +
                ", dataInput='" + dataInput + '\'' +
                ", dataOutput='" + dataOutput + '\'' +
                ", dataMaxRuntimeTime=" + dataMaxRuntimeTime +
                ", dataMaxRuntimeMemory=" + dataMaxRuntimeMemory +
                '}';
    }
}
