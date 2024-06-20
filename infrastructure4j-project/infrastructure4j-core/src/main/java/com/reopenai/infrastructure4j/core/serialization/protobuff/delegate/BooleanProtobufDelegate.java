package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Message;

/**
 * Boolean类型的转换器
 *
 * @author Allen Huang
 */
public class BooleanProtobufDelegate implements ProtobufDelegate<Boolean, BoolValue> {

    @Override
    public void merge(Boolean data, Message.Builder builder) {
        if (data != null) {
            ((BoolValue.Builder) builder).setValue(data);
        }
    }

    @Override
    public Boolean parse(BoolValue value) {
        return BoolValue.getDefaultInstance() == value ? null : value.getValue();
    }

}
