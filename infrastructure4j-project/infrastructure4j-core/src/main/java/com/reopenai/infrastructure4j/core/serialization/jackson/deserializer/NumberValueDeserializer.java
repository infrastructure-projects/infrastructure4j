package com.reopenai.infrastructure4j.core.serialization.jackson.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.reopenai.infrastructure4j.beans.builtin.NumberValue;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * LocalDateTime的反序列化实例
 *
 * @author Allen Huang
 */
@Slf4j
public class NumberValueDeserializer extends JsonDeserializer<NumberValue> {

    @Override
    public NumberValue deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        Number value = parser.getNumberValue();
        return value != null ? new NumberValue(value) : null;
    }

}
