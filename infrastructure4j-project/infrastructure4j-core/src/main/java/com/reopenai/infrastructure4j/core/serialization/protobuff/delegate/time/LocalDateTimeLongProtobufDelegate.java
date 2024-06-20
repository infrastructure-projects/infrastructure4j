package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.time;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.google.protobuf.Int64Value;
import com.google.protobuf.Message;
import com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.ProtobufDelegate;

import java.time.LocalDateTime;

/**
 * Boolean类型的转换器
 *
 * @author Allen Huang
 */
public class LocalDateTimeLongProtobufDelegate implements ProtobufDelegate<LocalDateTime, Int64Value> {

    @Override
    public void merge(LocalDateTime data, Message.Builder builder) {
        if (data != null) {
            long timestamp = LocalDateTimeUtil.toEpochMilli(data);
            ((Int64Value.Builder) builder).setValue(timestamp);
        }
    }

    @Override
    public LocalDateTime parse(Int64Value stringValue) {
        if (Int64Value.getDefaultInstance() == stringValue) {
            return null;
        }
        long timestamp = stringValue.getValue();
        return LocalDateTimeUtil.of(timestamp);
    }


}
