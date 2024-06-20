package com.reopenai.infrastructure4j.core.serialization.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.reopenai.infrastructure4j.core.builtin.constants.JavaTimeConstants;

import java.io.IOException;
import java.time.LocalDate;

/**
 * LocalDate序列化器.将时间序列化成yyyy-MM-dd格式
 *
 * @author Allen Huang
 */
public class LocalDateSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(LocalDate value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeString(value.format(JavaTimeConstants.DATE_FORMAT));
    }

}
