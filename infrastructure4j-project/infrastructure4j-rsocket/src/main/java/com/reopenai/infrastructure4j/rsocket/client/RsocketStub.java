package com.reopenai.infrastructure4j.rsocket.client;

import org.springframework.core.annotation.AliasFor;
import org.springframework.messaging.rsocket.service.RSocketExchange;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RsocketStub
 *
 * @author Allen Huang
 */
@Component
@RSocketExchange
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RsocketStub {

    String host();

    @AliasFor(
            annotation = RSocketExchange.class,
            attribute = "value"
    )
    String prefix() default "v1";

    /**
     * 阻塞请求的超时时间.默认为30s
     */
    long readTimeout() default 30000;

}
