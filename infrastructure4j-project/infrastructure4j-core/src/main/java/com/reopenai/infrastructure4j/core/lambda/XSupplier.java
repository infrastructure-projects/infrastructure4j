package com.reopenai.infrastructure4j.core.lambda;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * 具有序列化能力的XSupplier
 *
 * @author Allen Huang
 */
@FunctionalInterface
public interface XSupplier<T> extends Supplier<T>, Serializable {
}
