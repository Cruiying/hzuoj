package com.hqz.hzuoj.util;

import javassist.NotFoundException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/1 14:01
 * @Description: TODO
 */
public class PmsUploadUtil {
    public static String uploadImage(MultipartFile multipartFile) {
        //配置fdfs的全局链接地址

        //获取配置文件的路径
        String tracker = PmsUploadUtil.class.getResource("/tracker.conf").getPath();
        StringBuffer url = new StringBuffer();
        try {
            ClientGlobal.init(tracker);

            TrackerClient trackerClient = new TrackerClient();

            //获得trackerService的实例
            TrackerServer trackerServer = trackerClient.getConnection();

            //获得上传的二进制对象
            byte[] bytes = multipartFile.getBytes();

            //获取上传文件名
            String filename = multipartFile.getOriginalFilename();

            //获取上传文件名最后一个.的位置
            int index = filename.lastIndexOf(".");

            //获取上传文件名的扩展名
            String extName = filename.substring(index + 1);

            //通过tracker获得一个Storage链接客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);

            String[] uploadInfo = storageClient.upload_file(bytes, extName, null);


            url.append("http://192.168.230.129");
            for (String info: uploadInfo) {
                url.append("/").append(info);
            }
            return url.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url.toString();
    }
}
