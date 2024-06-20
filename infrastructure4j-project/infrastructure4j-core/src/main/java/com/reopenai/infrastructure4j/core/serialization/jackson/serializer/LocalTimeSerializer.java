package com.reopenai.infrastructure4j.core.serialization.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.reopenai.infrastructure4j.core.builtin.constants.JavaTimeConstants;

import java.io.IOException;
import java.time.LocalTime;

/**
 * 将时间序列化成HH:mm:ss格式
 *
 * @author Allen Huang
 */
public class LocalTimeSerializer extends JsonSerializer<LocalTime> {

    @Override
    public void serialize(LocalTime localTime, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(localTime.format(JavaTimeConstants.TIME_FORMAT));
    }

}
