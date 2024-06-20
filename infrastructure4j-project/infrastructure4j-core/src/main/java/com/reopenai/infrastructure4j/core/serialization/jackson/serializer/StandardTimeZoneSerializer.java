
package com.reopenai.infrastructure4j.core.serialization.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.reopenai.infrastructure4j.beans.builtin.StandardTimeZone;

import java.io.IOException;

/**
 * LocalDate序列化器
 *
 * @author Allen Huang
 */
public class StandardTimeZoneSerializer extends JsonSerializer<StandardTimeZone> {

    @Override
    public void serialize(StandardTimeZone value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeString(value.getCode());
    }

}
