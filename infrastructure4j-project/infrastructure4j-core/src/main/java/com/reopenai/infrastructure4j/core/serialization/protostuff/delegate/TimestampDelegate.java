package com.reopenai.infrastructure4j.core.serialization.protostuff.delegate;

import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.Pipe;
import io.protostuff.WireFormat;
import io.protostuff.runtime.Delegate;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Timestamp类型的转换器
 *
 * @author Allen Huang
 */
public class TimestampDelegate implements Delegate<Timestamp> {
    @Override
    public WireFormat.FieldType getFieldType() {
        return WireFormat.FieldType.GROUP;
    }

    @Override
    public Timestamp readFrom(Input input) throws IOException {
        return new Timestamp(input.readInt64());
    }

    @Override
    public void writeTo(Output output, int number, Timestamp value, boolean repeated) throws IOException {
        output.writeInt64(number, value.getTime(), repeated);
    }

    @Override
    public void transfer(Pipe pipe, Input input, Output output, int number, boolean repeated) throws IOException {
        output.writeInt64(number, input.readInt64(), repeated);
    }

    @Override
    public Class<?> typeClass() {
        return Timestamp.class;
    }
}
