package com.hqz.hzuoj.judge.system;

import com.hqz.hzuoj.bean.language.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/1 11:35
 * @Description: TODO
 */
@Component
public class Runner {


    @Autowired
    private JudgeSystem judgeSystem;

    /**
     * 获取(用户)程序运行结果.
     *
     * @param language       - 编译语言
     * @param timeLimit      - 程序运行的最大时间限制
     * @param memoryLimit    - 程序运行的最大内存限制
     * @param workDirectory  - 编译生成结果的目录以及程序输出的目录
     * @param baseFileName   - 待执行的应用程序文件名(不包含文件后缀)
     * @param inputFilePath  - 输入文件路径
     * @param outputFilePath - 输出文件路径
     * @return 一个包含程序运行结果的Map<String, Object>对象
     */
    public Map<String, Object> getRuntimeResult(Language language, int timeLimit, int memoryLimit, String workDirectory, String baseFileName, String inputFilePath, String outputFilePath) {
        /*获取程序运行命令*/
        String cmd = getRuntimeCmd(language, workDirectory, baseFileName);
        Map<String, Object> result = new HashMap<>();
        String runtimeResult = "SE";
        int usedTime = 0, usedMemory = 0, exitCode = 0;
        /*运行程序并，获取程序运行信息*/
        Map<String, Object> RuntimeInfo = judgeSystem.getRuntimeResult(cmd, inputFilePath, outputFilePath, timeLimit, memoryLimit);
        exitCode = (Integer) RuntimeInfo.get("exitCode");
        usedTime = (Integer) RuntimeInfo.get("usedTime");
        usedMemory = (Integer) RuntimeInfo.get("usedMemory");
        //根据程序运行信息封装运行结果
        runtimeResult = getRuntimeResult(exitCode, timeLimit, usedTime, memoryLimit, usedMemory);
        result.put("runtimeResult", runtimeResult);
        result.put("usedTime", usedTime);
        result.put("usedMemory", usedMemory);
        return result;
    }

    /**
     * 根据系统运行返回的结果封装评测结果.
     *
     * @param exitCode    - 程序退出状态位
     * @param timeLimit   - 最大时间限制
     * @param timeUsed    - 程序运行所用时间
     * @param memoryLimit - 最大空间限制
     * @param memoryUsed  - 程序运行所用空间(最大值)
     * @return 程序运行结果的唯一英文缩写
     */
    private String getRuntimeResult(int exitCode, int timeLimit, int timeUsed, int memoryLimit, int memoryUsed) {
        if (timeUsed >= timeLimit) {
            return "TLE";
        }
        if (memoryUsed >= memoryLimit) {
            return "MLE";
        }
        if (exitCode == 0) {
            return "AC";
        }
        return "RE";
    }

    /**
     * 获取待执行的命令行.
     *
     * @param language      - 编译语言
     * @param workDirectory - 编译生成结果的目录以及程序输出的目录
     * @param baseFileName  - 待执行的应用程序文件名(不包含文件后缀)
     * @return 待执行的命令行
     */
    private String getRuntimeCmd(Language language, String workDirectory, String baseFileName) {
        String filePathWithoutExtension = String.format("%s/%s", new Object[]{workDirectory, baseFileName});
        StringBuilder cmd = new StringBuilder(language.getLanguageRuntimeCmd().replaceAll("\\{filename\\}", filePathWithoutExtension));
        if ("Java".equalsIgnoreCase(language.getLanguageName())) {
            int lastIndexOfSpace = cmd.lastIndexOf("/");
            cmd.setCharAt(lastIndexOfSpace, ' ');
        }
        return cmd.toString();
    }
}
