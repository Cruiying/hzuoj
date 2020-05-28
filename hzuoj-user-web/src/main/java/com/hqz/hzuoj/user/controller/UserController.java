package com.hqz.hzuoj.user.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.annotations.LoginWeb;
import com.hqz.hzuoj.annotations.UserLoginCheck;
import com.hqz.hzuoj.base.ResultEntity;
import com.hqz.hzuoj.bean.contest.Contest;
import com.hqz.hzuoj.bean.contest.ContestQuery;
import com.hqz.hzuoj.bean.language.Language;
import com.hqz.hzuoj.bean.problem.Data;
import com.hqz.hzuoj.bean.problem.Problem;
import com.hqz.hzuoj.bean.problem.ProblemQuery;
import com.hqz.hzuoj.bean.submit.Submit;
import com.hqz.hzuoj.bean.submit.SubmitQuery;
import com.hqz.hzuoj.bean.user.Rank;
import com.hqz.hzuoj.bean.user.RankingQuery;
import com.hqz.hzuoj.bean.user.User;
import com.hqz.hzuoj.service.*;
import com.hqz.hzuoj.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/12 9:42
 * @Description: TODO
 */
@Controller
@Slf4j
public class UserController implements ErrorController {

    @Reference
    private ContestService contestService;

    @Reference
    private ProblemService problemService;

    @Reference
    private DataService dataService;

    @Reference
    private JudgeResultService judgeResultService;

    @Reference
    private LanguageService languageService;

    @Reference
    private UserService userService;

    @Reference
    private SubmitService submitService;

    @Autowired
    private FastDFSClientWrapper fastDFSClientWrapper;

    @Value("${uploadImgPath}")
    private String uploadImgPath;


    /**
     * 用户首页
     *
     * @return
     */
    @RequestMapping("/user/index")
    public String index() {
        return "index";
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        //获取statusCode:401,404,500
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == 401) {
            return "/401";
        } else if (statusCode == 404) {
            return "/404";
        } else if (statusCode == 403) {
            return "/403";
        } else {
            return "/500";
        }

    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    /**
     * 用户登陆页
     *
     * @return
     */
    @RequestMapping("/user/login")
    @LoginWeb
    public String login() {
        return "login";
    }

    /**
     * 用户登陆页
     *
     * @return
     */
    @RequestMapping("/user/register")
    public String register() {
        return "register";
    }


    /**
     * 用户登录判断
     *
     * @param u
     * @param request
     * @return
     */
    @RequestMapping("/login")
    @ResponseBody
    public ResultEntity login(@RequestBody User u, HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = userService.loginUser(u);
            if (null != user) {
                user.setPassword(null);
                String token = getToken(user, request.getRemoteAddr());
                return ResultEntity.success("登录成功", token);
            } else {
                return ResultEntity.error("用户不存在");
            }
        }catch (Exception e) {
            log.error("login({}) error message: ", u, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    private String getToken(User user, String ip) {
        String token = "";
        // 用jwt制作token
        Integer userId = user.getUserId();
        String username = user.getUsername();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", userId.toString());
        userMap.put("username", username);
        userMap.put("userImg", user.getUserImg());
        userMap.put("user", user);
        if (StringUtils.isBlank(ip)) {
            ip = "127.0.0.1";
        }
        // 按照设计的算法对参数进行加密后，生成token
        token = JwtUtil.encode("hzuoj", userMap, ip);
        return token;
    }

    @RequestMapping("/user/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Object user = request.getSession().getAttribute("userId");
        if (user == null) {
            return null;
        }
        request.getSession().removeAttribute("userId");
        request.getSession().removeAttribute("username");
        CookieUtil.deleteCookie(request, response, "userOldToken");
        return "login";
    }

    @RequestMapping("/register")
    @ResponseBody
    public ResultEntity register(@RequestBody User u) {
        try {
            if (u == null || u.getUsername() == null || u.getPassword() == null || "".equals(u.getUsername()) || "".equals(u.getPassword())) {
                return ResultEntity.error("用户名或者密码不能为空");
            }
            u.setGender("男"); u.setRegisterTime(new Date());
            u.setUserRating(1500); u.setUserImg("");
            return ResultEntity.success("注册成功", userService.saveUser(u));
        }catch (Exception e) {
            log.error("register({}) error message: {}", u, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 排行页面
     *
     * @return
     */
    @RequestMapping("/ranking")
    public String getRanking(Integer page, ModelMap modelMap, RankingQuery rankingQuery) {
        if (page == null || page <= 0) {
            page = 1;
        }
        if(rankingQuery == null) {
            rankingQuery = new RankingQuery();
        }
        PageInfo<Rank> pageInfo = userService.getRanks(page, rankingQuery);
        modelMap.put("pageInfo", pageInfo);
        return "ranking";
    }

    /**
     * 获取用户排行榜
     * @param page
     * @return
     */
    @RequestMapping("/rank/list")
    @ResponseBody
    public ResultEntity getRanking(Integer page) {
        try {
            return ResultEntity.success("获取成功", userService.getRanks(page, new RankingQuery()));
        }catch (Exception e) {
            log.error("getRanking({}) error message: {}", page, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }
    /**
     * 获取用户信息
     *
     * @param userId
     * @param modelMap
     * @return
     */
    @RequestMapping("/user/{userId}")
    public String getUser(@PathVariable Integer userId, ModelMap modelMap) {
        User user = userService.getUser(userId);
        if (user == null) {
            return "404";
        }
        modelMap.put("user", user);
        return "user";
    }


    /**
     * 用户修改
     *
     * @param user
     * @param request
     * @return
     */
    @RequestMapping("/user/editor")
    @ResponseBody
    @UserLoginCheck
    public ResultEntity editorUser(@RequestBody User user, HttpServletRequest request) {
        try {
            Integer userId = SessionUtils.getUserId(request);
            user.setUserId(userId);
            return ResultEntity.success("修改成功", userService.updateUser(user));
        }catch (Exception e) {
            log.error("editorUser({}) error message: {}", user, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 获取用户比赛
     *
     * @param userId
     * @param page
     * @return
     */
    @RequestMapping("/user/contests/{userId}")
    @ResponseBody
    public ResultEntity getUserContests(@PathVariable Integer userId, Integer page) {
        try {
            ContestQuery contestQuery = new ContestQuery();
            contestQuery.setUserId(userId);
            contestQuery.setContestPublic(1);
            return ResultEntity.success("获取成功", contestService.getAllContest(page, contestQuery));
        }catch (Exception e) {
            log.error("getUserContests({}, {}) error message: {}", userId, page, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 获取用户题目
     *
     * @param userId
     * @param page
     * @return
     */
    @RequestMapping("/user/problems/{userId}")
    @ResponseBody
    public ResultEntity getUserSubmit(@PathVariable Integer userId, Integer page) {
        try {
            SubmitQuery submitQuery = new SubmitQuery();
            submitQuery.setUserId(userId);
            return ResultEntity.success("获取成功", submitService.getSubmits(page, submitQuery));
        }catch (Exception e) {
            log.error("getUserSubmit({}, {}) error message: {}", userId, page, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 图片上传
     * @param file
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/user/img/upload")
    @UserLoginCheck
    @ResponseBody
    public ResultEntity upload(@RequestParam MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        try {
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            if (!"jpg,jpeg,gif,png".toUpperCase().contains(suffix.toUpperCase())) {
                return ResultEntity.error("不支持该文件格式");
            }
            if (file.getSize() > 1024 * 128) {
                return ResultEntity.error("上传文件过大");
            }
            User user = SessionUtils.getUser(request);
            String uploadPath = uploadImgPath + "/" + fastDFSClientWrapper.uploadFile(file);
            user.setUserImg(uploadPath);
            user = userService.updateUserImg(user);
            String token = getToken(user,  request.getRemoteAddr());
            CookieUtil.setCookie(request, response, "userOldToken", token, 60 * 60 * 24 * 30, true);
            request.getSession().setAttribute("userImg", user.getUserImg());
            return ResultEntity.success("上传成功", uploadPath);
        }catch (Exception e) {
            log.error("upload({}) error message:{}", file, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

}

