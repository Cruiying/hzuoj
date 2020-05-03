package com.hqz.hzuoj.user.mq;

import org.apache.catalina.core.StandardContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * 自测代码结果消息事件
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/31 22:21
 * @Description: TODO
 */
public class TestResultMessageEvent extends ApplicationEvent {
    /**
     * 自测代码唯一ID记录
     */
    private Long testId;
    /**
     * 是不是最后的测试消息
     */
    private boolean completed;
    /**
     * 提交代码信息测评消息记录(以json的形式存放）
     */
    private String judgeInfo;

    public TestResultMessageEvent(Object source) {
        super(source);
    }

    /**
     * testEvent的构造函数.
     *
     * @param source - 消息发布源
     * @param testId - 提交记录的唯一标识符
     */
    public TestResultMessageEvent(Object source, Long testId) {
        super(source);
        this.testId = testId;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getJudgeInfo() {
        return judgeInfo;
    }

    public void setJudgeInfo(String judgeInfo) {
        this.judgeInfo = judgeInfo;
    }

    @Override
    public String toString() {
        return "TestResultMessageEvent{" +
                "testId=" + testId +
                ", completed=" + completed +
                ", judgeInfo='" + judgeInfo + '\'' +
                '}';
    }
}
