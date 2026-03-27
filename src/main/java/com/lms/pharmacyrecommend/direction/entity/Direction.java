package com.lms.pharmacyrecommend.direction.entity;

import com.lms.pharmacyrecommend.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "direction")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Table(indexes = {
        @Index(name = "idx_direction_input", columnList = "inputAddress"),
        @Index(name = "idx_direction_created", columnList = "createdDate")
})
public class Direction extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String inputAddress;
    private double inputLatitude;
    private double inputLongitude;

    private String targetPharmacyName;
    private String targetAddress;
    private double targetLatitude;
    private double targetLongitude;

    private double distance;
}
