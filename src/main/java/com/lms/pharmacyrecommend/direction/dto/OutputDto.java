package com.lms.pharmacyrecommend.direction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "약국 추천 결과 DTO")
public class OutputDto {

    @Schema(description = "약국 명", example = "강남365약국")
    private String pharmacyName;

    @Schema(description = "약국 주소", example = "서울 강남구 역삼동 123-45")
    private String pharmacyAddress;

    @Schema(description = "단축 길안내 URL", example = "https://pharmacy.lemuel.co.kr/dir/abc123")
    private String directionUrl;

    @Schema(description = "카카오맵 로드뷰 URL", example = "https://map.kakao.com/link/roadview/37.5,127.0")
    private String roadViewUrl;

    @Schema(description = "사용자 주소와 약국 주소 사이 거리", example = "1.23 km")
    private String distance;
}
