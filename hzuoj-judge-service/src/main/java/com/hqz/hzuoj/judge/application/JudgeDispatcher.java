package com.hqz.hzuoj.judge.application;

import com.hqz.hzuoj.bean.problem.Data;
import com.hqz.hzuoj.bean.submit.Submit;
import com.hqz.hzuoj.bean.submit.TestCode;
import com.hqz.hzuoj.judge.mapper.DataMapper;
import com.hqz.hzuoj.judge.mapper.JudgeResultMapper;
import com.hqz.hzuoj.judge.mapper.SubmitMapper;
import com.hqz.hzuoj.judge.mapper.TestPointMapper;
import com.hqz.hzuoj.judge.mq.MessageSender;
import com.hqz.hzuoj.judge.system.*;
import com.hqz.hzuoj.judge.system.Comparator;
import com.hqz.hzuoj.judge.system.Compiler;
import com.hqz.hzuoj.judge.util.FileUtils;
import com.hqz.hzuoj.util.DigestUtils;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

/**
 * 评测机调度器
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/30 15:54
 * @Description: TODO
 */
@Component
public class JudgeDispatcher {

    @Autowired
    private SubmitMapper submitMapper;

    @Autowired
    private TestPointMapper testPointMapper;

    @Autowired
    private ApplicationDispatcher applicationDispatcher;


    /**
     * 获取测数据
     */
    @Autowired
    private DataMapper dataMapper;

    @Autowired
    private JudgeResultMapper judgeResultMapper;
    /**
     * 消息发送
     */
    @Autowired
    private MessageSender sender;
    /**
     * 预处理器，处理程序运行前所需要的东西
     */
    @Autowired
    private Preprocessor preprocessor;
    /**
     * 程序编译器，编译文件
     */
    @Autowired
    private Compiler compiler;

    /**
     * 对比器
     */
    @Autowired
    private Comparator comparator;

    @Autowired
    private FileUtils fileUtils;

    /**
     * 运行器
     */
    @Autowired
    private Runner runner;
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

    /**
     * 程序运行时需要的测试数据存放的目录
     */
    @Value("${judgeSystemDataPath}")
    private String judgeSystemDataPath;

    @Value("${dataPath}")
    private String dataPath;

    /**
     * 程序运行输出目录
     */
    @Value("${judgeSystemAnsPath}")
    private String judgeSystemAnsPath;

    /**
     * 创建用户提交测评
     *
     * @param submitId
     */
    public void RunningSubmit(Integer submitId) {
        /**
         * 创建运行目录和文件
         */
        String randomName = DigestUtils.getRandomString(25, DigestUtils.Mode.ALPHA);
        String baseDirectory = String.format("%s/submit/%s", new Object[]{judgeSystemAnsPath, randomName});
        String baseFileName = DigestUtils.getRandomString(12, DigestUtils.Mode.ALPHA);
        Submit submit = submitMapper.getSubmit(submitId);
        if (submit == null) {
            submit = new Submit();
            submit.setSubmitId(submitId);
            applicationDispatcher.onSubmitErrorOccurred(submitId, submit, true);
            return;
        }
        try {
            testPointMapper.deleteSubmitTestPoint(submitId);
            //预处理用户提交所需要的资源
            if (!submitPreprocess(submit, baseDirectory, baseFileName)) {
                applicationDispatcher.onSubmitErrorOccurred(submitId, submit, true);
                return;
            }
            applicationDispatcher.onSubmitCompile(submitId, submit);
            //编译用户提交
            if (!submitCompile(submit, baseDirectory, baseFileName)) {
                //编译失败
                return;
            }
            SubmitRun(submit, baseDirectory, baseFileName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //运行完成，清理程序运行输出
            cleanUp(baseDirectory);
        }

    }

    /**
     * 创建用户提交的自测
     *
     * @param testId
     */
    public void RunningTest(Long testId, TestCode testCode) {
        if (testCode == null) return;
        /**
         * 创建运行目录和文件
         */
        String randomName = DigestUtils.getRandomString(25, DigestUtils.Mode.ALPHA);
        String baseDirectory = String.format("%s/test/%s", new Object[]{judgeSystemAnsPath, randomName});
        String checkpointsFilePath = String.format("%s/test/%s", new Object[]{judgeSystemDataPath, testCode.getTestId()});
        String baseFileName = UUID.randomUUID().toString().replaceAll("-", "");
        try {
            //预处理用户自测所需要的资源
            if (!TestPreprocess(testCode, baseDirectory, baseFileName)) {
                //如果预处理失败
                return;
            }
            applicationDispatcher.onTestCompile(testId, testCode);
            //编译用户提交自测
            if (!TestCompile(testCode, baseDirectory, baseFileName)) {
                //编译失败
                return;
            }
            //运行程序
            TestRun(testCode, baseDirectory, baseFileName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //运行完成，清理程序运行输出
            cleanUp(baseDirectory);
            cleanUp(checkpointsFilePath);
        }
    }

    /**
     * 完成用户提交评测前的预处理工作.
     * 说明: 随机文件名用于防止应用程序自身递归调用.
     *
     * @param submit        - 用户评测记录对象
     * @param workDirectory - 用于产生编译输出的目录
     * @param baseFileName  - 随机文件名(不包含后缀)
     */
    private boolean submitPreprocess(Submit submit, String workDirectory, String baseFileName) {
        try {
            //加载题目数据
            String s = preprocessor.loadProblemData(submit.getProblem());
            if ("error".equals(s)) {
                return false;
            }
            //创建源代码
            preprocessor.createRuntimeCode(submit.getSubmitCode(), submit.getLanguage(), workDirectory, baseFileName);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            //如果预处理器发生错误
            applicationDispatcher.onSubmitErrorOccurred(submit.getSubmitId(), submit, true);
        }
        return false;
    }

    /**
     * 完成用户自测评测前的预处理工作.
     * 说明: 随机文件名用于防止应用程序自身递归调用.
     *
     * @param testCode      - 用户自测记录对象
     * @param workDirectory - 用于产生编译输出的目录
     * @param baseFileName  - 随机文件名(不包含后缀)
     */
    private boolean TestPreprocess(TestCode testCode, String workDirectory, String baseFileName) {
        try {
            preprocessor.createRuntimeCode(testCode.getTestCode(), testCode.getLanguage(), workDirectory, baseFileName);
            preprocessor.saveTestData(testCode);
            return true;
        } catch (Exception e) {
            //预处理器发生错误
            applicationDispatcher.onTestErrorOccurred(testCode.getTestId(), testCode, true);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取当前测试点输出路径.
     *
     * @param workDirectory - 编译生成结果的目录以及程序输出的目录
     * @param outputId      - 当前测试点编号
     * @return 当前测试点输出路径
     */
    private String getOutputFilePath(String workDirectory, long outputId) {
        return String.format("%s/output#%s.txt", new Object[]{workDirectory, outputId});
    }

    /**
     * 创建用户提交的编译任务.
     * 说明: 随机文件名用于防止应用程序自身递归调用.
     *
     * @param submit        - 评测记录对象
     * @param workDirectory - 用于产生编译输出的目录
     * @param baseFileName  - 随机文件名(不包含后缀)
     */
    private boolean submitCompile(Submit submit, String workDirectory, String baseFileName) {
        Map<String, Object> result = compiler.getCompileResult(submit.getLanguage(), workDirectory, baseFileName);
        applicationDispatcher.onSubmitCompileFinished(submit.getSubmitId(), submit, result, getCompileInfo(workDirectory, baseFileName), !isCompileSuccess(result));
        return (Boolean) result.get("isCompileSuccess");
    }

    /**
     * 创建用户提交的自测编译任务.
     *
     * @param testCode      自测记录对象
     * @param workDirectory 用于产生编译输出的目录
     * @param baseFileName  随机文件名(不包含后缀)
     * @return
     */
    private boolean TestCompile(TestCode testCode, String workDirectory, String baseFileName) {
        Map<String, Object> result = compiler.getCompileResult(testCode.getLanguage(), workDirectory, baseFileName);
        //编译结束，发送编译结束信息
        applicationDispatcher.onTestCompileFinished(testCode.getTestId(), testCode, result, getCompileInfo(workDirectory, baseFileName), !isCompileSuccess(result));
        return (Boolean) result.get("isCompileSuccess");
    }

    /**
     * 执行用户提交的测评程序.
     *
     * @param submit        - 评测记录对象
     * @param workDirectory - 编译生成结果的目录以及程序输出的目录
     * @param baseFileName  - 待执行的应用程序文件名(不包含文件后缀)
     */
    private void SubmitRun(Submit submit, String workDirectory, String baseFileName) throws NotFoundException {
        List<Map<String, Object>> runtimeResults = new ArrayList<>();
        try {
            Integer submitId = submit.getSubmitId();
            Integer problemId = submit.getProblem().getProblemId();
            List<Data> datas = getDatas(problemId);
            int size;
            if (datas == null || datas.size() == 0) {
                applicationDispatcher.onSubmitErrorOccurred(submitId, submit, true);
                return;
            }
            size = datas.size();
            int acNumber = 0, usedTime = 0, usedMemory = 0;
            for (Data data : datas) {
                Map<String, Object> runtimeResult = null;
                try {
                    int dataId = data.getDataId();
                    int score = 100;
                    String inputFilePath = dataPath + "/" + problemId + "/" + data.getDataInput();
                    String stdOutputFilePath = dataPath + "/" + problemId + "/" + data.getDataOutput();
                    String outputFilePath = getOutputFilePath(workDirectory, dataId);
                    Map<String, Object> runtimeResult1 = runner.getRuntimeResult(submit.getLanguage(), data.getDataMaxRuntimeTime(), data.getDataMaxRuntimeMemory(), workDirectory, baseFileName, inputFilePath, outputFilePath);
                    runtimeResult = getRuntimeResult(runtimeResult1, stdOutputFilePath, outputFilePath);
                    runtimeResult.put("score", score);
                    runtimeResults.add(runtimeResult);
                    if ("AC".equals(runtimeResult.get("runtimeResult"))) {
                        acNumber++;
                    }
                    int runTime = getUsedTime(runtimeResult);
                    int runMemory = getUsedMemory(runtimeResult);
                    usedTime = Integer.max(usedTime, runTime);
                    usedMemory = Integer.max(usedMemory, runMemory);
                    /*一个测试运行完成*/
                    applicationDispatcher.submitOneTestPointFinished(submitId, submit, runtimeResult, usedTime, usedMemory, acNumber * 100 / size, false);
                } catch (Exception e) {
                    /*系统发生错误*/
                    applicationDispatcher.submitOneTestPointFinished(submitId, submit, runtimeResult, usedTime, usedMemory, acNumber * 100 / size, false);
                    return;
                }
            }
            /*程序运行完成*/
            applicationDispatcher.submitAllTestPointsFinished(submitId, submit, runtimeResults, usedTime, usedMemory, acNumber * 100 / size, true);
        } catch (Exception e) {
            /*系统发生错误*/
            applicationDispatcher.onSubmitErrorOccurred(submit.getSubmitId(), submit, true);
            e.printStackTrace();
        }
    }

    /**
     * 获取题目测试数据
     *
     * @param problemId
     * @return
     */
    private List<Data> getDatas(Integer problemId) {
        return dataMapper.getProblemDatas(problemId);
    }

    /**
     * 运行用户自测消息
     *
     * @param testCode
     * @param workDirectory
     * @param baseFileName
     */
    private void TestRun(TestCode testCode, String workDirectory, String baseFileName) {
        try {
            Long testId = testCode.getTestId();
            String inputFilePath = String.format("%s/test/%s/input#%s.txt", new Object[]{judgeSystemDataPath, testId, testId});
            String stdOutputFilePath = String.format("%s/test/%s/output#%s.txt", new Object[]{judgeSystemDataPath, testId, testId});
            String outputFilePath = getOutputFilePath(workDirectory, testId);
            Map<String, Object> runtimeResult = getRuntimeResult(runner.getRuntimeResult(testCode.getLanguage(), 1000, 128 * 1024, workDirectory, baseFileName, inputFilePath, outputFilePath), stdOutputFilePath, outputFilePath);
            //程序运行正常结束
            runtimeResult.put("score", 100);

            applicationDispatcher.TestCodeRuntimeFinished(testId, testCode, runtimeResult, true);
        } catch (Exception e) {
            e.printStackTrace();
            //预处理器发生错误
            applicationDispatcher.onTestErrorOccurred(testCode.getTestId(), testCode, true);
        }
    }

    /**
     * 获取程序运行结果(及答案比对结果).
     *
     * @param result                 - 包含程序运行结果的Map对象
     * @param standardOutputFilePath - 标准输出文件路径
     * @param outputFilePath         - 用户输出文件路径
     * @return 包含程序运行结果的Map对象
     */
    private Map<String, Object> getRuntimeResult(Map<String, Object> result, String standardOutputFilePath, String outputFilePath) {
        if (result.get("runtimeResult").equals("AC")){
            if (isOutputTheSame(standardOutputFilePath,outputFilePath)) {
                result.put("runtimeResult", "AC");
            } else {
                result.put("runtimeResult", "WA");
            }
        }
        return result;
    }

    /**
     * 获取用户输出和标准输出的比对结果.
     *
     * @param standardOutputFilePath - 标准输出文件路径
     * @param outputFilePath         - 用户输出文件路径
     * @return 用户输出和标准输出是否相同
     */
    private boolean isOutputTheSame(String standardOutputFilePath, String outputFilePath) {
        return comparator.isOutputTheSame(standardOutputFilePath, outputFilePath);
    }

    /**
     * 获取编译信息
     *
     * @param workDirectory
     * @param baseFileName
     * @return
     */
    private String getCompileInfo(String workDirectory, String baseFileName) {
        String compileInfoPath = compiler.getCompileInfoPath(workDirectory, baseFileName);
        File file = new File(compileInfoPath);
        FileReader fileReader = null;
        BufferedReader compileInfo = null;
        StringBuffer sb = new StringBuffer();
        try {
            fileReader = new FileReader(file);
            compileInfo = new BufferedReader(fileReader);
            String str;
            while ((str = compileInfo.readLine()) != null) {
                sb.append(str);
                if (sb.length() > 500) {
                    return sb.toString();
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fileUtils.closeBufferedReader(compileInfo);
            fileUtils.closeFileReader(fileReader);
        }
        return null;
    }


    /**
     * 评测完成后, 清理所生成的文件.
     *
     * @param baseDirectory - 用于产生输出结果目录
     */
    private void cleanUp(String baseDirectory) {
        File baseDirFile = new File(baseDirectory);
        deleteDirectory(baseDirFile);
    }

    /**
     * 删除文件夹
     *
     * @param file
     */
    private void deleteDirectory(File file) {
        if (file == null) return;
        if (file.isFile()) {// 表示该文件不是文件夹
            file.delete();
        } else {
            // 首先得到当前的路径
            String[] childFilePaths = file.list();
            if (childFilePaths != null) {
                for (String childFilePath : childFilePaths) {
                    File childFile = new File(file.getAbsolutePath() + "/" + childFilePath);
                    deleteDirectory(childFile);
                }
            }
            file.delete();
        }
    }

    /**
     * 根据编译运行结果，判断编译是否结束
     *
     * @param result 编译运行结果
     * @return
     */
    private Boolean isCompileSuccess(Map<String, Object> result) {
        return (Boolean) result.get("isCompileSuccess");
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

}
