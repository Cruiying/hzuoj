package com.hqz.hzuoj.bean.discussion;

import com.hqz.hzuoj.bean.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子实体类
 * @Author: HQZ
 * @CreateTime: 2020/1/17 12:55
 * @Description: TODO
 */
@Table(name = "hzuoj_discussions")
public class Discussion implements Serializable {
    //帖子主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column
    private Integer discussionId;
    //帖子内容
    @Column
    private String discussionContent;
    //帖子创建时间
    @Column
    private Date discussionCreateTime;
    //帖子修改时间
    @Column
    private Date discussionModifyTime;
    //帖子标题
    @Column
    private String discussionTitle;
    //帖子是否置顶
    @Column
    private Integer discussionTop;
    //帖子回复数量
    @Column
    private Integer discussionReplyCount;
    //帖子点赞数量
    @Column
    private Integer discussionCommendCount;
    //帖子浏览数量
    @Column
    private Integer discussionBrowseCount;
    //帖子所属用户
    @Transient
    private User user;
    //评论
    @Transient
    private List<DiscussionComment> comments;

    /**
     * 获取帖子主键
     * @return
     */
    public Integer getDiscussionId() {
        return discussionId;
    }

    /**
     * 设置帖子主键
     * @param discussionId
     */
    public void setDiscussionId(Integer discussionId) {
        this.discussionId = discussionId;
    }

    /**
     * 获取帖子内容
     * @return
     */
    public String getDiscussionContent() {
        return discussionContent;
    }

    /**
     * 设置帖子内容
     * @param discussionContent
     */
    public void setDiscussionContent(String discussionContent) {
        this.discussionContent = discussionContent;
    }

    /**
     * 获取帖子创建时间
     * @return
     */
    public Date getDiscussionCreateTime() {
        return discussionCreateTime;
    }

    /**
     * 设置帖子创建时间
     * @param discussionCreateTime
     */
    public void setDiscussionCreateTime(Date discussionCreateTime) {
        this.discussionCreateTime = discussionCreateTime;
    }

    /**
     * 获取帖子修改时间
     * @return
     */
    public Date getDiscussionModifyTime() {
        return discussionModifyTime;
    }

    /**
     * 设置帖子修改时间
     * @param discussionModifyTime
     */
    public void setDiscussionModifyTime(Date discussionModifyTime) {
        this.discussionModifyTime = discussionModifyTime;
    }

    /**
     * 获取帖子标题
     * @return
     */
    public String getDiscussionTitle() {
        return discussionTitle;
    }

    /**
     * 设置帖子标题
     * @param discussionTitle
     */
    public void setDiscussionTitle(String discussionTitle) {
        this.discussionTitle = discussionTitle;
    }

    /**
     * 获取帖子是置顶值
     * @return
     */
    public Integer getDiscussionTop() {
        return discussionTop;
    }

    /**
     * 设置帖子置顶值
     * @param discussionTop
     */
    public void setDiscussionTop(Integer discussionTop) {
        this.discussionTop = discussionTop;
    }

    /**
     * 获取帖子回复数量
     * @return
     */
    public Integer getDiscussionReplyCount() {
        return discussionReplyCount;
    }

    /**
     * 设置帖子回复数量
     * @param discussionReplyCount
     */
    public void setDiscussionReplyCount(Integer discussionReplyCount) {
        this.discussionReplyCount = discussionReplyCount;
    }

    /**
     * 获取帖子点赞数量
     * @return
     */
    public Integer getDiscussionCommendCount() {
        return discussionCommendCount;
    }

    /**
     * 设置帖子点赞数量
     * @param discussionCommendCount
     */
    public void setDiscussionCommendCount(Integer discussionCommendCount) {
        this.discussionCommendCount = discussionCommendCount;
    }

    /**
     * 获取帖子浏览数量
     * @return
     */
    public Integer getDiscussionBrowseCount() {
        return discussionBrowseCount;
    }

    /**
     * 设帖子浏览数量
     * @param discussionBrowseCount
     */
    public void setDiscussionBrowseCount(Integer discussionBrowseCount) {
        this.discussionBrowseCount = discussionBrowseCount;
    }

    /**
     * 获取帖子所属用户
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     * 设置帖子所属用户
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }

    public List<DiscussionComment> getComments() {
        return comments;
    }

    public void setComments(List<DiscussionComment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Discussion{" +
                "discussionId=" + discussionId +
                ", discussionContent='" + discussionContent + '\'' +
                ", discussionCreateTime=" + discussionCreateTime +
                ", discussionModifyTime=" + discussionModifyTime +
                ", discussionTitle='" + discussionTitle + '\'' +
                ", discussionTop=" + discussionTop +
                ", discussionReplyCount=" + discussionReplyCount +
                ", discussionCommendCount=" + discussionCommendCount +
                ", discussionBrowseCount=" + discussionBrowseCount +
                ", user=" + user +
                ", comments=" + comments +
                '}';
    }
}
