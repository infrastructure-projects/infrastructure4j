package com.reopenai.infrastructure4j.core.builtin.traits;

/**
 * 常用于Bean的转换，将一个实例转换成另一个实例
 *
 * @author Allen Huang
 */
public interface Into<T> {

    /**
     * 将某个类转换成另一个实例
     *
     * @return 转换后的实例
     */
    T into();

}
