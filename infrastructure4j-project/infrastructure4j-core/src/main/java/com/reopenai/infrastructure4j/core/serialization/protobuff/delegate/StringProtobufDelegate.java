package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate;

import cn.hutool.core.util.StrUtil;
import com.google.protobuf.Message;
import com.google.protobuf.StringValue;

/**
 * 字符串类型的转换器
 *
 * @author Allen Huang
 */
public class StringProtobufDelegate implements ProtobufDelegate<String, StringValue> {

    @Override
    public void merge(String data, Message.Builder builder) {
        if (StrUtil.isNotBlank(data)) {
            ((StringValue.Builder) builder).setValue(data);
        }
    }

    @Override
    public String parse(StringValue value) {
        return StringValue.getDefaultInstance() == value ? null : value.getValue();
    }

}
