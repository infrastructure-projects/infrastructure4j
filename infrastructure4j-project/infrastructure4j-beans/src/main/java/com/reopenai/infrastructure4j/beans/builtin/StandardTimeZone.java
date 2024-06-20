package com.reopenai.infrastructure4j.beans.builtin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * TimeZone
 *
 * @author Allen Huang
 */
@Getter
@RequiredArgsConstructor
public enum StandardTimeZone {

    UTC_M12("UTC-12", -12),
    UTC_M11("UTC-11", -11),
    UTC_M10("UTC-10", -10),
    UTC_M9("UTC-9", -9),
    UTC_M8("UTC-8", -8),
    UTC_M7("UTC-7", -7),
    UTC_M6("UTC-6", -6),
    UTC_M5("UTC-5", -5),
    UTC_M4("UTC-4", -4),
    UTC_M3("UTC-3", -3),
    UTC_M2("UTC-2", -2),
    UTC_M1("UTC-1", -1),
    UTC("UTC+0", 0),
    UTC_P1("UTC+1", 1),
    UTC_P2("UTC+2", 2),
    UTC_P3("UTC+3", 3),
    UTC_P4("UTC+4", 4),
    UTC_P5("UTC+5", 5),
    UTC_P6("UTC+6", 6),
    UTC_P7("UTC+7", 7),
    UTC_P8("UTC+8", 8),
    UTC_P9("UTC+9", 9),
    UTC_P10("UTC+10", 10),
    UTC_P11("UTC+11", 11);

    private final String code;

    private final Integer id;

    /**
     * 将TimeZoneId转换成StandardTimeZone实例
     *
     * @param id Time Zone Id
     * @return 如果转换成功则返回转换后的结果，否则返回Null
     */
    public static StandardTimeZone from(Integer id) {
        for (StandardTimeZone standardTimeZone : StandardTimeZone.values()) {
            if (standardTimeZone.id.equals(id)) {
                return standardTimeZone;
            }
        }
        return null;
    }

    /**
     * 将Time Zone Code转换成StandardTimeZone实例
     *
     * @param code Time Zone Code
     * @return 如果转换成功则返回转换后的结果，否则返回Null
     */
    public static StandardTimeZone from(String code) {
        for (StandardTimeZone standardTimeZone : StandardTimeZone.values()) {
            if (standardTimeZone.getCode().equals(code)) {
                return standardTimeZone;
            }
        }
        return null;
    }

}
