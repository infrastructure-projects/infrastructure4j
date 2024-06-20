package com.reopenai.infrastructure4j.core.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
 * AOP基类
 *
 * @author Allen Huang
 */
public abstract class BaseAopSupport implements Ordered {

    /**
     * 获取且点执行的方法
     *
     * @param joinPoint 切点
     * @return 返回切点的方法
     */
    protected Method resolveMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> targetClass = joinPoint.getTarget().getClass();

        Method method = getDeclaredMethodFor(targetClass, signature.getName(),
                signature.getMethod().getParameterTypes());
        if (method == null) {
            throw new IllegalStateException("Cannot resolve target method: " + signature.getMethod().getName());
        }
        return method;
    }

    /**
     * 根据方法名以及参数信息，获取类中的某个方法
     *
     * @param clazz          目标类
     * @param name           方法名称
     * @param parameterTypes 方法参数
     * @return 如果有匹配到的方法，则返回方法实例，否则返回空
     */
    private Method getDeclaredMethodFor(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getDeclaredMethodFor(superClass, name, parameterTypes);
            }
        }
        return null;
    }

}
