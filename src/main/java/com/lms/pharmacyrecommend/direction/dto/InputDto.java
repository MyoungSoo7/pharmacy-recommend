package com.lms.pharmacyrecommend.direction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주소 검색 입력 DTO")
public class InputDto {

    @Schema(description = "검색할 사용자 주소", example = "서울특별시 강남구 역삼동")
    private String address;
}
