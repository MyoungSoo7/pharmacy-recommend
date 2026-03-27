package com.lms.pharmacyrecommend.pharmacy.service;

import com.lms.pharmacyrecommend.pharmacy.cache.PharmacyRedisTemplateService;
import com.lms.pharmacyrecommend.pharmacy.dto.PharmacyDto;
import com.lms.pharmacyrecommend.pharmacy.entity.Pharmacy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PharmacySearchService {

    private final PharmacyRepositoryService pharmacyRepositoryService;
    private final PharmacyRedisTemplateService pharmacyRedisTemplateService;

    public List<PharmacyDto> searchPharmacyDtoList() {

        // redis cache-first
        List<PharmacyDto> pharmacyDtoList = pharmacyRedisTemplateService.findAll();
        if(CollectionUtils.isNotEmpty(pharmacyDtoList)) {
            log.info("[PharmacySearchService] cache hit, size: {}", pharmacyDtoList.size());
            return pharmacyDtoList;
        }

        // cache miss → DB fallback + auto-reload cache
        log.info("[PharmacySearchService] cache miss, loading from DB and reloading cache");
        List<PharmacyDto> dbList = pharmacyRepositoryService.findAll()
                .stream()
                .map(this::convertToPharmacyDto)
                .collect(Collectors.toList());

        // DB 데이터를 Redis에 자동 적재 (다음 요청부터 캐시 히트)
        dbList.forEach(pharmacyRedisTemplateService::save);
        log.info("[PharmacySearchService] cache reloaded, size: {}", dbList.size());

        return dbList;
    }

    private PharmacyDto convertToPharmacyDto(Pharmacy pharmacy) {

        return PharmacyDto.builder()
                .id(pharmacy.getId())
                .pharmacyName(pharmacy.getPharmacyName())
                .pharmacyAddress(pharmacy.getPharmacyAddress())
                .latitude(pharmacy.getLatitude())
                .longitude(pharmacy.getLongitude())
                .build();
    }
}
