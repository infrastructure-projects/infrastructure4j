package com.reopenai.infrastructure4j.beans.spi;

/**
 * I18n处理能力提供器
 *
 * @author Allen Huang
 */
public interface I18nProvider {

    /**
     * 根据国际化代码和格式化参数，解析出该国际化代码对应的本地语言国际化信息.
     *
     * @param code 国际化代码
     * @param args 国际化信息的格式化参数
     * @return 解析后的国际化信息
     */
    String parseLocaleMessage(String code, Object... args);

}
