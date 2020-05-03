package com.hqz.hzuoj.judge.system;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.hqz.hzuoj.bean.language.Language;
import com.hqz.hzuoj.bean.problem.Data;
import com.hqz.hzuoj.bean.problem.Problem;
import com.hqz.hzuoj.bean.submit.TestCode;
import com.hqz.hzuoj.judge.mapper.DataMapper;
import com.hqz.hzuoj.service.DataService;
import com.hqz.hzuoj.util.FastDFSClientWrapper;
import com.hqz.hzuoj.util.util.RedisUtil;
import javassist.NotFoundException;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import net.lingala.zip4j.core.ZipFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 程序运行预处理器，用于完成评测前准备工作.
 *
 * @Author: HQZ
 * @CreateTime: 2020/1/1 20:52
 * @Description: TODO
 */
@Component
public class Preprocessor {

    @Autowired
    private DataMapper dataMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 问题数据解压后测试点数据上传路径
     */
    @Value("${dataPath}")
    private String dataPath;
    /**
     * 问题数据没有解压前的测试数据
     */
    @Value("${problemDataPath}")
    private String problemDataPath;
    /**
     * 程序运行时测试数据存放的目录
     */
    @Value("${judgeSystemDataPath}")
    private String judgeSystemDataPath;

    @Autowired
    private FastDFSClientWrapper fastDFSClientWrapper;


    /**
     * 创建运行代码至本地磁盘.
     *
     * @param userCode      创建用户需要保存的代码
     * @param language      保存使用的语言
     * @param workDirectory 用于产生编译输出的目录
     * @param baseFileName  随机文件名(不包含后缀)
     * @throws NotFoundException
     * @throws IOException
     */
    public void createRuntimeCode(String userCode, Language language, String workDirectory, String baseFileName) throws NotFoundException, IOException {
        File workDirFile = new File(workDirectory);
        if (!workDirFile.exists() && !workDirFile.mkdirs()) {
            throw new NotFoundException("运行目录不存在");
        }
        //设置编译运行目录下的文件权限
        setWorkDirectoryPermission(workDirFile);
        //运行代码的替换
        String code = replaceClassName(language, userCode, baseFileName);
        //创建
        String codeFilePath = String.format("%s/%s.%s", new Object[]{workDirectory, baseFileName, getCodeFileSuffix(language)});
        //将代码写入磁盘
        FileOutputStream outputStream = new FileOutputStream(new File(codeFilePath));
        IOUtils.write(code, outputStream, "UTF-8");
        IOUtils.closeQuietly(outputStream);
    }

    /**
     * 加载题目的测试数据
     *
     * @param problem
     * @return
     * @throws IOException
     */
    public String loadProblemData(Problem problem) throws IOException, ZipException {
        String problemDataAddress = problem.getProblemDataAddress();
        Integer problemDataVersions = problem.getProblemDataVersions();
        if (problemDataVersions == null) return "error";
        if (problemDataAddress == null) return "error";
        //下载路径
        String filepath = problemDataPath + "/" + problem.getProblemId();
        //解压路径
        String dataZipPath = dataPath + "/" + problem.getProblemId();
        //确保路径存在
        File file2 = new File(filepath);
        File file3 = new File(dataZipPath);
        if (!file2.exists()) {
            file2.mkdirs();
        }
        if (!file3.exists()) {
            file3.mkdirs();
        }
        String saveFile = filepath + "/hzuoj-data(" + problemDataVersions + ").zip";
        File file1 = new File(saveFile);
        //该版本的数据是否已经存在
        if (file1.exists()) {
            return "success";
        }
        try {
            byte[] bytes = fastDFSClientWrapper.downFile(problemDataAddress);
            OutputStream os = null;
            os = new FileOutputStream(saveFile, true);
            file1.createNewFile();
            os.write(bytes, 0, bytes.length);    //3、写入文件
            os.flush();    //将存储在管道中的数据强制刷新出去
            os.close();
            ZipFile zipFile = new ZipFile(file1);
            zipFile.extractAll(dataZipPath);
        } catch (Exception e) {
            //如果发生异常，删除数据
            e.printStackTrace();
            if (file1.exists()) file1.delete();
        }
        return "success";
    }


    /**
     * 获取代码文件的后缀名.
     *
     * @param language - 编程语言对象
     * @return 代码文件的后缀名
     */
    private String getCodeFileSuffix(Language language) {
        return language.getLanguageSuffix();
    }

    /**
     * 替换部分语言中的类名(如Java), 以保证正常通过编译.
     *
     * @param language     - 编程语言对象
     * @param code         - 待替换的代码
     * @param newClassName - 新的类名
     */
    private String replaceClassName(Language language, String code, String newClassName) {
        if (!language.getLanguageName().equalsIgnoreCase("Java")) {
            return code;
        }
        return code.replaceAll("class[ \n]+Main", "class " + newClassName);
    }

    /**
     * 设置代码文件所在目录的读写权限.
     *
     * @param workDirectory 用于产生编译输出的目录
     */
    private void setWorkDirectoryPermission(File workDirectory) throws IOException {

    }


    /**
     * 保存用户的自测输入输出信息
     *
     * @param testCode
     */
    public void saveTestData(TestCode testCode) throws NotFoundException, IOException {
        synchronized (this) {
            String checkpointsFilePath = String.format("%s/test/%s", new Object[]{judgeSystemDataPath, testCode.getTestId()});
            File checkpointsDirFile = new File(checkpointsFilePath);
            if (!checkpointsDirFile.exists() && !checkpointsDirFile.mkdirs()) {
                throw new NotFoundException("Failed to create the checkpoints directory: " + checkpointsFilePath);
            }
            {
                String inputFilePath = String.format("%s/input#%s.txt", new Object[]{checkpointsFilePath, testCode.getTestId()});
                FileOutputStream inputStream = new FileOutputStream(new File(inputFilePath));
                String input = testCode.getTestInput();
                IOUtils.write(input, inputStream, "UTF-8");
                IOUtils.closeQuietly(inputStream);
                if (inputStream != null) inputStream.close();
            }
            {
                String outputFilePath = String.format("%s/output#%s.txt", new Object[]{checkpointsFilePath, testCode.getTestId()});
                FileOutputStream outputStream = new FileOutputStream(new File(outputFilePath));
                String output = testCode.getTestOutput();
                IOUtils.write(output, outputStream, "UTF-8");
                IOUtils.closeQuietly(outputStream);
                outputStream.close();
            }
        }

    }

}
