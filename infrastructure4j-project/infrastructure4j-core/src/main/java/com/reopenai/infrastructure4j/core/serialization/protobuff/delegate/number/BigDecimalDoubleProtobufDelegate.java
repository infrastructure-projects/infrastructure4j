package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.number;

import com.google.protobuf.DoubleValue;
import com.google.protobuf.Message;
import com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.ProtobufDelegate;

import java.math.BigDecimal;

/**
 * Double类型的转换器
 *
 * @author Allen Huang
 */
public class BigDecimalDoubleProtobufDelegate implements ProtobufDelegate<BigDecimal, DoubleValue> {

    @Override
    public void merge(BigDecimal data, Message.Builder builder) {
        if (data != null) {
            ((DoubleValue.Builder) builder).setValue(data.doubleValue());
        }
    }

    @Override
    public BigDecimal parse(DoubleValue value) {
        if (DoubleValue.getDefaultInstance() == value) {
            return null;
        }
        return BigDecimal.valueOf(value.getValue());
    }

}
