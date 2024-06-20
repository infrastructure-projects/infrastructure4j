package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.number;

import com.google.protobuf.Int64Value;
import com.google.protobuf.Message;
import com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.ProtobufDelegate;

/**
 * Long类型的转换器
 *
 * @author Allen Huang
 */
public class LongProtobufDelegate implements ProtobufDelegate<Long, Int64Value> {

    @Override
    public void merge(Long data, Message.Builder builder) {
        if (data != null) {
            ((Int64Value.Builder) builder).setValue(data);
        }
    }

    @Override
    public Long parse(Int64Value value) {
        return Int64Value.getDefaultInstance() == value ? null : value.getValue();
    }

}
