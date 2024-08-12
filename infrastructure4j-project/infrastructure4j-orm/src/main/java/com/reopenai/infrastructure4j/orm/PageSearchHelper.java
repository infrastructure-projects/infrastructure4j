package com.reopenai.infrastructure4j.orm;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reopenai.infrastructure4j.beans.http.PageSearchRequest;
import com.reopenai.infrastructure4j.beans.http.PageSearchVO;
import com.reopenai.infrastructure4j.beans.http.SortDTO;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    /**
     * 根据分页查询参数构建MyBatis-Plus的分页查询对象。
     * 如果查询参数中存在排序参数，此方法会尝试将排序字段的名称从驼峰转换成下划线格式。
     * 如果排序参数中未指定排序规则，默认会采用升序的排序规则。
     *
     * @param request 分页查询参数
     * @return MyBatis-Plus分页查询对象
     */
    public static <R> Page<R> createPage(PageSearchRequest<?> request) {
        Page<R> page = new Page<>(request.getPageNum(), request.getPageSize());
        page.setSearchCount(request.isCount());
        List<OrderItem> sorts = Optional.ofNullable(request.getSorts())
                .stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(PageSearchHelper::createOrderItem)
                .toList();
        if (CollUtil.isNotEmpty(sorts)) {
            page.addOrder(sorts);
        }
        return page;
    }

    /**
     * 根据分页查询的排序参数构建MyBatis-Plus的排序对象。
     * 此方法会尝试将排序字段从驼峰转换成下划线模式，
     * 如果排序参数中未指定排序规则，默认会设置升序排序规则。
     *
     * @param order 查询排序参数
     * @return MyBatis-Plus的排序对象
     */
    private static OrderItem createOrderItem(SortDTO order) {
        String fieldName = StrUtil.toUnderlineCase(order.getFieldName());
        return order.isAsc() ? OrderItem.asc(fieldName) : OrderItem.desc(fieldName);
    }

    /**
     * 将MyBatis-Plus的分页查询结果转换成{@link PageSearchVO}
     *
     * @param page MyBatis Plus分页查询结果
     * @return 分页查询结果
     */
    public static <T> PageSearchVO<T> searchResult(IPage<T> page) {
        return searchResult(page, Function.identity());
    }

    /**
     * 将MyBatis-Plus的分页查询结果转换成{@link PageSearchVO}
     *
     * @param page   MyBatis Plus分页查询结果
     * @param mapper DTO转换函数，将MyBatis-Plus的查询结果转换成想要的实体
     * @return 分页查询结果
     */
    public static <T, R> PageSearchVO<R> searchResult(IPage<T> page, Function<T, R> mapper) {
        PageSearchVO<R> result = new PageSearchVO<>();
        result.setTotal(page.getSize());
        result.setTotal(page.getTotal());
        List<R> data = Optional.ofNullable(page.getRecords())
                .stream()
                .flatMap(Collection::stream)
                .map(mapper)
                .collect(Collectors.toList());
        result.setRecords(data);
        return result;
    }

    /**
     * 快速的分页查询方法
     *
     * @param request 分页查询请求参数
     * @param mapper  实际执行分页查询的函数
     * @return 分页查询结果
     */
    public static <T, R> PageSearchVO<R> fastSearch(PageSearchRequest<T> request, BiFunction<Page<R>, T, IPage<R>> mapper) {
        Page<R> page = createPage(request);
        return searchResult(mapper.apply(page, request.getParams()));
    }

    /**
     * 快速的分页查询方法
     *
     * @param request  分页查询请求参数
     * @param function 实际执行分页查询的函数
     * @param mapper   查询结果的DTO转换函数
     * @return 分页查询结果
     */
    public static <T, E, R> PageSearchVO<R> fastSearch(PageSearchRequest<T> request, BiFunction<Page<E>, T, IPage<E>> function, Function<E, R> mapper) {
        Page<E> page = createPage(request);
        return searchResult(function.apply(page, request.getParams()), mapper);
    }

    /**
     * 使用分页查询助手开启一个分页查询
     *
     * @return 分页查询结果
     * @see Searcher
     */
    public static <T, E, R> Searcher<T, E, R> startSearch() {
        return new Searcher<>();
    }

    /**
     * 一个分页查询助手，降低查询的复杂度。用例如下:
     * <pre>{@code
     * PageSearchHelper.<DemoRequest, Demo, DemoVo>startSearch()
     *                 .request(searchParam)
     *                 .method(demoReadRepository::doSearch)
     *                 .trans(DemoVO::from)
     *                 .search();
     * }</pre>
     * 如果不需要做DTO转换，你可以调整查询的方法:
     * <pre>{@code
     * PageSearchHelper.<DemoRequest, Demo, DemoVo>startSearch()
     *                 .request(searchParam)
     *                 .method(demoReadRepository::doSearch)
     *                 .trans(Function.identity())
     *                 .search();
     * }</pre>
     */
    public static class Searcher<T, E, R> {

        private PageSearchRequest<T> request;

        private BiFunction<Page<E>, T, IPage<E>> function;

        private Function<E, R> mapper;

        /**
         * 设计分页查询请求参数
         *
         * @param request 请求参数实例
         * @return Searcher实例
         */
        public Searcher<T, E, R> request(PageSearchRequest<T> request) {
            this.request = request;
            return this;
        }

        /**
         * 设置调用分页查询的dao方法的函数
         *
         * @param function 执行实际查询的函数
         * @return Searcher实例
         */
        public Searcher<T, E, R> method(BiFunction<Page<E>, T, IPage<E>> function) {
            this.function = function;
            return this;
        }

        /**
         * 从数据库查询到结果时做结果转换的函数
         *
         * @param mapper DTO转换函数
         * @return Searcher实例
         */
        public Searcher<T, E, R> map(Function<E, R> mapper) {
            this.mapper = mapper;
            return this;
        }

        /**
         * 执行分页查询，并将查询的结果转换成{@link PageSearchVO}
         *
         * @return 分页查询结果
         */
        public PageSearchVO<R> search() {
            if (request == null) {
                throw new IllegalArgumentException("search request can not be null");
            }
            if (function == null) {
                throw new IllegalArgumentException("search method can not be null");
            }
            if (mapper == null) {
                throw new IllegalArgumentException("trans can not be null");
            }
            return fastSearch(request, function, mapper);
        }
    }


}
