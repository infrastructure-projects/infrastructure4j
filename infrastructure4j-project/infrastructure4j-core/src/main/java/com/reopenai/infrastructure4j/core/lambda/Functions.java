package com.reopenai.infrastructure4j.core.lambda;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Functions
 *
 * @author Allen Huang
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Functions {

    /**
     * 去重函数
     *
     * @return 函数实例
     */
    public static <T> BinaryOperator<T> Deduplication() {
        return (v1, v2) -> v1;
    }

    /**
     * 安全的转换成Map,避免出现重复key的问题
     *
     * @param keyMapper   keyMapper
     * @param valueMapper valueMapper
     * @return 转换方法实例
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> safeToMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(keyMapper, valueMapper, Deduplication());
    }

}
