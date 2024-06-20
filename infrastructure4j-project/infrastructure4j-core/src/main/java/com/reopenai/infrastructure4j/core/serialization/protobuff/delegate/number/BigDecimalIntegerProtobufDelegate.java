package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.number;

import com.google.protobuf.Int32Value;
import com.google.protobuf.Message;
import com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.ProtobufDelegate;

import java.math.BigDecimal;

/**
 * Integer类型的转换器
 *
 * @author Allen Huang
 */
public class BigDecimalIntegerProtobufDelegate implements ProtobufDelegate<BigDecimal, Int32Value> {

    @Override
    public void merge(BigDecimal data, Message.Builder builder) {
        if (data != null) {
            ((Int32Value.Builder) builder).setValue(data.intValue());
        }
    }

    @Override
    public BigDecimal parse(Int32Value value) {
        if (Int32Value.getDefaultInstance() == value) {
            return null;
        }
        return BigDecimal.valueOf(value.getValue());
    }

}
