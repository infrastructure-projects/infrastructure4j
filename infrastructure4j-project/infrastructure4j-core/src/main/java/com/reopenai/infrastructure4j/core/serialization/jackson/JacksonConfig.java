package com.reopenai.infrastructure4j.core.serialization.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reopenai.infrastructure4j.beans.builtin.NumberValue;
import com.reopenai.infrastructure4j.beans.builtin.StandardTimeZone;
import com.reopenai.infrastructure4j.core.serialization.jackson.deserializer.*;
import com.reopenai.infrastructure4j.core.serialization.jackson.serializer.*;
import org.springframework.beans.factory.Aware;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Jackson自动配置类
 *
 * @author Allen Huang
 */
@Configuration
@ConditionalOnClass(ObjectMapper.class)
public class JacksonConfig implements Aware {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder
                // 序列化配置
                .serializerByType(Number.class, new NumberSerializer())
                .serializerByType(LocalTime.class, new LocalTimeSerializer())
                .serializerByType(LocalDate.class, new LocalDateSerializer())
                .serializerByType(BigDecimal.class, new BigDecimalSerializer())
                .serializerByType(NumberValue.class, new NumberValueSerializer())
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer())
                .serializerByType(StandardTimeZone.class, new StandardTimeZoneSerializer())
                // 反序列化配置
                .deserializerByType(LocalTime.class, new LocalTimeDeserializer())
                .deserializerByType(LocalDate.class, new LocalDateDeserializer())
                .deserializerByType(NumberValue.class, new NumberValueDeserializer())
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer())
                .deserializerByType(StandardTimeZone.class, new StandardTimeZoneDeserializer());
    }

}
