package com.reopenai.infrastructure4j.core.serialization.protobuff.delegate;

import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;

/**
 * Java类型和Protobuf类型的类型转换器
 *
 * @author Allen Huang
 */
public interface ProtobufDelegate<SOURCE, TARGET extends MessageOrBuilder> {

    /**
     * 将Java类型转换成Protobuf的数据类型
     *
     * @param data    Java类型
     * @param builder 构建此参数的builder对象
     */
    void merge(SOURCE data, Message.Builder builder);

    /**
     * 将Protobuf类型转换成Java的数据类型
     *
     * @param target Protobuf类型
     * @return 如果转换成功，则返回Java的数据类型，否则返回null.
     */
    SOURCE parse(TARGET target);

}
