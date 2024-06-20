package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.number;

import com.google.protobuf.DoubleValue;
import com.google.protobuf.Message;
import com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.ProtobufDelegate;

/**
 * Double类型的转换器
 *
 * @author Allen Huang
 */
public class DoubleProtobufDelegate implements ProtobufDelegate<Double, DoubleValue> {

    @Override
    public void merge(Double data, Message.Builder builder) {
        if (data != null) {
            ((DoubleValue.Builder) builder).setValue(data);
        }
    }

    @Override
    public Double parse(DoubleValue value) {
        return DoubleValue.getDefaultInstance() == value ? null : value.getValue();
    }

}
