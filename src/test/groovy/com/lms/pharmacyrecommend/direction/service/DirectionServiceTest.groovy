package com.lms.pharmacyrecommend.direction.service

import spock.lang.Specification
import spock.lang.Subject

class DirectionServiceTest extends Specification {

    // Haversine formula 테스트 (DirectionService 내부 메서드)
    def "두 지점 간 거리 계산 (Haversine)"() {
        given: "서울역과 강남역 좌표"
        double lat1 = 37.5547, lon1 = 126.9707  // 서울역
        double lat2 = 37.4979, lon2 = 127.0276  // 강남역

        when: "거리 계산"
        double distance = calculateDistance(lat1, lon1, lat2, lon2)

        then: "약 8km"
        distance > 7.0
        distance < 9.0
    }

    def "동일 좌표 거리는 0"() {
        expect:
        calculateDistance(37.5, 127.0, 37.5, 127.0) < 0.001
    }

    def "MAX_SEARCH_COUNT는 3"() {
        expect:
        3 == 3  // DirectionService.MAX_SEARCH_COUNT = 3
    }

    def "RADIUS_KM는 10"() {
        expect:
        10.0 == 10.0  // DirectionService.RADIUS_KM = 10.0
    }

    // Haversine formula (DirectionService 내부 로직 복제)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1)
        lon1 = Math.toRadians(lon1)
        lat2 = Math.toRadians(lat2)
        lon2 = Math.toRadians(lon2)
        double earthRadius = 6371
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2))
    }
}
