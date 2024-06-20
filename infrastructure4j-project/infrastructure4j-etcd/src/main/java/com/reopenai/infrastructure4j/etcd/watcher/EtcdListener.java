package com.reopenai.infrastructure4j.etcd.watcher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个方法为ETCD Watcher的接受者.当有数据变更时会调用被标记的方法.
 * 如果有异常，请在方法中对异常进行处理.此消费不会尝试进行retry.
 * <br>
 * example:<pre>{@code
 *
 * // 只支持以下三种参数类型
 *
 * @EtcdListener(key = "key")
 * public void onWatchResponse(WatchResponse response){
 *     List<WatchEvent> events = response.getEvents();
 *     for (WatchEvent event : events) {
 *         // ....
 *     }
 * }
 *
 * @EtcdListener(key = "key")
 * public void onEvents(List<WatchEvent> events){
 *     for (WatchEvent event : events) {
 *         // ....
 *     }
 * }
 *
 * @EtcdListener(key = "key")
 * public void onEvent(WatchEvent event){
 *
 * }
 *
 * }</pre>
 *
 * @author Allen Huang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EtcdListener {

    /**
     * 支持SpEL语法.例如: ${spring.application.name}
     */
    String key();

    /**
     * 支持SpEL语法.例如: ${spring.application.name}
     */
    String endKey() default "";

    /**
     * revision
     */
    long revision();

    /**
     * prevKV
     */
    boolean prevKV();

    /**
     * progressNotify
     */
    boolean progressNotify();

    /**
     * createdNotify
     */
    boolean createdNotify();

    /**
     * noPut
     */
    boolean noPut();

    /**
     * noDelete
     */
    boolean noDelete();

    /**
     * requireLeader
     */
    boolean requireLeader();

    /**
     * prefix
     */
    boolean prefix();

    /**
     * 在系统初始化完成之后对key进行查询操作，查询出结果后调用被标记的方法.hook将在@EtcdListener方法被调用之前调用
     * example: <pre>{@code
     *
     * @EtcdListener.InitHook("myKey")
     * public void onInit(GetResponse response){
     *
     * }
     *
     * }</pre>
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface InitHook {

        /**
         * 支持SpEL语法.例如: ${spring.application.name}
         */
        String[] value();

        /**
         * 是否通过异步的方式去查询数据.
         * 如果通过异步的方式查询可以显著增加性能,
         * 但方法被调用的时间不可控,无法保证会在@EtcdListener调用之前被调用.
         * 并且调用失败时仅仅只会打印失败日志.
         */
        boolean async() default true;

    }

}
