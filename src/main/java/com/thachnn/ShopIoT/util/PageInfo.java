package com.thachnn.ShopIoT.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageInfo {
    public static final String PAGE_NUMBER_DEFAULT = "1";
    public static final String PAGE_SIZE_DEFAULT = "10";

    private Integer size;
    private long totalElements;
    private Integer totalPages;
    private Integer page;
}
