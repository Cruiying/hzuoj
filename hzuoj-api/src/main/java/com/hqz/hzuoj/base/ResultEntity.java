package com.hqz.hzuoj.base;
import com.hqz.hzuoj.common.Constants;
import lombok.*;

import java.io.Serializable;

/**
 * @Author: HQZ
 * @CreateTime: 2020/5/2 22:14
 * @Description: TODO
 */
@Data
@Getter
@Setter
@AllArgsConstructor
public class ResultEntity implements Serializable {

    /**
     * 请求状态码
     */
    private String code;
    /**
     * 请求消息
     */
    private String message;
    /**
     * 请求数据
     */
    private Object data;

    public ResultEntity(){

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static ResultEntity success(String code, String message, Object data) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(code);
        resultEntity.setMessage(message);
        resultEntity.setData(data);
        return resultEntity;
    }

    public static ResultEntity success(String message, Object data) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(Constants.SUCCESS);
        resultEntity.setMessage(message);
        resultEntity.setData(data);
        return resultEntity;
    }

    public static ResultEntity success(Object data) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(Constants.SUCCESS);
        resultEntity.setMessage(Constants.SUCCESS_MSG);
        resultEntity.setData(data);
        return resultEntity;
    }

    public static ResultEntity error(String code, String message, Object data) {
        return success(code, message, data);
    }

    public static ResultEntity error(String message, Object data) {
        ResultEntity resultEntity = success(message, data);
        resultEntity.setCode(Constants.ERROR);
        return resultEntity;
    }

    public static ResultEntity error(Object data) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(Constants.ERROR);
        resultEntity.setMessage(Constants.ERROR_MSG);
        resultEntity.setData(data);
        return resultEntity;
    }

}
