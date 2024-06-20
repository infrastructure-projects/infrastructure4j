package com.reopenai.infrastructure4j.beans.builtin;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 数字包装类
 *
 * @param value 数字值
 * @author Allen Huang
 */
@Schema(implementation = Number.class)
public record NumberValue(Number value) {

}
