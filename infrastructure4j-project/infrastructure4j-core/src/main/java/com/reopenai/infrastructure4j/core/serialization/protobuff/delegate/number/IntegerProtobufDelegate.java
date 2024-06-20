package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.number;

import com.google.protobuf.Int32Value;
import com.google.protobuf.Message;
import com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.ProtobufDelegate;

/**
 * Integer类型的转换器
 *
 * @author Allen Huang
 */
public class IntegerProtobufDelegate implements ProtobufDelegate<Integer, Int32Value> {

    @Override
    public void merge(Integer data, Message.Builder builder) {
        if (data != null) {
            ((Int32Value.Builder) builder).setValue(data);
        }
    }

    @Override
    public Integer parse(Int32Value value) {
        return Int32Value.getDefaultInstance() == value ? null : value.getValue();
    }

}
