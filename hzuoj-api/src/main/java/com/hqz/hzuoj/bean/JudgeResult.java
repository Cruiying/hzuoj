package com.hqz.hzuoj.bean;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 测评结果实体类
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/11 18:29
 * @Description: TODO
 */
@Table(name = "hzuoj_judge_result")
public class JudgeResult  implements Serializable {
    //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer judgeResultId;
    //测评结果名称
    @Column
    private String judgeName;
    //测评结果名称缩写
    @Column
    private String judgeAbbreviation;

    /**
     * 获取主键
     *
     * @return
     */
    public Integer getJudgeResultId() {
        return judgeResultId;
    }

    /**
     * 设置主键
     *
     * @param judgeResultId
     */
    public void setJudgeResultId(Integer judgeResultId) {
        this.judgeResultId = judgeResultId;
    }

    /**
     * 获取测评结果名称
     *
     * @return
     */
    public String getJudgeName() {
        return judgeName;
    }

    /**
     * 设置测评结果名称
     *
     * @param judgeName
     */
    public void setJudgeName(String judgeName) {
        this.judgeName = judgeName;
    }

    /**
     * 获取测评结果名称缩写
     *
     * @return
     */
    public String getJudgeAbbreviation() {
        return judgeAbbreviation;
    }

    /**
     * 设置测评结果名称缩写
     *
     * @param judgeAbbreviation
     */
    public void setJudgeAbbreviation(String judgeAbbreviation) {
        this.judgeAbbreviation = judgeAbbreviation;
    }

    @Override
    public String toString() {
        return "JudgeResult{" +
                "judgeResultId=" + judgeResultId +
                ", judgeName='" + judgeName + '\'' +
                ", judgeAbbreviation='" + judgeAbbreviation + '\'' +
                '}';
    }
}
