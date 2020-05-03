package com.hqz.hzuoj.bean.problem;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 样例实体类
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/11 18:59
 * @Description: TODO
 */
@Table(name = "hzuoj_examples")
public class Example  implements Serializable {
    //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer exampleId;
    //该样例所属的题目
    @Transient
    private Problem problem;
    //该样例的输入数据
    @Column
    private String exampleInput;
    //该样例的输出数据
    @Column
    private String exampleOutput;

    /**
     * 获取主键
     * @return
     */
    public Integer getExampleId() {
        return exampleId;
    }

    /**
     * 设置主键
     * @param exampleId
     */
    public void setExampleId(Integer exampleId) {
        this.exampleId = exampleId;
    }

    /**
     * 获取该样例对应的输入数据
     * @return
     */
    public Problem getProblem() {
        return problem;
    }

    /**
     * 设置该样例对应的问题
     * @param problem
     */
    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    /**
     * 获取该样例的输入数据
     * @return
     */
    public String getExampleInput() {
        return exampleInput;
    }

    /**
     * 设置该样例的输入数据
     * @param exampleInput
     */
    public void setExampleInput(String exampleInput) {
        this.exampleInput = exampleInput;
    }

    /**
     * 获取该样例的输出数据
     * @return
     */
    public String getExampleOutput() {
        return exampleOutput;
    }

    /**
     * 设置该样例的输出数据
     * @param exampleOutput
     */
    public void setExampleOutput(String exampleOutput) {
        this.exampleOutput = exampleOutput;
    }


    @Override
    public String toString() {
        return "Example{" +
                "exampleId=" + exampleId +
                ", problem=" + problem +
                ", exampleInput='" + exampleInput + '\'' +
                ", exampleOutput='" + exampleOutput + '\'' +
                '}';
    }
}
