package com.reopenai.infrastructure4j.core.runtime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Optional;

/**
 * 运行时的一些环境信息的工具类
 *
 * @author Allen Huang
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RuntimeUtil {

    public static final String PROJECT_PATH;

    static {
        String packageName = RuntimeUtil.class.getPackageName();
        int commonIndex = packageName.indexOf("infrastructure4j");
        if (commonIndex < 1) {
            commonIndex = packageName.length();
        }
        PROJECT_PATH = packageName.substring(0, commonIndex - 1);
    }

    /**
     * 获取主函数的包名
     *
     * @return 主函数包名
     */
    public static String getMainPackage() {
        return Optional.ofNullable(deduceMainApplicationClass())
                // 兼容单元测试
                .filter(clazz -> !"com.intellij.rt.junit.JUnitStarter".equals(clazz.getName()))
                .map(Class::getPackage)
                .map(Package::getName)
                .orElse(PROJECT_PATH);
    }

    /**
     * 获取主函数的类信息
     */
    public static Class<?> deduceMainApplicationClass() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        } catch (ClassNotFoundException ex) {
            //ignore
        }
        return null;
    }

}
