package com.reopenai.infrastructure4j.etcd.watcher;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明一个类为EtcdWatcher，并交由Spring管理。
 * example:
 * <pre>{@code
 *
 * @EtcdComponent
 * public class WatchClass {
 *
 * }
 *
 * }</pre>
 *
 * @author Allen Huang
 * @see EtcdListener
 * @see EtcdListeners
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EtcdComponent {
}
