package com.reopenai.infrastructure4j.etcd.watcher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * EtcdListeners
 *
 * @author Allen Huang
 * @see EtcdListener
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EtcdListeners {

    /**
     * 监听器列表
     */
    EtcdListener[] value();

}
