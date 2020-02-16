package com.hqz.hzuoj.bean;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 比赛报名实体类
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/11 19:18
 * @Description: TODO
 */
@Table(name = "hzuoj_contests_applys")
public class ContestApply  implements Serializable {
    //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer contestApplyId;
    //备注名称
    @Column
    private String remarkName;
    //报名时间
    @Column
    private Date applyTime;
    //报的的比赛
    @Column
    private Integer contestId;
    //报名的用户
    @Column
    private Integer userId;
    //关联用户
    @Transient
    private User user;

    /**
     * 获取主键
     *
     * @return
     */
    public Integer getContestApplyId() {
        return contestApplyId;
    }

    /**
     * 设置主键
     *
     * @param contestApplyId
     */
    public void setContestApplyId(Integer contestApplyId) {
        this.contestApplyId = contestApplyId;
    }

    /**
     * 获取报名的比赛
     *
     * @return
     */
    public Integer getContestId() {
        return contestId;
    }






    /**
     * 设置报名的比赛
     *
     * @param contestId
     */
    public void setContestId(Integer contestId) {
        this.contestId = contestId;
    }

    /**
     * 获取报名的用户
     *
     * @return
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置报名的用户
     *
     * @param userId
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取备注的名称
     *
     * @return
     */
    public String getRemarkName() {
        return remarkName;
    }

    /**
     * 设置备注的名称
     *
     * @param remarkName
     */
    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    /**
     * 获取报名时间
     *
     * @return
     */
    public Date getApplyTime() {
        return applyTime;
    }

    /**
     * 设置报名时间
     *
     * @param applyTime
     */
    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ContestApply{" +
                "contestApplyId=" + contestApplyId +
                ", remarkName='" + remarkName + '\'' +
                ", applyTime=" + applyTime +
                ", contestId=" + contestId +
                ", userId=" + userId +
                ", user=" + user +
                '}';
    }
}
