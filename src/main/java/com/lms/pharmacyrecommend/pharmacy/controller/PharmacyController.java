package com.lms.pharmacyrecommend.pharmacy.controller;

import com.lms.pharmacyrecommend.pharmacy.cache.PharmacyRedisTemplateService;
import com.lms.pharmacyrecommend.pharmacy.dto.PharmacyDto;
import com.lms.pharmacyrecommend.pharmacy.entity.Pharmacy;
import com.lms.pharmacyrecommend.pharmacy.service.PharmacyRepositoryService;
import com.lms.pharmacyrecommend.util.CsvUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Pharmacy", description = "약국 데이터 적재 / 캐시 관리 API")
public class PharmacyController {

    private final PharmacyRepositoryService pharmacyRepositoryService;
    private final PharmacyRedisTemplateService pharmacyRedisTemplateService;

    @Operation(
            summary = "DB 약국 데이터를 Redis 캐시에 저장",
            description = "DB에 저장된 모든 약국 데이터를 조회하여 Redis 해시 캐시(PHARMACY)에 적재한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공 시 'success save' 문자열 반환")
    })
    @GetMapping("/csv/save")
    public String saveCsv() {
        saveCsvToRedis();
        return "success save";
    }

    public void saveCsvToDatabase() {
        List<Pharmacy> pharmacyList = loadPharmacyList();
        pharmacyRepositoryService.saveAll(pharmacyList);
    }

    public void saveCsvToRedis() {
        List<PharmacyDto> pharmacyDtoList = pharmacyRepositoryService.findAll()
                .stream().map(pharmacy -> PharmacyDto.builder()
                        .id(pharmacy.getId())
                        .pharmacyName(pharmacy.getPharmacyName())
                        .pharmacyAddress(pharmacy.getPharmacyAddress())
                        .latitude(pharmacy.getLatitude())
                        .longitude(pharmacy.getLongitude())
                        .build())
                .collect(Collectors.toList());

        log.info("[PharmacyController] saving {} pharmacies to Redis (batch)", pharmacyDtoList.size());
        int batchSize = 500;
        for (int i = 0; i < pharmacyDtoList.size(); i += batchSize) {
            List<PharmacyDto> batch = pharmacyDtoList.subList(i, Math.min(i + batchSize, pharmacyDtoList.size()));
            batch.forEach(pharmacyRedisTemplateService::save);
            log.info("[PharmacyController] batch {}/{} saved", Math.min(i + batchSize, pharmacyDtoList.size()), pharmacyDtoList.size());
        }
        log.info("[PharmacyController] Redis save complete");
    }

    private List<Pharmacy> loadPharmacyList() {
        return CsvUtils.convertToPharmacyDtoList()
                .stream().map(pharmacyDto ->
                        Pharmacy.builder()
                                .id(pharmacyDto.getId())
                                .pharmacyName(pharmacyDto.getPharmacyName())
                                .pharmacyAddress(pharmacyDto.getPharmacyAddress())
                                .latitude(pharmacyDto.getLatitude())
                                .longitude(pharmacyDto.getLongitude())
                                .build())
                .collect(Collectors.toList());
    }
}
