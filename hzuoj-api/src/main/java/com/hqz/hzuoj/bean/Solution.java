package com.hqz.hzuoj.bean;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 题解实体类
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/11 20:35
 * @Description: TODO
 */
public class Solution implements Serializable {
    //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer solutionId;
    //题解内容
    @Column
    private String solutionContent;
    @Column
    private String solutionTitle;
    //题解发表时间
    @Column
    private Date solutionCreateTime;
    //修改发表时间
    @Column
    private Date solutionModifyTime;
    //题解关联的问题
    @Transient
    private Problem problem;
    //题解关联的用户
    @Transient
    private User user;
    //题解状态
    @Transient
    private SolutionStatus solutionStatus;

    /**
     * 获取题解主键
     * @return
     */
    public Integer getSolutionId() {
        return solutionId;
    }

    /**
     * 设置题解主键
     * @param solutionId
     */
    public void setSolutionId(Integer solutionId) {
        this.solutionId = solutionId;
    }

    /**
     * 获取题解关联的问题
     * @return
     */
    public Problem getProblem() {
        return problem;
    }

    /**
     * 设置题解关联的问题
     * @param problem
     */
    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    /**
     * 获取题解发表的用户
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     * 设置题解发表的用户
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 获取题解内容
     * @return
     */
    public String getSolutionContent() {
        return solutionContent;
    }

    /**
     * 设置题解内容
     * @param solutionContent
     */
    public void setSolutionContent(String solutionContent) {
        this.solutionContent = solutionContent;
    }

    /**
     * 获取题解发表时间
     * @return
     */
    public Date getSolutionCreateTime() {
        return solutionCreateTime;
    }

    /**
     * 设置题解发表时间
     * @param solutionCreateTime
     */
    public void setSolutionCreateTime(Date solutionCreateTime) {
        this.solutionCreateTime = solutionCreateTime;
    }

    public Date getSolutionModifyTime() {
        return solutionModifyTime;
    }

    public void setSolutionModifyTime(Date solutionModifyTime) {
        this.solutionModifyTime = solutionModifyTime;
    }

    public String getSolutionTitle() {
        return solutionTitle;
    }

    public void setSolutionTitle(String solutionTitle) {
        this.solutionTitle = solutionTitle;
    }

    /**
     * 获取题解状状态
     * @return
     */
    public SolutionStatus getSolutionStatus() {
        return solutionStatus;
    }

    /**
     * 设置题解状态
     * @param solutionStatus
     */
    public void setSolutionStatus(SolutionStatus solutionStatus) {
        this.solutionStatus = solutionStatus;
    }

    @Override
    public String toString() {
        return "Solution{" +
                "solutionId=" + solutionId +
                ", solutionContent='" + solutionContent + '\'' +
                ", solutionCreateTime=" + solutionCreateTime +
                ", solutionModifyTime=" + solutionModifyTime +
                ", problem=" + problem +
                ", user=" + user +
                ", solutionStatus=" + solutionStatus +
                '}';
    }
}
