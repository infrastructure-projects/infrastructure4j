package com.reopenai.infrastructure4j.core.serialization.jackson.deserializer;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.reopenai.infrastructure4j.beans.builtin.StandardTimeZone;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LocalDateTime的反序列化实例
 *
 * @author Allen Huang
 */
@Slf4j
public class StandardTimeZoneDeserializer extends JsonDeserializer<StandardTimeZone> {

    private static final Map<String, StandardTimeZone> TIMEZONE_MAP = Stream.of(StandardTimeZone.values())
            .collect(Collectors.toMap(StandardTimeZone::getCode, Function.identity()));

    @Override
    public StandardTimeZone deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String languageStr = parser.getValueAsString();
        return StrUtil.isNotBlank(languageStr) ? TIMEZONE_MAP.get(languageStr) : null;
    }

}
