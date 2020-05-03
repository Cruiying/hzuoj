package com.hqz.hzuoj.judge.system;

import com.hqz.hzuoj.judge.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * 用于比对用户输出结果和标准结果.
 *
 * @Author: HQZ
 * @CreateTime: 2020/1/1 12:29
 * @Description: TODO
 */
@Component
public class Comparator {

    @Autowired
    private FileUtils fileUtils;

    /**
     * 获取用户输出和标准输出的比对结果.
     *
     * @param standardOutputFilePath - 标准输出文件路径
     * @param outputFilePath         - 用户输出文件路径
     * @return 用户输出和标准输出是否相同:相同（true）,不相同（false）
     */
    public boolean isOutputTheSame(String standardOutputFilePath, String outputFilePath) {

        File stdFile = new File(standardOutputFilePath);
        File userFile = new File(outputFilePath);
        FileReader stdFileReader = null, userFileReader = null;
        BufferedReader stdOutput = null, userOutput = null;
        try {
            stdFileReader = new FileReader(stdFile);
            userFileReader = new FileReader(userFile);
            /*读取字符流*/
            stdOutput = new BufferedReader(stdFileReader);
            userOutput = new BufferedReader(userFileReader);
            return isFileOutputTheSame(stdOutput, userOutput);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /*关闭文件*/
            fileUtils.closeBufferedReader(stdOutput);
            fileUtils.closeBufferedReader(userOutput);
            fileUtils.closeFileReader(stdFileReader);
            fileUtils.closeFileReader(userFileReader);
        }
        return false;
    }


    /**
     * 比对标准输出和用户输出是否相同.
     *
     * @param stdOutput  标准输出文件
     * @param userOutput 用户输出文件
     * @return 标准输出和用户输出是否相同
     */
    private boolean isFileOutputTheSame(BufferedReader stdOutput, BufferedReader userOutput) {
        try {
            String stdLine, line;
            while (true) {
                /*获取数据*/
                stdLine = stdOutput.readLine();
                line = userOutput.readLine();
                /*如果有一个为空，跳出循环*/
                if (stdLine == null || line == null) {
                    break;
                }
                /*判断数据是否相等*/
                if (!isLineOutputTheSame(stdLine, line)) {
                    return false;
                }
            }
            /*如果标准输入不为空*/
            while (stdLine != null) {
                /*判断*/
                if (!isLineEmpty(stdLine, 0)) {
                    return false;
                }
                /*进行读取数据*/
                stdLine = stdOutput.readLine();
            }
            /*如果用户程序输出不为空*/
            while (line != null) {
                /*判断*/
                if (!isLineEmpty(line, 0)) {
                    return false;
                }
                /*进行读取*/
                line = userOutput.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 比对某行的标准输出和用户输出(忽略行尾空格).
     *
     * @param stdLine - 标准输出中的某一行
     * @param line    - 用户输出中的某一行
     * @return 某行的标准输出和用户输出是否相同
     */
    private boolean isLineOutputTheSame(String stdLine, String line) {
        int i = 0, j = 0;
        for (; i < stdLine.length() && j < line.length(); ++i, ++j) {
            if (stdLine.charAt(i) != line.charAt(j)) {
                if (stdLine.charAt(i) == '\n') {
                    if (!isLineEmpty(line, j)) {
                        return false;
                    }
                    return true;
                } else if (line.charAt(j) == '\n') {
                    if (!isLineEmpty(stdLine, i)) {
                        return false;
                    }
                    return true;
                }
                return false;
            }
        }
        while (i < stdLine.length()) {
            if (!isLineEmpty(stdLine, i)) {
                return false;
            }
            ++i;
        }
        while (j < line.length()) {
            if (!isLineEmpty(line, j)) {
                return false;
            }
            ++j;
        }
        return true;
    }

    /**
     * 忽略文件结尾的空行与空格.
     *
     * @param line       - 某行文件内容
     * @param startIndex - 开始检查位置的索引
     * @return 该行内容中是否只包含空格和换行符
     */
    private boolean isLineEmpty(String line, int startIndex) {
        for (int i = startIndex; i < line.length(); ++i) {
            if (!(line.charAt(i) == ' ' || line.charAt(i) == '\n')) {
                return false;
            }
        }
        return true;
    }
}
