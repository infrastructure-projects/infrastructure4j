package com.reopenai.infrastructure4j.core.idempotent;

import java.lang.reflect.Type;

/**
 * 幂等能力提供器
 *
 * @author Allen Huang
 */
public interface IdempotentProvider {

    String DEFAULT_PROVIDER_NAME = "defaultIdempotentProvider";

    /**
     * 配置前缀
     */
    String CONFIG_PREFIX = "application.idempotent";

    /**
     * 判断某个Key是否被执行过
     *
     * @param key 需要判断的key
     * @return 如果被执行过则返回true，否则返回false
     */
    boolean hasInvoked(String key);

    /**
     * 当执行成功时的回调
     *
     * @param key        执行的key
     * @param returnType 返回类型
     * @param value      执行的结果
     * @param expire     过期时间，单位为秒
     */
    void onSuccess(String key, Type returnType, Object value, long expire);

    /**
     * 当执行出现错误时被触发的方法
     *
     * @param key    执行的key
     * @param error  执行的错误
     * @param expire 过期时间，单位秒
     */
    void onError(String key, Throwable error, long expire);

    /**
     * 获取执行的结果
     *
     * @param key  执行的key
     * @param type 执行的结果的类型
     * @return 如果key被执行过则返回执行的结果，否则返回null
     */
    Object getInvokeResult(String key, Type type);

}
