package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.number;

import com.google.protobuf.Int64Value;
import com.google.protobuf.Message;
import com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.ProtobufDelegate;

import java.math.BigDecimal;

/**
 * Long类型的转换器
 *
 * @author Allen Huang
 */
public class BigDecimalLongProtobufDelegate implements ProtobufDelegate<BigDecimal, Int64Value> {

    @Override
    public void merge(BigDecimal data, Message.Builder builder) {
        if (data != null) {
            ((Int64Value.Builder) builder).setValue(data.longValue());
        }
    }

    @Override
    public BigDecimal parse(Int64Value value) {
        if (Int64Value.getDefaultInstance() == value) {
            return null;
        }
        return BigDecimal.valueOf(value.getValue());
    }

}
