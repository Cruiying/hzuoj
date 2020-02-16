package com.hqz.hzuoj.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 比赛实体类
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/11 11:54
 * @Description: TODO
 */
@Table(name = "hzuoj_contests")
public class Contest implements Serializable {
    /**
     * 比赛主键
     */

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer contestId;
    /**
     * 比赛名称
     */

    @Column
    private String contestName;
    /**
     * 比赛开始时间
     */
    @Column
    private Date contestStart;
    /**
     * 比赛结束时间
     */
    @Column
    private Date contestEnd;
    /**
     * 比赛说明
     */
    @Column
    private String contestExplain;
    /**
     * 比赛创建时间
     */

    @Column
    private Date contestCreateTime;
    /**
     * 比赛开始报名时间
     */

    @Column
    private Date contestApplyStartTime;
    /**
     * 比赛报名结束时间
     */

    @Column
    private Date contestApplyEndTime;
    /**
     * 比赛状态（还未开始，进行中，已经结束）
     */
    @Column
    private Integer contestStatus;
    /**
     * 比赛是否公开
     */
    @Column
    private Integer contestPublic;
    /**
     * 比赛邀请码
     */
    @Column
    private String contestCode;
    /**
     * 比赛报名人数
     */
    @Column
    private Integer contestApplyCount;
    /**
     * 比赛时长
     */
    @Column
    private Long contestTimeLength;

    /**
     * 比赛报名状态
     */
    @Column
    private Integer contestApplyStatus;
    /**
     * 是否计算Rank
     */
    @Column
    private Integer contestIsRank;

    /**
     * Rank更新状态
     */
    @Column
    private Integer contestRankStatus;
    /**
     * 比赛排名是否完成
     */
    @Column
    private  Boolean contestRankIsFinish;

    /**
     * 创建比赛的管理员
     */
    @Transient
    private Admin admin;
    /**
     * 比赛类型
     */

    @Transient
    private ContestType contestType;
    /**
     * 该比赛的题目集合
     */
    @Transient
    private List<ContestProblem> contestProblems;
    /**
     * 比赛提交集合
     */
    @Transient
    private List<Submit> submits;

    /**
     * 比赛报名表集合
     */
    @Transient
    private List<ContestApply> contestApplies;


    /**
     * 获取比赛主键
     *
     * @return
     */
    public Integer getContestId() {
        return contestId;
    }

    /**
     * 设置比赛主键
     *
     * @param contestId
     */
    public void setContestId(Integer contestId) {
        this.contestId = contestId;
    }

    /**
     * 获取比赛名称
     *
     * @return
     */
    public String getContestName() {
        return contestName;
    }

    /**
     * 设置比赛名称
     *
     * @param contestName
     */
    public void setContestName(String contestName) {
        this.contestName = contestName;
    }

    /**
     * 获取比赛类型
     *
     * @return
     */
    public ContestType getContestType() {
        return contestType;
    }

    /**
     * 设置比赛类型
     *
     * @param contestType
     */
    public void setContestType(ContestType contestType) {
        this.contestType = contestType;
    }

    /**
     * 获取比赛开始时间
     *
     * @return
     */
    public Date getContestStart() {
        return contestStart;
    }

    /**
     * 设置比赛开始时间
     *
     * @param contestStart
     */
    public void setContestStart(Date contestStart) {
        this.contestStart = contestStart;
    }

    /**
     * 获取比赛结束时间
     *
     * @return
     */
    public Date getContestEnd() {
        return contestEnd;
    }

    /**
     * 设置比赛结束时间
     *
     * @param contestEnd
     */
    public void setContestEnd(Date contestEnd) {
        this.contestEnd = contestEnd;
    }

    /**
     * 获取比赛说明
     *
     * @return
     */
    public String getContestExplain() {
        return contestExplain;
    }

    /**
     * 设比赛说明
     *
     * @param contestExplain
     */
    public void setContestExplain(String contestExplain) {
        this.contestExplain = contestExplain;
    }

    /**
     * 获取比赛状态
     *
     * @return
     */
    public Integer getContestStatus() {
        return contestStatus;
    }

    /**
     * 设置比赛状态
     *
     * @param contestStatus
     */
    public void setContestStatus(Integer contestStatus) {
        this.contestStatus = contestStatus;
    }

    /**
     * 获取比赛时长
     *
     * @return
     */
    public Long getContestTimeLength() {
        return contestTimeLength;
    }

    /**
     * 设置比赛时长
     *
     * @param contestTimeLength
     */
    public void setContestTimeLength(Long contestTimeLength) {
        this.contestTimeLength = contestTimeLength;
    }

    /**
     * 获取比赛是否公共
     *
     * @return
     */
    public Integer getContestPublic() {
        return contestPublic;
    }

    /**
     * 设置比赛是否公开
     *
     * @param contestPublic
     */
    public void setContestPublic(Integer contestPublic) {
        this.contestPublic = contestPublic;
    }

    /**
     * 获取比赛邀请码
     * @return
     */
    public String getContestCode() {
        return contestCode;
    }

    /**
     * 设置比赛邀请码
     * @param contestCode
     */
    public void setContestCode(String contestCode) {
        this.contestCode = contestCode;
    }

    /**
     * 获取比赛排名是否完成
     * @return
     */
    public Boolean getContestRankIsFinish() {
        return contestRankIsFinish;
    }

    /**
     * 设置比赛排名是否完成
     * @param contestRankIsFinish
     */
    public void setContestRankIsFinish(Boolean contestRankIsFinish) {
        this.contestRankIsFinish = contestRankIsFinish;
    }

    /**
     * 获取比赛创建者
     *
     * @return
     */
    public Admin getAdmin() {
        return admin;
    }

    /**
     * 设置比赛创建者
     *
     * @param admin
     */
    public void setAdmin(Admin admin) {
        this.admin = admin;
    }


    /**
     * 获取比赛创建时间
     *
     * @return
     */
    public Date getContestCreateTime() {
        return contestCreateTime;
    }

    /**
     * 设置比赛创建时间
     *
     * @param contestCreateTime
     */
    public void setContestCreateTime(Date contestCreateTime) {
        this.contestCreateTime = contestCreateTime;
    }

    /**
     * 获取比赛注册开始时间
     *
     * @return
     */
    public Date getContestApplyStartTime() {
        return contestApplyStartTime;
    }

    /**
     * 获取报名人数
     *
     * @return
     */
    public Integer getContestApplyCount() {
        return contestApplyCount;
    }


    /**
     * 设置报名人数
     *
     * @param contestApplyCount
     */
    public void setContestApplyCount(Integer contestApplyCount) {
        this.contestApplyCount = contestApplyCount;
    }

    /**
     * 设置比赛注册开始时间
     *
     * @param contestApplyStartTime
     */
    public void setContestApplyStartTime(Date contestApplyStartTime) {
        this.contestApplyStartTime = contestApplyStartTime;
    }

    /**
     * 获取比赛比赛报名状态
     *
     * @return
     */
    public Integer getContestApplyStatus() {
        return contestApplyStatus;
    }

    /**
     * 设置比赛报名状态
     *
     * @param contestApplyStatus
     */
    public void setContestApplyStatus(Integer contestApplyStatus) {
        this.contestApplyStatus = contestApplyStatus;
    }

    /**
     * 获取比赛报名集合
     *
     * @return
     */
    public List<ContestApply> getContestApplies() {
        return contestApplies;
    }

    /**
     * 设置比赛报名集合
     *
     * @param contestApplies
     */
    public void setContestApplies(List<ContestApply> contestApplies) {
        this.contestApplies = contestApplies;
    }

    /**
     * 获取比赛结束时间
     *
     * @return
     */
    public Date getContestApplyEndTime() {
        return contestApplyEndTime;
    }

    /**
     * 设置比赛结束数据
     *
     * @param contestApplyEndTime
     */
    public void setContestApplyEndTime(Date contestApplyEndTime) {
        this.contestApplyEndTime = contestApplyEndTime;
    }

    /**
     * 获取比赛问题集合
     *
     * @return
     */
    public List<ContestProblem> getContestProblems() {
        return contestProblems;
    }

    /**
     * 设置比赛的问题集合
     *
     * @param contestProblems
     */
    public void setContestProblems(List<ContestProblem> contestProblems) {
        this.contestProblems = contestProblems;
    }

    /**
     * 获取比赛提交记录集合
     *
     * @return
     */
    public List<Submit> getSubmits() {
        return submits;
    }

    /**
     * 设置比赛提交记录集合
     *
     * @param submits
     */
    public void setSubmits(List<Submit> submits) {
        this.submits = submits;
    }

    /**
     * 获取比赛是否计算rank
     *
     * @return
     */
    public Integer getContestIsRank() {
        return contestIsRank;
    }

    /**
     * 设置比赛是否计算rank
     *
     * @param contestIsRank
     */
    public void setContestIsRank(Integer contestIsRank) {
        this.contestIsRank = contestIsRank;
    }

    /**
     * 获取比赛rank状态
     *
     * @return
     */
    public Integer getContestRankStatus() {
        return contestRankStatus;
    }

    /**
     * 设置比赛rank状态
     *
     * @param contestRankStatus
     */
    public void setContestRankStatus(Integer contestRankStatus) {
        this.contestRankStatus = contestRankStatus;
    }

    @Override
    public String toString() {
        return "Contest{" +
                "contestId=" + contestId +
                ", contestName='" + contestName + '\'' +
                ", contestStart=" + contestStart +
                ", contestEnd=" + contestEnd +
                ", contestExplain='" + contestExplain + '\'' +
                ", contestCreateTime=" + contestCreateTime +
                ", contestApplyStartTime=" + contestApplyStartTime +
                ", contestApplyEndTime=" + contestApplyEndTime +
                ", contestStatus=" + contestStatus +
                ", contestPublic=" + contestPublic +
                ", contestCode='" + contestCode + '\'' +
                ", contestApplyCount=" + contestApplyCount +
                ", contestTimeLength=" + contestTimeLength +
                ", contestApplyStatus=" + contestApplyStatus +
                ", contestIsRank=" + contestIsRank +
                ", contestRankStatus=" + contestRankStatus +
                ", contestRankIsFinish=" + contestRankIsFinish +
                ", admin=" + admin +
                ", contestType=" + contestType +
                ", contestProblems=" + contestProblems +
                ", submits=" + submits +
                ", contestApplies=" + contestApplies +
                '}';
    }
}
