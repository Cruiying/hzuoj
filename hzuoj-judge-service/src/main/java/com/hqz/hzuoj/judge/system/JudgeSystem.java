package com.hqz.hzuoj.judge.system;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 系统运行器
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/29 18:18
 * @Description: TODO
 */
@Component
public class JudgeSystem {

    @Value("${judgeSystem.username}")
    private String username;

    @Value("${judgeSystem.password}")
    private String password;


    //加载动态连接库
    static {
        try {
            String path = System.getProperty("user.dir") + "\\JudgeSystem.dll";
            System.err.println("动态库与项目放入同一目录:" + path);
            System.load(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行系统的cmd命令（以低权限的方式执行cmd命令）
     *
     * @param cmd              待执行的命令
     * @param username         用户名
     * @param password         密码
     * @param inputFilePath    程序输入文件路径
     * @param outputFilePath   输出文件路径
     * @param maxRuntimeTime   程序运行的最大时间限制
     * @param maxRuntimeMemory 程序运行的最大内存限制
     * @return 执行命令所得的结果集封装为map形式（userTime, userMemory, exitCode）
     */
    private synchronized native Map<String, Object> runner(String cmd, String username, String password, String inputFilePath, String outputFilePath, int maxRuntimeTime, int maxRuntimeMemory);

    /**
     * 执行编译命令
     *
     * @param cmd            待编译的命令
     * @param inputFilePath  程序的输入路径
     * @param outputFilePath 程序的输出路径
     * @return 执行命令所得的结果集
     */
    public Map<String, Object> getCompileResult(String cmd, String inputFilePath, String outputFilePath) {
        int maxRuntimeTime = 15000;
        int maxRuntimeMemory = 1024 * 256;
        return runner(cmd, username, password, inputFilePath, outputFilePath, maxRuntimeTime, maxRuntimeMemory);
    }

    /**
     * 执行运行命令
     *
     * @param cmd              待运行的命令
     * @param inputFilePath    程序的输入文件路径
     * @param outputFilePath   程序的输出文件路径
     * @param maxRuntimeTime   程序运行的最大时间限制
     * @param maxRuntimeMemory 程序运行的最大内存限制
     * @return 执行命令所得的结果集
     */
    public Map<String, Object> getRuntimeResult(String cmd, String inputFilePath, String outputFilePath, Integer maxRuntimeTime, Integer maxRuntimeMemory) {
        return runner(cmd, username, password, inputFilePath, outputFilePath, maxRuntimeTime, maxRuntimeMemory);
    }
}
