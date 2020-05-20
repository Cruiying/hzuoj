package com.hqz.hzuoj.bean.submit;

import java.io.Serializable;

/**
 * 测评记录查询实体类
 *
 * @Author: HQZ
 * @CreateTime: 2020/1/10 15:03
 * @Description: TODO
 */
public class SubmitQuery implements Serializable {
    /**
     *  指定用户提交测评记录
     */

    private Integer userId;
    /**
     * 指定测评记录
     */

    private Integer submitId;
    /**
     * 指定题目测评记录
     */
    private Integer problemId;
    /**
     * 指定语言测评记录
     */
    private Integer languageId;
    /**
     * 指定测评结果的测评记录
     */
    private Integer judgeResultId;
    /**
     * 模糊查询题目标题的测评记录
     */
    private String problemTitle;
    /**
     * 模糊查询用户名的测评记录
     */
    private String username;

    private Integer start = 0;

    private Integer size = 20;

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSubmitId() {
        return submitId;
    }

    public void setSubmitId(Integer submitId) {
        this.submitId = submitId;
    }

    public Integer getProblemId() {
        return problemId;
    }

    public void setProblemId(Integer problemId) {
        this.problemId = problemId;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public Integer getJudgeResultId() {
        return judgeResultId;
    }

    public void setJudgeResultId(Integer judgeResultId) {
        this.judgeResultId = judgeResultId;
    }

    public String getProblemTitle() {
        return problemTitle;
    }

    public void setProblemTitle(String problemTitle) {
        this.problemTitle = problemTitle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "SubmitQuery{" +
                "userId=" + userId +
                ", submitId=" + submitId +
                ", problemId=" + problemId +
                ", languageId=" + languageId +
                ", judgeResultId=" + judgeResultId +
                ", problemTitle='" + problemTitle + '\'' +
                ", username='" + username + '\'' +
                ", start=" + start +
                ", size=" + size +
                '}';
    }
}
