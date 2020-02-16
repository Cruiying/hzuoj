package com.hqz.hzuoj.bean;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 比赛类型
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/11 12:26
 * @Description: TODO
 */
@Table(name = "hzuoj_contest_types")
public class ContestType implements Serializable {
    //比赛类型主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer contestTypeId;
    //比赛类型名称
    @Column
    private String contestTypeName;
    //比赛类型说明
    @Column
    private String contestTypeExplain;

    /**
     * 获取比赛类型主键
     * @return
     */
    public Integer getContestTypeId() {
        return contestTypeId;
    }

    /**
     * 设置比赛类型主键
     * @param contestTypeId
     */
    public void setContestTypeId(Integer contestTypeId) {
        this.contestTypeId = contestTypeId;
    }

    /**
     * 获取比赛类型名称
     * @return
     */
    public String getContestTypeName() {
        return contestTypeName;
    }

    /**
     * 设比赛类型名称
     * @param contestTypeName
     */
    public void setContestTypeName(String contestTypeName) {
        this.contestTypeName = contestTypeName;
    }

    /**
     * 获取比赛类型说明
     * @return
     */
    public String getContestTypeExplain() {
        return contestTypeExplain;
    }

    /**
     * 设置比赛类型说明
     * @param contestTypeExplain
     */
    public void setContestTypeExplain(String contestTypeExplain) {
        this.contestTypeExplain = contestTypeExplain;
    }

    @Override
    public String toString() {
        return "ContestType{" +
                "contestType=" + contestTypeId +
                ", contestTypeName='" + contestTypeName + '\'' +
                ", contestTypeExplain='" + contestTypeExplain + '\'' +
                '}';
    }
}
