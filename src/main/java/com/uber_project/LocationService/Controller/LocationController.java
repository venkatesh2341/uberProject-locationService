package com.uber_project.LocationService.Controller;

import com.uber_project.LocationService.DTO.DriverLocationDto;
import com.uber_project.LocationService.DTO.NearbyDriversRequestDto;
import com.uber_project.LocationService.DTO.SaveDriverLocationRequestDto;
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

    private static final String DRIVER_GEO_OPS_KEY = "Driver";
    private static final Double SEARCH_RADIUS= 1.0;
    private final StringRedisTemplate stringRedisTemplate;


    public LocationController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostMapping("/drivers")
    public ResponseEntity<String> saveDriverLocation(@RequestBody SaveDriverLocationRequestDto saveDriverLocationRequestDto){

        try{
            GeoOperations<String,String> geoOps= stringRedisTemplate.opsForGeo();
            geoOps.add(DRIVER_GEO_OPS_KEY,
                    new RedisGeoCommands.GeoLocation<>(
                            saveDriverLocationRequestDto.getDriverId(),
                            new Point(saveDriverLocationRequestDto.getLatitude(), saveDriverLocationRequestDto.getLongitude())
                    ));
            return new ResponseEntity<>("Added driver location", HttpStatus.CREATED);
        }
        catch (Exception exception)
        {
            return new ResponseEntity<>("Entry failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/nearby/drivers")
    public ResponseEntity<List<String>> nearbyDrivers(@RequestBody NearbyDriversRequestDto nearbyDriversRequestDto){
        try{

            GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
            Distance radius= new Distance(SEARCH_RADIUS, Metrics.KILOMETERS);
            Circle within= new Circle(new Point(nearbyDriversRequestDto.getLatitude(),nearbyDriversRequestDto.getLongitude()),radius);

            GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOps.radius(DRIVER_GEO_OPS_KEY,within);
            List<String> nearbyDrivers= new ArrayList<>();
            for(GeoResult<RedisGeoCommands.GeoLocation<String>> result: results){
                nearbyDrivers.add(result.getContent().getName());
            }
            return new ResponseEntity<>(nearbyDrivers,HttpStatus.OK);
        }
        catch (Exception exception){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
