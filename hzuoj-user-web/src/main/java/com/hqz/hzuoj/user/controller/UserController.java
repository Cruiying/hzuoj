package com.hqz.hzuoj.user.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.annotations.LoginWeb;
import com.hqz.hzuoj.annotations.UserLoginCheck;
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
import com.hqz.hzuoj.util.CookieUtil;
import com.hqz.hzuoj.util.FastDFSClientWrapper;
import com.hqz.hzuoj.util.JwtUtil;
import com.hqz.hzuoj.util.MarkdownUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
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
    public String login(@RequestBody User u, HttpServletRequest request, HttpServletResponse response) {
        String token = "";
        // 调用用户服务验证用户名和密码
        User user = userService.loginUser(u);
        if (user != null) {
            user.setPassword(null);
        }
        //用户登陆成功，才进行token日志
        if (user != null) {
            // 登录成功
            token = getToken(user,  request.getRemoteAddr());
            // 将token存入redis一份

        } else {
            // 登录失败
            token = "fail";
        }
        return token;
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
    public String register(@RequestBody User u) {
        if (u == null || u.getUsername() == null || u.getPassword() == null || "".equals(u.getUsername()) || "".equals(u.getPassword())) {
            return "0";
        }
        u.setRegisterTime(new Date());
        u.setUserRating(1500);u.setGender("男");u.setUserImg("");
        User user = userService.saveUser(u);
        if (user.getUserId() != null) {
            return "1";
        } else {
            return "0";
        }
    }

    /**
     * 排行页面
     *
     * @return
     */
    @RequestMapping("/ranking")
    public String getRanking(Integer page, ModelMap modelMap, RankingQuery rankingQuery) {
        System.err.println(userService);
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

    @RequestMapping("/rank/list")
    @ResponseBody
    public PageInfo<Rank> getRanking(Integer page) {
        System.err.println(userService);
        if (page == null || page <= 0) {
            page = 1;
        }
        PageInfo<Rank> pageInfo = userService.getRanks(page, new RankingQuery());
        return pageInfo;
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
    public User editorUser(@RequestBody User user, HttpServletRequest request) {
        String str = (String) request.getSession().getAttribute("userId");
        Integer userId = Integer.parseInt(str);
        user.setUserId(userId);
        user = userService.updateUser(user);
        return user;
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
    public PageInfo<Contest> getUserContests(@PathVariable Integer userId, Integer page) {
        ContestQuery contestQuery = new ContestQuery();
        contestQuery.setUserId(userId);
        contestQuery.setSignUpFilter(1);
        return  contestService.getAllContest(page, contestQuery);
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
    public PageInfo<Submit> getUserSubmit(@PathVariable Integer userId, Integer page) {
        SubmitQuery submitQuery = new SubmitQuery();
        submitQuery.setUserId(userId);
        return submitService.getSubmits(page, submitQuery);
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
    public String upload(@RequestParam MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        String uploadPath = "error";
        String str = (String) request.getSession().getAttribute("userId");
        if (str == null) {
            return uploadPath;
        }
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1, file.getOriginalFilename().length());
        if (!"jpg,jpeg,gif,png".toUpperCase().contains(suffix.toUpperCase())) {
            return "error";
        }
        if (file.getSize() > 1024 * 128) {
            return uploadPath;
        }
        try {
            Integer userId = Integer.parseInt(str);
            uploadPath = uploadImgPath + "/" + fastDFSClientWrapper.uploadFile(file);
            User user = new User();
            user.setUserId(userId);
            user.setUserImg(uploadPath);
            User user1 = userService.updateUserImg(user);
            String token = getToken(user1,  request.getRemoteAddr());
            CookieUtil.setCookie(request, response, "userOldToken", token, 60 * 60 * 24 * 30, true);
            request.getSession().setAttribute("userImg", user.getUserImg());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uploadPath;
    }

}

