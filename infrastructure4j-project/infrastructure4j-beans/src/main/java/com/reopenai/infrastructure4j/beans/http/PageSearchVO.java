package com.reopenai.infrastructure4j.beans.http;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 分页查询响应的结构体
 *
 * @author Allen Huang
 */
@Data
public class PageSearchVO<E> implements Serializable {

    @Schema(description = "总记录数。如果请求参数中'是否查询总记录数'为true则此参数将会返回.")
    private Long total;

    @Schema(description = "分页查询的结果", requiredMode = Schema.RequiredMode.REQUIRED)
    private Collection<E> records;

    @Schema(description = "扩展字段.如果存在OrderData字段，在查询下一页时需要将此字段带入extra中.")
    private Map<String, Object> extra;

    /**
     * 转换分页查询结果的泛型
     *
     * @param result 要返回的数据列表
     * @return 分页查询结果实例
     */
    public <R> PageSearchVO<R> create(Collection<R> result) {
        PageSearchVO<R> searchResult = new PageSearchVO<>();
        searchResult.setRecords(result);
        searchResult.setTotal(this.getTotal());
        searchResult.setExtra(new HashMap<>(2));
        return searchResult;
    }

    /**
     * 转换分页查询结果的泛型
     *
     * @param pageResult 原始结果
     * @param result     新的返回结果内容
     * @return 转换后的分页查询结果
     */
    public static <T, R> PageSearchVO<R> transform(PageSearchVO<T> pageResult, Collection<R> result) {
        PageSearchVO<R> searchResult = new PageSearchVO<>();
        searchResult.setRecords(result);
        searchResult.setTotal(pageResult.getTotal());
        searchResult.setExtra(pageResult.getExtra());
        return searchResult;
    }

    /**
     * 创建分页查询结果实例。
     *
     * @param data  分页查询返回的数据内容
     * @param total 分页查询的总记录数
     * @return 分页查询结果实例
     */
    public static <T> PageSearchVO<T> create(Collection<T> data, Long total) {
        PageSearchVO<T> result = new PageSearchVO<>();
        result.setTotal(total);
        result.setRecords(data);
        result.setExtra(new HashMap<>(2));
        return result;
    }

    /**
     * 创建一个不包含任何返回数据(null)的分页查询结果
     *
     * @return 分页查询结果实例
     */
    public static <T> PageSearchVO<T> emptyOfNull() {
        PageSearchVO<T> result = new PageSearchVO<>();
        result.setTotal(0L);
        result.setExtra(new HashMap<>(2));
        return result;
    }

    /**
     * 创建一个空的列表的分页查询结果
     *
     * @return 分页查询结果实例
     */
    public static <T> PageSearchVO<T> emptyOfArray() {
        PageSearchVO<T> result = new PageSearchVO<>();
        result.setTotal(0L);
        result.setRecords(Collections.emptyList());
        return result;
    }


}
