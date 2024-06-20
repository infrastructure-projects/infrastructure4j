package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.number;

import com.google.protobuf.FloatValue;
import com.google.protobuf.Message;
import com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.ProtobufDelegate;

/**
 * Float类型的转换器
 *
 * @author Allen Huang
 */
public class FloatProtobufDelegate implements ProtobufDelegate<Float, FloatValue> {

    @Override
    public void merge(Float data, Message.Builder builder) {
        if (data != null) {
            ((FloatValue.Builder) builder).setValue(data);
        }
    }

    @Override
    public Float parse(FloatValue value) {
        return FloatValue.getDefaultInstance() == value ? null : value.getValue();
    }

}
