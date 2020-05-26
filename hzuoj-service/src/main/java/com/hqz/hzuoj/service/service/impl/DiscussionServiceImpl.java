package com.hqz.hzuoj.service.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.discussion.Discussion;
import com.hqz.hzuoj.bean.discussion.DiscussionComment;
import com.hqz.hzuoj.bean.discussion.DiscussionQuery;
import com.hqz.hzuoj.bean.user.User;
import com.hqz.hzuoj.mapper.discussion.DiscussionMapper;
import com.hqz.hzuoj.service.DiscussionService;
import com.hqz.hzuoj.util.util.RedisUtil;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.stream.events.Comment;
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
     *
     * @param discussionId
     * @return
     */
    @Override
    public Discussion getDiscussion(Integer discussionId) {
        Discussion discussion = discussionMapper.getDiscussion(discussionId);
        if (discussion != null) {
            //浏览次数+1
            discussionMapper.updateDiscussionCount(discussionId, 0, 0, 1);
            discussion.setDiscussionBrowseCount(discussion.getDiscussionBrowseCount() + 1);
        }
        return discussion;
    }

    /**
     * 保存讨论
     *
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
     *
     * @param page
     * @param discussionQuery
     * @return
     */
    @Override
    public PageInfo<Discussion> getDiscussions(Integer page, DiscussionQuery discussionQuery) {
        if (page == null || page <= 0) {
            page = 1;
        }
        if (discussionQuery == null) {
            discussionQuery = new DiscussionQuery();
        }
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
        List<DiscussionComment> comments = discussionMapper.getDiscussionComments(discussionId);
        Map<Integer, List<DiscussionComment>> commentTree = buildCommentTree(comments);
        List<DiscussionComment> commentList = new ArrayList<>();
        for (DiscussionComment comment : comments) {
            if (comment.getParentComment() == null) {
                commentDelete(comment);
                continue;
            }
            if (comment.getParentComment().getCommentId() == -1) {
                getDepthComment(commentTree, comment, 2);
                commentList.add(comment);
            }
        }
        return commentList;
    }


    /**
     * 构建评论回复树关系
     *
     * @param comments
     * @return
     */
    private Map<Integer, List<DiscussionComment>> buildCommentTree(List<DiscussionComment> comments) {
        Map<Integer, List<DiscussionComment>> commentTree = new HashMap<>();
        HashMap<Integer, DiscussionComment> commentMap = new HashMap<>();
        for (DiscussionComment comment : comments) {
            comment.setComments(new ArrayList<>());
            commentMap.put(comment.getCommentId(), comment);
        }
        for (DiscussionComment comment : comments) {
            if (comment.getParentComment().getCommentId() != -1) {
                DiscussionComment discussionComment = commentMap.get(comment.getParentComment().getCommentId());
                comment.setParentComment(JSON.parseObject(JSON.toJSONString(discussionComment), DiscussionComment.class));
            }
        }
        for (DiscussionComment comment : comments) {
            DiscussionComment parentComment = comment.getParentComment();
            if (parentComment != null) {
                List<DiscussionComment> list = commentTree.get(parentComment.getCommentId());
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(JSON.parseObject(JSON.toJSONString(comment), DiscussionComment.class));
                commentTree.put(parentComment.getCommentId(), list);
            }
        }
        return commentTree;
    }

    /**
     * 获取评论depth级回复
     *
     * @param commentTree
     * @param comment
     * @param depth
     */
    private void getDepthComment(Map<Integer, List<DiscussionComment>> commentTree, DiscussionComment comment, int depth) {
        if (depth == 1) {
            //到达三级回复
            ArrayList<DiscussionComment> comments = new ArrayList<>();
            dfs(commentTree, comments, comment.getCommentId());
            comments.sort(new Comparator<DiscussionComment>() {
                @Override
                public int compare(DiscussionComment o1, DiscussionComment o2) {
                    return o1.getCommentId() - o2.getCommentId();
                }
            });
            comment.setComments(comments);
        } else {
            ArrayList<DiscussionComment> comments = new ArrayList<>();
            List<DiscussionComment> commentList = commentTree.get(comment.getCommentId());
            if (commentList != null) {
                //遍历子树
                for (DiscussionComment discussionComment : commentList) {
                    getDepthComment(commentTree, discussionComment, depth - 1);
                    comments.add(discussionComment);
                }
            }
            comment.setComments(comments);
        }
    }

    /**
     * 遍历回复子树，并将子树所有节点加入到回复根节点中
     *
     * @param commentTree
     * @param comments
     * @param commentId
     */
    private void dfs(Map<Integer, List<DiscussionComment>> commentTree, List<DiscussionComment> comments, int commentId) {
        List<DiscussionComment> commentList = commentTree.get(commentId);
        if (null != commentList) {
            //遍历子树
            for (DiscussionComment discussionComment : commentList) {
                comments.add(discussionComment);
                dfs(commentTree, comments, discussionComment.getCommentId());
            }
        }
    }


    /**
     * 保存评论
     *
     * @param comment
     * @return
     */
    @Override
    public DiscussionComment saveDiscussionComment(DiscussionComment comment) {
        //评论
        if (comment.getParentComment() == null || comment.getParentComment().getCommentId() == -1) {
            DiscussionComment discussionComment = new DiscussionComment();
            discussionComment.setCommentId(-1);
            comment.setParentComment(discussionComment);
            discussionMapper.saveDiscussionComment(comment);
            discussionMapper.updateDiscussionCount(comment.getDiscussion().getDiscussionId(), 1, 0, 0);
        } else {
            //回复
            discussionMapper.saveDiscussionComment(comment);
        }
        comment = discussionMapper.getDiscussionComment(comment.getCommentId());
        if (comment == null) {
            return null;
        }
        if (comment.getComments() == null || comment.getComments().isEmpty()) {
            comment.setComments(new ArrayList<>());
        }
        return comment;
    }

    /**
     * 讨论置顶
     *
     * @param discussion
     * @return
     */
    @Override
    public Discussion updateDiscussionTop(Discussion discussion) {
        if (discussion.getDiscussionTop() == null || discussion.getDiscussionTop() < 0) {
            return null;
        }
        Discussion d = discussionMapper.getDiscussion(discussion.getDiscussionId());
        d.setDiscussionTop(discussion.getDiscussionTop());
        discussionMapper.updateByPrimaryKey(d);
        return d;
    }

    /**
     * 删除讨论
     *
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

    /**
     * 删除用户讨论
     *
     * @param userId
     * @param discussionId
     * @return
     */
    @Override
    public boolean discussionDelete(Integer userId, Integer discussionId) {
        Discussion discussion = discussionMapper.getDiscussion(discussionId);
        if (null == discussion) {
            throw new RuntimeException("讨论不存在");
        }
        User user = discussion.getUser();
        if (!user.getUserId().equals(userId)) {
            throw new RuntimeException("不能删除别人的讨论");
        }
        discussionMapper.deleteByPrimaryKey(discussionId);
        return true;
    }

    /**
     * 删除回复
     *
     * @param comment
     * @return
     */
    @Override
    public String commentDelete(DiscussionComment comment) {
        DiscussionComment discussionComment = discussionMapper.getDiscussionComment(comment.getCommentId());
        if (discussionComment == null) {
            return "error";
        }
        if (discussionComment.getParentComment() == null || "-1".equals(discussionComment.getParentComment().getCommentId().toString())) {
            discussionMapper.updateDiscussionCount(discussionComment.getDiscussion().getDiscussionId(), -1, 0, 0);
        }
        if (discussionMapper.commentDelete(comment) > 0) {
            return "success";
        }
        throw new RuntimeException("删除失败");
    }
}
