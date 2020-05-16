package com.hqz.hzuoj.judge.mq;

import com.alibaba.fastjson.JSON;
import com.hqz.hzuoj.bean.submit.TestCode;
import com.hqz.hzuoj.judge.application.ApplicationDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;


/**
 * 测评提交的消息接收
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/29 21:01
 * @Description: TODO
 */
@Component
public class SubmitMessageReceiver {
    /**
     * 测评应用程序的调度器
     */
    @Autowired
    private ApplicationDispatcher dispatcher;

    @Value("${testResult}")
    private String testResult;

    @Value("${submitResult}")
    private String submitResult;

    @Value("${submitQueue}")
    private String submitQueue;

    @Value("${testQueue}")
    private String testQueue;

    /**
     * 测评提交消息的接收
     *
     * @param message
     * @throws JMSException
     */
    @JmsListener(destination = "${submitQueue}", concurrency = "5", containerFactory = "jmsQueueListenerContainerFactory")
    public void submitReceiver(Message message) throws JMSException, InterruptedException {
        MapMessage mapMessage = (MapMessage) message;
        Integer submitId = mapMessage.getInt("submitId");
        createSubmit(submitId);
    }

    /**
     * 自测提交消息的接收
     *
     * @param message
     * @throws JMSException
     */
    @JmsListener(destination = "${testQueue}", concurrency = "5", containerFactory = "jmsQueueListenerContainerFactory")
    public void testReceiver(Message message) throws JMSException {
        if (message != null) {
            MapMessage mapMessage = (MapMessage) message;
            Long testId = mapMessage.getLong("testId");
            String test = mapMessage.getString("testCode");
            TestCode testCode = JSON.parseObject(test, TestCode.class);
            createTest(testId, testCode);
        }
    }

    /**
     * 处理新提交测评请求.
     *
     * @param submitId
     */
    private void createSubmit(Integer submitId) {
        dispatcher.createSubmit(submitId);
    }

    /**
     * 处理新的提交自测请求
     *
     * @param testId
     */
    private void createTest(Long testId, TestCode testCode) {
        dispatcher.createTest(testId, testCode);
    }
}
