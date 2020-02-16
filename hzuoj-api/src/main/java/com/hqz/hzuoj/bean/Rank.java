package com.hqz.hzuoj.bean;

import java.io.Serializable;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/20 8:37
 * @Description: TODO
 */
public class Rank implements Serializable {

    private Integer rank;
    private User user;
    private Integer userAccepted;

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getUserAccepted() {
        return userAccepted;
    }

    public void setUserAccepted(Integer userAccepted) {
        this.userAccepted = userAccepted;
    }

    @Override
    public String toString() {
        return "Rank{" +
                "rank=" + rank +
                ", user=" + user +
                ", userAccepted=" + userAccepted +
                '}';
    }
}
