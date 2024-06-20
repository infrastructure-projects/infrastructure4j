package com.reopenai.infrastructure4j.beans.builtin;

import lombok.Getter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 用于获取泛型的真实类型
 *
 * @author Allen Huang
 */
@Getter
public class TypeReference<T> implements Comparable<TypeReference<T>> {

    /**
     * 真实的类型
     */
    private final Type type;

    protected TypeReference() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        }
        this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    /**
     * 获取类型的名字
     *
     * @return 类型的名字
     */
    public String getTypeName() {
        return this.type.getTypeName();
    }

    @Override
    public String toString() {
        return this.type.toString();
    }

    @Override
    public int compareTo(TypeReference<T> o) {
        // 直接返回0，实现Comparable的原因并不是想要真的做compare
        // 仅仅只是确保构造方法中能够得到正确的类型信息
        return 0;
    }
}
