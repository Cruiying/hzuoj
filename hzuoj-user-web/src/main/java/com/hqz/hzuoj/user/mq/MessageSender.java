package com.hqz.hzuoj.user.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 提交测评的消息发送队列
 * @Author: HQZ
 * @CreateTime: 2019/12/29 20:21
 * @Description: TODO
 */
@Component
public class MessageSender {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    /**
     * 发送消息
     * @param queueName - 发送的队列名
     * @param mapMessage - 发送的消息
     */
    public void sendQueue(String queueName, final Map<String, Object> mapMessage) {
        jmsMessagingTemplate.convertAndSend(queueName, mapMessage);
    }

}
