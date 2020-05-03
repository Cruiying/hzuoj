package com.hqz.hzuoj.user.test;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/7 21:56
 * @Description: TODO
 */
@Component
public class testActiveMQ {

    @JmsListener(destination = "testActiveMq")
    private void test(Message message) throws JMSException {
        MapMessage mapMessage = (MapMessage) message;
        System.out.println(mapMessage.getString("flag"));
    }
}
