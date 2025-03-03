package com.thachnn.ShopIoT.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolutionResponse {

    private Integer id;
    private String name;
    private String slug;
    private String content;
    private boolean enabled;
}
