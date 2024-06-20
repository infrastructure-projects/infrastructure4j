package com.reopenai.infrastructure4j.core.builtin.conditions;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定某个bean的初始化模式。
 * 当部署在非生产环境时，此bean会被初始化，并被Spring容器加载.
 *
 * @author Allen Huang
 * @see ProfileConstants
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ConditionalOnExpression("!environment['application.env'].matches('.*\\b(prod|live)\\b.*')")
public @interface OnNotProdEnv {
}
