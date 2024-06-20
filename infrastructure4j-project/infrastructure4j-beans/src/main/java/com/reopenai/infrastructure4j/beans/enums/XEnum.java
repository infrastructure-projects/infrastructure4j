package com.reopenai.infrastructure4j.beans.enums;

/**
 * 通用的枚举接口，不以枚举名称作为枚举值的枚举可实现此接口
 *
 * @author Allen Huang
 */
public interface XEnum<T> {

    /**
     * 获取枚举的枚举值
     *
     * @return 枚举对应的枚举值
     */
    T getValue();

}
