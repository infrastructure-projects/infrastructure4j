package com.reopenai.infrastructure4j.core.i18n;

import com.reopenai.infrastructure4j.beans.builtin.ErrorCode;
import com.reopenai.infrastructure4j.core.builtin.constants.EmptyConstants;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 国际化工具类
 *
 * @author Allen Huang
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class I18nUtil {

    /**
     * 根据异常代码和格式化参数，解析出该代码对应的本地语言国际化信息.
     * 异常代码是全局唯一的，每一个异常代码都对应一个国际化代码。
     *
     * @param errorCode 异常代码
     * @return 解析后的国际化信息
     * @see LocaleContextHolder
     * @see ErrorCode
     */
    public static String parseLocaleMessage(ErrorCode errorCode) {
        return parseLocaleMessage(errorCode.getValue());
    }

    /**
     * 根据国际化代码解析出该代码对应的本地语言国际化信息.
     *
     * @param code 国际化代码
     * @return 解析后的国际化信息
     * @see LocaleContextHolder
     */
    public static String parseLocaleMessage(String code) {
        return parseLocaleMessage(code, EmptyConstants.EMPTY_OBJECT_ARRAY);
    }

    /**
     * 根据异常代码和格式化参数，解析出该国际化代码对应的本地语言国际化信息.
     * 异常代码是全局唯一的，每一个异常代码都对应一个国际化代码。
     *
     * @param errorCode 异常代码
     * @param args      国际化信息的格式化参数
     * @return 解析后的国际化信息
     * @see LocaleContextHolder
     * @see ErrorCode
     */
    public static String parseLocaleMessage(ErrorCode errorCode, Object... args) {
        return parseLocaleMessage(errorCode.getValue(), args);
    }

    /**
     * 根据国际化代码和格式化参数，解析出该国际化代码对应的本地语言国际化信息.
     *
     * @param code 国际化代码
     * @param args 国际化信息的格式化参数
     * @return 解析后的国际化信息
     * @see LocaleContextHolder
     */
    public static String parseLocaleMessage(String code, Object... args) {
        return "";
    }

}
