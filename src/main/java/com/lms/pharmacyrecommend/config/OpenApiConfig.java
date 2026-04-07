package com.lms.pharmacyrecommend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Pharmacy Recommendation API",
                version = "1.0.0",
                description = "사용자가 입력한 주소를 기반으로 카카오 지오코딩 API와 Haversine 거리 계산을 활용하여 " +
                        "반경 10km 내 최근접 약국 3곳을 추천하는 서비스의 OpenAPI 명세.",
                contact = @Contact(name = "Pharmacy Recommend Team"),
                license = @License(name = "Educational use only")
        ),
        servers = {
                @Server(url = "http://localhost:8081", description = "Local server")
        }
)
public class OpenApiConfig {
}
