package com.hqz.hzuoj.service.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.discussion.Discussion;
import com.hqz.hzuoj.bean.discussion.DiscussionComment;
import com.hqz.hzuoj.bean.discussion.DiscussionQuery;
import com.hqz.hzuoj.mapper.discussion.DiscussionMapper;
import com.hqz.hzuoj.service.DiscussionService;
import com.hqz.hzuoj.util.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/17 13:19
 * @Description: TODO
 */
@Service
public class DiscussionServiceImpl implements DiscussionService {
    @Autowired
    private DiscussionMapper discussionMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取讨论
     * @param discussionId
     * @return
     */
    @Override
    public Discussion getDiscussion(Integer discussionId) {
        Discussion discussion = discussionMapper.getDiscussion(discussionId);
        if (discussion != null) {
            //浏览次数+1
            discussionMapper.updateDiscussionCount(discussionId,0,0,1);
            discussion.setDiscussionBrowseCount(discussion.getDiscussionBrowseCount() + 1);
        }
        return discussion;
    }

    /**
     * 保存讨论
     * @param discussion
     * @return
     */
    @Override
    public Discussion saveDiscussion(Discussion discussion) {
        Integer discussionId = discussion.getDiscussionId();
        int userId = discussion.getUser().getUserId();
        if (discussionId != null) {
            discussion.setDiscussionModifyTime(new Date());
            Discussion discussion1 = getDiscussion(discussionId);
            if (discussion1 == null) {
                return null;
            }
            int userId1 = discussion1.getUser().getUserId();
            if (userId1 != userId) {
                return null;
            }
            discussionMapper.updateDiscussion(discussion);
        } else {
            discussion.setDiscussionModifyTime(new Date());
            discussion.setDiscussionCreateTime(new Date());
            discussion.setDiscussionCommendCount(0);
            discussion.setDiscussionBrowseCount(0);
            discussion.setDiscussionReplyCount(0);
            discussion.setDiscussionTop(0);
            discussionMapper.saveDiscussion(discussion);
        }
        return discussion;
    }

    /**
     * 获取讨论列表
     * @param page
     * @param discussionQuery
     * @return
     */
    @Override
    public PageInfo<Discussion> getDiscussions(Integer page, DiscussionQuery discussionQuery) {
        if (page == null || page <= 0) page = 1;
        if (discussionQuery == null) discussionQuery = new DiscussionQuery();
        PageHelper.startPage(page, 20, true);
        List<Discussion> discussions = discussionMapper.getDiscussions(discussionQuery);
        return new PageInfo<>(discussions, 20);
    }

    /**
     * 获取评论列表
     *
     * @param discussionId
     * @return
     */
    @Override
    public List<DiscussionComment> getDiscussionComments(Integer discussionId) {
        List<DiscussionComment> discussionComments = discussionMapper.getDiscussionComments(discussionId);
        return getDiscussionComments(discussionComments);
    }

    private List<DiscussionComment> getDiscussionComments(List<DiscussionComment> comments) {
        List<DiscussionComment> commentList = new ArrayList<>();
        int tot = 0;
        Map<Integer, Integer> father = new HashMap<>();
        for (int i = 0; i < comments.size(); i++) {
            int u = comments.get(i).getCommentId();

            if (comments.get(i).getParentComment() != null
                    && comments.get(i).getParentComment().getCommentId() != null
                    && comments.get(i).getParentComment().getCommentId() != -1) {
                int v = comments.get(i).getParentComment().getCommentId();
                Integer x = father.get(v);
                father.put(u, x);
                DiscussionComment discussionComment = commentList.get(x);
                discussionComment.getComments().add(comments.get(i));
            } else {
                father.put(u, tot);
                commentList.add(comments.get(i));
                commentList.get(tot).setComments(new ArrayList<>());
                tot++;
            }
        }
        return commentList;
    }

    /**
     * 保存评论
     *
     * @param comment
     * @return
     */
    @Override
    public DiscussionComment saveDiscussionComment(DiscussionComment comment) {
        if (comment.getParentComment() == null || comment.getParentComment().getCommentId() == -1) {
            DiscussionComment discussionComment = new DiscussionComment();
            discussionComment.setCommentId(-1);
            comment.setParentComment(discussionComment);
            discussionMapper.saveDiscussionComment(comment);
            discussionMapper.updateDiscussionCount(comment.getDiscussion().getDiscussionId(),1,0,0);
        } else {
            discussionMapper.saveDiscussionComment(comment);
        }
        comment = discussionMapper.getDiscussionComment(comment.getCommentId());
        return comment;
    }

    /**
     * 讨论置顶
     * @param discussion
     * @return
     */
    @Override
    public Discussion updateDiscussionTop(Discussion discussion) {
        if (discussion.getDiscussionTop() == null || discussion.getDiscussionTop() < 0) return null;
        Discussion d = discussionMapper.getDiscussion(discussion.getDiscussionId());
        d.setDiscussionTop(discussion.getDiscussionTop());
        discussionMapper.updateByPrimaryKey(d);
        return d;
    }

    /**
     * 删除讨论
     * @param discussionId
     * @return
     */
    @Override
    public boolean deleteDiscussion(Integer discussionId) {
        Discussion discussion = new Discussion();
        discussion.setDiscussionId(discussionId);
        discussionMapper.deleteByPrimaryKey(discussion);
        return true;
    }
}
