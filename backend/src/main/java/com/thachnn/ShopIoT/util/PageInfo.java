package com.thachnn.ShopIoT.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageInfo {
    public static final String PAGE_NUMBER_DEFAULT = "1";
    public static final String PAGE_SIZE_DEFAULT = "10";

    private Integer size;
    private long totalElements;
    private Integer totalPages;
    private Integer page;
    private Boolean hasNext = null;
    private Boolean hasPrevious = null;
}
