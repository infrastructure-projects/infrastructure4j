package com.reopenai.infrastructure4j.core.serialization.protostuff.delegate;

import cn.hutool.core.util.StrUtil;
import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.Pipe;
import io.protostuff.WireFormat;
import io.protostuff.runtime.Delegate;
import io.protostuff.runtime.RuntimeReflectionFieldFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * BigDecimal类型的包装器
 *
 * @author Allen Huang
 */
@Slf4j
public class BigDecimalDelegate implements Delegate<BigDecimal> {

    @Override
    public WireFormat.FieldType getFieldType() {
        return RuntimeReflectionFieldFactory.BIGDECIMAL.getFieldType();
    }

    @Override
    public BigDecimal readFrom(Input input) throws IOException {
        String number = StrUtil.trim(input.readString());
        try {
            return new BigDecimal(number);
        } catch (NumberFormatException e) {
            log.error("无法解析BigDecimal数据.value={}", number, e);
            return RuntimeReflectionFieldFactory.BIGDECIMAL.readFrom(input);
        }
    }

    @Override
    public void writeTo(Output output, int number, BigDecimal value, boolean repeated) throws IOException {
        if (value.scale() > 18) {
            value = value.setScale(18, RoundingMode.DOWN);
        }
        output.writeString(number, value.stripTrailingZeros().toPlainString(), repeated);
    }

    @Override
    public void transfer(Pipe pipe, Input input, Output output, int number, boolean repeated) throws IOException {
        RuntimeReflectionFieldFactory.BIGDECIMAL.transfer(pipe, input, output, number, repeated);
    }

    @Override
    public Class<?> typeClass() {
        return BigDecimal.class;
    }

}
