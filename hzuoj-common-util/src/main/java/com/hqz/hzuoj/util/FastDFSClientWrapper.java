package com.hqz.hzuoj.util;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.proto.storage.DownloadCallback;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @Author: HQZ
 * @CreateTime: 2020/2/19 15:54
 * @Description: TODO
 */
@Component
public class FastDFSClientWrapper {

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    /**
     * @Description: 上传文件
     * @param file 文件对象
     * @return 文件路径
     * @throws IOException String
     */
    public String uploadFile(MultipartFile file) throws IOException {
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(),
                FilenameUtils.getExtension(file.getOriginalFilename()), null);
        return storePath.getFullPath();
    }

    /**
     * @Description: 上传文件
     * @param bytes 文件数据
     * @param format 文件格式（后缀）
     * @return String 文件路径
     */
    public String uploadFile(byte[] bytes, String format) {
        StorePath storePath = storageClient.uploadFile(new ByteArrayInputStream(bytes), bytes.length, format, null);
        return storePath.getFullPath();
    }

    /**
     * @Description: 上传文件
     * @param file 文件对象
     * @return
     * @throws IOException String
     */
    public String uploadFile(File file) throws IOException {
        StorePath storePath = storageClient.uploadFile(FileUtils.openInputStream(file), file.length(),
                FilenameUtils.getExtension(file.getName()), null);
        return storePath.getFullPath();
    }

    /**
     * @Description: 把字符串作为指定格式的文件上传
     * @param content
     * @param fileExtension
     * @return String
     */
    public String uploadFile(String content, String fileExtension) {
        byte[] buff = content.getBytes(Charset.forName("UTF-8"));
        ByteArrayInputStream stream = new ByteArrayInputStream(buff);
        StorePath storePath = storageClient.uploadFile(stream, buff.length, fileExtension, null);
        return storePath.getFullPath();
    }

    /**
     * @Description: 上传文件
     * @param file 文件对象
     * @return 文件路径
     * @throws IOException String
     */
    public String uploadImageAndCrtThumbImage(MultipartFile file) throws IOException {
        StorePath storePath = storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(),
                FilenameUtils.getExtension(file.getOriginalFilename()), null);
        return storePath.getFullPath();
    }

    /**
     * @Description: 根据图片路径获取缩略图路径（使用uploadImageAndCrtThumbImage方法上传图片）
     * @param filePath 图片路径
     * @return String 缩略图路径
     */
    public String getThumbImagePath(String filePath) {
        return thumbImageConfig.getThumbImagePath(filePath);
    }

    /**
     * @Description: 根据文件路径下载文件
     * @param filePath 文件路径
     * @return 文件字节数据
     * @throws IOException byte[]
     */
    public byte[] downFile(String filePath) throws IOException {
        StorePath storePath = StorePath.praseFromUrl(filePath);
        return storageClient.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadCallback<byte[]>() {
            @Override
            public byte[] recv(InputStream ins) throws IOException {
                return org.apache.commons.io.IOUtils.toByteArray(ins);
            }
        });
    }

    /**
     * @Description: 根据文件地址删除文件
     * @param filePath 文件访问地址
     */
    public void deleteFile(String filePath) {
        StorePath storePath = StorePath.praseFromUrl(filePath);
        storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
    }
}