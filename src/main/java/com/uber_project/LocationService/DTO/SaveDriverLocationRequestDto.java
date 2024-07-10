package com.uber_project.LocationService.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveDriverLocationRequestDto {

    private String driverId;
    private Double latitude;
    private Double longitude;
}
