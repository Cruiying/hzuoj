package com.hqz.hzuoj.bean;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/16 21:48
 * @Description: TODO
 */
@Table(name = "hzuoj_contests_rank_info")
public class ContestRankInfo implements Serializable {
    /**
     * 主键
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Long contestRankInfoId;
    /**
     * 题目id
     */
    @Column
    private Integer problemId;
    /**
     * 题目总分数
     */
    @Transient
    private Integer contestProblemScore;
    /**
     * 是否是第一个通过该题目
     */
    @Column
    private Boolean firstAccepted;
    /**
     * 提交数量
     */
    @Column
    private Integer submitTotal;
    /**
     * 罚时
     */
    @Column
    private Long punishTime;
    /**
     * 分数
     */
    @Column
    private Integer score;
    /**
     * 是否通过该题目
     */
    @Column
    private Boolean accepted;
    /**
     * 所属排名
     */
    @Column
    private Long contestRankId;

    public Boolean isFirstAccepted() {
        return firstAccepted;
    }

    public void setFirstAccepted(Boolean firstAccepted) {
        this.firstAccepted = firstAccepted;
    }

    public Integer getSubmitTotal() {
        return submitTotal;
    }

    public void setSubmitTotal(Integer submitTotal) {
        this.submitTotal = submitTotal;
    }

    public Long getPunishTime() {
        return punishTime;
    }

    public void setPunishTime(Long punishTime) {
        this.punishTime = punishTime;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Integer getProblemId() {
        return problemId;
    }

    public void setProblemId(Integer problemId) {
        this.problemId = problemId;
    }

    public Integer getContestProblemScore() {
        return contestProblemScore;
    }

    public void setContestProblemScore(Integer contestProblemScore) {
        this.contestProblemScore = contestProblemScore;
    }

    public Long getContestRankId() {
        return contestRankId;
    }

    public void setContestRankId(Long contestRankId) {
        this.contestRankId = contestRankId;
    }

    public Long getContestRankInfoId() {
        return contestRankInfoId;
    }

    public void setContestRankInfoId(Long contestRankInfoId) {
        this.contestRankInfoId = contestRankInfoId;
    }

    @Override
    public String toString() {
        return "ContestRankInfo{" +
                "contestRankInfoId=" + contestRankInfoId +
                ", problemId=" + problemId +
                ", contestProblemScore=" + contestProblemScore +
                ", firstAccepted=" + firstAccepted +
                ", submitTotal=" + submitTotal +
                ", punishTime=" + punishTime +
                ", score=" + score +
                ", accepted=" + accepted +
                ", contestRankId=" + contestRankId +
                '}';
    }
}
