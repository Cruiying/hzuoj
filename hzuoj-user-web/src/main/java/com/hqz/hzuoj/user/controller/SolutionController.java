package com.hqz.hzuoj.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.annotations.UserLoginCheck;
import com.hqz.hzuoj.base.ResultEntity;
import com.hqz.hzuoj.bean.problem.Problem;
import com.hqz.hzuoj.bean.solution.Solution;
import com.hqz.hzuoj.bean.user.User;
import com.hqz.hzuoj.service.ProblemService;
import com.hqz.hzuoj.service.SolutionService;
import com.hqz.hzuoj.util.MarkdownUtils;
import com.hqz.hzuoj.vo.UserSolutionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/18 10:17
 * @Description: TODO
 */
@Controller
@Slf4j
public class SolutionController {

    @Reference
    private SolutionService solutionService;

    @Reference
    private ProblemService problemService;

    /**
     * 题解页面
     *
     * @param problemId
     * @return
     */
    @RequestMapping("/problem/solutions/{problemId}")
    public String getSolutions(Integer page, @PathVariable Integer problemId, ModelMap modelMap) {
        if (page == null || page <= 0) page = 1;
        PageInfo<Solution> pageInfo = solutionService.getSolutions(page, problemId);
        Problem problem = problemService.getProblem(problemId);
        List<Solution> solutions = pageInfo.getList();
        for (Solution solution : solutions) {
            String s = MarkdownUtils.markdownToHtml(solution.getSolutionContent());
            solution.setSolutionContent(s);
        }
        modelMap.put("pageInfo", pageInfo);
        modelMap.put("problem", problem);
        return "solutions";
    }

    /**
     * 题解编辑页面
     *
     * @param problemId
     * @param modelMap
     * @param request
     * @return
     */
    @RequestMapping("/problem/solution/editor/{problemId}")
    @UserLoginCheck
    public String editorSolution(@PathVariable Integer problemId, Integer solutionId, ModelMap modelMap, HttpServletRequest request) {
        if (problemId != null) {
            Problem problem = problemService.getProblem(problemId);
            if (problem == null) return "404";
            Solution solution = solutionService.getSolution(solutionId);
            if (solution != null) {
                String str = (String) request.getSession().getAttribute("userId");
                int userId = Integer.parseInt(str);
                int duserId = solution.getUser().getUserId();
                if (duserId != userId) {
                    return "404";
                }

                modelMap.put("solutionId", solutionId);
                modelMap.put("solution", solution);
            }
            modelMap.put("problemId", problem.getProblemId());
        } else {
            return "404";
        }
        return "s_editor";
    }

    /**
     * 保存或者修改
     *
     * @param solution
     * @return
     */
    @RequestMapping("/problem/solution/save")
    @UserLoginCheck
    @ResponseBody
    public String saveSolution(@RequestBody Solution solution, HttpServletRequest request) {
        if (solution == null) return null;
        if (solution.getProblem() == null || solution.getProblem().getProblemId() == null) return null;
        String str = (String) request.getSession().getAttribute("userId");
        Integer userId = Integer.parseInt(str);
        User user = new User();
        user.setUserId(userId);
        solution.setUser(user);
        Solution saveSolution = solutionService.saveSolution(solution);
        if (saveSolution == null) return null;
        return JSON.toJSONString(saveSolution);
    }

    /**
     * 获取用户的题解信息
     *
     * @param userSolutionVO
     * @return
     */
    @RequestMapping("/user/solutions")
    @ResponseBody
    public ResultEntity getUserSolutions(@RequestBody UserSolutionVO userSolutionVO) {
        try {
            System.out.println(userSolutionVO);
            return ResultEntity.success("获取成功", solutionService.getUserSolutions(userSolutionVO));
        } catch (Exception e) {
            log.error("error:", e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 删除用户题解
     * @param solutionId
     * @param session
     * @return
     */
    @RequestMapping("/solution/delete/{solutionId}")
    @ResponseBody
    @UserLoginCheck
    public ResultEntity solutionDelete(@PathVariable Integer solutionId, HttpSession session) {
        try {
            Solution solution = solutionService.getSolution(solutionId);
            Integer u = Integer.parseInt(session.getAttribute("userId").toString());
            if (!u.equals(solution.getUser().getUserId())) {
                return ResultEntity.error("不能删除别人发表的题解");
            }
            return ResultEntity.success("删除成功", solutionService.deleteSolution(solutionId));
        } catch (Exception e) {
            log.info("Error", e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }
}
