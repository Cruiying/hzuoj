package com.hqz.hzuoj.admin.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.annotations.AdminLoginCheck;
import com.hqz.hzuoj.base.ResultEntity;
import com.hqz.hzuoj.bean.discussion.Discussion;
import com.hqz.hzuoj.bean.discussion.DiscussionQuery;
import com.hqz.hzuoj.service.DiscussionService;
import com.hqz.hzuoj.util.MarkdownUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: HQZ
 * @CreateTime: 2020/2/20 18:34
 * @Description: TODO
 */
@Controller
@RequestMapping("/admin")
@AdminLoginCheck
@Slf4j
public class DiscussionController {

    @Reference
    private DiscussionService discussionService;

    /**
     * 讨论列表页面
     *
     * @return
     */
    @RequestMapping("/discussions")
    public String discussions() {
        return "discussions";
    }

    /**
     * 获取讨论列表
     *
     * @param page
     * @return
     */
    @RequestMapping("/discussions/list")
    @ResponseBody
    public ResultEntity getDiscussions(Integer page) {
        try {
            return ResultEntity.success("获取成功", discussionService.getDiscussions(page, new DiscussionQuery()));
        }catch (Exception e) {
            log.error("getDiscussions({}) error message: {}", page, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 讨论页面
     *
     * @param discussionId
     * @param modelMap
     * @return
     */
    @RequestMapping("/discussion")
    public String discussion(Integer discussionId, ModelMap modelMap) {
        Discussion discussion = discussionService.getDiscussion(discussionId);
        discussion.setDiscussionContent(MarkdownUtils.markdownToHtml(discussion.getDiscussionContent()));
        modelMap.put("discussion", discussion);
        return "discussion";
    }

    /**
     * 获取讨论
     *
     * @param discussion
     * @return
     */
    @RequestMapping("/discussion/top")
    @ResponseBody
    public ResultEntity discussionTop(@RequestBody Discussion discussion) {
        try {
            return ResultEntity.success("获取成功", discussionService.updateDiscussionTop(discussion));
        }catch (Exception e) {
            log.error("discussionTop({}) error message: {}", discussion, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 删除讨论
     * @param discussionId
     * @return
     */
    @RequestMapping("/delete/discussion/{discussionId}")
    @ResponseBody
    private ResultEntity deleteDiscussion(@PathVariable Integer discussionId) {
        try {
            return ResultEntity.success("删除成功", discussionService.deleteDiscussion(discussionId));
        }catch (Exception e) {
            log.error("deleteDiscussion({}) error message: {}", discussionId, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

}
