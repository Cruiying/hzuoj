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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public String saveDiscussion(@RequestBody Discussion discussion, HttpServletRequest request) {
        String str = (String) request.getSession().getAttribute("userId");
        if (str == null) {
            return null;
        }
        Integer userId = Integer.parseInt(str);
        User user = new User();
        user.setUserId(userId);
        discussion.setUser(user);
        Discussion saveDiscussion = discussionService.saveDiscussion(discussion);
        return JSON.toJSONString(saveDiscussion);
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
        PageInfo<Discussion> pageInfo = discussionService.getDiscussions(1,new DiscussionQuery());
        if (discussion == null) {
            return "404";
        }
        String s = MarkdownUtils.markdownToHtml(discussion.getDiscussionContent());
        List<DiscussionComment> discussionComments = discussionService.getDiscussionComments(discussionId);
        discussion.setDiscussionContent(s);
        modelMap.put("discussion", discussion);
        modelMap.put("comments", discussionComments);
        modelMap.put("discussions", pageInfo.getList());
        return "discussion";
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
    public String discussionComment(@RequestBody DiscussionComment comment, HttpServletRequest request) {
        String str = (String)request.getSession().getAttribute("userId");
        Integer userId = Integer.parseInt(str);
        User user = new User();
        user.setUserId(userId);
        comment.setUser(user);
        comment.setCommentTime(new Date());
        DiscussionComment discussionComment = discussionService.saveDiscussionComment(comment);
        if (discussionComment == null) {
            return null;
        }
        return JSON.toJSONString(discussionComment);
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
            String userId = (String)request.getSession().getAttribute("userId");
            return ResultEntity.success("删除成功", discussionService.discussionDelete(Integer.parseInt(userId), discussionId));
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
            String userId = (String)request.getSession().getAttribute("userId");
            User user = new User();
            user.setUserId(Integer.parseInt(userId));
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
