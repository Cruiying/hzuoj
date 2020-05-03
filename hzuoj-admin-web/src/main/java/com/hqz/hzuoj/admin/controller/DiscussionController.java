package com.hqz.hzuoj.admin.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.annotations.AdminLoginCheck;
import com.hqz.hzuoj.bean.discussion.Discussion;
import com.hqz.hzuoj.bean.discussion.DiscussionQuery;
import com.hqz.hzuoj.service.DiscussionService;
import com.hqz.hzuoj.util.MarkdownUtils;
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
    public PageInfo<Discussion> getDiscussions(Integer page) {
        return discussionService.getDiscussions(page, new DiscussionQuery());
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
    public Discussion discussionTop(@RequestBody Discussion discussion) {
        return discussionService.updateDiscussionTop(discussion);
    }

    @RequestMapping("/delete/discussion/{discussionId}")
    @ResponseBody
    private String deleteDiscussion(@PathVariable Integer discussionId) {
        boolean flag = discussionService.deleteDiscussion(discussionId);
        Map<String, Boolean> map = new HashMap<>();
        map.put("flag", flag);
        return JSON.toJSONString(map);
    }

}
