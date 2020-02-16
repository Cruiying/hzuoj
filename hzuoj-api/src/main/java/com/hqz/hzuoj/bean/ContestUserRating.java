package com.hqz.hzuoj.bean;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/29 9:05
 * @Description: TODO
 */
@Table(name = "hzuoj_contests_users_rank")
public class ContestUserRating implements Serializable {
    /**
     * 主键
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer id;
    /**
     * 用户
     */
    @Column
    private Integer userId;
    /**
     * 比赛
     */
    @Column
    private Integer contestId;
    /**
     * rank值
     */
    @Column
    private Integer rank;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getContestId() {
        return contestId;
    }

    public void setContestId(Integer contestId) {
        this.contestId = contestId;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ContestUserRank{" +
                "id=" + id +
                ", userId=" + userId +
                ", contestId=" + contestId +
                ", rank=" + rank +
                '}';
    }
}
