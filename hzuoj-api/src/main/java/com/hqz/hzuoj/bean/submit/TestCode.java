package com.hqz.hzuoj.bean.submit;

import com.hqz.hzuoj.bean.language.Language;
import com.hqz.hzuoj.bean.problem.TestPoint;

import java.io.Serializable;

/**
 * 自测类
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/30 16:36
 * @Description: TODO
 */
public class TestCode implements Serializable {
    //自测ID
    private Long testId;
    //自测代码
    private String testCode;
    //自测输出
    private String testInput;
    //自测输出
    private String testOutput;
    //运行输出
    private String runtimeOutput;
    //编译信息
    private String compileInfo;
    //自测使用的语言
    private Language language;
    //自测结果
    private JudgeResult judgeResult;
    //运行信息
    private TestPoint testPoint;

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public String getTestInput() {
        return testInput;
    }

    public void setTestInput(String testInput) {
        this.testInput = testInput;
    }

    public String getTestOutput() {
        return testOutput;
    }

    public void setTestOutput(String testOutput) {
        this.testOutput = testOutput;
    }

    public String getRuntimeOutput() {
        return runtimeOutput;
    }

    public void setRuntimeOutput(String runtimeOutput) {
        this.runtimeOutput = runtimeOutput;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public JudgeResult getJudgeResult() {
        return judgeResult;
    }

    public void setJudgeResult(JudgeResult judgeResult) {
        this.judgeResult = judgeResult;
    }

    public String getCompileInfo() {
        return compileInfo;
    }

    public void setCompileInfo(String compileInfo) {
        this.compileInfo = compileInfo;
    }

    public TestPoint getTestPoint() {
        return testPoint;
    }

    public void setTestPoint(TestPoint testPoint) {
        this.testPoint = testPoint;
    }

    @Override
    public String toString() {
        return "TestCode{" +
                "testId=" + testId +
                ", testCode='" + testCode + '\'' +
                ", testInput='" + testInput + '\'' +
                ", testOutput='" + testOutput + '\'' +
                ", runtimeOutput='" + runtimeOutput + '\'' +
                ", compileInfo='" + compileInfo + '\'' +
                ", language=" + language +
                ", judgeResult=" + judgeResult +
                ", testPoint=" + testPoint +
                '}';
    }
}
