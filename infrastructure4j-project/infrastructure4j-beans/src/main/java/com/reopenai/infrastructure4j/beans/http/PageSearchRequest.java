package com.reopenai.infrastructure4j.beans.http;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.function.Function;

/**
 * 分页查询结构体
 *
 * @author Allen Huang
 */
@Data
@PageSearchRequest.PageSearchChecker
public class PageSearchRequest<T> {

    /**
     * 页码数
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 页大小
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 是否查询总记录数
     */
    public static final String COUNT = "count";

    /**
     * 排序规则
     */
    public static final String ORDERS = "orders";

    @Min(value = 1, message = "pageNum cannot be less than 1")
    @Schema(
            example = "1",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Number of pages"
    )
    private int pageNum;

    @Min(value = 1, message = "pageSize cannot be less than 1")
    @Max(value = 20480, message = "pageSize cannot be more than 20480")
    @Schema(
            minimum = "1",
            example = "10",
            maximum = "20480",
            description = "page size",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private int pageSize;

    @Schema(
            example = "false",
            requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Whether to query the total number of records. Querying the total number of records will seriously affect performance. Do not query unless necessary."
    )
    private boolean count;

    @Valid
    @NotNull(message = "params cannot be null")
    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Condition parameters of the query. If your query does not have any conditions, please pass in an empty object (non-null)"
    )
    private T params;

    @Valid
    @Size(max = 10, message = "sorts cannot be greater than 10")
    @Schema(description = "排序字段", example = "null")
    private List<SortDTO> sorts;

    /**
     * 创建一个默认的分页查询请求参数。
     * 默认的请求参数的pageSize=1，pageNum=10
     *
     * @return 分页查询参数实例
     */
    public static <T> PageSearchRequest<T> create() {
        return from(10, 1);
    }

    /**
     * 根据页大小和页码数创建分页查询参数实例
     *
     * @param pageSize 页大小
     * @param pageNum  页码
     * @return 分页查询参数实例
     */
    public static <T> PageSearchRequest<T> from(int pageSize, int pageNum) {
        return from(pageSize, pageNum, false);
    }

    /**
     * 根据页大小和页码数创建分页查询参数实例
     *
     * @param pageSize 页大小
     * @param pageNum  页码
     * @param count    是否查询总记录数
     * @return 分页查询参数实例
     */
    public static <T> PageSearchRequest<T> from(int pageSize, int pageNum, boolean count) {
        PageSearchRequest<T> param = new PageSearchRequest<>();
        param.setCount(count);
        param.setPageNum(pageNum);
        param.setPageSize(pageSize);
        return param;
    }

    /**
     * 复制一份分页查询参数
     *
     * @param request   原分页查询请求
     * @param converter 参数转换函数
     * @return 转换后的结果
     */
    public static <T, P> PageSearchRequest<P> copy(PageSearchRequest<T> request, Function<T, P> converter) {
        PageSearchRequest<P> result = new PageSearchRequest<>();
        result.setCount(request.isCount());
        result.setSorts(request.getSorts());
        result.setPageNum(request.getPageNum());
        result.setPageSize(request.getPageSize());
        T params = request.getParams();
        if (params != null && converter != null) {
            result.setParams(converter.apply(params));
        }
        return result;
    }

    /**
     * 分页查询参数验证器
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = PageSearchChecker.PageSearchCheckerValidator.class)
    @interface PageSearchChecker {

        /**
         * 异常消息
         */
        String message() default "";

        /**
         * 分组
         */
        Class<?>[] groups() default {};

        /**
         * 有效载荷
         */
        Class<? extends Payload>[] payload() default {};


        final class PageSearchCheckerValidator implements ConstraintValidator<PageSearchChecker, PageSearchRequest<?>> {

            @Override
            public boolean isValid(PageSearchRequest<?> value, ConstraintValidatorContext context) {
                int pageSize = value.getPageSize();
                int pageNum = value.getPageNum();
                int offset = (pageNum - 1) * pageSize;
                if (offset > 100000000) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("Paging query exceeds the maximum number of items that can be queried (100000000)")
                            .addConstraintViolation();
                    return false;
                }
                return true;
            }

        }

    }


}
