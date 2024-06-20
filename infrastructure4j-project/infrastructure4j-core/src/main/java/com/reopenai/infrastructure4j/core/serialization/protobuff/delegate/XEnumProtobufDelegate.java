package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate;

import com.google.protobuf.Int32Value;
import com.google.protobuf.Message;
import com.reopenai.infrastructure4j.beans.enums.XEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * XEnum的Protobuf转换器
 *
 * @author Allen Huang
 */
public class XEnumProtobufDelegate<T extends XEnum<Integer>> implements ProtobufDelegate<T, Int32Value> {

    private final Map<Integer, T> instances;

    public XEnumProtobufDelegate(Class<T> target) {
        T[] enumConstants = target.getEnumConstants();
        instances = new HashMap<>(enumConstants.length);
        for (T enumConstant : enumConstants) {
            instances.put(enumConstant.getValue(), enumConstant);
        }
    }

    @Override
    public void merge(T data, Message.Builder builder) {
        Integer value = data.getValue();
        ((Int32Value.Builder) builder).setValue(value);
    }

    @Override
    public T parse(Int32Value int32Value) {
        if (int32Value == Int32Value.getDefaultInstance()) {
            return null;
        }
        int value = int32Value.getValue();
        return instances.get(value);
    }

}
