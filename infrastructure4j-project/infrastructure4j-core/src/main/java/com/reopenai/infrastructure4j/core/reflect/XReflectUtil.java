package com.reopenai.infrastructure4j.core.reflect;

import cn.hutool.core.util.ReflectUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;

import java.util.Locale;

/**
 * 反射工具类。
 * 如果有反射工具类的需求，请优先使用{@link ReflectUtil}
 *
 * @author Allen Huang
 */
@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class XReflectUtil {

    private static final String GET = "get";
    private static final String SET = "set";
    private static final String IS = "is";

    /**
     * 将getter、setter名称转换成属性名称
     *
     * @param name getter/setter名称
     * @return 属性名称
     */
    public static String methodToProperty(String name) {
        if (name.startsWith(IS)) {
            name = name.substring(2);
        } else if (name.startsWith(GET) || name.startsWith(SET)) {
            name = name.substring(3);
        } else {
            return null;
        }
        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }
        return name;
    }

    /**
     * 获取某个对象的Class。如果这个对象是代理对象，则会获取这个对象真正的Class
     *
     * @param bean 需要获取Class的实例
     * @return Class实例
     */
    public static Class<?> getClass(Object bean) {
        return AopUtils.isAopProxy(bean) ? AopUtils.getTargetClass(bean) : bean.getClass();
    }

}
