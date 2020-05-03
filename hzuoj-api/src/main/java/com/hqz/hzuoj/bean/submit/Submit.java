package com.hqz.hzuoj.bean.submit;

import com.hqz.hzuoj.bean.language.Language;
import com.hqz.hzuoj.bean.problem.TestPoint;
import com.hqz.hzuoj.bean.user.User;
import com.hqz.hzuoj.bean.problem.Problem;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户提交实体类
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/11 18:09
 * @Description: TODO
 */
@Table(name = "hzuoj_submits")
public class Submit implements Serializable {
    //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer submitId;
    //提交时间
    @Column
    private Date submitTime;
    //提交的代码
    @Column
    private String submitCode;
    //编译信息
    private String submitCompileInfo;
    //代码长度
    @Column
    private Integer submitCodeLength;
    //提交获得的分数
    @Column
    private Integer submitScore;

    //提交运行的时间
    @Column
    private Integer submitRuntimeTime;

    //提交运行的内存
    @Column
    private Integer submitRuntimeMemory;
    //提交结果是否可见
    @Column
    private Integer submitPublic;
    //使用的语言
    @Transient
    @OneToOne
    private Language language;
    //对应关联的问题
    @Transient
    @OneToOne
    private Problem problem;
    //对于关联的用户
    @Transient
    @OneToOne
    private User user;
    //关联的测试集合
    @Transient
    @OneToMany
    private List<TestPoint> testPoints;
    //提交结果
    @Transient
    @OneToOne
    private JudgeResult judgeResult;


    /**
     * 获取主键
     *
     * @return
     */
    public Integer getSubmitId() {
        return submitId;
    }

    /**
     * 设置主键
     *
     * @param submitId
     */
    public void setSubmitId(Integer submitId) {
        this.submitId = submitId;
    }

    /**
     * 获取对应的问题
     *
     * @return
     */
    public Problem getProblem() {
        return problem;
    }

    /**
     * 设置对应的问题
     *
     * @param problem
     */
    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    /**
     * 获取提交的用户
     *
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     * 设置提交的用户
     *
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 获取用户提交的时间
     *
     * @return
     */
    public Date getSubmitTime() {
        return submitTime;
    }

    /**
     * 设置用户提交的时间
     *
     * @param submitTime
     */
    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    /**
     * 获取用户提交的代码
     *
     * @return
     */
    public String getSubmitCode() {
        return submitCode;
    }

    /**
     * 设置用户提交的代码
     *
     * @param submitCode
     */
    public void setSubmitCode(String submitCode) {
        this.submitCode = submitCode;
    }

    /**
     * 获取用户代码编译的信息
     *
     * @return
     */
    public String getSubmitCompileInfo() {
        return submitCompileInfo;
    }

    /**
     * 设置用户代码编译的信息
     *
     * @param submitCompileInfo
     */
    public void setSubmitCompileInfo(String submitCompileInfo) {
        this.submitCompileInfo = submitCompileInfo;
    }

    /**
     * 获取用户提交使用的语言
     *
     * @return
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * 设置用户提交使用的语言
     *
     * @param language
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * 获取用户提交的代码长度
     *
     * @return
     */
    public Integer getSubmitCodeLength() {
        return submitCodeLength;
    }

    /**
     * 设置用户提交的代码长度
     *
     * @param submitCodeLength
     */
    public void setSubmitCodeLength(Integer submitCodeLength) {
        this.submitCodeLength = submitCodeLength;
    }

    /**
     * 获取当前提交是否可见
     *
     * @return
     */
    public Integer getSubmitPublic() {
        return submitPublic;
    }

    /**
     * 设置当前提交是否可可见
     *
     * @param submitPublic
     */
    public void setSubmitPublic(Integer submitPublic) {
        this.submitPublic = submitPublic;
    }

    /**
     * 获取关联的测试集合
     *
     * @return
     */
    public List<TestPoint> getTestPoints() {
        return testPoints;
    }

    /**
     * 设置关联的测试集合
     *
     * @param testPoints
     */
    public void setTestPoints(List<TestPoint> testPoints) {
        this.testPoints = testPoints;
    }

    /**
     * 获取提交所得的分数
     *
     * @return
     */
    public Integer getSubmitScore() {
        return submitScore;
    }

    /**
     * 设提交所得的分数
     *
     * @param submitScore
     */
    public void setSubmitScore(Integer submitScore) {
        this.submitScore = submitScore;
    }





    /**
     * 获取提交的运行的时间
     *
     * @return
     */
    public Integer getSubmitRuntimeTime() {
        return submitRuntimeTime;
    }



    /**
     * 设置提交的运行内存
     *
     * @param submitRuntimeTime
     */
    public void setSubmitRuntimeTime(Integer submitRuntimeTime) {
        this.submitRuntimeTime = submitRuntimeTime;
    }
    /**
     * 获取提交的运行内存
     *
     * @return
     */
    public Integer getSubmitRuntimeMemory() {
        return submitRuntimeMemory;
    }


    /**
     * 设提交的运行内存
     *
     * @param submitRuntimeMemory
     */
    public void setSubmitRuntimeMemory(Integer submitRuntimeMemory) {
        this.submitRuntimeMemory = submitRuntimeMemory;
    }

    /**
     * 获取提交的结果
     *
     * @return
     */
    public JudgeResult getJudgeResult() {
        return judgeResult;
    }



    /**
     * 设置提交的结果
     *
     * @param judgeResult
     */
    public void setJudgeResult(JudgeResult judgeResult) {
        this.judgeResult = judgeResult;
    }

    @Override
    public String toString() {
        return "Submit{" +
                "submitId=" + submitId +
                ", submitTime=" + submitTime +
                ", submitCode='" + submitCode + '\'' +
                ", submitCompileInfo='" + submitCompileInfo + '\'' +
                ", submitCodeLength=" + submitCodeLength +
                ", submitScore=" + submitScore +
                ", submitRuntimeTime=" + submitRuntimeTime +
                ", submitRuntimeMemory=" + submitRuntimeMemory +
                ", submitPublic=" + submitPublic +
                ", language=" + language +
                ", problem=" + problem +
                ", user=" + user +
                ", testPoints=" + testPoints +
                ", judgeResult=" + judgeResult +
                '}';
    }
}
