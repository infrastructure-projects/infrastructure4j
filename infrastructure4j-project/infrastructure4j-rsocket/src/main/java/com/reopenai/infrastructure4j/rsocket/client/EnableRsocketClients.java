package com.reopenai.infrastructure4j.rsocket.client;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable Rsocket clients
 *
 * @author Allen Huang
 */
@Target(ElementType.TYPE)
@Import(RsocketClientScanner.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableRsocketClients {

    /**
     * 在指定报名的情况下是否还加载项目路径
     */
    boolean loadProjectPackages() default false;

    /**
     * 指定要扫描的报名列表
     */
    String[] value() default {};

    /**
     * 指定要扫描的包名列表
     */
    String[] basePackages() default {};

    /**
     * 指定要加载的包名所在的class
     */
    Class<?>[] basePackageClasses() default {};

}
