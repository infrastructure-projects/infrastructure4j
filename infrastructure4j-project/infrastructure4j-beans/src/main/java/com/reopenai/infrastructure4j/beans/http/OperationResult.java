package com.reopenai.infrastructure4j.beans.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.reopenai.infrastructure4j.beans.builtin.ErrorCode;
import com.reopenai.infrastructure4j.beans.spi.I18nProvider;
import com.reopenai.infrastructure4j.beans.spi.RequestIdProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.*;

/**
 * HTTP统计响应结构体
 *
 * @author Allen Huang
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperationResult<T> {

    private static final I18nProvider I18N_PROVIDER;

    private static final RequestIdProvider REQUEST_ID_PROVIDER;

    static {
        REQUEST_ID_PROVIDER = Optional.of(ServiceLoader.load(RequestIdProvider.class))
                .stream()
                .flatMap(ServiceLoader::stream)
                .findFirst()
                .map(ServiceLoader.Provider::get)
                .orElse(() -> null);
        I18N_PROVIDER = Optional.of(ServiceLoader.load(I18nProvider.class))
                .stream()
                .flatMap(ServiceLoader::stream)
                .findFirst()
                .map(ServiceLoader.Provider::get)
                .orElse(((code, args) -> code));
    }

    public static final String CODE_SUCCESS = "200";

    public static final String MESSAGE_SUCCESS = "success";

    @Schema(
            example = "200",
            requiredMode = Schema.RequiredMode.REQUIRED,
            description = "请求结果的状态码.如果这个值返回的是200, 那么就代表业务处理成功了"
    )
    private String code = CODE_SUCCESS;

    @Schema(
            example = "success",
            requiredMode = Schema.RequiredMode.REQUIRED,
            description = "请求结果的描述. 如果业务处理失败，将会描述失败的原因."
    )
    private String message = MESSAGE_SUCCESS;

    @Schema(description = "请求返回的结果")
    private T data;

    @Schema(
            example = "00000001",
            description = "请求ID,有值时才返回",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String requestId;

    @Schema(
            type = "array",
            subTypes = String.class,
            example = "[\"var0\",\"var1\"]",
            description = "异常错误参数,有值时才存在"
    )
    private Object[] errorVars;

    /**
     * 构建一个{@link OperationResult}实例，该实例的状态为成功，不携带任何的数据。
     *
     * @return OperationResult 实例
     */
    public static <T> OperationResult<T> returnOk() {
        return returnOk(null);
    }

    /**
     * 构建一个{@link OperationResult}实例，该实例的状态为成功且将携带一个空的List实例。
     *
     * @return OperationResult 实例
     */
    public static <T> OperationResult<List<T>> returnEmptyList() {
        return returnOk(Collections.emptyList());
    }

    /**
     * 构建一个{@link OperationResult}实例，该实例的状态为成功且将携带一个空的Set实例。
     *
     * @return OperationResult 实例
     */
    public static <T> OperationResult<Set<T>> returnEmptySet() {
        return returnOk(Collections.emptySet());
    }

    /**
     * 构建一个{@link OperationResult}实例，该实例的状态为成功且携带请求结果
     *
     * @param data 请求结果
     * @return OperationResult 实例
     */
    public static <T> OperationResult<T> returnOk(T data) {
        OperationResult<T> result = new OperationResult<>();
        result.setData(data);
        result.setRequestId(REQUEST_ID_PROVIDER.getRequestId());
        return result;
    }

    /**
     * 构建一个{@link OperationResult}实例，并指定响应的状态
     *
     * @param errorCode 错误代码
     * @param args      错误参数
     * @return 非成功状态的OperationResult实例
     */
    public static <T> OperationResult<T> returnFail(ErrorCode errorCode, Object... args) {
        return returnFail(errorCode.getValue(), args);
    }

    /**
     * 构建一个{@link OperationResult}实例，并指定响应的状态
     *
     * @param code 错误代码
     * @param args 错误参数
     * @return 非成功状态的OperationResult实例
     */
    public static <T> OperationResult<T> returnFail(String code, Object... args) {
        OperationResult<T> result = new OperationResult<>();
        result.setCode(code);
        result.setErrorVars(args);
        result.setRequestId(REQUEST_ID_PROVIDER.getRequestId());
        result.setMessage(I18N_PROVIDER.parseLocaleMessage(code, args));
        return result;
    }

}
