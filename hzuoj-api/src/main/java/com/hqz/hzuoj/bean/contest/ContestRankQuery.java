package com.hqz.hzuoj.bean.contest;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: HQZ
 * @CreateTime: 2020/2/17 20:59
 * @Description: TODO
 */
public class ContestRankQuery implements Serializable {
    /**
     * 指定用排名
     */
    private Integer userId;
    /**
     * 模糊搜索用户名和学校
     */
    private String indistinct;

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

    @Override
    public String toString() {
        return "ContestRankQuery{" +
                "userId=" + userId +
                ", indistinct='" + indistinct + '\'' +
                '}';
    }
}
