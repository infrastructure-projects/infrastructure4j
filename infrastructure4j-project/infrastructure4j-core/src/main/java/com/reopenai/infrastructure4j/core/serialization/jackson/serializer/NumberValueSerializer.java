package com.reopenai.infrastructure4j.core.serialization.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.reopenai.infrastructure4j.beans.builtin.NumberValue;

import java.io.IOException;

/**
 * 序列化NumberValue类型，将NumberValue序列化成数字
 *
 * @author Allen Huang
 */
public class NumberValueSerializer extends JsonSerializer<NumberValue> {

    @Override
    public void serialize(NumberValue numberValue, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeNumber(numberValue.value().toString());
    }

}
