package com.reopenai.infrastructure4j.core.lambda;

import cn.hutool.core.util.StrUtil;
import com.reopenai.infrastructure4j.core.reflect.XReflectUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lambda工具类，能够通过Lambda表达式完成一些任务。
 *
 * @author Allen Huang
 */
@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class LambdaUtil {

    private static final Map<Class<?>, SerializedLambda> SERIALIZED_LAMBDA_CACHE = new ConcurrentHashMap<>();

    /**
     * 根据Lambda表达式获取属性名称，如果属性的名称是驼峰命名法，则会将此名称转换成下划线形式。
     * 该方法为属性的getter或setter方法，且遵守Java Bean规范。示例如下:
     * <pre>{@code
     *   XLambdaUtil.propertyUnderlineCase(Demo::getUsername)
     * }</pre>
     *
     * @param serializable 支持序列化的Lambda表达式
     * @return 属性的名称
     */
    public static <T> String propertyUnderlineCase(XFunction<T, ?> serializable) {
        return StrUtil.toUnderlineCase(property(serializable));
    }

    /**
     * 根据Lambda表达式获取属性名称，该方法为属性的getter或setter方法，且遵守Java Bean规范。示例如下:
     * <pre>{@code
     *   XLambdaUtil.property(Demo::getUsername)
     * }</pre>
     *
     * @param serializable 支持序列化的Lambda表达式
     * @return 属性的名称
     */
    public static <T> String property(XFunction<T, ?> serializable) {
        return parseMethodName(serializable);
    }

    /**
     * 根据Lambda表达式获取属性名称，该方法为属性的getter或setter方法，且遵守Java Bean规范。示例如下:
     * <pre>{@code
     *   XLambdaUtil.property(Demo::getUsername)
     * }</pre>
     *
     * @param serializable 支持序列化的Lambda表达式
     * @return 属性的名称
     */
    public static <T> String property(XSupplier<T> serializable) {
        return parseMethodName(serializable);
    }

    /**
     * 获取此Lambda表达式的方法名称
     *
     * @param serializable 支持序列化的Lambda表达式
     * @return 方法名称
     */
    public static <T> String getMethodName(XFunction<T, ?> serializable) {
        SerializedLambda lambda = resolve(serializable);
        return lambda.getImplMethodName();
    }

    private static String parseMethodName(Serializable serializable) {
        SerializedLambda lambda = resolve(serializable);
        return XReflectUtil.methodToProperty(lambda.getImplMethodName());
    }

    private static SerializedLambda resolve(Serializable serializable) {
        return SERIALIZED_LAMBDA_CACHE.computeIfAbsent(serializable.getClass(), clazz -> {
            try {
                Method method = clazz.getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                return (SerializedLambda) method.invoke(serializable);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to resolve lambda", e);
            }
        });
    }

}
