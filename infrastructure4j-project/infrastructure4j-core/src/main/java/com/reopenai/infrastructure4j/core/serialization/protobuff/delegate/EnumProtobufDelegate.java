package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate;

import cn.hutool.core.util.EnumUtil;
import com.google.protobuf.Message;
import com.google.protobuf.StringValue;
import lombok.RequiredArgsConstructor;

/**
 * XEnum的Protobuf转换器
 *
 * @author Allen Huang
 */
@RequiredArgsConstructor
public class EnumProtobufDelegate<T extends Enum<T>> implements ProtobufDelegate<T, StringValue> {

    private final Class<T> target;


    @Override
    public void merge(T data, Message.Builder builder) {
        if (data != null) {
            ((StringValue.Builder) builder).setValue(data.name());
        }
    }

    @Override
    public T parse(StringValue value) {
        if (StringValue.getDefaultInstance() != value) {
            String enumName = value.getValue();
            return EnumUtil.fromStringQuietly(target, enumName);
        }
        return null;
    }
}
