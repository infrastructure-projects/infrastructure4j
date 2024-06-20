package com.reopenai.infrastructure4j.core.aop;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.StaticMethodMatcher;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 匹配Method注解的切面
 *
 * @author Allen Huang
 */
public class AnnotationMethodPoint implements Pointcut {

    private final Class<? extends Annotation> annotationType;

    public AnnotationMethodPoint(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        this.annotationType = annotationType;
    }

    @Override
    public @NonNull ClassFilter getClassFilter() {
        return ClassFilter.TRUE;
    }

    @Override
    public @NonNull MethodMatcher getMethodMatcher() {
        return new AnnotationMethodMatcher(annotationType);
    }

    private static class AnnotationMethodMatcher extends StaticMethodMatcher {
        private final Class<? extends Annotation> annotationType;

        public AnnotationMethodMatcher(Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }

        @Override
        public boolean matches(@NonNull Method method, @NonNull Class<?> targetClass) {
            if (matchesMethod(method)) {
                return true;
            }
            if (Proxy.isProxyClass(targetClass)) {
                return false;
            }
            Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
            return (specificMethod != method && matchesMethod(specificMethod));
        }

        private boolean matchesMethod(Method method) {
            return AnnotatedElementUtils.hasAnnotation(method, this.annotationType);
        }

    }
}