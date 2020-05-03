package com.hqz.hzuoj.bean.problem;

import com.hqz.hzuoj.bean.submit.JudgeResult;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户提交实体类
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/11 17:58
 * @Description: TODO
 */
@Table(name = "hzuoj_test_points")
public class TestPoint implements Serializable {
    //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer testPointId;

    //测试点实际运行时间
    @Column
    private Integer testPointTime;
    //测试点实际运行内存
    @Column
    private Integer testPointMemory;
    //测试点所得分值
    @Column
    private Integer testPointScore;
    //关联的提交
    @Column
    private Integer submitId;
    //测评结果
    @Column
    @Transient
    private JudgeResult judgeResult;

    /**
     * 获取主键
     *
     * @return
     */
    public Integer getTestPointId() {
        return testPointId;
    }

    /**
     * 设置主键
     *
     * @param testPointId
     */
    public void setTestPointId(Integer testPointId) {
        this.testPointId = testPointId;
    }


    /**
     * 获取当前测试点的实际运行时间
     *
     * @return
     */
    public Integer getTestPointTime() {
        return testPointTime;
    }

    /**
     * 设置当前测试点的实际运行时间
     *
     * @param testPointTime
     */
    public void setTestPointTime(Integer testPointTime) {
        this.testPointTime = testPointTime;
    }

    /**
     * 获取当前测试点的实际运行内存
     *
     * @return
     */
    public Integer getTestPointMemory() {
        return testPointMemory;
    }

    /**
     * 设置当前测试点的实际运行内存
     *
     * @param testPointMemory
     */
    public void setTestPointMemory(Integer testPointMemory) {
        this.testPointMemory = testPointMemory;
    }

    /**
     * 获取当前测试点获得的分数
     *
     * @return
     */
    public Integer getTestPointScore() {
        return testPointScore;
    }

    /**
     * 设置当前测试点获得的分数
     *
     * @param testPointScore
     */
    public void setTestPointScore(Integer testPointScore) {
        this.testPointScore = testPointScore;
    }


    /**
     * 获得当前用户提交
     *
     * @return
     */
    public Integer getSubmitId() {
        return submitId;
    }


    /**
     * 设置当前用户的提交
     *
     * @param submitId
     */
    public void setSubmitId(Integer submitId) {
        this.submitId = submitId;
    }

    /**
     * 获取测试点的测评结果
     *
     * @return
     */
    public JudgeResult getJudgeResult() {
        return judgeResult;
    }

    /**
     * 设置测试点的测评结果
     *
     * @param judgeResult
     */
    public void setJudgeResult(JudgeResult judgeResult) {
        this.judgeResult = judgeResult;
    }

    @Override
    public String toString() {
        return "TestPoint{" +
                "testPointId=" + testPointId +
                ", testPointTime=" + testPointTime +
                ", testPointMemory=" + testPointMemory +
                ", testPointScore=" + testPointScore +
                ", submitId=" + submitId +
                ", judgeResult=" + judgeResult +
                '}';
    }
}
