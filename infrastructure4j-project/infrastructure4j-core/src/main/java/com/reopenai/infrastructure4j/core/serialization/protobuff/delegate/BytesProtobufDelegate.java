package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate;

import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.Message;

/**
 * ByteArray类型的转换器
 *
 * @author Allen Huang
 */
public class BytesProtobufDelegate implements ProtobufDelegate<byte[], BytesValue> {

    @Override
    public void merge(byte[] data, Message.Builder builder) {
        if (data != null) {
            ((BytesValue.Builder) builder).setValue(ByteString.copyFrom(data));
        }
    }

    @Override
    public byte[] parse(BytesValue value) {
        return BytesValue.getDefaultInstance() == value ? null : value.getValue().toByteArray();
    }

}
