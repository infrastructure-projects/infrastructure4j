package com.reopenai.infrastructure4j.orm;

/**
 * 基于MyBatis Plus的分页查询工具。
 *
 * @author Allen Huang
 */
public final class PageSearchHelper {

    /**
     * 根据总记录数、页大小计算总页数
     *
     * @param total   总记录数
     * @param maxSize 页面的条目数
     * @return 总页数
     */
    public static long totalPage(long total, int maxSize) {
        return (total + maxSize - 1) / maxSize;
    }



}
