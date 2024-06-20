package com.reopenai.infrastructure4j.beans.http;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排序结构体
 *
 * @author Allen Huang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortDTO {

    @NotBlank(message = "fieldName cannot be blank")
    @Pattern(regexp = "^[a-z][a-zA-Z0-9_]{1,16}$", message = "invalid orders.fieldName")
    @Schema(description = "The field name of the sort field", requiredMode = Schema.RequiredMode.REQUIRED, example = "username")
    private String fieldName;

    @Schema(description = "是否升序排序.true-升序/false-降序.默认按照降序排序")
    private boolean asc;

}
