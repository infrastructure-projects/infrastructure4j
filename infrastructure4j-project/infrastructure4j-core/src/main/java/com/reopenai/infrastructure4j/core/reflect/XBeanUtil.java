package com.reopenai.infrastructure4j.core.reflect;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.cglib.core.Converter;
import org.springframework.cglib.core.DebuggingClassWriter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BeanCopy工具类
 *
 * @author Allen Huang
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class XBeanUtil {

    static {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "target");
    }

    /**
     * 多层次结构缓存CustomBeanCopier。具体结构如下:
     * .
     * └── sourceClass
     * └── targetClass
     * └── hasConverter
     * └── key
     * └── CustomBeanCopier
     */
    private static final Map<Class<?>, Map<Class<?>, Map<Boolean, Map<String, CustomBeanCopierGenerate>>>> BEAN_COPIER_MAP = new ConcurrentHashMap<>();

    /**
     * 将源对象中的属性复制给目标对象。能够被成功复制的属性的类型和名称必须一致。
     *
     * @param source       源对象
     * @param target       目标对象
     * @param ignoreFields 需要忽略的字段名
     */
    public static void copyProperties(Object source, Object target, String... ignoreFields) {
        copyProperties(source, target, null, ignoreFields);
    }

    /**
     * 使用转换器将源对象中的属性复制给目标对象。能够被成功复制的属性的类型和名称必须一致。
     *
     * @param source    源对象
     * @param target    目标对象
     * @param converter 转换器实例
     */
    public static void copyProperties(Object source, Object target, Converter converter, String... ignoreFields) {
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();
        //获取所有需要被忽略的字段
        Set<String> ignores = ignoreFields.length == 0 ? Collections.emptySet() : new TreeSet<>(Set.of(ignoreFields));
        getBeanCopier(sourceClass, targetClass, converter, ignores)
                .copy(source, target, converter);
    }

    private static CustomBeanCopierGenerate getBeanCopier(Class<?> sourceClass, Class<?> targetClass, Converter converter, Set<String> ignores) {
        //计算出需要忽略的字段
        StringJoiner joiner = new StringJoiner("&");
        for (String ignore : ignores) {
            joiner.add(ignore);
        }
        String key = joiner.toString();
        boolean hasConverter = converter != null;

        // 根据sourceClass获取第一层
        Map<Class<?>, Map<Boolean, Map<String, CustomBeanCopierGenerate>>> sourceMap = BEAN_COPIER_MAP
                .computeIfAbsent(sourceClass, k -> new ConcurrentHashMap<>(16));

        // 根据targetClass获取第二层
        Map<Boolean, Map<String, CustomBeanCopierGenerate>> targetMap = sourceMap
                .computeIfAbsent(targetClass, k -> buildTargetMap());

        // 根据hasConverter获取第三层
        Map<String, CustomBeanCopierGenerate> instantMap = targetMap.get(hasConverter ? Boolean.TRUE : Boolean.FALSE); // 优化拆箱带来的性能损失

        return instantMap.computeIfAbsent(key, k -> CustomBeanCopierGenerate.create(sourceClass, targetClass, hasConverter, ignores));
    }

    private static Map<Boolean, Map<String, CustomBeanCopierGenerate>> buildTargetMap() {
        Map<Boolean, Map<String, CustomBeanCopierGenerate>> map = new HashMap<>();
        map.put(Boolean.FALSE, new ConcurrentHashMap<>(4));
        map.put(Boolean.TRUE, new ConcurrentHashMap<>(0));
        return map;
    }

}
