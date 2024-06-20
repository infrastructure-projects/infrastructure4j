package com.reopenai.infrastructure4j.core.serialization.jackson.deserializer;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.reopenai.infrastructure4j.core.builtin.constants.JavaTimeConstants;

import java.io.IOException;
import java.time.LocalTime;

/**
 * 反序列化LocalTime对象.从HH:mm:ss格式反序列化成LocalTime
 *
 * @author Allen Huang
 */
public class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {

    @Override
    public LocalTime deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JacksonException {
        String localDate = jsonParser.getValueAsString();
        return StrUtil.isNotBlank(localDate) ? LocalTime.parse(localDate, JavaTimeConstants.TIME_FORMAT) : null;
    }

}
