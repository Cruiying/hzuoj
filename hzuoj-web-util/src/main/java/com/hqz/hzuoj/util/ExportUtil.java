package com.hqz.hzuoj.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Author: HQZ
 * @CreateTime: 2020/2/1 21:02
 * @Description: TODO
 */
public class ExportUtil {
    /**
     * 导出文件
     *
     * @param response
     * @param file
     *            导出文件
     * @param name
     * @param contentType
     * @throws IOException
     */
    public static void download(HttpServletResponse response, File file,
                                String name, String contentType) throws IOException {
        String fileName = StringUtils.isBlank(name) ? file.getName() : name;
        download(response, new FileInputStream(file), fileName, contentType);
    }

    /**
     * 下载数据/文件
     *
     * @param response
     *            HTTP输出
     * @param inputStream
     *            文件流
     * @param fileName
     *            文件名
     * @param contentType
     *            ContentType in HTTP Header
     * @throws IOException
     *             IO异常
     */
    public static void download(HttpServletResponse response,
                                InputStream inputStream, String fileName, String contentType)
            throws IOException {

        response.setContentType(StringUtils.isEmpty(contentType) ? "application/octet-stream"
                : contentType);
        response.setHeader("Content-Disposition", "attachment;filename="
                + new String(fileName.getBytes("UTF-8"), "UTF-8"));
        response.setStatus(HttpServletResponse.SC_OK);

        BufferedInputStream reader = null;
        try {
            reader = new BufferedInputStream(inputStream);
            IOUtils.copy(reader, response.getOutputStream());
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
