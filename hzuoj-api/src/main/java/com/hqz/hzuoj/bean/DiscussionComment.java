package com.hqz.hzuoj.bean;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/17 19:10
 * @Description: TODO
 */
@Table(name = "hzuoj_discussions_comments")
public class DiscussionComment implements Serializable {
    //评论主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer commentId;
    //评论内容
    @Column
    private String commentContent;
    //评论时间
    @Column
    private Date commentTime;
    //所属用户
    @Transient
    private User user;
    //评论所属的讨论
    @Transient
    private Discussion discussion;

    @Transient
    private DiscussionComment parentComment;

    @Transient
    private List<DiscussionComment> comments;

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Discussion getDiscussion() {
        return discussion;
    }

    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public Date getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Date commentTime) {
        this.commentTime = commentTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<DiscussionComment> getComments() {
        return comments;
    }

    public void setComments(List<DiscussionComment> comments) {
        this.comments = comments;
    }

    public DiscussionComment getParentComment() {
        return parentComment;
    }

    public void setParentComment(DiscussionComment parentComment) {
        this.parentComment = parentComment;
    }

    @Override
    public String toString() {
        return "DiscussionComment{" +
                "commentId=" + commentId +
                ", commentContent='" + commentContent + '\'' +
                ", commentTime=" + commentTime +
                ", user=" + user +
                ", discussion=" + discussion +
                ", parentComment=" + parentComment +
                ", comments=" + comments +
                '}';
    }
}
