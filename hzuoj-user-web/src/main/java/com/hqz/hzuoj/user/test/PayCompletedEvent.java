package com.hqz.hzuoj.user.test;

import org.springframework.context.ApplicationEvent;

/**
 * @Author: HQZ
 * @CreateTime: 2019/12/31 11:39
 * @Description: TODO
 */
public class PayCompletedEvent extends ApplicationEvent {

    private Long payRecordId;

    public PayCompletedEvent(Object source, Long payRecordId) {
        super(source);
        this.payRecordId = payRecordId;
    }

    public Long getPayRecordId() {
        return payRecordId;
    }

    public void setPayRecordId(Long payRecordId) {
        this.payRecordId = payRecordId;
    }
}