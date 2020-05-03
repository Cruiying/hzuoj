package com.hqz.hzuoj.user.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

/**
 * 测试结果消息队列监听器
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/30 10:50
 * @Description: TODO
 */
@Component
public class ResultMessageReceiver {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 监听用户提交消息队列中的消息
     *
     * @param message
     * @throws JMSException
     * @throws InterruptedException
     */
    @JmsListener(destination = "${submitResult}", concurrency = "10", containerFactory = "jmsQueueListenerContainerFactory")
    public void submitResultReceiver(Message message) throws JMSException {
        //如果测试结果的消息队列中有消息
        MapMessage mapMessage = (MapMessage) message;
        boolean completed = mapMessage.getBoolean("completed");
        String judgeInfo = mapMessage.getString("judgeInfo");
        Integer submitId = mapMessage.getInt("submitId");
        SubmitResultMessageEvent messageEvent = new SubmitResultMessageEvent(this, submitId);
        messageEvent.setSubmitId(submitId);
        messageEvent.setCompleted(completed);
        messageEvent.setJudgeInfo(judgeInfo);
        applicationContext.publishEvent(messageEvent);


    }

    /**
     * 监听用户自测消息队列中的消息
     *
     * @param message
     * @throws JMSException
     * @throws InterruptedException
     */
    @JmsListener(destination = "${testResult}", concurrency = "10", containerFactory = "jmsQueueListenerContainerFactory")
    public void TestResultReceiver(Message message) throws JMSException {
        if (message != null) {
//                //如果测试结果的消息队列中有消息
            MapMessage mapMessage = (MapMessage) message;
            Long testId = mapMessage.getLong("testId");
            TestResultMessageEvent messageEvent = new TestResultMessageEvent(this, testId);
            messageEvent.setTestId(testId);
            boolean completed = mapMessage.getBoolean("completed");
            String judgeInfo = mapMessage.getString("judgeInfo");
            messageEvent.setCompleted(completed);
            messageEvent.setJudgeInfo(judgeInfo);
            applicationContext.publishEvent(messageEvent);
        }
    }
}
