package com.hqz.hzuoj.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.hqz.hzuoj.annotations.LoginWeb;
import com.hqz.hzuoj.annotations.UserLoginCheck;
import com.hqz.hzuoj.base.ResultEntity;
import com.hqz.hzuoj.bean.language.Language;
import com.hqz.hzuoj.bean.submit.JudgeResult;
import com.hqz.hzuoj.bean.submit.Submit;
import com.hqz.hzuoj.bean.submit.SubmitQuery;
import com.hqz.hzuoj.bean.submit.TestCode;
import com.hqz.hzuoj.bean.user.User;
import com.hqz.hzuoj.service.JudgeResultService;
import com.hqz.hzuoj.service.LanguageService;
import com.hqz.hzuoj.service.SubmitService;
import com.hqz.hzuoj.service.UserService;
import com.hqz.hzuoj.user.mq.MessageSender;
import com.hqz.hzuoj.user.mq.SubmitResultMessageListener;
import com.hqz.hzuoj.user.mq.TestResultMessageListener;
import com.hqz.hzuoj.util.MarkdownUtils;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/30 12:21
 * @Description: TODO
 */
@Controller
@CrossOrigin
public class SubmitController {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    private SubmitResultMessageListener submitResultMessageListener;

    @Autowired
    private TestResultMessageListener testResultMessageListener;

    @Reference
    private SubmitService submitService;

    @Reference
    private LanguageService languageService;

    @Reference
    private JudgeResultService judgeResultService;

    @Autowired
    private MessageSender sender;
    @Value("${testResult}")
    private String testResult;

    @Value("${submitResult}")
    private String submitResult;

    @Value("${submitQueue}")
    private String submitQueue;

    @Value("${testQueue}")
    private String testQueue;

    @Reference
    private UserService userService;

    /**
     * 获取历史提交信息（数据库中查询）
     *
     * @param submitId
     * @return
     */
    @RequestMapping("/submit/{submitId}")
    public String getSubmit(@PathVariable Integer submitId, ModelMap modelMap) {
        modelMap.put("submitId", submitId);
        Submit submit = submitService.getSubmit(submitId);
        if (submit == null) return "404";
        modelMap.put("submit", submit);
        return "submission";
    }

    /**
     * 获取提交信息
     *
     * @param submitId
     * @return
     * @throws NotFoundException
     */
    @RequestMapping("/submit/{submitId}/info")
    @ResponseBody
    public Map<String, Object> getSubmit(@PathVariable Integer submitId) throws NotFoundException {
        Map<String, Object> map = new HashMap<>();
        Submit submit = submitService.getSubmit(submitId);
        if (submit == null) {
            throw new NotFoundException("没有找到该提交");
        }
        map.put("submit", submit);
        return map;
    }

    /**
     * 用户自测提交
     *
     * @param testCode
     * @return
     */
    @RequestMapping(value = "/test")
    @ResponseBody
    public Map<String, Object> test(@RequestBody TestCode testCode) {
        Map<String, Object> map = new HashMap<>();
        TestCode saveTestCode = submitService.saveTestCode(testCode);
        if (saveTestCode == null) {
            throw new RuntimeException("");
        }
        Map<String, Object> saveMap = new HashMap<>();
        saveMap.put("testId", saveTestCode.getTestId());
        saveMap.put("testCode", JSON.toJSONString(saveTestCode));
        sender.sendQueue(testQueue, saveMap);
        map.put("testId", saveTestCode.getTestId());
        return map;
    }

    /**
     * 获取自测结果信息
     *
     * @param testId
     * @return
     */
    @RequestMapping(value = "/test/{testId}/latest", produces = "text/event-stream", method = RequestMethod.GET)
    @ResponseBody
    @LoginWeb
    public SseEmitter getTestResult(@PathVariable Long testId) {
        final SseEmitter emitter = new SseEmitter();
        try {
            testResultMessageListener.addSseEmitters(testId, emitter);
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    /**
     * 用户普通提交
     *
     * @param submit
     * @param request
     * @return 返回用户的提交记录
     */
    @RequestMapping("/submit")
    @ResponseBody
    @UserLoginCheck
    public String submit(@RequestBody Submit submit, HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
        submit.setSubmitPublic(1); //公开提交
        submit.setSubmitTime(new Date());
        User user = new User();
        String userId = (String) request.getSession().getAttribute("userId");
        user.setUserId(Integer.parseInt(userId));
        submit.setUser(user);
        submit.setSubmitCodeLength(submit.getSubmitCode().length());
        Submit saveSubmit = submitService.saveSubmit(submit);
        if (saveSubmit == null) {
            throw new RuntimeException("");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("submitId", saveSubmit.getSubmitId());
        sender.sendQueue(submitQueue, map);
        Thread.sleep(100);
        return saveSubmit.getSubmitId().toString();
    }

    /**
     * 获取最新提交信息（从消息队列中获取最新消息）
     *
     * @param submitId
     * @return
     */
    @RequestMapping("/submit/{submitId}/latest")
    @ResponseBody
    @UserLoginCheck
    public SseEmitter submit(@PathVariable Integer submitId) throws RuntimeException {
        final SseEmitter emitter = new SseEmitter();
        Submit submit = submitService.getSubmit(submitId);
        if (submit == null) {
            throw new RuntimeException("该提交不存在");
        }
        if ("PD".equals(submit.getJudgeResult().getJudgeName())
                || "queue".equals(submit.getJudgeResult().getJudgeName())
                || "Running".equals(submit.getJudgeResult().getJudgeName())
                || "compile".equals(submit.getJudgeResult().getJudgeName())) {
            try {
                submitResultMessageListener.addSseEmitters(submitId, emitter);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
            return emitter;
        } else {
            throw new RuntimeException("该提交已经运行完成");
        }
    }

    /**
     * 测评列表页面
     *
     * @return
     */
    @RequestMapping("/submits/list")
    @ResponseBody
    public PageInfo<Submit> submitList(Integer page, @RequestBody SubmitQuery submitQuery) {
        return submitService.getSubmits(page, submitQuery);
    }

    /**
     * 测评列表页面
     *
     * @return
     */
    @RequestMapping("/submits")
    public String submissions(Integer page, ModelMap modelMap, SubmitQuery submitQuery) {
        PageInfo<Submit> pageInfo = submitService.getSubmits(page, submitQuery);
        List<Language> languages = languageService.getLanguages();
        List<JudgeResult> judgeResults = judgeResultService.getJudgeResults();
        modelMap.put("pageInfo", pageInfo);
        modelMap.put("languages", languages);
        modelMap.put("judgeResults", judgeResults);
        modelMap.put("submitQuery", submitQuery);
        return "submissions";
    }

    @RequestMapping("/contest/{contestId}/excel")
    @ResponseBody
    public ResultEntity getContestExcel(@PathVariable Integer contestId) {
        try {
            return ResultEntity.success("获取成功", submitService.getContestExcel(contestId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.success(e.getMessage(), null);
        }
    }

    @Description("模板下载")
    @RequestMapping("/downloadOnlineLearnMaterials")
    public String downloadFile(HttpServletRequest request, HttpServletResponse response, String fileName) {
        if (fileName != null) {
            //设置文件路径
            //这里使用配置类配置文件路径
            File file = new File(fileName);
            if (file.exists()) {
                response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

}
