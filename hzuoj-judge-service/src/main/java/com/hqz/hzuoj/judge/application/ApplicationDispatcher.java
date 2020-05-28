package com.hqz.hzuoj.judge.application;

import com.alibaba.fastjson.JSON;
import com.hqz.hzuoj.bean.submit.JudgeResult;
import com.hqz.hzuoj.bean.submit.Submit;
import com.hqz.hzuoj.bean.submit.TestCode;
import com.hqz.hzuoj.bean.problem.TestPoint;
import com.hqz.hzuoj.bean.user.User;
import com.hqz.hzuoj.judge.mapper.JudgeResultMapper;
import com.hqz.hzuoj.judge.mapper.SubmitMapper;
import com.hqz.hzuoj.judge.mapper.TestPointMapper;
import com.hqz.hzuoj.judge.mq.MessageSender;
import com.hqz.hzuoj.judge.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 消息调度器.
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/30 15:30
 * @Description: TODO
 */
@Service
public class ApplicationDispatcher {

    /**
     * 测评调度器
     */
    @Autowired
    private JudgeDispatcher judgeDispatcher;

    @Autowired
    private JudgeResultMapper judgeResultMapper;

    @Autowired
    private SubmitMapper submitMapper;

    @Autowired
    private TestPointMapper testPointMapper;
    /**
     * 消息发送
     */
    @Autowired
    private MessageSender messageSender;


    /**
     * 用户自测结果消息队列
     */
    @Value("${testResult}")
    private String testResult;
    /**
     * 用户提交结果队列
     */
    @Value("${submitResult}")
    private String submitResult;
    /**
     * 用户提交消息队列
     */
    @Value("${submitQueue}")
    private String submitQueue;
    /**
     * 用户自测消息队列
     */
    @Value("${testQueue}")
    private String testQueue;

    @Autowired
    private UserMapper userMapper;

    /**
     * 收到消息队列的新的评测请求时的回调函数.
     *
     * @param submitId - 评测记录的唯一标识符
     */
    public void createSubmit(Integer submitId) {
        System.out.println("题目本地测评机运行");
        judgeDispatcher.RunningSubmit(submitId);
    }

    /**
     * 当系统错误发生时通知用户.
     *
     * @param submitId - 评测记录的唯一标识符
     */
    public void onSubmitErrorOccurred(Integer submitId, Submit submit, boolean completed) {
        JudgeResult judgeResult = getSystemErrorJudgeResult();
        submit.setJudgeResult(judgeResult);
        submit.setSubmitScore(0);
        submit.setSubmitRuntimeTime(0);
        submit.setSubmitRuntimeMemory(0);
        updateSubmit(submit);
        senderSubmitMessage("System Error", completed, JSON.toJSONString(submit), submitId);
    }

    /**
     * 编译开始
     *
     * @param submitId
     * @param submit
     */
    public void onSubmitCompile(Integer submitId, Submit submit) {
        submit.setJudgeResult(judgeResultMapper.selectJudgeName("compile"));
        submit.setSubmitScore(0);
        String judgeInfo = JSON.toJSONString(submit);
        updateSubmit(submit);
        senderSubmitMessage("compile", false, judgeInfo, submitId);
    }

    /**
     * 当编译阶段结束时通知用户.
     *
     * @param submitId - 评测记录的唯一标识符
     * @param result   - 编译结果
     */
    public void onSubmitCompileFinished(Integer submitId, Submit submit, Map<String, Object> result, String compileInfo, boolean completed) {
        JudgeResult judgeResult = getCompileJudgeResult(result);
        submit.setSubmitCompileInfo(compileInfo);
        submit.setJudgeResult(judgeResult);
        updateSubmit(submit);
        senderSubmitMessage("CompileFinished", completed, JSON.toJSONString(submit), submitId);
    }

    /**
     * 实时返回评测结果.
     *
     * @param submitId      - 提交记录的编号
     * @param submit        提交的测试
     * @param runtimeResult - 某个测试点的程序运行结果
     */
    public void submitOneTestPointFinished(Integer submitId, Submit submit, Map<String, Object> runtimeResult, int usedTime, int usedMemory, Integer score, boolean completed) {
        String runtimeResultSlug = getRuntimeResult(runtimeResult);
        JudgeResult judgeResult = judgeResultMapper.selectJudgeName(runtimeResultSlug);
        TestPoint testPoint = getTestPoint(getUsedTime(runtimeResult), getUsedMemory(runtimeResult), getScore(runtimeResult));
        testPoint.setSubmitId(submitId);
        testPoint.setJudgeResult(judgeResult);
        if (!"AC".equals(judgeResult.getJudgeName())) {
            testPoint.setTestPointScore(0);
        }
        List<TestPoint> testPoints = submit.getTestPoints();
        if (testPoints == null) {
            testPoints = new ArrayList<>();
        }
        testPoints.add(testPoint);
        submit.setSubmitScore(score);
        submit.setTestPoints(testPoints);
        submit.setSubmitRuntimeTime(usedTime);
        submit.setSubmitRuntimeMemory(usedMemory);
        updateSubmit(submit);
        saveTestPoint(testPoint);
        senderSubmitMessage("oneTestPointFinished", completed, JSON.toJSONString(submit), submitId);
    }

    /**
     * 持久化程序评测结果
     *
     * @param submitId       - 提交记录的编号
     * @param runtimeResults - 对各个测试点的评测结果集
     */
    public void submitAllTestPointsFinished(Integer submitId, Submit submit, List<Map<String, Object>> runtimeResults, int usedTime, int usedMemory, int score, boolean completed) {
        String judgeName = "AC";
        for (Map<String, Object> runtimeResult : runtimeResults) {
            String runtimeJudgeName = getRuntimeResult(runtimeResult);
            if (!"AC".equals(runtimeJudgeName)) {
                judgeName = runtimeJudgeName;
            }
        }
        JudgeResult judgeResult = null;
        try {
            judgeResult = judgeResultMapper.selectJudgeName(judgeName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (judgeName == null) {
            judgeResult = getSystemErrorJudgeResult();
        }
        submit.setJudgeResult(judgeResult);
        submit.setSubmitRuntimeMemory(usedMemory);
        submit.setSubmitRuntimeTime(usedTime);
        submit.setSubmitScore(score);
        updateSubmit(submit);
        updateUser(submit.getUser().getUserId());
        senderSubmitMessage("AllTestPointsFinished", completed, JSON.toJSONString(submit), submitId);
    }


    /**
     * 收到消息队列的新的自测请求时的回调函数.
     *
     * @param testId - 自测记录的唯一标识符
     */
    public void createTest(Long testId, TestCode testCode) {
        judgeDispatcher.RunningTest(testId, testCode);
    }

    /**
     * 根据信息封装测试信息
     *
     * @param testPointTime
     * @param testPointMemory
     * @param score
     * @return
     */
    private TestPoint getTestPoint(Integer testPointTime, Integer testPointMemory, Integer score) {
        TestPoint testPoint = new TestPoint();
        testPoint.setTestPointScore(score);
        testPoint.setTestPointTime(testPointTime);
        testPoint.setTestPointMemory(testPointMemory);
        return testPoint;
    }

    /**
     * 自测开始编译
     *
     * @param testId
     * @param testCode
     */
    public void onTestCompile(Long testId, TestCode testCode) {
        testCode.setJudgeResult(judgeResultMapper.selectJudgeName("compile"));
        String judgeInfo = JSON.toJSONString(testCode);
        senderTestMessage("compile", false, judgeInfo, testId);
    }

    /**
     * 用户提交的自测发生系统错误
     *
     * @param testId
     */
    public void onTestErrorOccurred(Long testId, TestCode testCode, boolean completed) {
        JudgeResult judgeResult = getSystemErrorJudgeResult();
        TestPoint testPoint = getTestPoint(0, 0, 0);
        testPoint.setJudgeResult(judgeResult);
        testCode.setJudgeResult(judgeResult);
        testCode.setTestPoint(testPoint);
        senderTestMessage("System Error", completed, JSON.toJSONString(testCode), testId);
    }

    /**
     * 根据编译返回结果信息，进行封装信息
     *
     * @param result
     * @return
     */
    private JudgeResult getCompileJudgeResult(Map<String, Object> result) {
        boolean isCompileSuccess = (Boolean) result.get("isCompileSuccess");
        JudgeResult judgeResult;
        if (!isCompileSuccess) {
            judgeResult = judgeResultMapper.selectJudgeName("CE");
        } else {
            judgeResult = judgeResultMapper.selectJudgeName("Running");
        }
        return judgeResult;
    }

    /**
     * 用户提交的自测代码编译结束，发送编译结束信息
     *
     * @param testId      自测唯一标识
     * @param result      编译结果
     * @param compileInfo 编译信息
     */
    public void onTestCompileFinished(Long testId, TestCode testCode, Map<String, Object> result, String compileInfo, boolean completed) {
        JudgeResult judgeResult = getCompileJudgeResult(result);
        TestPoint testPoint = new TestPoint();
        testCode.setTestPoint(testPoint);
        testCode.setJudgeResult(judgeResult);
        testCode.setCompileInfo(compileInfo);
        senderTestMessage("CompileFinished", completed, JSON.toJSONString(testCode), testId);
    }

    /**
     * 用户自测代码运行完成
     *
     * @param testId
     * @param testCode
     * @param result
     * @param completed
     */
    public void TestCodeRuntimeFinished(Long testId, TestCode testCode, Map<String, Object> result, boolean completed) {
        TestPoint testPoint = getTestPoint(getUsedTime(result), getUsedMemory(result), getScore(result));
        testCode.setTestPoint(testPoint);
        JudgeResult judgeResult = judgeResultMapper.selectJudgeName(getRuntimeResult(result));
        testPoint.setJudgeResult(judgeResult);
        testCode.setJudgeResult(judgeResult);
        senderTestMessage("RuntimeFinished", completed, JSON.toJSONString(testCode), testId);
    }

    /**
     * 向测评记录队列发送消息
     *
     * @param event
     * @param completed
     * @param judgeInfo
     * @param submitId
     */
    private void senderSubmitMessage(String event, Boolean completed, String judgeInfo, Integer submitId) {
        Map<String, Object> mapMessage = new HashMap<>();
        mapMessage.put("event", event);
        mapMessage.put("completed", completed);
        mapMessage.put("judgeInfo", judgeInfo);
        mapMessage.put("submitId", submitId);
        messageSender.sendQueue(submitResult, mapMessage);
    }

    /**
     * 向自测队列发送消息
     *
     * @param event
     * @param completed
     * @param judgeInfo
     * @param testId
     */
    private void senderTestMessage(String event, Boolean completed, String judgeInfo, Long testId) {
        Map<String, Object> mapMessage = new HashMap<>();
        mapMessage.put("completed", completed);
        mapMessage.put("testId", testId);
        mapMessage.put("judgeInfo", judgeInfo);
        mapMessage.put("event", event);
        messageSender.sendQueue(testResult, mapMessage);
    }

    /**
     * 从评测结果集中获取程序运行时间(ms).
     *
     * @param runtimeResult - 程序评测结果
     * @return 程序运行时间(ms)
     */
    private int getUsedTime(Map<String, Object> runtimeResult) {
        Object usedTimeObject = runtimeResult.get("usedTime");
        if (usedTimeObject == null) {
            return 0;
        }
        return (Integer) usedTimeObject;
    }

    /**
     * 从评测结果集中获取内存使用量(KB).
     *
     * @param runtimeResult - 程序评测结果
     * @return 内存使用量(KB)
     */
    private int getUsedMemory(Map<String, Object> runtimeResult) {
        Object usedMemoryObject = runtimeResult.get("usedMemory");
        if (usedMemoryObject == null) {
            return 0;
        }
        return (Integer) usedMemoryObject;
    }

    /**
     * 从评测结果集中获取测试点对应的分值
     *
     * @param runtimeResult - 程序评测结果
     * @return 测试点对应的分值
     */
    private int getScore(Map<String, Object> runtimeResult) {
        Object scoreObject = runtimeResult.get("score");
        if (scoreObject == null) {
            return 0;
        }
        return (Integer) scoreObject;
    }

    /**
     * 从评测结果集中获取程序评测结果的唯一英文缩写.
     *
     * @param runtimeResult - 程序评测结果
     * @return 程序评测结果的唯一英文缩写
     */
    private String getRuntimeResult(Map<String, Object> runtimeResult) {
        Object runtimeResultObject = runtimeResult.get("runtimeResult");
        if (runtimeResultObject == null) {
            return "SE";
        }
        return (String) runtimeResultObject;
    }

    /**
     * 更新测评记录
     *
     * @param submit
     */
    private void updateSubmit(Submit submit) {
        if (submit.getSubmitScore() == null) {
            submit.setSubmitScore(0);
        }
        submitMapper.updateSubmit(submit);
    }

    private void updateUser(Integer userId) {
        JudgeResult judgeResult = judgeResultMapper.selectJudgeName("AC");
        User user;
        user = userMapper.selectByPrimaryKey(userId);
        user.setUserSubmitAcceptedTotal(userMapper.getUserSubmitAcceptedTotal(userId, judgeResult.getJudgeResultId(), 1));
        user.setUserChallengeTotal(userMapper.getUserChallengeTotal(userId, judgeResult.getJudgeResultId(), 1));
        user.setUserSubmitTotal(userMapper.getUserSubmitTotal(userId, judgeResult.getJudgeResultId(), 1));
        user.setUserAcceptedTotal(userMapper.getUserAcceptedTotal(userId, judgeResult.getJudgeResultId(), 1));
        user.setUserSubmitAcceptedTotal(userMapper.getUserSubmitAcceptedTotal(userId, judgeResult.getJudgeResultId(), 1));
        userMapper.updateByPrimaryKey(user);
    }

    /**
     * 保存测试点
     *
     * @param testPoint
     */
    private void saveTestPoint(TestPoint testPoint) {
        testPointMapper.saveTestPoint(testPoint);
    }

    /**
     * 获取系统错误的测试结果
     *
     * @return
     */
    private JudgeResult getSystemErrorJudgeResult() {
        return judgeResultMapper.selectJudgeName("SE");
    }
}
