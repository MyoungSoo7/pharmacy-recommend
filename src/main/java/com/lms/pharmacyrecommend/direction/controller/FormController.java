package com.lms.pharmacyrecommend.direction.controller;

import com.lms.pharmacyrecommend.direction.dto.InputDto;
import com.lms.pharmacyrecommend.pharmacy.service.PharmacyRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Form", description = "약국 추천 폼 화면 / 검색 API")
public class FormController {
    private final PharmacyRecommendationService pharmacyRecommendationService;

    @Operation(summary = "메인 화면", description = "주소 입력 폼 메인 페이지를 반환한다.")
    @ApiResponse(responseCode = "200", description = "메인 페이지 (Mustache 템플릿)")
    @GetMapping("/")
    public String main() {
        log.info("main");

        return "main";
    }

    @Operation(
            summary = "주소 기반 약국 추천",
            description = "사용자 입력 주소를 카카오 지오코딩 API로 좌표 변환한 뒤, " +
                    "Haversine 공식으로 반경 10km 내 가장 가까운 약국 3곳을 추천하여 결과 페이지를 반환한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추천 결과 페이지 (Mustache 템플릿)"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    @PostMapping("/search")
    public ModelAndView postDirection(@ModelAttribute InputDto inputDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("output");
        modelAndView.addObject("outputFormList",
                pharmacyRecommendationService.recommendPharmacyList(inputDto.getAddress()));

        return modelAndView;
    }
}
