package com.reopenai.infrastructure4j.core.reflect;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MethodHandle的工具类
 *
 * @author Allen Huang
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MethodHandleUtil {

    /**
     * 通过方法实例找到方法的MethodHandle实例
     *
     * @param method 目标方法名称
     * @return MethodHandle的实例
     * @throws NoSuchMethodException  如果方法未找到，将会抛出此异常
     * @throws IllegalAccessException 如果方法不能被访问，将会抛出此异常
     */
    public static MethodHandle findVirtual(Method method) throws NoSuchMethodException, IllegalAccessException {
        Class<?> declaringClass = method.getDeclaringClass();
        MethodType methodType = getMethodType(method);
        return MethodHandles.lookup().findVirtual(declaringClass, method.getName(), methodType);
    }

    /**
     * 通过Method创建MethodType实例
     *
     * @param method MethodType实例
     * @return MethodType实例
     */
    public static MethodType getMethodType(Method method) {
        MethodType methodType;
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            methodType = MethodType.methodType(method.getReturnType());
        } else {
            List<Class<?>> paramTypes = Stream.of(parameters)
                    .map(Parameter::getType)
                    .collect(Collectors.toList());
            methodType = MethodType.methodType(method.getReturnType(), paramTypes);
        }
        return methodType;
    }

}