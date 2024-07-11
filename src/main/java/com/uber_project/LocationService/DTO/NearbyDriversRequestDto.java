package com.uber_project.LocationService.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyDriversRequestDto {
    private Double latitude;
    private Double longitude;
    private Integer radius;
}