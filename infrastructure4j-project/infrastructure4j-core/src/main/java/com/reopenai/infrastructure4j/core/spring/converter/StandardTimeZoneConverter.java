package com.reopenai.infrastructure4j.core.spring.converter;

import com.reopenai.infrastructure4j.beans.builtin.StandardTimeZone;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link StandardTimeZone} 转换器
 *
 * @author Allen Huang
 */
public class StandardTimeZoneConverter implements Converter<String, StandardTimeZone> {

    private static final Map<String, StandardTimeZone> TIMEZONE_MAP = Stream.of(StandardTimeZone.values())
            .collect(Collectors.toMap(StandardTimeZone::getCode, Function.identity()));

    @Override
    public StandardTimeZone convert(String source) {
        return TIMEZONE_MAP.get(source);
    }

}
