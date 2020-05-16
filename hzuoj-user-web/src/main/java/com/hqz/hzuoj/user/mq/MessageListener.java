package com.hqz.hzuoj.user.mq;

import com.hqz.hzuoj.bean.problem.Data;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: HQZ
 * @CreateTime: 2020/5/16 9:07
 * @Description: TODO
 */
public class MessageListener implements Serializable {

    private Date date;
    private Object message;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
