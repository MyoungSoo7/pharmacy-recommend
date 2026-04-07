package com.lms.pharmacyrecommend.pharmacy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "약국 데이터 DTO (캐시/응답 전달용)")
public class PharmacyDto {

    @Schema(description = "약국 ID", example = "1")
    private Long id;

    @Schema(description = "약국 명", example = "강남365약국")
    private String pharmacyName;

    @Schema(description = "약국 주소", example = "서울 강남구 역삼동 123-45")
    private String pharmacyAddress;

    @Schema(description = "위도", example = "37.5012")
    private double latitude;

    @Schema(description = "경도", example = "127.0396")
    private double longitude;
}
