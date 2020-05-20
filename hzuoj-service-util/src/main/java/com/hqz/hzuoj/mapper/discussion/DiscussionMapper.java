package com.hqz.hzuoj.mapper.discussion;

import com.hqz.hzuoj.bean.discussion.Discussion;
import com.hqz.hzuoj.bean.discussion.DiscussionComment;
import com.hqz.hzuoj.bean.discussion.DiscussionQuery;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/17 13:21
 * @Description: TODO
 */
public interface DiscussionMapper extends Mapper<Discussion> {
    Discussion getDiscussion(Integer discussionId);

    void saveDiscussion(Discussion discussion);

    List<Discussion> getDiscussions(DiscussionQuery discussionQuery);

    void updateDiscussion(Discussion discussion);

    void saveDiscussionComment(DiscussionComment discussionComment);

    DiscussionComment getDiscussionComment(Integer commentId);

    List<DiscussionComment> getDiscussionComments(Integer discussionId);

    void updateDiscussionCount(Integer discussionId, Integer replyCount, Integer commendCount, Integer browseCount);

    Integer commentDelete(DiscussionComment comment);

    void deleteComment(Integer commentId);
}
