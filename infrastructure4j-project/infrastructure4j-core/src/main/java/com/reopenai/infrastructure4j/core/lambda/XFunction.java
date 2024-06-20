package com.reopenai.infrastructure4j.core.lambda;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 扩展了{@link Function}的能力，提供了序列化的支持
 *
 * @author Allen Huang
 * @see Function
 */
@FunctionalInterface
public interface XFunction<T, R> extends Function<T, R>, Serializable {

}