package com.seoul.openproject.partner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ListResponse<T> {

    @Schema(name= "valueCount" , example = "4", description = "values 의 개수.")
    @NotNull
    private Integer valueCount;

    @Builder.Default
    @Schema(name= "values" , example = " ", description = "Json 객체 배열")
    private List<T> values = new ArrayList<>();
}
