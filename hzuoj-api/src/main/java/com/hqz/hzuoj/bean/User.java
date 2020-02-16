package com.hqz.hzuoj.bean;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户实体类
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/11 12:44
 * @Description: TODO
 */

@Table(name = "hzuoj_users")
public class User implements Serializable {
    //用户主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer userId;
    //用户名
    @Column
    private String username;
    //密码
    @Column
    private String password;
    //邮箱
    @Column
    private String email;
    //用户注册时间
    @Column
    private Date registerTime;
    //性别
    @Column
    private String gender;
    //用户头像
    @Column
    private String UserImg;
    //用户分数
    @Column
    private Integer userRating;
    //学校
    @Column
    private String school;
    //个性标签
    @Column
    private String signature;
    //用户通过题目的数量
    private Integer userAcceptedTotal;

    @Transient
    private UserContestInfo userContestInfo;
    @Transient
    private UserProblemInfo userProblemInfo;

    public static class UserContestInfo implements Serializable {
        //用户rank值
        private Integer userRating;
        //用户rank排名
        private Integer userRanking;
        //用户通过题目数排名
        private Integer userAcceptedRanking;
        //用户参加比赛比赛数量
        private Integer contestCount;
        //参加的比赛列表
        private PageInfo<Contest> contests;

        public Integer getUserRating() {
            return userRating;
        }

        public void setUserRating(Integer userRating) {
            this.userRating = userRating;
        }

        public Integer getUserRanking() {
            return userRanking;
        }

        public void setUserRanking(Integer userRanking) {
            this.userRanking = userRanking;
        }

        public Integer getUserAcceptedRanking() {
            return userAcceptedRanking;
        }

        public void setUserAcceptedRanking(Integer userAcceptedRanking) {
            this.userAcceptedRanking = userAcceptedRanking;
        }

        public Integer getContestCount() {
            return contestCount;
        }

        public void setContestCount(Integer contestCount) {
            this.contestCount = contestCount;
        }

        public PageInfo<Contest> getContests() {
            return contests;
        }

        public void setContests(PageInfo<Contest> contests) {
            this.contests = contests;
        }

        @Override
        public String toString() {
            return "UserContestInfo{" +
                    "userRating=" + userRating +
                    ", userRanking=" + userRanking +
                    ", userAcceptedRanking=" + userAcceptedRanking +
                    ", contestCount=" + contestCount +
                    ", contests=" + contests +
                    '}';
        }
    }

    public static class UserProblemInfo implements Serializable {
        //用户挑战的题目数量
        private Integer userChallengeTotal;
        //用户通过题目的数量
        private Integer userAcceptedTotal;
        //用户提交数量
        private Integer submitTotal;
        //用户提交信息
        private PageInfo<Submit> submits;

        public Integer getUserChallengeTotal() {
            return userChallengeTotal;
        }

        public void setUserChallengeTotal(Integer userChallengeTotal) {
            this.userChallengeTotal = userChallengeTotal;
        }

        public Integer getUserAcceptedTotal() {
            return userAcceptedTotal;
        }

        public void setUserAcceptedTotal(Integer userAcceptedTotal) {
            this.userAcceptedTotal = userAcceptedTotal;
        }

        public Integer getSubmitTotal() {
            return submitTotal;
        }

        public void setSubmitTotal(Integer submitTotal) {
            this.submitTotal = submitTotal;
        }

        public PageInfo<Submit> getSubmits() {
            return submits;
        }

        public void setSubmits(PageInfo<Submit> submits) {
            this.submits = submits;
        }

        @Override
        public String toString() {
            return "UserProblemInfo{" +
                    "userChallengeTotal=" + userChallengeTotal +
                    ", userAcceptedTotal=" + userAcceptedTotal +
                    ", submitTotal=" + submitTotal +
                    ", submits=" + submits +
                    '}';
        }
    }

    /**
     * 获取用户主键
     *
     * @return
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户主键
     *
     * @param userId
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取用户名
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取用户邮箱
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置用户邮箱
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取用户注册时间
     *
     * @return
     */
    public Date getRegisterTime() {
        return registerTime;
    }

    /**
     * 设置用户登陆时间
     *
     * @param registerTime
     */
    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    /**
     * 获取用户性别
     *
     * @return
     */
    public String getGender() {
        return gender;
    }

    /**
     * 设置用户性别
     *
     * @param gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }


    /**
     * 获取用户头像
     *
     * @return
     */
    public String getUserImg() {
        return UserImg;
    }

    /**
     * 设置用户头像
     *
     * @param userImg
     */
    public void setUserImg(String userImg) {
        UserImg = userImg;
    }

    /**
     * 获取用户分值
     *
     * @return
     */
    public Integer getUserRating() {
        return userRating;
    }

    /**
     * 设置用户分值
     *
     * @param userRating
     */
    public void setUserRating(Integer userRating) {
        this.userRating = userRating;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public UserContestInfo getUserContestInfo() {
        return userContestInfo;
    }

    public void setUserContestInfo(UserContestInfo userContestInfo) {
        this.userContestInfo = userContestInfo;
    }

    public UserProblemInfo getUserProblemInfo() {
        return userProblemInfo;
    }

    public void setUserProblemInfo(UserProblemInfo userProblemInfo) {
        this.userProblemInfo = userProblemInfo;
    }

    public Integer getUserAcceptedTotal() {
        return userAcceptedTotal;
    }

    public void setUserAcceptedTotal(Integer userAcceptedTotal) {
        this.userAcceptedTotal = userAcceptedTotal;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", registerTime=" + registerTime +
                ", gender='" + gender + '\'' +
                ", UserImg='" + UserImg + '\'' +
                ", userRating=" + userRating +
                ", school='" + school + '\'' +
                ", signature='" + signature + '\'' +
                ", userAcceptedTotal=" + userAcceptedTotal +
                ", userContestInfo=" + userContestInfo +
                ", userProblemInfo=" + userProblemInfo +
                '}';
    }
}
