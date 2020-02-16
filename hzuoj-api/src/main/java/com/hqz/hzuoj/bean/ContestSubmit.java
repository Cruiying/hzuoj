package com.hqz.hzuoj.bean;

import org.springframework.stereotype.Controller;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 比赛提交
 *
 * @Author: HQZ
 * @CreateTime: 2020/1/14 11:32
 * @Description: TODO
 */
@Table(name = "hzuoj_contests_submits")
public class ContestSubmit implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer contestSubmitId;
    @Transient
    private Submit submit;
    @Transient
    private Contest contest;

    public Integer getContestSubmitId() {
        return contestSubmitId;
    }

    public void setContestSubmitId(Integer contestSubmitId) {
        this.contestSubmitId = contestSubmitId;
    }

    public Submit getSubmit() {
        return submit;
    }

    public void setSubmit(Submit submit) {
        this.submit = submit;
    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }

    @Override
    public String toString() {
        return "ContestSubmit{" +
                "contestSubmitId=" + contestSubmitId +
                ", submit=" + submit +
                ", contest=" + contest +
                '}';
    }
}
