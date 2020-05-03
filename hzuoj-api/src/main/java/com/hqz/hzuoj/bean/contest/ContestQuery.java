package com.hqz.hzuoj.bean.contest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 比赛查询条件
 * @Author: HQZ
 * @CreateTime: 2019/12/22 21:33
 * @Description: TODO
 */
public class ContestQuery implements Serializable {
    /**
     * 比赛名称
     */
    private String contestName;
    /**
     * 比赛创建者
     */
    private Integer adminId;
    /**
     * 比赛开始时间的最开开始
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date StartTime;
    /**
     * 比赛开始时间的最后时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date EndTime;
    /**
     * 比赛赛制
     */
    private Integer contestTypeId;
    /**
     * 比赛是否公开
     */
    private Integer contestPublic;
    /**
     * 比赛状态
     */
    private Integer contestStatus;
    /**
     * 状态
     */
    private Integer signUpFilter;
    /**
     * 分类
     */
    private Integer categoryFilter;
    /**
     * 用户
     */
    private Integer userId;

    /**
     * 获取比赛名称
     * @return
     */
    public String getContestName() {
        return contestName;
    }

    /**
     * 设置比赛名称
     * @param contestName
     */
    public void setContestName(String contestName) {
        this.contestName = contestName;
    }

    /**
     * 获取比赛创建者
     * @return
     */
    public Integer getAdminId() {
        return adminId;
    }

    /**
     * 设置比赛创建者
     * @param adminId
     */
    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    /**
     * 获取比赛开始时间的最开始时间
     * @return
     */
    public Date getStartTime() {
        return StartTime;
    }

    /**
     * 设置比赛开始时间的最开始时间
     * @param startTime
     */
    public void setStartTime(Date startTime) {
        StartTime = startTime;
    }

    /**
     * 获取比赛的开始时间的最结尾时间
     * @return
     */
    public Date getEndTime() {
        return EndTime;
    }

    /**
     * 设置比赛的开始时间的最结尾时间
     * @param endTime
     */
    public void setEndTime(Date endTime) {
        EndTime = endTime;
    }

    /**
     * 获取比赛类型
     * @return
     */
    public Integer getContestTypeId() {
        return contestTypeId;
    }

    /**
     * 设置比赛类型
     * @param contestTypeId
     */
    public void setContestTypeId(Integer contestTypeId) {
        this.contestTypeId = contestTypeId;
    }

    /**
     * 获取比赛是否公开
     * @return
     */
    public Integer getContestPublic() {
        return contestPublic;
    }
    /**
     * 设置比赛是否公开
     * @param contestPublic
     */
    public void setContestPublic(Integer contestPublic) {
        this.contestPublic = contestPublic;
    }

    /**
     * 获取比赛转态
     * @return
     */
    public Integer getContestStatus() {
        return contestStatus;
    }
    /**
     * 设置比赛状态
     * @param contestStatus
     */
    public void setContestStatus(Integer contestStatus) {
        this.contestStatus = contestStatus;
    }


    public Integer getSignUpFilter() {
        return signUpFilter;
    }

    public void setSignUpFilter(Integer signUpFilter) {
        this.signUpFilter = signUpFilter;
    }

    public Integer getCategoryFilter() {
        return categoryFilter;
    }

    public void setCategoryFilter(Integer categoryFilter) {
        this.categoryFilter = categoryFilter;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ContestQuery{" +
                "contestName='" + contestName + '\'' +
                ", adminId=" + adminId +
                ", StartTime=" + StartTime +
                ", EndTime=" + EndTime +
                ", contestTypeId=" + contestTypeId +
                ", contestPublic=" + contestPublic +
                ", contestStatus=" + contestStatus +
                ", signUpFilter=" + signUpFilter +
                ", categoryFilter=" + categoryFilter +
                ", userId=" + userId +
                '}';
    }
}
