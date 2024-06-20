package com.reopenai.infrastructure4j.core.builtin.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Java时间工具类
 *
 * @author Allen Huang
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class JavaTimeConstants {
    /**
     * 系统默认的TimeZone
     */
    public static final ZoneOffset SYSTEM_ZONE_OFFSET = OffsetDateTime.now().getOffset();

    /**
     * 时间的格式化
     */
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * 日期的格式化
     */
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 简洁的日期格式化
     */
    public static final DateTimeFormatter SIMPLE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 日期时间的格式化
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 简洁的精确到小时的时间格式化
     */
    public static final DateTimeFormatter SIMPLE_HOUR_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH");

    /**
     * 简洁的精确到分钟的时间格式化
     */
    public static final DateTimeFormatter SIMPLE_MINUTES_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    /**
     * 简洁的时间日期格式化
     */
    public static final DateTimeFormatter SIMPLE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 精确的日期时间格式化(带毫秒数)
     */
    public static final DateTimeFormatter PRECISE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");

    /**
     * 简洁的精确的日期时间格式化(带毫秒数)
     */
    public static final DateTimeFormatter SIMPLE_PRECISE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

}
