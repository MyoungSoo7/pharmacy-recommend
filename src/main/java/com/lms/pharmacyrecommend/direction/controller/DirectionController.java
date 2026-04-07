package com.lms.pharmacyrecommend.direction.controller;

import com.lms.pharmacyrecommend.direction.service.DirectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Direction", description = "약국 길안내 단축 URL 리다이렉트 API")
public class DirectionController {

    private final DirectionService directionService;

    @Operation(
            summary = "단축 URL → 카카오맵 길안내 리다이렉트",
            description = "Base62로 인코딩된 directionId를 받아 해당 약국의 카카오맵 길안내 URL로 302 리다이렉트한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "카카오맵 URL로 리다이렉트"),
            @ApiResponse(responseCode = "404", description = "잘못된 인코딩 ID 또는 데이터 없음")
    })
    @GetMapping("/dir/{encodedId}")
    public String searchDirection(
            @Parameter(description = "Base62 인코딩된 direction ID", example = "abc123")
            @PathVariable("encodedId") String encodedId) {

        String result = directionService.findDirectionUrlById(encodedId);

        log.info("[DirectionController searchDirection] direction url: {}", result);

        return "redirect:" + result;
    }
}
