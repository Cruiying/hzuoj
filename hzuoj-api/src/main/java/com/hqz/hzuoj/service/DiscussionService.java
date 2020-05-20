package com.hqz.hzuoj.service;

import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.discussion.Discussion;
import com.hqz.hzuoj.bean.discussion.DiscussionComment;
import com.hqz.hzuoj.bean.discussion.DiscussionQuery;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/17 13:17
 * @Description: TODO
 */
public interface DiscussionService {

    Discussion getDiscussion(Integer discussionId);

    Discussion saveDiscussion(Discussion discussion);

    PageInfo<Discussion> getDiscussions(Integer page, DiscussionQuery discussionQuery);

    List<DiscussionComment> getDiscussionComments(Integer discussionId);

    DiscussionComment saveDiscussionComment(DiscussionComment comment);

    Discussion updateDiscussionTop(Discussion discussion);

    boolean deleteDiscussion(Integer discussionId);

    boolean discussionDelete(Integer userId, Integer discussionId);

    String commentDelete(DiscussionComment comment);
}
