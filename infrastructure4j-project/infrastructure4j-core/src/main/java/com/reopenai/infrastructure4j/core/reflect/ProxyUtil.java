package com.reopenai.infrastructure4j.core.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 动态代理工具类
 *
 * @author Allen Huang
 */
public final class ProxyUtil {

    private static final int ALLOWED_MODES = MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
            | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC;
    private static final Constructor<MethodHandles.Lookup> lookupConstructor;
    private static final Method privateLookupInMethod;

    static {
        Method privateLookupIn;
        try {
            privateLookupIn = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
        } catch (NoSuchMethodException e) {
            privateLookupIn = null;
        }
        privateLookupInMethod = privateLookupIn;

        Constructor<MethodHandles.Lookup> lookup = null;
        if (privateLookupInMethod == null) {
            // JDK 1.8
            try {
                lookup = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                lookup.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        "There is neither 'privateLookupIn(Class, Lookup)' nor 'Lookup(Class, int)' method in java.lang.invoke.MethodHandles.",
                        e);
            } catch (Exception e) {
                lookup = null;
            }
        }
        lookupConstructor = lookup;
    }

    /**
     * 执行代理的默认方法
     *
     * @param proxy  对象实例
     * @param method 待执行的方法
     * @param args   执行参数
     * @return 执行结果
     * @throws Throwable 执行异常
     */
    public static Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            MethodHandle handle = privateLookupInMethod == null ? getMethodHandleJava8(method) : getMethodHandleJava9(method);
            return handle.bindTo(proxy).invokeWithArguments(args);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException
                 | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Java9及以上版本获取MethodHandler的方式
     *
     * @param method 待执行的方法
     * @return MethodHandler实例
     * @throws NoSuchMethodException     异常信息
     * @throws IllegalAccessException    方法无法被访问时的异常信息
     * @throws InvocationTargetException 无法执行目标方法的异常信息
     */
    private static MethodHandle getMethodHandleJava9(Method method) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> declaringClass = method.getDeclaringClass();
        return ((MethodHandles.Lookup) privateLookupInMethod
                .invoke(null, declaringClass, MethodHandles.lookup()))
                .findSpecial(declaringClass, method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()), declaringClass);
    }

    /**
     * Java8版本获取MethodHandler的方式
     *
     * @param method 待执行的方法
     * @return MethodHandler实例
     * @throws IllegalAccessException    方法无法被访问时的异常信息
     * @throws InvocationTargetException 无法执行目标方法的异常信息
     */
    private static MethodHandle getMethodHandleJava8(Method method) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Class<?> declaringClass = method.getDeclaringClass();
        return lookupConstructor.newInstance(declaringClass, ALLOWED_MODES)
                .unreflectSpecial(method, declaringClass);
    }


}
