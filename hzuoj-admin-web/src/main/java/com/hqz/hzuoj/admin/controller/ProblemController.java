package com.hqz.hzuoj.admin.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.annotations.AdminLoginCheck;
import com.hqz.hzuoj.base.ResultEntity;
import com.hqz.hzuoj.bean.problem.Problem;
import com.hqz.hzuoj.bean.problem.ProblemLevel;
import com.hqz.hzuoj.bean.problem.Tag;
import com.hqz.hzuoj.service.DataService;
import com.hqz.hzuoj.service.ExampleService;
import com.hqz.hzuoj.service.ProblemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/18 18:04
 * @Description: TODO
 */
@Controller
@RequestMapping("/admin")
@AdminLoginCheck
@Slf4j
public class ProblemController {

    @Reference
    private ProblemService problemService;

    @Reference
    private DataService dataService;

    @Reference
    private ExampleService exampleService;

    /**
     * 题目列表页面
     *
     * @return
     */
    @RequestMapping("/problems")
    public String problems() {
        return "problems";
    }


    /**
     * 等级列表页面
     *
     * @return
     */
    @RequestMapping("/levels")
    public String levels() {
        return "levels";
    }

    /**
     * 等级列表
     *
     * @param page
     * @return
     */
    @RequestMapping("/levels/list")
    @ResponseBody
    public ResultEntity getLevelsList(Integer page) {
        try {
            return ResultEntity.success("获取成功", problemService.getProblemLevels(page));
        }catch (Exception e) {
            log.error("getLevelsList({}) error message: {}", page, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 添加等级页面
     *
     * @param modelMap
     * @return
     */
    @GetMapping("/add/level")
    public String addLevel(ModelMap modelMap, Integer levelId) {
        ProblemLevel problemLevel = new ProblemLevel();
        if (levelId == null) {
            modelMap.put("problemLevel", problemLevel);
            return "add-level";
        }
        problemLevel = problemService.getProblemLevel(levelId);
        if (problemLevel == null) {
            problemLevel = new ProblemLevel();
        }
        modelMap.put("problemLevel", problemLevel);
        return "add-level";
    }

    /**
     * 添加标签
     * @param problemLevel
     * @param request
     * @return
     */
    @PostMapping("/add/level")
    @ResponseBody
    public ResultEntity addLevel(@RequestBody ProblemLevel problemLevel, HttpServletRequest request) {
        try {
            return ResultEntity.success("添加成功", problemService.saveProblemLevel(problemLevel));
        }catch (Exception e) {
            log.error("addLevel({}) error message: {}", problemLevel, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 删除等级
     * @param levelId
     * @return
     */
    @RequestMapping("/delete/level/{levelId}")
    @ResponseBody
    public ResultEntity deleteLevel(@PathVariable Integer levelId) {
        try {
            return ResultEntity.success("删除成功", problemService.deleteProblemLevel(levelId));
        }catch (Exception e) {
            log.error("deleteLevel({}) error message: {}", levelId, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 标签列表页面
     *
     * @return
     */
    @RequestMapping("/tags")
    public String tags() {
        return "tags";
    }

    /**
     * 返回标签列表
     *
     * @param page
     * @return
     */
    @RequestMapping("/tags/list")
    @ResponseBody
    public ResultEntity getTagsList(Integer page) {
        try {
            return ResultEntity.success("获取成功", problemService.getTags(page));
        }catch (Exception e) {
            log.error("getTasList({}) error message: {}", page, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 添加标签页面
     *
     * @param modelMap
     * @return
     */
    @GetMapping("/add/tag")
    public String addTag(ModelMap modelMap, Integer tagId) {
        Tag tag = new Tag();
        if (tagId == null) {
            modelMap.put("tag", tag);
            return "add-tag";
        }
        tag = problemService.getTag(tagId);
        if (tag == null) {
            tag = new Tag();
        }
        modelMap.put("tag", tag);
        return "add-tag";
    }

    /**
     * 添加标签
     * @param tag
     * @param request
     * @return
     */
    @PostMapping("/add/tag")
    @ResponseBody
    public ResultEntity addTag(@RequestBody Tag tag, HttpServletRequest request) {
        try {
            return ResultEntity.success("添加成功", problemService.saveTag(tag));
        }catch (Exception e) {
            log.error("addTag({}) error message: {}", tag, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 删除标签
     * @param tagId
     * @return
     */
    @RequestMapping("/delete/tag/{tagId}")
    @ResponseBody
    public ResultEntity deleteTag(@PathVariable Integer tagId) {
        try {
            return ResultEntity.success("删除成功", problemService.deleteTag(tagId));
        }catch (Exception e) {
            log.error("deleteTag({}) error message: {}", tagId, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 题目比赛列表分页
     *
     * @param page
     * @return
     */
    @RequestMapping("/problems/list")
    @ResponseBody
    public ResultEntity list(Integer page) {
        try {
            return ResultEntity.success("获取成功", problemService.getAllProblem(page));
        }catch (Exception e) {
            log.error("list({}) error message:{}", page, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }


    /**
     * 添加题目页面
     *
     * @param modelMap
     * @param request
     * @return
     */
    @RequestMapping("addProblem")
    public String addProblem(ModelMap modelMap, Integer problemId, HttpServletRequest request) {
        modelMap.put("adminId", request.getAttribute("adminId"));
        modelMap.put("problemId", problemId);
        Problem problem = new Problem();
        List<Tag> tags = problemService.getAllTags();
        if (problemId != null) {
            problem = problemService.getProblem(problemId);
        }
        List<Tag> userTag = problem.getTags();
        List<ProblemLevel> problemLevels = problemService.getProblemLevels();
        Map<Integer, Boolean> map = new HashMap<>();
        if (userTag != null) {
            for (Tag tag : userTag) {
                map.put(tag.getTagId(), true);
            }
        }
        List<UserSelectTag> userSelectTags = new ArrayList<>();
        for (Tag tag : tags) {
            UserSelectTag userSelectTag = new UserSelectTag();
            userSelectTag.setTag(tag);
            Boolean aBoolean = map.get(tag.getTagId());
            if (aBoolean != null) {
                userSelectTag.setUserSelect(true);
            } else {
                userSelectTag.setUserSelect(false);
            }
            userSelectTags.add(userSelectTag);
        }
        modelMap.put("userSelectTags", userSelectTags);
        modelMap.put("problem", problem);
        modelMap.put("problemLevels",problemLevels);
        return "add-problem";
    }

    /**
     * 根据问题的problemId返回问题信息
     *
     * @param problemId
     * @return
     */
    @RequestMapping("problem/{problemId}/info")
    @ResponseBody
    public ResultEntity getProblemInfo(@PathVariable Integer problemId, ModelMap modelMap, HttpServletRequest request) {
        try {
            return ResultEntity.success("获取成功", problemService.getProblem(problemId));
        }catch (Exception e) {
            log.error("getProblemInfo({}) error message: {}", problemId, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 添加题目
     *
     * @param problem
     * @return
     */
    @RequestMapping("add/problem")
    @ResponseBody
    public ResultEntity add(@RequestBody Problem problem, HttpServletRequest request, ModelMap modelMap) {
        try {
            return ResultEntity.success("成功", problemService.saveProblem(problem));
        }catch (Exception e) {
            log.error("add({}) error message: {}", problem, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 更新用户是否看可见
     *
     * @param problemId     更新的问题
     * @param problemPublic 问题可见（1表示可见，0表示不可见）
     * @return 返回更新信息
     */
    @RequestMapping("/update/{problemId}/public")
    @ResponseBody
    private ResultEntity updateProblemPublic(@PathVariable Integer problemId, Integer problemPublic) {
        try {
            Problem problem = new Problem();
            problem.setProblemId(problemId); problem.setProblemPublic(problemPublic);
            return ResultEntity.success("修改成功", problemService.updateProblemPublic(problem));
        }catch (Exception e) {
            log.error("updateProblemPublic({}, {}) error message: {}", problemId, problemPublic, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 删除问题
     *
     * @param problemId
     * @return
     */
    @RequestMapping("delete/problem/{problemId}")
    @ResponseBody
    public ResultEntity deleteProblem(@PathVariable Integer problemId) {
        try {
            return ResultEntity.success(problemService.deleteProblem(problemId));
        }catch (Exception e) {
            log.error("deleteProblem({}) error message: {}", problemId, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }
}

class UserSelectTag implements Serializable {
    private Tag tag;
    private boolean userSelect;

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public boolean isUserSelect() {
        return userSelect;
    }

    public void setUserSelect(boolean userSelect) {
        this.userSelect = userSelect;
    }

    @Override
    public String toString() {
        return "UserSelect{" +
                "tag=" + tag +
                ", userSelect=" + userSelect +
                '}';
    }
}
