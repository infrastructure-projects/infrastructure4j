package com.reopenai.infrastructure4j.core.builtin.event;

import org.springframework.context.ApplicationEventPublisher;

/**
 * 事件工具类
 *
 * @author Allen Huang
 */
public class ApplicationEventUtil {

    private static ApplicationEventPublisher publisher;

    /**
     * 发布应用内事件
     *
     * @param event 事件内容
     */
    public static void publishEvent(Object event) {
        publisher.publishEvent(event);
    }

}
