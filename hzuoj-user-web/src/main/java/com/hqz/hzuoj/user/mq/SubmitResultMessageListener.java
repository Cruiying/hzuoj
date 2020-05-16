package com.hqz.hzuoj.user.mq;

import com.alibaba.fastjson.JSON;
import com.hqz.hzuoj.bean.problem.Data;
import com.hqz.hzuoj.user.test.PayCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提交代码事件监听器.
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/31 16:27
 * @Description: TODO
 */
@Slf4j
@Component
@EnableScheduling
public class SubmitResultMessageListener {

    /**
     * 每三小时清空一次队列发送回来的消息
     */
    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    private void clearSseEmitter() {
        long nowTime = System.currentTimeMillis();
        long diff = 1000 * 60 * 60 * 24;
        log.info("开始清空监听");
        long start = System.currentTimeMillis();
        for (Map.Entry<Integer, LinkedList<MessageListener>> entry : queueMessage.entrySet()) {
            Integer key = entry.getKey();
            LinkedList<MessageListener> value = entry.getValue();
            while(null != value && !value.isEmpty()) {
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
    private static Map<Integer, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    /**
     * 存放发送回来消息
     */
    private static Map<Integer, LinkedList<MessageListener>> queueMessage = new ConcurrentHashMap<>();

    /**
     * 注册sse的发送者对象.
     *
     * @param submitId   - 提交记录的唯一标识符
     * @param sseEmitter - sse的发送者对象
     */
    public void addSseEmitters(Integer submitId, SseEmitter sseEmitter) {
        sseEmitters.put(submitId, sseEmitter);
    }

    /**
     * 移除sse的发送者对象.
     *
     * @param submitId - 提交记录的唯一标识符
     */
    public void removeSseEmitters(Integer submitId) {
        sseEmitters.remove(submitId);
    }

    /**
     * 提交代码事件的处理器.
     *
     * @param messageEvent - 提交记录事件
     * @throws IOException
     */
    @EventListener
    public void deployEventHandler(SubmitResultMessageEvent messageEvent) throws IOException, InterruptedException {
        synchronized (this) {
            Integer submitId = messageEvent.getSubmitId();
            //保证发送的消息顺序性
            MessageListener messageListener = new MessageListener();
            messageListener.setDate(new Date());
            messageListener.setMessage(messageEvent);
            LinkedList<MessageListener> messageListeners = queueMessage.get(submitId);
            if (null == messageListeners) {
                messageListeners = new LinkedList<>();
            }
            messageListeners.add(messageListener);
            queueMessage.put(submitId, messageListeners);
            SseEmitter sseEmitter = sseEmitters.get(submitId);
            if (null != sseEmitter) {
                send(submitId);
            } else {
                Thread.sleep(500);
                send(submitId);
            }
        }
    }

    /**
     * 自测代码消息的分发
     *
     * @param submitId
     * @throws IOException
     */
    private void send(Integer submitId) throws InterruptedException {
        SseEmitter sseEmitter = sseEmitters.get(submitId);
        if (sseEmitter != null) {
            LinkedList<MessageListener> messageListeners = queueMessage.get(submitId);
            while (null != messageListeners && !messageListeners.isEmpty()) {
                MessageListener messageListener = messageListeners.getFirst();
                SubmitResultMessageEvent event = (SubmitResultMessageEvent) messageListener.getMessage();
                boolean completed = event.isCompleted();
                try {
                    //发送测评消息
                    sseEmitter.send(JSON.toJSONString(event));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //判断是否最后的测评消息
                if (completed) {
                    //如果是最后的测试消息，移除监听器中的监听
                    sseEmitter.complete();
                    removeSseEmitters(submitId);
                }
                //已经发送，移除测评消息
                messageListeners.remove(messageListener);
            }
        }
    }

}
