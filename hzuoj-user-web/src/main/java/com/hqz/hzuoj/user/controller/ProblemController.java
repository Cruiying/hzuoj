package com.hqz.hzuoj.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.bean.language.Language;
import com.hqz.hzuoj.bean.problem.Data;
import com.hqz.hzuoj.bean.problem.Problem;
import com.hqz.hzuoj.bean.problem.ProblemQuery;
import com.hqz.hzuoj.bean.submit.Submit;
import com.hqz.hzuoj.bean.submit.TestCode;
import com.hqz.hzuoj.service.DataService;
import com.hqz.hzuoj.service.LanguageService;
import com.hqz.hzuoj.service.ProblemService;
import com.hqz.hzuoj.util.MarkdownUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/30 17:44
 * @Description: TODO
 */
@Controller
@Async
public class ProblemController {

    @Reference
    private ProblemService problemService;

    @Reference
    private DataService dataService;

    @Reference
    private LanguageService languageService;

    /**
     * 题目列表页面
     *
     * @return
     */
    @RequestMapping("/problems")
    public String problems(Integer page, HttpServletRequest request, ModelMap modelMap, ProblemQuery problemQuery) {
        String str = (String) request.getSession().getAttribute("userId");
        Integer userId = null;
        if (str != null) userId = Integer.parseInt(str);
        PageInfo<Problem> pageInfo;
        pageInfo = problemService.getProblemQuery(page, userId, problemQuery);
        modelMap.put("pageInfo", pageInfo);
        return "list";
    }

    /**
     * 获取题目
     *
     * @param problemId
     * @return
     */
    @RequestMapping("/problem/{problemId}/info")
    @ResponseBody
    public Map<String, Object> problemInfo(@PathVariable Integer problemId) {
        Problem problem = getProblem(problemId);
        Map<String, Object> map = new HashMap<>();
        map.put("problem", problem);
        return map;
    }
    /**
     * 题目详细信息
     *
     * @param problemId 题目ID
     * @param map
     * @return
     */
    @RequestMapping("/problem/{problemId}")
    public String problem(@PathVariable Integer problemId, ModelMap map) {
        Problem problem = getProblem(problemId);
        if (problem == null) return "404";
        map.put("problemId", problemId);
        map.put("problem", problem);
        map.put("testCode", new TestCode());
        map.put("contestId", null);
        List<Language> languages = languageService.getLanguages();
        map.put("languages", languages);
        return "problem";
    }

    private Problem getProblem(Integer problemId) {
        Problem problem = problemService.getProblem(problemId);
        if (problem != null && 0 == problem.getProblemPublic()) {
            return null;
        }
        String problemContent = problem.getProblemContent();
        String problemBackground = problem.getProblemBackground();
        String problemExplain = problem.getProblemExplain();
        problem.setProblemContent(MarkdownUtils.markdownToHtml(problemContent));
        problem.setProblemBackground(MarkdownUtils.markdownToHtml(problemBackground));
        problem.setProblemExplain(MarkdownUtils.markdownToHtml(problemExplain));
        List<Data> problemDatas = dataService.getProblemDatas(problemId);
        problem.setDatas(problemDatas);
        return problem;
    }
}
