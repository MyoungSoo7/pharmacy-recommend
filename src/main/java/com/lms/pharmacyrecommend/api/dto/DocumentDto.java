package com.lms.pharmacyrecommend.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {

    @JsonProperty("place_name")
    private String placeName;

    @JsonProperty("address_name")
    private String addressName;

    @JsonProperty("y")
    private Double latitude;

    @JsonProperty("x")
    private Double longitude;

    @JsonProperty("distance")
    private Double distance;
}
