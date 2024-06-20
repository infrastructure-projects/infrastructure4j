package com.reopenai.infrastructure4j.core;

import com.reopenai.infrastructure4j.core.aop.AnnotationMethodPoint;
import com.reopenai.infrastructure4j.core.idempotent.IdempotentInterceptor;
import com.reopenai.infrastructure4j.core.idempotent.IdempotentMethod;
import com.reopenai.infrastructure4j.core.idempotent.IdempotentProvider;
import com.reopenai.infrastructure4j.core.serialization.jackson.JacksonConfig;
import com.reopenai.infrastructure4j.core.spring.SpringExtConfiguration;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;

/**
 * 核心包自动配置
 *
 * @author Allen Huang
 */
@Configuration
@Import({JacksonConfig.class, SpringExtConfiguration.class})
public class Infrastructure4jAutoConfiguration {

    @Bean
    @ConditionalOnBean(IdempotentProvider.class)
    public IdempotentInterceptor idempotentAdvice(Map<String, IdempotentProvider> providerMap) {
        return new IdempotentInterceptor(providerMap);
    }

    @Bean
    public PointcutAdvisor idempotentPointcutAdvisor(IdempotentInterceptor interceptor) {
        Pointcut pointcut = new AnnotationMethodPoint(IdempotentMethod.class);
        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }

}
