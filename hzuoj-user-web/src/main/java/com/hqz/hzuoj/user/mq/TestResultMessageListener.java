package com.hqz.hzuoj.user.mq;

import com.alibaba.fastjson.JSON;
import com.hqz.hzuoj.bean.problem.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.sql.DataSource;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 自测代码事件监听器.
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/31 22:26
 * @Description: TODO
 */
@Component
@Slf4j
@EnableScheduling
public class TestResultMessageListener {

    /**
     * 每三小时清空一次队列发送回来的消息
     */
    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    private void clearSseEmitter() {
        long nowTime = System.currentTimeMillis();
        long diff = 1000 * 60 * 60 * 24;
        log.info("开始清空监听");
        long start = System.currentTimeMillis();
        for (Map.Entry<Long, LinkedList<MessageListener>> entry : queueMessage.entrySet()) {
            Long key = entry.getKey();
            LinkedList<MessageListener> value = entry.getValue();
            while (null != value && !value.isEmpty()) {
                MessageListener message = value.getFirst();
                long time = message.getDate().getTime();
                if (nowTime - time > diff) {
                    value.remove(message);
                }
            }
            //如果为空,移除监听
            if (null != value && value.isEmpty()) {
                queueMessage.remove(key);
                sseEmitters.remove(key);
            }
        }
        long end = System.currentTimeMillis();
        log.info("清空完成，总共耗时:{}", end - start);
    }

    /**
     * SseEmitter对象的列表.
     * Map中的Key表示提交记录的唯一标识符.
     * Map中的Value表示对应的SseEmitter对象, 用于推送实时评测信息.
     */
    private static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    /**
     * 存放发送回来消息
     */
    private static Map<Long, LinkedList<MessageListener>> queueMessage = new ConcurrentHashMap<>();


    /**
     * 注册sse的发送者对象.
     *
     * @param testId     - 提交记录的唯一标识符
     * @param sseEmitter - sse的发送者对象
     */
    public void addSseEmitters(Long testId, SseEmitter sseEmitter) {
        sseEmitters.put(testId, sseEmitter);
    }

    /**
     * 移除sse的发送者对象.
     *
     * @param testId - 提交记录的唯一标识符
     */
    public void removeSseEmitters(Long testId) {
        sseEmitters.remove(testId);
    }

    /**
     * 自测代码事件的处理器.
     *
     * @param messageEvent - 提交记录事件
     * @throws IOException
     */
    @EventListener
    public void deployEventHandler(TestResultMessageEvent messageEvent) throws IOException, InterruptedException {

        Long testId = messageEvent.getTestId();
        MessageListener messageListener = new MessageListener();
        //保证发送的消息顺序性
        synchronized (this) {
            messageListener.setDate(new Date());
        }
        messageListener.setMessage(messageEvent);
        LinkedList<MessageListener> messageListeners = queueMessage.get(testId);
        if (null == messageListeners) {
            messageListeners = new LinkedList<>();
        }
        messageListeners.add(messageListener);
        queueMessage.put(testId, messageListeners);
        SseEmitter sseEmitter = sseEmitters.get(testId);
        if (null != sseEmitter) {
            send(testId);
        } else {
            //等待500继续发送消息
            Thread.sleep(2000);
            send(testId);
        }

    }

    /**
     * 自测代码消息的分发
     *
     * @param testId
     * @throws IOException
     */
    private void send(Long testId) throws IOException, InterruptedException {
        SseEmitter sseEmitter = sseEmitters.get(testId);
        if (sseEmitter != null) {
            long index = 0;
            LinkedList<MessageListener> messageListeners = queueMessage.get(testId);
            while (null != messageListeners && !messageListeners.isEmpty()) {
                MessageListener messageListener = messageListeners.getFirst();
                TestResultMessageEvent event = (TestResultMessageEvent) messageListener.getMessage();
                boolean completed = event.isCompleted();
                try {
                    long time = messageListener.getDate().getTime();
                    //发送测评消息
                    if(index < time) {
                        index = time;
                        sseEmitter.send(JSON.toJSONString(event));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //判断是否最后的测评消息
                    if (completed) {
                        //如果是最后的测试消息，移除监听器中的监听
                        sseEmitter.complete();
                        removeSseEmitters(testId);
                    }
                    //已经发送，移除测评消息
                    messageListeners.remove(messageListener);
                }

            }
        }

    }

}
