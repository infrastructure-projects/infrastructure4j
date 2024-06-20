package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.time;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.google.protobuf.Message;
import com.google.protobuf.StringValue;
import com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.ProtobufDelegate;

import java.time.LocalDateTime;

/**
 * Boolean类型的转换器
 *
 * @author Allen Huang
 */
public class LocalDateTimeStringProtobufDelegate implements ProtobufDelegate<LocalDateTime, StringValue> {

    @Override
    public void merge(LocalDateTime data, Message.Builder builder) {
        if (data != null) {
            long timestamp = LocalDateTimeUtil.toEpochMilli(data);
            ((StringValue.Builder) builder).setValue(String.valueOf(timestamp));
        }
    }

    @Override
    public LocalDateTime parse(StringValue stringValue) {
        if (StringValue.getDefaultInstance() == stringValue) {
            return null;
        }
        String timeStr = stringValue.getValue();
        return LocalDateTimeUtil.of(Long.parseLong(timeStr));
    }


}
