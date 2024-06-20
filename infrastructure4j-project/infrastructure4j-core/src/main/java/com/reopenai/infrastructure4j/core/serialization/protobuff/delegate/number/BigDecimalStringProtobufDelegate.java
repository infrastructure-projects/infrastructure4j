package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.number;

import cn.hutool.core.util.StrUtil;
import com.google.protobuf.Message;
import com.google.protobuf.StringValue;
import com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.ProtobufDelegate;

import java.math.BigDecimal;

/**
 * 字符串类型的转换器
 *
 * @author Allen Huang
 */
public class BigDecimalStringProtobufDelegate implements ProtobufDelegate<BigDecimal, StringValue> {

    @Override
    public void merge(BigDecimal data, Message.Builder builder) {
        ((StringValue.Builder) builder).setValue(data.stripTrailingZeros().toPlainString());
    }

    @Override
    public BigDecimal parse(StringValue value) {
        if (StringValue.getDefaultInstance() == value) {
            return null;
        }
        String strValue = value.getValue();
        if (StrUtil.isNotBlank(strValue)) {
            return new BigDecimal(strValue);
        }
        return null;
    }

}
