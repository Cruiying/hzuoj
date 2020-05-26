package com.hqz.hzuoj.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: HQZ
 * @CreateTime: 2020/5/24 15:54
 * @Description: TODO
 */
@Getter
@Setter
public class UserSolutionVO implements Serializable {

    private String solutionId;

    private String userId;

    private Integer pageSize;

    private Integer page;

    public String getSolutionId() {
        return solutionId;
    }

    public void setSolutionId(String solutionId) {
        this.solutionId = solutionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "UserSolutionVO{" +
                "solutionId='" + solutionId + '\'' +
                ", userId='" + userId + '\'' +
                ", pageSize=" + pageSize +
                ", page=" + page +
                '}';
    }
}
