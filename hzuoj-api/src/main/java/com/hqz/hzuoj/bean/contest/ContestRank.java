package com.hqz.hzuoj.bean.contest;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/15 19:50
 * @Description: TODO
 */
@Table(name = "hzuoj_contests_rank")
public class ContestRank implements Serializable {
    /**
     * 主键
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Long contestRankId;
    /**
     * 排名
     */
    @Column
    private Integer rank;

    /**
     * 通过数量
     */
    @Column
    private Integer acceptedTotal;
    /**
     * 总分
     */
    @Column
    private Integer totalScore;
    /**
     * 罚时
     */
    @Column
    private Long punishTime;
    /**
     * 所属比赛
     */
    @Column
    private Integer contestId;
    /**
     * 用户比赛题目提交信息
     */
    @Transient
    private List<ContestRankInfo> contestRankInfos;
    /**
     * 报名表信息
     */
    @Transient
    private ContestApply contestApply;

    public ContestRank() {
        this.totalScore = 0;
        this.acceptedTotal = 0;
        this.punishTime = 0L;
        this.rank = 0;
        this.contestApply = null;
        this.contestId = null;
        this.contestRankInfos = null;
        this.contestRankId = null;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public ContestApply getContestApply() {
        return contestApply;
    }

    public void setContestApply(ContestApply contestApply) {
        this.contestApply = contestApply;
    }

    public Integer getAcceptedTotal() {
        return acceptedTotal;
    }

    public void setAcceptedTotal(Integer acceptedTotal) {
        this.acceptedTotal = acceptedTotal;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Long getPunishTime() {
        return punishTime;
    }

    public void setPunishTime(Long punishTime) {
        this.punishTime = punishTime;
    }

    public Long getContestRankId() {
        return contestRankId;
    }
    public void setContestRankId(Long contestRankId) {
        this.contestRankId = contestRankId;
    }

    public List<ContestRankInfo> getContestRankInfos() {
        return contestRankInfos;
    }
    public void setContestRankInfos(List<ContestRankInfo> contestRankInfos) {
        this.contestRankInfos = contestRankInfos;
    }

    public Integer getContestId() {
        return contestId;
    }

    public void setContestId(Integer contestId) {
        this.contestId = contestId;
    }


    @Override
    public String toString() {
        return "ContestRank{" +
                "contestRankId=" + contestRankId +
                ", rank=" + rank +
                ", acceptedTotal=" + acceptedTotal +
                ", totalScore=" + totalScore +
                ", punishTime=" + punishTime +
                ", contestRankInfos=" + contestRankInfos +
                ", contestApply=" + contestApply +
                ", contestId=" + contestId +
                '}';
    }
}


