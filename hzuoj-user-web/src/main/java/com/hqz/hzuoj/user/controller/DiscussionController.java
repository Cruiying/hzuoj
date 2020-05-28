package com.hqz.hzuoj.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.annotations.UserLoginCheck;
import com.hqz.hzuoj.base.ResultEntity;
import com.hqz.hzuoj.bean.discussion.Discussion;
import com.hqz.hzuoj.bean.discussion.DiscussionComment;
import com.hqz.hzuoj.bean.discussion.DiscussionQuery;
import com.hqz.hzuoj.bean.user.User;
import com.hqz.hzuoj.service.DiscussionService;
import com.hqz.hzuoj.util.MarkdownUtils;
import com.hqz.hzuoj.util.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/17 13:11
 * @Description: TODO
 */
@Slf4j
@Controller
public class DiscussionController {

    @Reference
    private DiscussionService discussionService;

    /**
     * 帖子创建与修改页面
     *
     * @param discussionId
     * @return
     */
    @RequestMapping("/discussion/editor")
    @UserLoginCheck
    public String getDiscussion(Integer discussionId, ModelMap modelMap, HttpServletRequest request) {
        if (discussionId != null) {
            Discussion discussion = discussionService.getDiscussion(discussionId);
            if (discussion == null) {
                return "404";
            }
            String str = (String) request.getSession().getAttribute("userId");
            int userId = Integer.parseInt(str);
            int duserId = discussion.getUser().getUserId();
            if (duserId != userId) {
                return "404";
            }
            modelMap.put("discussionId", discussionId);
            modelMap.put("discussion", discussion);
        }
        return "d_editor";
    }

    /**
     * 帖子保存与修改接口
     * @param discussion
     * @param request
     * @return
     */
    @RequestMapping("/discussion/save")
    @ResponseBody
    @UserLoginCheck
    public ResultEntity saveDiscussion(@RequestBody Discussion discussion, HttpServletRequest request) {
        try {
            Integer userId = Integer.parseInt(request.getSession().getAttribute("userId").toString());
            User user = new User();
            user.setUserId(userId);
            discussion.setUser(user);
            return ResultEntity.success("保存或者修改成功", discussionService.saveDiscussion(discussion));
        }catch (Exception e) {
            log.error("saveDiscussion({}) error message: {}", discussion, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 讨论列表页面页面
     *
     * @return
     */
    @RequestMapping("/discussions")
    public String getDiscussions(Integer page, ModelMap modelMap,DiscussionQuery discussionQuery) {
        PageInfo<Discussion> pageInfo = discussionService.getDiscussions(page,discussionQuery);
        modelMap.put("pageInfo", pageInfo);
        modelMap.put("discussionQuery", discussionQuery);
        return "discussions";
    }


    /**
     * 讨论列表页面页面
     *
     * @return
     */
    @RequestMapping("/discussion/{discussionId}")
    public String getDiscussion(@PathVariable Integer discussionId, ModelMap modelMap) {
        if (discussionId == null) {
            return "404";
        }
        Discussion discussion = discussionService.getDiscussion(discussionId);
        if (discussion == null) {
            return "404";
        }
        String s = MarkdownUtils.markdownToHtml(discussion.getDiscussionContent());
        discussion.setDiscussionContent(s);
        modelMap.put("discussion", discussion);
        return "discussion";
    }

    /**
     * 获取讨论列表
     * @param discussionQuery
     * @return
     */
    @RequestMapping(value = "/discussions/info", method = RequestMethod.POST)
    @ResponseBody
    public ResultEntity getDiscussions(@RequestBody DiscussionQuery discussionQuery) {
        try {
            return ResultEntity.success("获取成功", discussionService.getDiscussions(1, discussionQuery));
        }catch (Exception e) {
            log.error("getDiscussions({}), Error message: {}", discussionQuery, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 获取讨论回复列表
     * @param discussionId
     * @return
     */
    @RequestMapping("/discussion/comments/{discussionId}")
    @ResponseBody
    public ResultEntity getDiscussionComments(@PathVariable Integer discussionId) {
        try {
            return ResultEntity.success("获取成功", discussionService.getDiscussionComments(discussionId));
        } catch (Exception e) {
            log.error("getDiscussionComments({}) Error: {}", discussionId, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 讨论回复接口
     * @param comment
     * @param request
     * @return
     */
    @RequestMapping("/discussion/comment")
    @ResponseBody
    @UserLoginCheck
    public ResultEntity discussionComment(@RequestBody DiscussionComment comment, HttpServletRequest request) {
        try {
            User user = SessionUtils.getUser(request);
            comment.setUser(user);
            comment.setCommentTime(new Date());
            return ResultEntity.success("获取成功", discussionService.saveDiscussionComment(comment));
        }catch (Exception e) {
            log.error("discussionComment({}), error message: {}", comment, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 删除讨论
     * @param discussionId
     * @param request
     * @return
     */
    @RequestMapping("/discussion/delete")
    @ResponseBody
    @UserLoginCheck
    public ResultEntity discussionDelete(Integer discussionId, HttpServletRequest request) {
        try {
            Integer userId = SessionUtils.getUserId(request);
            return ResultEntity.success("删除成功", discussionService.discussionDelete(userId, discussionId));
        }catch (Exception e) {
            e.printStackTrace();
            log.error("Error:{}", e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 删除讨论回复
     * @param commentId
     * @param request
     * @return
     */
    @RequestMapping("/discussion/delete/comment/{commentId}")
    @ResponseBody
    @UserLoginCheck
    public ResultEntity commentDelete(@PathVariable Integer commentId, HttpServletRequest request) {
        try{
            User user = SessionUtils.getUser(request);
            DiscussionComment discussionComment = new DiscussionComment();
            discussionComment.setCommentId(commentId);
            discussionComment.setUser(user);
            return ResultEntity.success("删除成功",discussionService.commentDelete(discussionComment));
        }catch (Exception e) {
            log.error("error", e.getMessage());
            return ResultEntity.error("删除失败", "");
        }
    }
}
