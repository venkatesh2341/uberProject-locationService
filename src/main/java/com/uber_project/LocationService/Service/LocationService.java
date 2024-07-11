package com.uber_project.LocationService.Service;

import com.uber_project.LocationService.DTO.DriverLocationDto;

import java.util.List;

public interface LocationService {

    public void saveDriverLocation(String driverId, Double latitude, Double longitude);

    public List<DriverLocationDto> getNearbyDrivers(Double latitude, Double longitude, Integer radius);
}
