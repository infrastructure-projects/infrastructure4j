package com.reopenai.infrastructure4j.core.serialization.jackson.deserializer;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.reopenai.infrastructure4j.core.builtin.constants.JavaTimeConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;

/**
 * LocalDateTime的反序列化实例
 *
 * @author Allen Huang
 */
@Slf4j
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String localDate = parser.getValueAsString();
        return StrUtil.isNotBlank(localDate) ? LocalDate.parse(localDate, JavaTimeConstants.DATE_FORMAT) : null;
    }

}
