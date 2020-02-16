package com.hqz.hzuoj.bean;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/** 回复实体类
 * @Author: HQZ
 * @CreateTime: 2020/1/18 9:46
 * @Description: TODO
 */
@Table(name = "hzuoj_discussions_replys")
public class DiscussionReply implements Serializable {
    //回复主键
    private Integer replyId;
    //回复内容
    private String replyContent;
    //回复时间
    private Date replyTime;
    //回复所属用户
    private User user;
    //是否有父亲回复
    private DiscussionReply parentReply;
    //回复属于评论
    private DiscussionComment comment;

    public Integer getReplyId() {
        return replyId;
    }

    public void setReplyId(Integer replyId) {
        this.replyId = replyId;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public Date getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DiscussionReply getParentReply() {
        return parentReply;
    }

    public void setParentReply(DiscussionReply parentReply) {
        this.parentReply = parentReply;
    }

    public DiscussionComment getComment() {
        return comment;
    }

    public void setComment(DiscussionComment comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "DiscussionReply{" +
                "replyId=" + replyId +
                ", replyContent='" + replyContent + '\'' +
                ", replyTime=" + replyTime +
                ", user=" + user +
                ", parentReply=" + parentReply +
                ", comment=" + comment +
                '}';
    }
}
