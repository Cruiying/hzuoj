package com.hqz.hzuoj.bean.user;

import java.io.Serializable;

/**
 * @Author: HQZ
 * @CreateTime: 2020/2/15 12:15
 * @Description: TODO
 */
public class RankingQuery implements Serializable {
    //指定用户
    private Integer userId;
    //模糊查询用户名和学校
    private String indistinct;
    //排序（1：rating排序，1：题目数量排序）
    private Integer order;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getIndistinct() {
        return indistinct;
    }

    public void setIndistinct(String indistinct) {
        this.indistinct = indistinct;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "RankingQuery{" +
                "userId=" + userId +
                ", indistinct='" + indistinct + '\'' +
                ", order=" + order +
                '}';
    }
}
