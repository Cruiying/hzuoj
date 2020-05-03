package com.hqz.hzuoj.user.mq;

import com.alibaba.fastjson.JSON;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自测代码事件监听器.
 *
 * @Author: HQZ
 * @CreateTime: 2019/12/31 22:26
 * @Description: TODO
 */
@Component
public class TestResultMessageListener {

    /**
     * SseEmitter对象的列表.
     * Map中的Key表示提交记录的唯一标识符.
     * Map中的Value表示对应的SseEmitter对象, 用于推送实时评测信息.
     */
    private static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

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
        SseEmitter sseEmitter = sseEmitters.get(testId);
        //判断当前监听器中是否有数据
        if (sseEmitter == null) {
            //如果没有数据,先暂停100ms,再查看是否有消息，
            Thread.sleep(100);
            sseEmitter = sseEmitters.get(testId);
            //如果100ms后还是没有消息，直接放弃
            if (sseEmitter == null) return;
        }
        send(messageEvent);
    }

    /**
     * 自测代码消息的分发
     *
     * @param messageEvent
     * @throws IOException
     */
    private void send(TestResultMessageEvent messageEvent) throws IOException {
            Long testId = messageEvent.getTestId();
            boolean completed = messageEvent.isCompleted();
            SseEmitter sseEmitter = sseEmitters.get(testId);
            if (sseEmitter != null) {
                sseEmitter.send(JSON.toJSONString(messageEvent));
                if (completed) {
                    //如果是最后的测试，移除监听器中的监听
                    sseEmitter.complete();
                    removeSseEmitters(testId);
                }
            }
    }
}
