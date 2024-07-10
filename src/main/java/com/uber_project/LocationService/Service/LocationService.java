package com.uber_project.LocationService.Service;

import java.util.List;

public interface LocationService {

    public void saveDriverLocation(String driverId, Double latitude, Double longitude);

    public List<String> getNearbyDrivers(Double latitude, Double longitude);
}
