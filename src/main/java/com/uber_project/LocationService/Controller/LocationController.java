package com.uber_project.LocationService.Controller;

import com.uber_project.LocationService.DTO.DriverLocationDto;
import com.uber_project.LocationService.DTO.NearbyDriversRequestDto;
import com.uber_project.LocationService.DTO.SaveDriverLocationRequestDto;
import com.uber_project.LocationService.Service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService= locationService;
    }

    @PostMapping("/drivers")
    public ResponseEntity<String> saveDriverLocation(@RequestBody SaveDriverLocationRequestDto saveDriverLocationRequestDto){

        try{
            locationService.saveDriverLocation(saveDriverLocationRequestDto.getDriverId(),saveDriverLocationRequestDto.getLatitude(),saveDriverLocationRequestDto.getLongitude());
            return new ResponseEntity<>("Added driver location", HttpStatus.CREATED);
        }
        catch (Exception exception)
        {
            return new ResponseEntity<>("Entry failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/nearby/drivers")
    public ResponseEntity<List<DriverLocationDto>> nearbyDrivers(@RequestBody NearbyDriversRequestDto nearbyDriversRequestDto){
        try{
            List<DriverLocationDto> nearbyDriversInfo = locationService.getNearbyDrivers(nearbyDriversRequestDto.getLatitude(),nearbyDriversRequestDto.getLongitude(), nearbyDriversRequestDto.getRadius());
            return new ResponseEntity<>(nearbyDriversInfo,HttpStatus.OK);
        }
        catch (Exception exception){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
