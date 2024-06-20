package com.reopenai.infrastructure4j.etcd.types;

import io.etcd.jetcd.ByteSequence;

import java.nio.charset.StandardCharsets;

/**
 * ByteSequence工具类
 *
 * @author Allen Huang
 */
public class ByteSequences {

    /**
     * 将字符串转换成ByteSequence实例
     *
     * @param value 字符串key
     * @return 字符串value
     */
    public static ByteSequence from(String value) {
        return ByteSequence.from(value, StandardCharsets.UTF_8);
    }

}
