package com.hqz.hzuoj.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

/**
 * @Author: HQZ
 * @CreateTime: 2020/1/8 8:45
 * @Description: TODO
 */
@Configuration
@EnableJms
public class ActiveMqConf {

    @Value("${activeMQTcp}")
    private String activeMQTcp;
    @Value("${activeMQUsername}")
    private String activeMQUsername;
    @Value("${activeMQPassword}")
    private String activeMQPassword;

    @Bean
    public javax.jms.Connection activeConnection() throws JMSException {
        ConnectionFactory connectionFactory = this.connectionFactory();

        return connectionFactory.createConnection();
    }
    @Bean
    public ConnectionFactory connectionFactory(){

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(activeMQTcp);
        connectionFactory.setUserName(activeMQUsername);
        connectionFactory.setPassword(activeMQPassword);
        return connectionFactory;

    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory() {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory());
        //true为topic，false为queue
        factory.setPubSubDomain(false);
        factory.setConcurrency("3-10");
        factory.setRecoveryInterval(1000L);
        return factory;
    }
}