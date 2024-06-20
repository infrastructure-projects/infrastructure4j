package com.reopenai.infrastructure4j.beans.builtin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * 执行错误的抽象。每一个错误都有一个唯一的code与之对应。错误状态码的格式如下所示:
 * <pre>{@code 1 01 001}</pre>
 * 第一位表示错误的类型，通常有三种类型:
 * <ul>
 *     <li>1: 参数错误</li>
 *     <li>2: 业务异常</li>
 *     <li>3: 系统异常</li>
 * </ul>
 * 第二位和第三位表示服务序号，每一个服务都有一个全局唯一的序号，示例中的服务编号是01。<br>
 * 最后三位为异常序号，他是一个自增的数字。
 *
 * @author Allen Huang
 */
public interface ErrorCode extends Serializable {

    /**
     * 错误状态码
     */
    String getValue();

    /**
     * 创建一个临时的错误码实例
     *
     * @param code 错误码
     * @return 错误码实例
     */
    static Temporary temporary(String code) {
        return new Temporary(code);
    }

    /**
     * 临时的ErrorCode实例
     *
     * @param getValue error code的内容
     */
    record Temporary(String getValue) implements ErrorCode {
    }

    /**
     * 内置的一些错误码
     */
    @Getter
    @RequiredArgsConstructor
    enum Builtin implements ErrorCode {

        //---------------
        //  请求相关
        //---------------

        MANY_REQUEST("429"),

        SERVER_ERROR("500"),

        //---------------
        //  序列化相关
        //---------------
        /**
         * 获取Protobuf对象默认值时出错
         */
        PROTOBUF_PARSE_DEFAULT_VALUE_ERROR("5002"),

        /**
         * protobuf转换异常
         */
        PROTOBUF_CONVERSION_ERROR("5003"),

        ;

        private final String value;
    }

}
