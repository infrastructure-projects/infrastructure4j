package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.number;

import com.google.protobuf.FloatValue;
import com.google.protobuf.Message;
import com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.ProtobufDelegate;

import java.math.BigDecimal;

/**
 * Float类型的转换器
 *
 * @author Allen Huang
 */
public class BigDecimalFloatProtobufDelegate implements ProtobufDelegate<BigDecimal, FloatValue> {

    @Override
    public void merge(BigDecimal data, Message.Builder builder) {
        if (data != null) {
            ((FloatValue.Builder) builder).setValue(data.floatValue());
        }
    }

    @Override
    public BigDecimal parse(FloatValue value) {
        if (FloatValue.getDefaultInstance() == value) {
            return null;
        }
        return BigDecimal.valueOf(value.getValue());
    }

}
