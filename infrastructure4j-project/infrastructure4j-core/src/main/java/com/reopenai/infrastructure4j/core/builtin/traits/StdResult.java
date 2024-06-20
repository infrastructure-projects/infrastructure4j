package com.reopenai.infrastructure4j.core.builtin.traits;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 一种异常处理方式。将结果或者异常包装在StdResult类中。
 * 请注意，此功能正在孵化中，请勿使用
 *
 * @author Allen Huang
 */
public class StdResult<T, E> {

    private T data;

    private E error;

    /**
     * 判断结果是否是无错误的
     *
     * @return 如果为无异常的结果则返回true，否则返回false
     */
    public boolean isOk() {
        return error == null;
    }

    /**
     * 判断结果是否是无错误的
     *
     * @param func 扩展执行函数
     * @return 如果result是ok，并且func返回的结果也是true,则返回true
     */
    public boolean ifOkAnd(Function<T, Boolean> func) {
        if (isOk()) {
            return func.apply(data);
        }
        return false;
    }

    /**
     * 判断结果是否是有错误的
     *
     * @return 如果为有异常的结果则返回true，否则返回false
     */
    public boolean isErr() {
        return this.error != null;
    }

    /**
     * 判断结果是否是无错误的
     *
     * @param func 扩展执行函数
     * @return 如果为有异常的结果，并且func返回的结果也是true,则返回true
     */
    public boolean ifErrAnd(Function<E, Boolean> func) {
        if (this.isOk()) {
            return func.apply(error);
        }
        return false;
    }

    /**
     * 如果是StdResult.Ok,则使用func函数对结果进行转换
     *
     * @param func 转换函数
     * @return 转换后的结果
     */
    public <R> StdResult<R, E> map(Function<T, R> func) {
        if (this.isOk()) {
            R newData = func.apply(this.data);
            return StdResult.Ok(newData);
        }
        return StdResult.Error(this.error);
    }

    /**
     * 如果是StdResult.Ok,则使用func函数对结果进行转换.
     * 如果是StdResult.Err，则将StdResult的结果更改为defaultValue
     *
     * @param func         转换函数
     * @param defaultValue 如果为错误时的默认值
     * @return 转换后的结果
     */
    public <R> StdResult<R, E> mapOr(Function<T, R> func, R defaultValue) {
        if (this.isOk()) {
            R newData = null;
            if (this.data != null) {
                newData = func.apply(this.data);
            }
            return StdResult.Ok(newData);
        }
        return StdResult.Ok(defaultValue);
    }

    /**
     * 如果是StdResult.Ok,则使用func函数对结果进行转换.
     * 如果是StdResult.Err，则将StdResult的结果更改为supplier函数创建的新值
     *
     * @param func     转换函数
     * @param supplier defaultValue创建函数
     * @return 转换后的结果
     */
    public <R> StdResult<R, E> mapOrElse(Function<T, R> func, Supplier<R> supplier) {
        if (this.isOk()) {
            R newData = null;
            if (this.data != null) {
                newData = func.apply(this.data);
            }
            return StdResult.Ok(newData);
        }
        R newData = supplier.get();
        return StdResult.Ok(newData);
    }

    /**
     * 如果是StdResult.Ok,则什么都不处理。
     * 如果是StdResult.Error,则使用func对Error内容进行转换
     *
     * @param func error转换函数
     * @return 转换后的结果
     */
    public <EX> StdResult<T, EX> mapErr(Function<E, EX> func) {
        if (this.error != null) {
            EX newError = func.apply(this.error);
            return StdResult.Error(newError);
        }
        return StdResult.Ok(this.data);
    }

    /**
     * 如果是StdResult.Ok,则将data取出来交由consumer函数处理
     *
     * @param consumer data处理函数
     * @return 当前StdResult实例
     */
    public StdResult<T, E> inspect(Consumer<T> consumer) {
        if (this.isOk()) {
            consumer.accept(this.data);
        }
        return this;
    }

    /**
     * 如果是StdResult.Error,则将error取出来交由consumer函数处理
     *
     * @param consumer error处理函数
     * @return 当前StdResult实例
     */
    public StdResult<T, E> inspectError(Consumer<E> consumer) {
        if (this.isErr()) {
            consumer.accept(this.error);
        }
        return this;
    }

    /**
     * 将StdResult转换成Stream
     *
     * @return 转换后的结果
     */
    public Stream<T> stream() {
        if (this.isOk() && this.data != null) {
            return Stream.of(this.data);
        }
        return Stream.empty();
    }

    /**
     * 将StdResult转换成Optional。
     * 此操作会忽略异常.
     *
     * @return 转换后的Optional结果
     */
    public Optional<T> optional() {
        if (this.isOk()) {
            return Optional.ofNullable(this.data);
        }
        return Optional.empty();
    }

    /**
     * 创建一个包含正常结果的实例
     *
     * @param data 包含的数据
     * @return StdResult实例
     */
    public static <T, E> StdResult<T, E> Ok(T data) {
        StdResult<T, E> result = new StdResult<>();
        result.data = data;
        return result;
    }

    /**
     * 创建一个包含正常结果的实例
     *
     * @return StdResult实例
     */
    public static <T, E> StdResult<T, E> Ok() {
        return Ok(null);
    }

    /**
     * 创建一个异常的实例
     *
     * @param error 异常内容
     * @return StdResult实例
     */
    public static <T, E> StdResult<T, E> Error(E error) {
        StdResult<T, E> result = new StdResult<>();
        result.error = error;
        return result;
    }


}
