package com.hqz.hzuoj.admin.controller;

import com.alibaba.dubbo.common.utils.Assert;
import com.alibaba.dubbo.config.annotation.Reference;
import com.hqz.hzuoj.annotations.AdminLoginCheck;
import com.hqz.hzuoj.base.ResultEntity;
import com.hqz.hzuoj.bean.problem.Data;
import com.hqz.hzuoj.bean.problem.Problem;
import com.hqz.hzuoj.service.DataService;
import com.hqz.hzuoj.service.ExampleService;
import com.hqz.hzuoj.service.ProblemService;
import com.hqz.hzuoj.util.FastDFSClientWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/18 18:07
 * @Description: TODO
 */
@Controller
@RequestMapping("/admin")
@AdminLoginCheck
@Slf4j
public class DataController {
    @Reference
    private ProblemService problemService;

    @Reference
    private DataService dataService;

    @Reference
    private ExampleService exampleService;
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

    @Autowired
    private FastDFSClientWrapper fastDFSClientWrapper;

    @Value("${uploadPath}")
    private String uploadPath;

    /**
     * 上传测试数据
     *
     * @param file
     * @param
     * @return
     */
    @RequestMapping("/upload/{problemId}")
    @ResponseBody
    public ResultEntity upload(@RequestParam("file") MultipartFile file, @PathVariable Integer problemId) {
        //确保路径存在
        File file2 = null;
        File file3 = null;
        try {
            //问题数据存放路径
            String filepath = problemDataPath + "/" + problemId;
            String filename = file.getOriginalFilename();
            //解压路径
            String dataZipPath = dataPath + "/" + problemId;
            //测试数据文件路径
            file2 = new File(filepath);
            file3 = new File(dataZipPath);
            ;
            Problem problem = problemService.getProblem(problemId);
            if (problem == null) {
                return ResultEntity.error("题目不存在");
            }
            Assert.notNull(file, "上传文件不能为空");
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1, file.getOriginalFilename().length());
            if (!"zip".toUpperCase().contains(suffix.toUpperCase())) {
                return ResultEntity.error("上传文件格式不对");
            }
            if (file.getSize() > 1024 * 1024 * 50) {
                return ResultEntity.error("文件不能大于50M");
            }
            deleteDirectory(file2);
            deleteDirectory(file3);
            file2.mkdirs();
            file3.mkdirs();
            String savePath = filepath + "/" + filename;
            //上传文件到服务器
            String up = uploadPath + "/" + fastDFSClientWrapper.uploadFile(file);
            //保存文件到服务器
            File file1 = new File(savePath);
            //保存
            file.transferTo(file1);
            //解压文件
            ZipFile zipFile = new ZipFile(file1);
            zipFile.extractAll(dataZipPath);
            List<Data> dataList = check(dataZipPath, problemId);
            //保存数据
            return ResultEntity.success("上传成功", dataService.saveData(problemId, dataList, up));
        } catch (Exception e) {
            log.error("upload({}) error message: {}", problemId, e.getMessage());
            return ResultEntity.error(e.getMessage());
        } finally {
            deleteDirectory(file2);
            deleteDirectory(file3);
        }
    }

    /**
     * 删除文件夹
     *
     * @param file
     */
    private void deleteDirectory(File file) {
        if (file == null) {
            return;
        }
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
     * 检查测试数据是否合法
     *
     * @param path
     * @param problemId
     * @return
     */
    private List<Data> check(String path, Integer problemId) {
        List<Data> dataList = new ArrayList<>();
        //输入测试数据Map
        Map<String, String> inMap = getFiles(path, ".in");
        //输出测试数据Map
        Map<String, String> outMap = getFiles(path, ".out");
        //判断大小是否相等并且文件数大于1
        if (inMap == null || outMap == null || inMap.size() <= 0 || outMap.size() <= 0 || inMap.size() != outMap.size()) {
            return dataList;
        }
        for (Map.Entry entry : inMap.entrySet()) {
            String key = (String) entry.getKey();
            String outValue = outMap.get(key);
            if (outValue == null) {
                return dataList;
            }
        }
        Problem problem = new Problem();
        problem.setProblemId(problemId);
        for (Map.Entry entry : inMap.entrySet()) {
            String key = (String) entry.getKey();
            String inValue = (String) entry.getValue();
            String outValue = outMap.get(key);
            Data data = new Data();
            data.setDataInput(inValue);
            data.setDataOutput(outValue);
            data.setProblem(problem);
            data.setDataMaxRuntimeTime(1000);
            data.setDataMaxRuntimeMemory(131072);
            dataList.add(data);
        }
        return dataList;
    }

    /**
     * 返回路径下的所有测试数据
     *
     * @param path 查找的路径
     * @return Map<String, String> key为文件名不带后缀，Value为文件名带后缀
     */
    private Map<String, String> getFiles(String path, String suffix) {
        Map<String, String> fileNameMap = new HashMap<>();
        File filePath = new File(path);
        File[] tempList = filePath.listFiles();
        if (tempList == null || tempList.length <= 0) {
            return null;
        }
        for (File file : tempList) {
            if (file.isFile()) {
                //如果是一个文件
                int index = file.getName().lastIndexOf(".");
                String fileSuffix = file.getName().substring(index);
                String fileName = file.getName().substring(0, index);
                if (".in".equals(fileSuffix) || ".out".equals(fileSuffix)) {
                    //判断是否是指定文件后缀
                    if (fileSuffix.equals(suffix)) {
                        fileNameMap.put(fileName, file.getName());
                    }
                } else {
                    return new HashMap<>();
                }
            } else {
                //否则是一个目录，不允许出现
                return new HashMap<>();
            }
        }
        return fileNameMap;
    }

    /**
     * 测试点页面
     *
     * @param problemId
     * @param modelMap
     * @return
     */
    @RequestMapping("{problemId}/dataEdit")
    public String adminEdit(@PathVariable Integer problemId, ModelMap modelMap) {
        if (problemId == null) {
            return "";
        }
        modelMap.put("problemId", problemId);
        return "dataEdit";
    }

    /**
     * 返回测试点信息
     *
     * @param problemId
     * @return
     */
    @RequestMapping("{problemId}/dataInfo")
    @ResponseBody
    public ResultEntity dataInfo(@PathVariable Integer problemId) {
        try {
            return ResultEntity.success("获取成功", dataService.getProblemDatas(problemId));
        } catch (Exception e) {
            log.error("dataInfo({}) error message: {}", problemId, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

    /**
     * 修改测试点
     *
     * @param problemId
     * @param datas
     * @return
     */
    @RequestMapping("{problemId}/updateData")
    @ResponseBody
    public ResultEntity updateProblemData(@PathVariable Integer problemId, @RequestBody List<Data> datas) {
        try {
            return ResultEntity.success("修改成功", dataService.updateProblemData(datas));
        } catch (Exception e) {
            log.error("updateProblemData({}, {}) error message: {}", problemId, datas, e.getMessage());
            return ResultEntity.error(e.getMessage());
        }
    }

}
