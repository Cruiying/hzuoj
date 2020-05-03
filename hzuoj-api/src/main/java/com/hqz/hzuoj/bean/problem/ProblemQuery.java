package com.hqz.hzuoj.bean.problem;

import java.io.Serializable;

/**
 * @Author: HQZ
 * @CreateTime: 2020/2/14 10:38
 * @Description: TODO
 */
public class ProblemQuery implements Serializable {
    /**
     * 指定题目ID
     */
    private Integer problemId;
    /**
     * 题目标题
     */
    private String problemTitle;
    /**
     * 题目标签
     */
    private Integer tagId;
    /**
     * 题目等级
     */
    private Integer problemLevelId;

    public Integer getProblemId() {
        return problemId;
    }

    public void setProblemId(Integer problemId) {
        this.problemId = problemId;
    }

    public String getProblemTitle() {
        return problemTitle;
    }

    public void setProblemTitle(String problemTitle) {
        this.problemTitle = problemTitle;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public Integer getProblemLevelId() {
        return problemLevelId;
    }

    public void setProblemLevelId(Integer problemLevelId) {
        this.problemLevelId = problemLevelId;
    }

    @Override
    public String toString() {
        return "ProblemQuery{" +
                "problemId=" + problemId +
                ", problemTitle='" + problemTitle + '\'' +
                ", tagId=" + tagId +
                ", problemLevelId=" + problemLevelId +
                '}';
    }
}
