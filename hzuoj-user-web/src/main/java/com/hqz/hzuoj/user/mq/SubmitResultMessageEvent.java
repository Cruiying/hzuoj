package com.hqz.hzuoj.user.mq;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * 提交代码结果消息事件
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/31 16:30
 * @Description: TODO
 */
public class SubmitResultMessageEvent extends ApplicationEvent {
    /**
     * 提交代码唯一ID记录
     */
    private Integer submitId;
    /**
     * 是不是最后的测试消息
     */
    private boolean completed;
    /**
     * 提交代码信息测评消息记录(以JSON形式存放）
     */
    private String JudgeInfo;

    /**
     * SubmissionEvent的构造函数.
     *
     * @param source
     */

    public SubmitResultMessageEvent(Object source) {
        super(source);
    }

    /**
     * SubmissionEvent的构造函数.
     *
     * @param source   - 消息发布源
     * @param submitId - 提交记录的唯一标识符
     */
    public SubmitResultMessageEvent(Object source, Integer submitId) {
        super(source);
        this.submitId = submitId;
    }

    public Integer getSubmitId() {
        return submitId;
    }

    public void setSubmitId(Integer submitId) {
        this.submitId = submitId;
    }

    public String getJudgeInfo() {
        return JudgeInfo;
    }

    public void setJudgeInfo(String judgeInfo) {
        JudgeInfo = judgeInfo;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "SubmitResultMessageEvent{" +
                "submitId=" + submitId +
                ", completed=" + completed +
                ", JudgeInfo='" + JudgeInfo + '\'' +
                '}';
    }
}
