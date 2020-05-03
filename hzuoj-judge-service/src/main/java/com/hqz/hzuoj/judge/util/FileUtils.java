package com.hqz.hzuoj.judge.util;

import com.hqz.hzuoj.judge.system.JudgeSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/28 20:24
 * @Description: TODO
 */
@Component
public class FileUtils {
    /**
     * 关闭文件
     *
     * @param fileReader
     */
    public void closeFileReader(FileReader fileReader) {
        if (fileReader != null) {
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭文件
     *
     * @param bufferedReader
     */
    public void closeBufferedReader(BufferedReader bufferedReader) {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void closeFileInputStream(FileInputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
