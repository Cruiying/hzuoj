package com.hqz.hzuoj.judge.system;

import com.hqz.hzuoj.bean.language.Language;
import com.hqz.hzuoj.judge.util.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 程序编译器
 *
 * @Author: HQZ
 * @CreateTime: 2020/1/1 11:34
 * @Description: TODO
 */
@Component
public class Compiler {

    @Autowired
    private JudgeSystem judgeSystem;

    @Autowired
    private FileUtils fileUtils;

    /**
     * 获取编译输出结果.
     *
     * @param language      - 编译语言
     * @param workDirectory - 编译输出目录
     * @param baseFileName  - 编译输出文件名
     * @return 包含编译输出结果的Map<String, Object>对象
     */
    public Map<String, Object> getCompileResult(Language language, String workDirectory, String baseFileName) {
        String cmd = getCompileCmd(language, workDirectory, baseFileName);
        String compileInfoPath = getCompileInfoPath(workDirectory, baseFileName);
        return getCompileResult(cmd, compileInfoPath);
    }

    /**
     * 获取编译命令.
     *
     * @param language      - 编译语言
     * @param workDirectory - 编译输出目录
     * @param baseFileName  - 编译输出文件名
     * @return 编译命令
     */
    private String getCompileCmd(Language language, String workDirectory, String baseFileName) {
        String filePathWithoutExtension = String.format("%s/%s", new Object[]{workDirectory, baseFileName});
        return language.getLanguageCompileCmd().replaceAll("\\{filename\\}", filePathWithoutExtension);
    }

    /**
     * 获取编译输出结果.
     *
     * @param cmd             - 编译命令
     * @param compileInfoPath - 编译信息输出路径
     * @return 包含编译输出结果的Map<String, Object>对象
     */
    private Map<String, Object> getCompileResult(String cmd, String compileInfoPath) {
        String inputFilePath = "";
        /*测评系统去编译代码，得到返回结果compileResult*/
        Map<String, Object> compileResult = judgeSystem.getCompileResult(cmd, inputFilePath, compileInfoPath);
        Map<String, Object> result = new HashMap<>();
        boolean isCompileSuccess = false;
        if (compileResult != null) {
            int exitCode = (Integer) compileResult.get("exitCode");
            isCompileSuccess = (exitCode == 0);
        }
        //编译正常:true，编译出错:false
        result.put("isCompileSuccess", isCompileSuccess);
        result.put("compileInfo", getCompileOutput(compileInfoPath));
        return result;
    }

    /**
     * 获取编译信息内容.
     *
     * @param compileInfoPath - 编译信息存放路径
     * @return 编译信息内容
     */
    private String getCompileOutput(String compileInfoPath) {
        FileInputStream inputStream = null;
        String compileInfo = "";
        try {
            inputStream = new FileInputStream(compileInfoPath);
            compileInfo = IOUtils.toString(inputStream,"UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            fileUtils.closeFileInputStream(inputStream);
        }
        return compileInfo;
    }

    /**
     * 获取编译信息输出的文件路径.
     *
     * @param workDirectory - 编译输出目录
     * @param baseFileName  - 编译输出文件名
     * @return 编译信息输出的文件路径
     */
    public String getCompileInfoPath(String workDirectory, String baseFileName) {
        return String.format("%s/%s_compileInfo.txt", new Object[]{workDirectory, baseFileName});
    }
}
