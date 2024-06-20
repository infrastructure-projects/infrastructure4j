package com.reopenai.infrastructure4j.core.idempotent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明一个方法是幂等的
 *
 * @author Allen Huang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IdempotentMethod {

    /**
     * 是否应该包含服务前缀，默认为true
     */
    boolean includePrefix() default true;

    /**
     * 幂等方法的key,支持SpEL
     */
    String value();

    /**
     * 是否应该对错误也做幂等处理，默认不处理
     */
    boolean includeException() default false;

    /**
     * 指定需要忽略的异常。
     * <br>
     * 当includeException=true时，异常也将被视为幂等调用，也就是说只要有异常，无论调用了多少次，都会抛出第一次调用的异常。
     * 该属性指明了在需要哪些异常时可以忽略这些异常，即便出现了这些异常，下一次还是能够被调用，而不是直接抛出异常。
     */
    Class<? extends Throwable>[] noNegativeFor() default {};

    /**
     * 幂等能力的提供者，默认使用全局幂等能力提供者.
     *
     * @see IdempotentProvider
     */
    String provider() default IdempotentProvider.DEFAULT_PROVIDER_NAME;

    /**
     * 幂等保护的过期时间,单位秒,默认为3天。
     * <br>
     * 幂等保护期的意思是该幂等处理的有效期。例如:
     * <br>
     * 按照默认配置(3天)，当第一次请求完成之后，在有效期内再次调用该方法时，该方法不会被真正调用，而是直接返回调用的结果，
     * 并且每一次调用都会刷新保护时间。
     */
    long expire() default 3 * 24 * 3600;

}
