package com.hqz.hzuoj.bean.contest;

import com.hqz.hzuoj.bean.problem.Problem;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/21 15:00
 * @Description: TODO
 */
@Table(name = "hzuoj_contests_problems")
public class ContestProblem implements Serializable {
    /**
     * 主键
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer contestProblemId;
    /**
     * 关联的比赛
     */
    @Transient
    private Contest contest;
    /**
     * 关联的问题
     */
    @Transient
    private Problem problem;
    /**
     * 题目分数
     */
    @Column
    private Integer contestProblemScore;

    /**
     * 获取主键
     *
     * @return
     */
    public Integer getContestProblemId() {
        return contestProblemId;
    }

    /**
     * 设置主键
     *
     * @param contestProblemId
     */
    public void setContestProblemId(Integer contestProblemId) {
        this.contestProblemId = contestProblemId;
    }

    /**
     * 获取关联的比赛
     *
     * @return
     */
    public Contest getContest() {
        return contest;
    }

    /**
     * 设置关联的比赛
     *
     * @param contest
     */
    public void setContest(Contest contest) {
        this.contest = contest;
    }

    /**
     * 获取关联的问题
     *
     * @return
     */
    public Problem getProblem() {
        return problem;
    }

    /**
     * 设置关联的问题
     *
     * @param problem
     */
    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    /**
     * 获取问题的分数
     *
     * @return
     */
    public Integer getContestProblemScore() {
        return contestProblemScore;
    }

    /**
     * 设置问题的分数
     *
     * @param contestProblemScore
     */
    public void setContestProblemScore(Integer contestProblemScore) {
        this.contestProblemScore = contestProblemScore;
    }

    @Override
    public String toString() {
        return "ContestProblem{" +
                "contestProblemId=" + contestProblemId +
                ", contest=" + contest +
                ", problem=" + problem +
                ", contestProblemScore=" + contestProblemScore +
                '}';
    }
}
