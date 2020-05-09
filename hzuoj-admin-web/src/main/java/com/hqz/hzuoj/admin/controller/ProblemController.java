package com.hqz.hzuoj.admin.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.annotations.AdminLoginCheck;
import com.hqz.hzuoj.bean.problem.Problem;
import com.hqz.hzuoj.bean.problem.ProblemLevel;
import com.hqz.hzuoj.bean.problem.Tag;
import com.hqz.hzuoj.service.DataService;
import com.hqz.hzuoj.service.ExampleService;
import com.hqz.hzuoj.service.ProblemService;
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
    public String getLevelsList(Integer page) {
        PageInfo<ProblemLevel> pageInfo = problemService.getProblemLevels(page);
        return JSON.toJSONString(pageInfo);
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
    public String addLevel(@RequestBody ProblemLevel problemLevel, HttpServletRequest request) {
        ProblemLevel saveProblemLevel = problemService.saveProblemLevel(problemLevel);
        return JSON.toJSONString(saveProblemLevel);
    }

    /**
     * 删除等级
     * @param levelId
     * @return
     */
    @RequestMapping("/delete/level/{levelId}")
    @ResponseBody
    public String deleteLevel(@PathVariable Integer levelId) {
        boolean flag = problemService.deleteProblemLevel(levelId);
        Map<String, Boolean> map = new HashMap<>();
        map.put("flag", flag);
        return JSON.toJSONString(map);
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
    public String getTagsList(Integer page) {
        if (page == null || page <= 0) {
            page = 1;
        }
        PageInfo<Tag> pageInfo = problemService.getTags(page);
        return JSON.toJSONString(pageInfo);
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
    public String addTag(@RequestBody Tag tag, HttpServletRequest request) {
        Tag saveTag = problemService.saveTag(tag);
        return JSON.toJSONString(saveTag);
    }

    /**
     * 删除标签
     * @param tagId
     * @return
     */
    @RequestMapping("/delete/tag/{tagId}")
    @ResponseBody
    public String deleteTag(@PathVariable Integer tagId) {
        boolean flag = problemService.deleteTag(tagId);
        Map<String, Boolean> map = new HashMap<>();
        map.put("flag", flag);
        return JSON.toJSONString(map);
    }

    /**
     * 题目比赛列表分页
     *
     * @param page
     * @return
     */
    @RequestMapping("/problems/list")
    @ResponseBody
    public Map<String, Object> list(Integer page) {
        if (page == null) {
            page = 1;
        }
        Map<String, Object> map = new HashMap<>();
        PageInfo<Problem> pageInfo = problemService.getAllProblem(page);
        map.put("pageInfo", pageInfo);
        return map;
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
    public Map<String, Object> getProblemInfo(@PathVariable Integer problemId, ModelMap modelMap, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Problem problem = problemService.getProblem(problemId);
        modelMap.put("adminId", problem.getAdmin().getAdminId());
        map.put("problem", problem);
        modelMap.put("problem", problem);
        return map;
    }

    /**
     * 添加题目
     *
     * @param problem
     * @return
     */
    @RequestMapping("add/problem")
    @ResponseBody
    public Map<String, Object> add(@RequestBody Problem problem, HttpServletRequest request, ModelMap modelMap) {
        Map<String, Object> map = new HashMap<>();
        modelMap.put("adminId", request.getAttribute("adminId"));
        if (StringUtils.isBlank(problem.getProblemTitle())) {
            map.put("msg", "添加失败，必须包含题目名称");
            map.put("problemId", "0");
            map.put("flag", false);
            return map;
        }
//        保存题目
        Integer problemId = problemService.saveProblem(problem);
        //保存测试样例
        exampleService.saveExamples(problem.getExamples(), problemId);
        if (problem.getProblemId() != null) {
            problemId = problem.getProblemId();
            map.put("msg", "题目修改成功");
        } else {
            map.put("msg", "题目添加成功");
        }
        request.setAttribute("problemId", problemId);
        map.put("problemId", problemId);
        map.put("flag", true);
        return map;
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
    private Map<String, Object> updateProblemPublic(@PathVariable Integer problemId, Integer problemPublic) {
        Map<String, Object> map = new HashMap<>();
        Problem problem = new Problem();
        problem.setProblemId(problemId);
        problem.setProblemPublic(problemPublic);
        try {
            problemService.updateProblemPublic(problem);
            map.put("msg", "修改成功");
            map.put("flag", true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", "修改失败");
            map.put("flag", false);
        }
        return map;
    }

    /**
     * 删除问题
     *
     * @param problemId
     * @return
     */
    @RequestMapping("delete/problem/{problemId}")
    @ResponseBody
    public Map<String, Object> deleteProblem(@PathVariable Integer problemId) {
        Map<String, Object> map = new HashMap<>();
        String msg = problemService.deleteProblem(problemId);
        if ("success".equals(msg)) {
            map.put("flag", true);
        } else {
            map.put("flag", false);
        }
        return map;
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
