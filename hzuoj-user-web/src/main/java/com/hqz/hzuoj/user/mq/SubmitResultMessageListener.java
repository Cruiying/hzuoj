package com.hqz.hzuoj.user.mq;

import com.alibaba.fastjson.JSON;
import com.hqz.hzuoj.user.test.PayCompletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提交代码事件监听器.
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/31 16:27
 * @Description: TODO
 */
@Component
public class SubmitResultMessageListener {
    /**
     * SseEmitter对象的列表.
     * Map中的Key表示提交记录的唯一标识符.
     * Map中的Value表示对应的SseEmitter对象, 用于推送实时评测信息.
     */
    private static Map<Integer, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

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
        Integer submitId = messageEvent.getSubmitId();
        SseEmitter sseEmitter = sseEmitters.get(submitId);
        //判断当前监听器中是否有数据
        if (sseEmitter == null) {
            return;
        }
        send(messageEvent);
    }

    /**
     * 自测代码消息的分发
     *
     * @param messageEvent
     * @throws IOException
     */
    private void send(SubmitResultMessageEvent messageEvent) {
        Integer submitId = messageEvent.getSubmitId();
        boolean completed = messageEvent.isCompleted();
        SseEmitter sseEmitter = sseEmitters.get(submitId);
        if (sseEmitter != null) {
            try {
                sseEmitter.send(JSON.toJSONString(messageEvent));
            } catch (Exception e) {

            }
            if (completed) {
                //如果是最后的测试，移除监听器中的监听
                sseEmitter.complete();
                removeSseEmitters(submitId);
            }
        }
    }
}
