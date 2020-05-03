package com.hqz.hzuoj.bean.discussion;

import java.io.Serializable;

/**
 * @Author: HQZ
 * @CreateTime: 2020/2/15 9:59
 * @Description: TODO
 */
public class DiscussionQuery implements Serializable {

    private Integer discussionId;

    private Integer userId;

    private String discussionTitle;

    private Integer order;

    public DiscussionQuery() {
    }

    public Integer getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(Integer discussionId) {
        this.discussionId = discussionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDiscussionTitle() {
        return discussionTitle;
    }

    public void setDiscussionTitle(String discussionTitle) {
        this.discussionTitle = discussionTitle;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "DiscussionQuery{" +
                "discussionId=" + discussionId +
                ", userId=" + userId +
                ", discussionTitle='" + discussionTitle + '\'' +
                ", order=" + order +
                '}';
    }
}
