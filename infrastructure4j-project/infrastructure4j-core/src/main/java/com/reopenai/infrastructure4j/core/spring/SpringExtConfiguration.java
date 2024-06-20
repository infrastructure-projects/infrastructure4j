package com.reopenai.infrastructure4j.core.spring;

import com.reopenai.infrastructure4j.core.builtin.event.ApplicationEventUtil;
import com.reopenai.infrastructure4j.core.spring.converter.StandardTimeZoneConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import java.lang.reflect.Field;

/**
 * Spring的扩展配置
 *
 * @author Allen Huang
 */
@Configuration
public class SpringExtConfiguration implements ApplicationEventPublisherAware {

    @Bean
    public StandardTimeZoneConverter customStandardTimeZoneConverter() {
        return new StandardTimeZoneConverter();
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher eventPublisher) {
        try {
            Field field = ApplicationEventUtil.class.getDeclaredField("publisher");
            boolean canAccess = field.canAccess(ApplicationEventUtil.class);
            field.setAccessible(true);
            field.set(ApplicationEventUtil.class, eventPublisher);
            field.setAccessible(canAccess);
        } catch (Exception e) {
            throw new BeanCreationException("无法初始化ApplicationEventUtil", e);
        }
    }

}
